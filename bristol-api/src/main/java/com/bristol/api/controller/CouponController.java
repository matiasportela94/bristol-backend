package com.bristol.api.controller;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.application.coupon.dto.CouponRedemptionDto;
import com.bristol.application.coupon.dto.CreateCouponRequest;
import com.bristol.application.coupon.dto.UpdateCouponRequest;
import com.bristol.application.coupon.dto.ValidateCouponRequest;
import com.bristol.application.coupon.usecase.*;
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

/**
 * REST controller for coupon endpoints.
 */
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon management endpoints")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final GetAllCouponsUseCase getAllCouponsUseCase;
    private final GetCouponByIdUseCase getCouponByIdUseCase;
    private final GetCouponRedemptionsUseCase getCouponRedemptionsUseCase;
    private final GetActiveCouponsUseCase getActiveCouponsUseCase;
    private final ValidateCouponUseCase validateCouponUseCase;
    private final UpdateCouponUseCase updateCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    /**
     * Get all coupons.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all coupons", description = "Retrieve all coupons (Admin only)")
    public ResponseEntity<List<CouponDto>> getAllCoupons() {
        List<CouponDto> coupons = getAllCouponsUseCase.execute();
        return ResponseEntity.ok(coupons);
    }

    /**
     * Get active coupons.
     */
    @GetMapping("/active")
    @Operation(summary = "Get active coupons", description = "Retrieve all active coupons (Public)")
    public ResponseEntity<List<CouponDto>> getActiveCoupons() {
        List<CouponDto> coupons = getActiveCouponsUseCase.execute();
        return ResponseEntity.ok(coupons);
    }

    /**
     * Get coupon by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get coupon by ID", description = "Retrieve a coupon by ID (Admin only)")
    public ResponseEntity<CouponDto> getCouponById(@PathVariable String id) {
        CouponDto coupon = getCouponByIdUseCase.execute(id);
        return ResponseEntity.ok(coupon);
    }

    /**
     * Get coupon redemption history.
     */
    @GetMapping("/{id}/redemptions")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get coupon redemptions", description = "Retrieve redemption history for a coupon (Admin only)")
    public ResponseEntity<List<CouponRedemptionDto>> getCouponRedemptions(@PathVariable String id) {
        return ResponseEntity.ok(getCouponRedemptionsUseCase.execute(id));
    }

    /**
     * Create a new coupon.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create coupon", description = "Create a new coupon (Admin only)")
    public ResponseEntity<CouponDto> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        CouponDto coupon = createCouponUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
    }

    /**
     * Update coupon information.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update coupon", description = "Update coupon basic information (Admin only)")
    public ResponseEntity<CouponDto> updateCoupon(
            @PathVariable String id,
            @Valid @RequestBody UpdateCouponRequest request
    ) {
        CouponDto coupon = updateCouponUseCase.execute(id, request);
        return ResponseEntity.ok(coupon);
    }

    /**
     * Validate a coupon code.
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate coupon", description = "Validate a coupon code (Public)")
    public ResponseEntity<CouponDto> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        CouponDto coupon = validateCouponUseCase.execute(request);
        return ResponseEntity.ok(coupon);
    }

    /**
     * Delete a coupon (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete coupon", description = "Soft delete a coupon (Admin only)")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
