package com.bristol.api.controller;

import com.bristol.application.distributorbranch.dto.AssignBranchUserRequest;
import com.bristol.application.distributorbranch.dto.CreateDistributorBranchRequest;
import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.application.distributorbranch.dto.UpdateDistributorBranchRequest;
import com.bristol.application.distributorbranch.usecase.*;
import com.bristol.application.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distributors/{distributorId}/branches")
@RequiredArgsConstructor
@Tag(name = "Distributor Branches", description = "Manage branches (franchise locations) of a distributor")
@SecurityRequirement(name = "Bearer Authentication")
public class DistributorBranchController {

    private final CreateDistributorBranchUseCase createBranchUseCase;
    private final GetBranchesByDistributorUseCase getBranchesUseCase;
    private final GetDistributorBranchByIdUseCase getBranchByIdUseCase;
    private final UpdateDistributorBranchUseCase updateBranchUseCase;
    private final DeleteDistributorBranchUseCase deleteBranchUseCase;
    private final CreateBranchUserUseCase createBranchUserUseCase;
    private final GetBranchUsersUseCase getBranchUsersUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "List branches", description = "DISTRIBUTOR: all branches. DISTRIBUTOR_BRANCH: only their own branch.")
    public ResponseEntity<List<DistributorBranchDto>> getBranches(@PathVariable String distributorId) {
        return ResponseEntity.ok(getBranchesUseCase.execute(distributorId));
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get branch by ID")
    public ResponseEntity<DistributorBranchDto> getBranchById(
            @PathVariable String distributorId,
            @PathVariable String branchId
    ) {
        return ResponseEntity.ok(getBranchByIdUseCase.execute(branchId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
    @Operation(summary = "Create branch", description = "Create a new branch for a distributor (Admin or distributor owner)")
    public ResponseEntity<DistributorBranchDto> createBranch(
            @PathVariable String distributorId,
            @Valid @RequestBody CreateDistributorBranchRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBranchUseCase.execute(distributorId, request));
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
    @Operation(summary = "Update branch", description = "Update branch information (Admin or distributor owner)")
    public ResponseEntity<DistributorBranchDto> updateBranch(
            @PathVariable String distributorId,
            @PathVariable String branchId,
            @Valid @RequestBody UpdateDistributorBranchRequest request
    ) {
        return ResponseEntity.ok(updateBranchUseCase.execute(branchId, request));
    }

    @DeleteMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
    @Operation(summary = "Delete branch", description = "Delete a branch (Admin or distributor owner)")
    public ResponseEntity<Void> deleteBranch(
            @PathVariable String distributorId,
            @PathVariable String branchId
    ) {
        deleteBranchUseCase.execute(branchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{branchId}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
    @Operation(summary = "List branch users", description = "List all users assigned to a specific branch.")
    public ResponseEntity<List<UserDto>> getBranchUsers(
            @PathVariable String distributorId,
            @PathVariable String branchId
    ) {
        return ResponseEntity.ok(getBranchUsersUseCase.execute(branchId));
    }

    @PostMapping("/{branchId}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
    @Operation(summary = "Create branch user", description = "Create a user account for a branch. The user gets DISTRIBUTOR_BRANCH role and can only see their branch's data.")
    public ResponseEntity<UserDto> createBranchUser(
            @PathVariable String distributorId,
            @PathVariable String branchId,
            @Valid @RequestBody AssignBranchUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBranchUserUseCase.execute(branchId, request));
    }
}
