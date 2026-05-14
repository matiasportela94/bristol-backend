package com.bristol.api.controller;

import com.bristol.application.distributor.dto.CreateDistributorRequest;
import com.bristol.application.distributor.dto.CreateDistributorRegistrationRequest;
import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.dto.RegistrationDocumentPayload;
import com.bristol.application.distributor.dto.RejectDistributorRegistrationRequest;
import com.bristol.application.distributor.dto.UpdateDistributorRequest;
import com.bristol.application.distributor.usecase.*;
import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.application.distributorbranch.usecase.GetMyBranchesUseCase;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.RegistrationDocument;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.domain.shared.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for distributor endpoints.
 */
@RestController
@RequestMapping("/api/distributors")
@RequiredArgsConstructor
@Tag(name = "Distributors", description = "Distributor management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DistributorController {

    private final CreateDistributorUseCase createDistributorUseCase;
    private final GetAllDistributorsUseCase getAllDistributorsUseCase;
    private final GetDistributorByIdUseCase getDistributorByIdUseCase;
    private final GetDistributorByUserIdUseCase getDistributorByUserIdUseCase;
    private final UpdateDistributorUseCase updateDistributorUseCase;
    private final DeleteDistributorUseCase deleteDistributorUseCase;

    // Registration workflow use cases
    private final RegisterDistributorUseCase registerDistributorUseCase;
    private final GetPendingRegistrationsUseCase getPendingRegistrationsUseCase;
    private final GetDistributorRegistrationByIdUseCase getDistributorRegistrationByIdUseCase;
    private final ApproveDistributorRegistrationUseCase approveDistributorRegistrationUseCase;
    private final RejectDistributorRegistrationUseCase rejectDistributorRegistrationUseCase;
    private final RegistrationDocumentService registrationDocumentService;
    private final DistributorDocumentQueryService distributorDocumentQueryService;
    private final DistributorAccessService distributorAccessService;
    private final AddDistributorDocumentUseCase addDistributorDocumentUseCase;
    private final DeleteDistributorDocumentUseCase deleteDistributorDocumentUseCase;
    private final GetMyBranchesUseCase getMyBranchesUseCase;

    @GetMapping("/me/branches")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get my branches", description = "Returns branches visible to the authenticated user. DISTRIBUTOR → all their branches. DISTRIBUTOR_BRANCH → only their own branch.")
    public ResponseEntity<List<DistributorBranchDto>> getMyBranches(Authentication authentication) {
        return ResponseEntity.ok(getMyBranchesUseCase.execute(authentication.getName()));
    }

    /**
     * Get all distributors.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all distributors", description = "Retrieve all distributors (Admin only)")
    public ResponseEntity<List<DistributorDto>> getAllDistributors() {
        return ResponseEntity.ok(getAllDistributorsUseCase.execute());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get distributor by user ID", description = "Retrieve the distributor associated to a user")
    public ResponseEntity<DistributorDto> getDistributorByUserId(
            @PathVariable String userId,
            Authentication authentication
    ) {
        resolveAccessibleDistributorByUserId(userId, authentication);
        return ResponseEntity.ok(getDistributorByUserIdUseCase.execute(userId));
    }

    /**
     * Get distributor by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get distributor by ID", description = "Retrieve a distributor by its ID")
    public ResponseEntity<DistributorDto> getDistributorById(@PathVariable String id, Authentication authentication) {
        resolveAccessibleDistributor(id, authentication);
        return ResponseEntity.ok(getDistributorByIdUseCase.execute(id));
    }

    /**
     * Create a new distributor.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create distributor", description = "Create a new distributor (Admin only)")
    public ResponseEntity<DistributorDto> createDistributor(@Valid @RequestBody CreateDistributorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createDistributorUseCase.execute(request));
    }

    /**
     * Update distributor information.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Update distributor", description = "Update distributor business information")
    public ResponseEntity<DistributorDto> updateDistributor(
            @PathVariable String id,
            @Valid @RequestBody UpdateDistributorRequest request,
            Authentication authentication
    ) {
        resolveAccessibleDistributor(id, authentication);
        DistributorDto distributor = updateDistributorUseCase.execute(id, request);
        return ResponseEntity.ok(distributor);
    }

    /**
     * Delete a distributor.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete distributor", description = "Delete a distributor (Admin only)")
    public ResponseEntity<Void> deleteDistributor(@PathVariable String id) {
        deleteDistributorUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Registration Workflow Endpoints ====================

    /**
     * Register a new distributor request (public endpoint).
     * Creates a PENDING registration request that must be approved by an admin.
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Register distributor request",
            description = "Submit a distributor registration request (Public endpoint - no authentication required)",
            security = {}
    )
    public ResponseEntity<DistributorRegistrationDto> registerDistributorJson(
            @Valid @RequestBody CreateDistributorRegistrationRequest request
    ) {
        DistributorRegistrationDto registration = registerDistributorUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Register distributor request with documents",
            description = "Submit a distributor registration request with ARCA documents (Public endpoint - no authentication required)",
            security = {}
    )
    public ResponseEntity<DistributorRegistrationDto> registerDistributorMultipart(
            @RequestParam String razonSocial,
            @RequestParam String cuit,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String ciudad,
            @RequestParam String direccion,
            @RequestParam(required = false) String codigoPostal,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents
    ) {
        CreateDistributorRegistrationRequest request = CreateDistributorRegistrationRequest.builder()
                .razonSocial(razonSocial)
                .cuit(cuit)
                .email(email)
                .telefono(telefono)
                .provincia(provincia)
                .ciudad(ciudad)
                .direccion(direccion)
                .codigoPostal(codigoPostal)
                .documents(toDocumentPayloads(documents))
                .build();

        DistributorRegistrationDto registration = registerDistributorUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    /**
     * Get all pending distributor registration requests (admin only).
     */
    @GetMapping("/registrations/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get pending registrations",
            description = "Retrieve all pending distributor registration requests (Admin only)"
    )
    public ResponseEntity<List<DistributorRegistrationDto>> getPendingRegistrations() {
        List<DistributorRegistrationDto> pendingRegistrations = getPendingRegistrationsUseCase.execute();
        return ResponseEntity.ok(pendingRegistrations);
    }

    @GetMapping("/registrations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get registration by ID",
            description = "Retrieve a distributor registration request by its ID, including uploaded files (Admin only)"
    )
    public ResponseEntity<DistributorRegistrationDto> getRegistrationById(@PathVariable String id) {
        return ResponseEntity.ok(getDistributorRegistrationByIdUseCase.execute(id));
    }

    @GetMapping("/registrations/{registrationId}/documents/{documentId}/download")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Download registration document", description = "Download a document attached to a distributor registration request (Admin only)")
    public ResponseEntity<byte[]> downloadRegistrationDocument(
            @PathVariable String registrationId,
            @PathVariable String documentId
    ) {
        RegistrationDocument document = registrationDocumentService.getDocumentForRegistration(
                new DistributorRegistrationRequestId(UUID.fromString(registrationId)),
                new RegistrationDocumentId(UUID.fromString(documentId))
        );

        return buildFileDownloadResponse(document);
    }

    @GetMapping("/{distributorId}/documents/{documentId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Download distributor document", description = "Download a document associated to an approved distributor")
    public ResponseEntity<byte[]> downloadDistributorDocument(
            @PathVariable String distributorId,
            @PathVariable String documentId,
            Authentication authentication
    ) {
        resolveAccessibleDistributor(distributorId, authentication);
        RegistrationDocument document = distributorDocumentQueryService.getDocumentForDistributor(distributorId, documentId);

        return buildFileDownloadResponse(document);
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Upload distributor document", description = "Upload a supporting document for a distributor")
    public ResponseEntity<DistributorDto> uploadDistributorDocument(
            @PathVariable String id,
            @RequestPart("document") MultipartFile document,
            @RequestParam(required = false) String documentType,
            Authentication authentication
    ) {
        var distributor = resolveAccessibleDistributor(id, authentication);

        try {
            DistributorDto distributorDto = addDistributorDocumentUseCase.execute(
                    distributor,
                    document.getOriginalFilename(),
                    document.getContentType(),
                    documentType,
                    document.getSize(),
                    document.getBytes()
            );
            return ResponseEntity.ok(distributorDto);
        } catch (IOException ex) {
            throw new ValidationException("Could not read uploaded distributor document");
        }
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Delete distributor document", description = "Delete a supporting document for a distributor")
    public ResponseEntity<DistributorDto> deleteDistributorDocument(
            @PathVariable String id,
            @PathVariable String documentId,
            Authentication authentication
    ) {
        var distributor = resolveAccessibleDistributor(id, authentication);
        return ResponseEntity.ok(deleteDistributorDocumentUseCase.execute(distributor, documentId));
    }

    /**
     * Approve a distributor registration request (admin only).
     * Creates a User and Distributor entity upon approval.
     */
    @PostMapping("/registrations/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve registration",
            description = "Approve a distributor registration request and create User + Distributor (Admin only)"
    )
    public ResponseEntity<DistributorRegistrationDto> approveRegistration(@PathVariable String id) {
        DistributorRegistrationDto approvedRegistration = approveDistributorRegistrationUseCase.execute(id);
        return ResponseEntity.ok(approvedRegistration);
    }

    /**
     * Reject a distributor registration request (admin only).
     */
    @PostMapping("/registrations/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject registration",
            description = "Reject a distributor registration request with a reason (Admin only)"
    )
    public ResponseEntity<DistributorRegistrationDto> rejectRegistration(
            @PathVariable String id,
            @Valid @RequestBody RejectDistributorRegistrationRequest request
    ) {
        DistributorRegistrationDto rejectedRegistration = rejectDistributorRegistrationUseCase.execute(id, request);
        return ResponseEntity.ok(rejectedRegistration);
    }

    private List<RegistrationDocumentPayload> toDocumentPayloads(List<MultipartFile> documents) {
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        return documents.stream()
                .map(this::toDocumentPayload)
                .toList();
    }

    private RegistrationDocumentPayload toDocumentPayload(MultipartFile document) {
        try {
            return RegistrationDocumentPayload.builder()
                    .fileName(document.getOriginalFilename())
                    .contentType(document.getContentType())
                    .fileSize(document.getSize())
                    .fileData(document.getBytes())
                    .build();
        } catch (IOException ex) {
            throw new ValidationException("Could not read uploaded registration document");
        }
    }

    private ResponseEntity<byte[]> buildFileDownloadResponse(RegistrationDocument document) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(document.getFileData());
    }

    private com.bristol.domain.distributor.Distributor resolveAccessibleDistributor(
            String distributorId,
            Authentication authentication
    ) {
        return distributorAccessService.getAccessibleDistributor(
                distributorId,
                authentication.getName(),
                distributorAccessService.isAdmin(authentication.getAuthorities())
        );
    }

    private com.bristol.domain.distributor.Distributor resolveAccessibleDistributorByUserId(
            String userId,
            Authentication authentication
    ) {
        return distributorAccessService.getAccessibleDistributorByUserId(
                userId,
                authentication.getName(),
                distributorAccessService.isAdmin(authentication.getAuthorities())
        );
    }
}
