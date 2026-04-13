# Flujo de Pago, Factura, Remito y Conformidad de Entrega

## Objetivo

Definir el flujo objetivo para que cada pedido tenga trazabilidad completa desde el pago hasta la recepción del cliente.

El alcance de este diseño contempla:

- pagos
- facturas
- remitos
- entregas
- conformidad del cliente dentro de la plataforma

No incluye todavía:

- integración con ARCA
- integración con mailing
- firma biométrica avanzada

## Flujo objetivo

1. Se crea la `order`.
2. Se crea el `payment`.
3. Cuando el pago es aprobado:
   - la `order` pasa a `PAID`
   - se crea la `invoice`
   - se crea el `delivery_note` / remito
   - se crea la `delivery`
4. La `delivery` queda asociada al remito.
5. Al entregar el pedido:
   - admin o distribuidor marca la entrega como realizada
   - el cliente confirma dentro de la plataforma que recibió correctamente el pedido
   - el remito queda conformado
   - la entrega queda cerrada

## Regla de negocio principal

Aunque operativamente la factura y el remito se disparan al aprobar un pago, conviene modelar la relación como:

- `payment` confirma el cobro
- `order` representa la venta
- `invoice` documenta fiscalmente la venta
- `delivery_note` documenta la entrega
- `delivery` ejecuta la logística

Eso evita acoplar demasiado la factura al pago y deja margen para futuros casos como:

- reintentos de pago
- múltiples pagos sobre una orden
- integraciones externas

## Entidades a incorporar o consolidar

### Payments

Responsabilidad:

- registrar el intento o transacción de cobro
- registrar su aprobación o rechazo
- actuar como disparador del workflow posterior

### Invoices

Responsabilidad:

- representar la factura asociada a la orden
- luego integrarse con ARCA

Campos sugeridos:

- `id`
- `invoice_number`
- `order_id`
- `payment_id`
- `status`
- `customer_name`
- `customer_document_type`
- `customer_document_number`
- `customer_tax_condition`
- `customer_email`
- `billing_address`
- `subtotal`
- `discount_total`
- `tax_total`
- `grand_total`
- `currency`
- `issued_at`
- `created_at`
- `updated_at`

Campos futuros para ARCA:

- `arca_voucher_type`
- `point_of_sale`
- `voucher_number`
- `cae`
- `cae_expiration`
- `arca_request_payload`
- `arca_response_payload`

### Delivery Notes

Responsabilidad:

- representar el remito de entrega
- acompañar la orden de envío
- servir como documento de recepción

Campos sugeridos:

- `id`
- `delivery_note_number`
- `order_id`
- `payment_id`
- `delivery_id`
- `status`
- `customer_name`
- `customer_email`
- `shipping_address`
- `issued_at`
- `delivered_at`
- `acknowledged_at`
- `acknowledged_by_user_id`
- `created_at`
- `updated_at`

### Deliveries

Responsabilidad:

- calendarizar y ejecutar la entrega
- vincular la operación logística con el remito

Campos/relaciones a reforzar:

- `delivery_note_id`
- `scheduled_date`
- `delivered_at`
- `status`

## Estados sugeridos

### Payment

- `PENDING`
- `APPROVED`
- `REJECTED`
- `CANCELLED`

### Invoice

- `PENDING_ISSUANCE`
- `ISSUED`
- `ISSUE_FAILED`
- `CANCELLED`

### Delivery Note

- `ISSUED`
- `ATTACHED_TO_DELIVERY`
- `DELIVERED_PENDING_ACK`
- `ACKNOWLEDGED`
- `CANCELLED`

### Delivery

- `SCHEDULED`
- `IN_TRANSIT`
- `DELIVERED_PENDING_CONFIRMATION`
- `DELIVERED`
- `FAILED`

## Conformidad del cliente

El cliente debe poder confirmar dentro de la plataforma que la entrega fue correcta.

Ese paso debería registrar:

- fecha y hora de confirmación
- usuario que confirmó
- aceptación explícita
- opcionalmente observaciones

Nombre sugerido para el proceso:

- `conformidad de entrega`

Más preciso que "consentimiento", porque no se trata de aceptar términos sino de confirmar la recepción del pedido.

## Reglas funcionales

- Cada `order` debe tener una sola `invoice`.
- Cada `order` debe tener un solo `delivery_note`.
- Cada `order` debe tener una sola `delivery` activa.
- El workflow de aprobación de pago debe ser idempotente.
- Si se reintenta aprobar un pago ya aprobado, no debe duplicar factura, remito ni entrega.
- La factura y el remito deben guardar snapshot de datos del cliente, dirección, items y totales.
- No conviene reconstruir factura o remito leyendo la orden viva, porque la información puede cambiar después.

## Orquestación sugerida

Extraer la lógica actual de aprobación a un servicio orquestador único, por ejemplo:

- `PaymentApprovalWorkflowService`

Responsabilidades:

- aprobar pago
- marcar orden como pagada
- registrar redenciones si aplica
- crear factura si no existe
- crear remito si no existe
- crear entrega si no existe

Métodos sugeridos:

- `ensureOrderPaid(order)`
- `ensureInvoiceCreated(order, payment)`
- `ensureDeliveryNoteCreated(order, payment)`
- `ensureDeliveryScheduled(order, deliveryNote)`

## API sugerida

### Invoices

- `GET /api/invoices`
- `GET /api/invoices/{id}`
- `GET /api/invoices/order/{orderId}`
- `GET /api/invoices/payment/{paymentId}`
- `PUT /api/invoices/{id}/issue`
- `PUT /api/invoices/{id}/retry`
- `PUT /api/invoices/{id}/cancel`

### Delivery Notes

- `GET /api/delivery-notes`
- `GET /api/delivery-notes/{id}`
- `GET /api/delivery-notes/order/{orderId}`
- `GET /api/delivery-notes/payment/{paymentId}`
- `PUT /api/delivery-notes/{id}/mark-delivered`
- `PUT /api/delivery-notes/{id}/acknowledge`

### Deliveries

- `GET /api/deliveries`
- `GET /api/deliveries/{id}`
- `GET /api/deliveries/order/{orderId}`
- `PUT /api/deliveries/{id}/start-transit`
- `PUT /api/deliveries/{id}/complete`
- `PUT /api/deliveries/{id}/acknowledge`
- `PUT /api/deliveries/{id}/reschedule`

### Orders

Agregado útil:

- `GET /api/orders/{id}/timeline`
- `GET /api/orders/{id}/documents`

Eso permitiría exponer en una sola respuesta:

- pago
- factura
- remito
- entrega
- conformidad

## Frontend sugerido

### Admin

Vistas o secciones:

- `Pedidos`
- `Pagos`
- `Facturas`
- `Remitos`
- `Entregas`

Navegación cruzada recomendada:

- `pedido -> pago`
- `pedido -> factura`
- `pedido -> remito`
- `pedido -> entrega`

### Customer

Vistas o secciones:

- `Mis pagos`
- `Mis facturas`
- `Mis remitos`
- `Mis entregas`

Acciones:

- descargar factura
- descargar remito
- confirmar recepción

## Integraciones futuras

### ARCA

La integración con ARCA debería vivir dentro del módulo de `invoices`, no dentro de `orders`.

Flujo futuro:

1. el pago se aprueba
2. se crea la factura interna
3. se intenta emitir en ARCA
4. si ARCA responde OK:
   - `invoice -> ISSUED`
5. si ARCA falla:
   - `invoice -> ISSUE_FAILED`

Recomendación:

- no bloquear la creación de entrega por una caída temporal de ARCA
- sí registrar claramente el fallo y permitir reintento

### Mailing

Más adelante se podrá enviar por mail:

- factura
- remito

Disparadores posibles:

- emisión de factura
- entrega marcada como realizada
- conformidad del cliente

## Orden recomendado de implementación

1. Crear entidad y API de `invoices`.
2. Crear entidad y API de `delivery_notes`.
3. Integrar ambas al workflow de aprobación de pagos.
4. Vincular `delivery` con `delivery_note`.
5. Agregar conformidad de entrega del cliente.
6. Exponer vistas y links cruzados en frontend.
7. Integrar ARCA sobre `invoices`.
8. Integrar mailing.

## Resultado esperado

Cada pedido tendrá trazabilidad documental completa:

- `order`
- `payment`
- `invoice`
- `delivery_note`
- `delivery`
- `delivery acknowledgement`

Y el flujo operativo será:

- se crea pedido
- se aprueba pago
- se generan factura y remito
- se crea entrega
- se entrega el pedido
- el cliente confirma recepción
