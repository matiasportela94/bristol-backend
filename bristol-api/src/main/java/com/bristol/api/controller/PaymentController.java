package com.bristol.api.controller;

import com.bristol.application.payment.dto.ApprovePaymentRequest;
import com.bristol.application.payment.dto.CreatePaymentRequest;
import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.application.payment.dto.RejectPaymentRequest;
import com.bristol.application.payment.usecase.ApprovePaymentUseCase;
import com.bristol.application.payment.usecase.CreatePaymentUseCase;
import com.bristol.application.payment.usecase.GetAllPaymentsUseCase;
import com.bristol.application.payment.usecase.GetPaymentByIdUseCase;
import com.bristol.application.payment.usecase.GetPaymentsByOrderUseCase;
import com.bristol.application.payment.usecase.GetUserPaymentsUseCase;
import com.bristol.application.payment.usecase.RejectPaymentUseCase;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentByIdUseCase getPaymentByIdUseCase;
    private final GetPaymentsByOrderUseCase getPaymentsByOrderUseCase;
    private final GetUserPaymentsUseCase getUserPaymentsUseCase;
    private final GetAllPaymentsUseCase getAllPaymentsUseCase;
    private final ApprovePaymentUseCase approvePaymentUseCase;
    private final RejectPaymentUseCase rejectPaymentUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Create payment", description = "Create a payment record for an order")
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPaymentUseCase.execute(request));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get payments by order", description = "Retrieve all payments for an order")
    public ResponseEntity<List<PaymentDto>> getPaymentsByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(getPaymentsByOrderUseCase.execute(orderId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get payments by user", description = "Retrieve all payments for a user")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable String userId) {
        return ResponseEntity.ok(getUserPaymentsUseCase.execute(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments", description = "Retrieve all payments (Admin only)")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(getAllPaymentsUseCase.execute());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Get payment by ID", description = "Retrieve a single payment by its ID")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(getPaymentByIdUseCase.execute(id));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve payment", description = "Approve a payment and trigger downstream order flow")
    public ResponseEntity<PaymentDto> approvePayment(
            @PathVariable String id,
            @RequestBody(required = false) ApprovePaymentRequest request
    ) {
        return ResponseEntity.ok(approvePaymentUseCase.execute(id, request));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject payment", description = "Reject a payment and mark the order as payment failed")
    public ResponseEntity<PaymentDto> rejectPayment(
            @PathVariable String id,
            @Valid @RequestBody RejectPaymentRequest request
    ) {
        return ResponseEntity.ok(rejectPaymentUseCase.execute(id, request));
    }
}
