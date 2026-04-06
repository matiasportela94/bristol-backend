# Domain Map

## Objetivo

Este documento define el mapa de dominios del MVP de Bristol Brewing para alinear negocio, arquitectura hexagonal y reparto de trabajo entre equipos.

La idea es separar el sistema en modulos con responsabilidades claras, bajo acoplamiento y lenguaje comun.

## Dominios principales del MVP

El MVP se organiza en los siguientes dominios:

- `catalog`
- `inventory`
- `customer`
- `order`
- `promotion`
- `loyalty`
- `shared`

## 1. Catalog

### Responsabilidad

Gestiona el catalogo comercial visible para administradores y clientes.

### Incluye

- productos
- variantes
- categorias
- subcategorias
- tipo de cerveza
- imagenes
- productos destacados
- publicacion y despublicacion
- modalidad de precio fijo o a consultar

### No incluye

- stock transaccional
- carrito
- calculo de descuentos
- puntos de fidelidad

### Entidades sugeridas

- `Product`
- `ProductVariant`
- `Category`
- `ProductImage`

### Casos de uso tipicos

- crear producto
- actualizar producto
- publicar producto
- listar catalogo
- obtener detalle de producto

## 2. Inventory

### Responsabilidad

Gestiona disponibilidad y movimientos de stock.

### Incluye

- stock por producto o variante
- ajustes manuales
- descuentos por pedido
- restauraciones por cancelacion
- auditoria de movimientos
- alertas de bajo stock

### No incluye

- datos comerciales del catalogo
- reglas de cupones
- ranking de clientes

### Entidades sugeridas

- `InventoryItem`
- `StockMovement`
- `StockReservation`

### Casos de uso tipicos

- aumentar stock
- disminuir stock
- reservar stock
- confirmar descuento de stock
- restaurar stock

## 3. Customer

### Responsabilidad

Gestiona identidad, perfil y datos del cliente.

### Incluye

- registro
- login
- perfil
- direcciones
- roles
- estado del cliente

### No incluye

- pedidos
- descuentos de promociones
- puntos de fidelidad

### Entidades sugeridas

- `Customer`
- `CustomerAddress`
- `CustomerCredential`

### Casos de uso tipicos

- registrar cliente
- autenticar cliente
- actualizar perfil
- agregar direccion
- listar direcciones

## 4. Order

### Responsabilidad

Gestiona el flujo de compra desde carrito hasta pedido.

### Incluye

- carrito
- items del carrito
- checkout
- pedido
- items del pedido
- estados del pedido
- totales base

### No incluye

- definicion de promociones
- acumulacion de puntos
- datos maestros del catalogo

### Entidades sugeridas

- `Cart`
- `CartItem`
- `Order`
- `OrderItem`
- `ShippingAddress`

### Casos de uso tipicos

- agregar item al carrito
- actualizar carrito
- confirmar checkout
- crear pedido
- cancelar pedido
- consultar pedido

## 5. Promotion

### Responsabilidad

Gestiona cupones y promociones comerciales.

### Incluye

- promociones automaticas
- cupones manuales
- alcances por producto, categoria, subcategoria y tipo de cerveza
- beneficios por porcentaje
- beneficios por monto fijo
- 2x1
- compra X lleva Y
- vigencia
- limites de uso
- prioridad
- combinabilidad

### No incluye

- calculo de puntos de fidelidad
- stock
- autenticacion

### Entidades sugeridas

- `Promotion`
- `PromotionScope`
- `PromotionCondition`
- `PromotionBenefit`
- `Coupon`
- `PromotionApplication`
- `PromotionRedemption`

### Casos de uso tipicos

- crear promocion
- activar promocion
- validar cupon
- calcular promociones aplicables
- registrar uso de promocion

## 6. Loyalty

### Responsabilidad

Gestiona el sistema de puntos, ranking y recompensas.

### Incluye

- cuenta de fidelidad
- transacciones de puntos
- ranking mensual
- tiers o niveles
- campanas de premios
- asignacion de recompensas
- historial de premios

### No incluye

- cupones y promociones comerciales
- stock
- gestion de catalogo

### Entidades sugeridas

- `LoyaltyAccount`
- `LoyaltyTransaction`
- `MonthlyRanking`
- `RewardCampaign`
- `RewardAssignment`

### Casos de uso tipicos

- acreditar puntos
- debitar puntos
- recalcular ranking mensual
- asignar premio
- consultar historial de recompensas

## 7. Shared

### Responsabilidad

Contiene conceptos tecnicos y de dominio reutilizables.

### Incluye

- identificadores
- money
- excepciones
- fecha y hora
- paginacion base
- eventos de dominio si se adoptan

## Relaciones entre dominios

### Catalog -> Inventory

`catalog` define que se vende.
`inventory` define cuanto hay disponible.

Relacion:
- un producto o variante del catalogo puede tener una posicion de inventario

### Customer -> Order

`customer` identifica a quien compra.
`order` registra la transaccion de compra.

Relacion:
- un cliente puede tener multiples pedidos

### Order -> Promotion

`order` consulta promociones aplicables.
`promotion` decide si una regla aplica y cuanto beneficio otorga.

Regla:
- `order` no debe contener la logica de definicion de promociones

### Order -> Inventory

`order` dispara reserva, descuento o restauracion de stock.
`inventory` ejecuta esas operaciones.

Regla:
- `order` no administra stock de forma directa

### Order -> Loyalty

`loyalty` reacciona a eventos del pedido, por ejemplo cuando una orden pasa a pagada.

Regla:
- los puntos no deben calcularse dentro del agregado `Order`

### Promotion y Loyalty

Son dominios distintos.

Regla:
- promociones impactan el precio de venta
- fidelidad recompensa comportamiento historico del cliente

## Fronteras recomendadas

### Dominios mas criticos para desacoplar bien desde el inicio

- `order`
- `promotion`
- `inventory`
- `loyalty`

Estos dominios concentran las reglas mas sensibles y con mas probabilidad de cambio.

## Puertos sugeridos por dominio

### Catalog

- `ProductRepository`
- `ProductQueryService`
- `ImageStoragePort`

### Inventory

- `InventoryRepository`
- `StockMovementRepository`

### Customer

- `CustomerRepository`
- `PasswordHasher`
- `TokenService`

### Order

- `OrderRepository`
- `CartRepository`
- `CheckoutPolicy`

### Promotion

- `PromotionRepository`
- `CouponRepository`
- `PromotionEngine`

### Loyalty

- `LoyaltyAccountRepository`
- `LoyaltyTransactionRepository`
- `RankingCalculator`

## Eventos de negocio recomendados

Para desacoplar dominios, estos eventos son utiles:

- `OrderCreated`
- `OrderPaid`
- `OrderCancelled`
- `StockReserved`
- `StockAdjusted`
- `PromotionRedeemed`
- `LoyaltyPointsAccrued`
- `RewardAssigned`

## Prioridad de implementacion por dominio

1. `shared`
2. `catalog`
3. `inventory`
4. `customer`
5. `order`
6. `promotion`
7. `loyalty`

## Criterio de calidad del mapa

Este mapa es correcto si:

- cada modulo tiene una responsabilidad clara
- las reglas de negocio no se mezclan entre dominios
- el equipo puede trabajar en paralelo
- promociones y fidelidad quedan separadas
- stock y pedidos no quedan acoplados de manera fragil
