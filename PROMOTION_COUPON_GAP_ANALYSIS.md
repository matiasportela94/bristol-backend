# Promotion And Coupon Gap Analysis

## Context

This document compares the current implementation with the promotion and coupon model defined in:

- `BRISTOL_ECOMMERCE_PLAN.md`
- `BUSINESS_GLOSSARY.md`
- `DOMAIN_MAP.md`

It is focused on the current codebase status after wiring the admin coupon screen to backend persistence.

## What The Plan Requires

From the plan and glossary, the target model is broader than simple coupons:

- manual coupons and automatic promotions
- scopes by:
  - entire order
  - specific product
  - category
  - subcategory
  - beer type
  - manual selection
- benefits by:
  - percentage
  - fixed amount
  - shipping discount
  - 2x1
  - buy X get Y
- rules:
  - date window
  - total usage limit
  - per-customer limit
  - combinability
  - priority
  - statuses including draft, active, paused, expired
- a promotion engine that applies these rules centrally, without hardcoding discount logic in order flows

## Current Code Snapshot

### Persisted coupon/admin contract

The current `Coupon` aggregate now persists the admin form and soft delete correctly:

- `bristol-domain/src/main/java/com/bristol/domain/coupon/Coupon.java`
- `bristol-infrastructure/src/main/java/com/bristol/infrastructure/persistence/entity/CouponEntity.java`
- `bristol-infrastructure/src/main/resources/db/migration/V17__expand_coupon_admin_fields.sql`

This includes:

- start/end schedule
- manual vs automatic
- minimum amount/quantity
- total/per-customer limits
- combinability flags
- selected item payloads
- trigger payloads
- quantity discount config stored in `ruleConfig`

### Order application flow

Discount application is still split by coupon type and hardcoded at use-case level:

- `bristol-application/src/main/java/com/bristol/application/order/usecase/ApplyOrderCouponUseCase.java`
- `bristol-application/src/main/java/com/bristol/application/order/usecase/ApplyShippingCouponUseCase.java`
- `bristol-domain/src/main/java/com/bristol/domain/order/OrderCalculationService.java`
- `bristol-domain/src/main/java/com/bristol/domain/coupon/CouponValidationService.java`

## Main Gaps Against The Plan

### 1. `Coupon` is acting as both coupon and promotion

The glossary distinguishes:

- coupon: manual code
- automatic promotion: no code required

Current code models both with the same aggregate and a `method` enum. That is workable short-term, but conceptually it mixes:

- activation mechanism
- commercial rule
- discount calculation

This is the main reason the aggregate is accumulating generic JSON fields like `selectedItems` and `ruleConfig`.

### 2. Scope is not explicit enough

The plan requires structured scope for:

- product
- category
- subcategory
- beer type
- manual selection
- entire order

Current implementation has:

- `appliesTo`
- `selectedItems` JSON
- legacy category/subcategory columns in `CouponEntity`

But there is no explicit domain model for scope, and no evaluator for category, subcategory, or beer type. The persisted data can exist, but business logic cannot reliably reason over it.

### 3. Benefit model is still partial

Current code supports:

- percentage
- fixed amount
- shipping discount

And now stores quantity-based rules in `ruleConfig`, but there is no first-class domain behavior for:

- 2x1
- buy X get Y
- buy X for Y
- percentage on quantity

So these promotions can be configured and persisted, but not correctly executed by the order engine.

### 4. There is no promotion engine yet

The plan explicitly calls for a `PromotionEngine`.

Current behavior is distributed across:

- order coupon use case
- shipping coupon use case
- `OrderCalculationService`
- `CouponValidationService`

That means:

- application paths are duplicated
- scope resolution is absent
- prioritization is absent
- combinability is incomplete
- future promotion types will keep increasing branching in use cases

### 5. Priority is missing

The plan requires priority when promotions compete.

There is no priority field in:

- domain
- DTOs
- persistence
- evaluation logic

Without priority, overlapping promotions cannot be resolved deterministically.

### 6. Status model is still narrower than the plan

The plan expects:

- draft
- active
- paused
- expired

Current system has:

- active
- paused
- expired

The admin front derives `scheduled` client-side, but there is no persisted `draft`.

### 7. Redemption tracking is incomplete

Current persistence only tracks aggregate usage counts:

- total times used

Missing pieces:

- per-customer redemption history
- promotion application audit
- promotion redemption event trail

Without that, per-customer limits are configuration only. They are not enforceable in a robust way.

### 8. Validation is not consistently used in application flows

`CouponValidationService` exists, but `ApplyOrderCouponUseCase` and `ApplyShippingCouponUseCase` do not delegate to a central validation/evaluation step.

As a result:

- duplicated discount math exists in use cases
- schedule/minimum rules can drift from application logic
- the model is harder to extend

### 9. Current JSON payloads are transitional, not final design

The newly-added fields:

- `selectedItems`
- `specificCustomers`
- `ruleConfig`

solve admin persistence, but they are a storage bridge. They should not become the final business model for the promotion engine.

## Recommended Target Design

## Core Concept

Introduce a first-class `Promotion` model as the business rule, and treat coupon code as an activation mechanism.

Recommended shape:

- `Promotion`
  - id
  - name
  - description
  - activation type: manual code or automatic
  - optional `couponCode`
  - status
  - priority
  - schedule
  - scope
  - benefit
  - eligibility / minimum requirements
  - usage policy
  - combinability policy
  - audit metadata

### Suggested value objects

- `PromotionScope`
  - entire order
  - specific products
  - category
  - subcategory
  - beer type
  - manual selection

- `PromotionBenefit`
  - percentage
  - fixed amount
  - shipping discount
  - buy x get y
  - buy x for y
  - percentage on quantity

- `PromotionEligibility`
  - minimum amount
  - minimum quantity
  - customer targeting
  - trigger product when applicable

- `PromotionUsagePolicy`
  - max total uses
  - max uses per customer

- `PromotionCombinability`
  - with product promotions
  - with order promotions
  - with shipping promotions

### Domain service

- `PromotionEngine`
  - input: order/cart snapshot + candidate promotions
  - output: ordered set of applications with discount breakdown
  - responsibilities:
    - filter by schedule and status
    - resolve activation
    - validate scope
    - validate minimum requirements
    - validate per-customer usage
    - resolve combinability
    - resolve priority
    - calculate final applications

## Recommended Persistence Direction

Short term, do not throw away the current `coupons` table immediately.

### Incremental path

#### Step 1

Keep the current `Coupon` API working, but introduce explicit internal types for:

- scope
- benefit
- priority
- usage policy

This can still map to the current table plus `ruleConfig`.

#### Step 2

Add structured persistence where the business rules need it most:

- `priority`
- `scope_type`
- `benefit_type`
- `beer_type`
- `deleted_at` already exists

For richer targeting, consider one of:

- normalized `promotion_targets` table
- normalized `promotion_segments` table
- keep JSON only as migration bridge

#### Step 3

Add redemption/application tables:

- `promotion_redemptions`
  - promotion_id
  - order_id
  - user_id
  - applied_amount
  - applied_at

- optional `promotion_applications`
  - per-line or per-scope application details

This is required to enforce per-customer limits correctly.

## Recommended Refactor In Code

### Replace direct coupon application use cases

Current:

- `ApplyOrderCouponUseCase`
- `ApplyShippingCouponUseCase`

Recommended direction:

- `ApplyPromotionCodeUseCase`
- `RepriceOrderPromotionsUseCase`

The first validates a manual code and attaches it as a candidate activation.
The second delegates to `PromotionEngine` and recalculates the order.

### Reduce business logic inside use cases

Use cases should orchestrate:

- load order
- load active/manual promotions
- call engine
- persist updated order

They should not implement discount math inline.

### Move from type-based branching to benefit-based evaluation

Today the code effectively asks:

- is this an order coupon?
- is this a shipping coupon?

The engine should instead evaluate:

- what is the scope?
- what is the benefit?
- does it match the order/cart?
- can it coexist with already-selected applications?

## Minimum Technical Backlog To Align With The Plan

This is the recommended implementation order.

1. Add `priority` and `draft` status to the current coupon model.
2. Introduce explicit scope model for category, subcategory, beer type, product selection.
3. Introduce explicit benefit model for quantity offers, instead of generic `ruleConfig`.
4. Build `PromotionEngine` in domain.
5. Replace duplicated order/shipping coupon application use cases with engine-backed orchestration.
6. Add redemption persistence for per-customer enforcement.
7. Only after that, decide whether to rename `Coupon` to `Promotion` at API/persistence level.

## Practical Recommendation

Do not do a big-bang rename first.

The safest sequence is:

- keep the current `/api/coupons` contract working for admin persistence
- internally introduce the promotion concepts behind it
- migrate application flows to the engine
- only then decide whether external naming should become `promotions`

That preserves momentum while moving the backend toward the model defined in the plan.
