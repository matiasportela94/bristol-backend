-- Align persisted order status values with the current domain model.
-- Existing records created before the Phase 0 decision update used PENDING.

UPDATE orders
SET order_status = 'PENDING_PAYMENT'
WHERE order_status = 'PENDING';
