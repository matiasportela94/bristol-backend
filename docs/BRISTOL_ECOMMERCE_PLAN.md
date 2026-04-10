# Bristol Brewing Ecommerce Plan

## Contexto

Proyecto para Cerveceria Artesanal Bristol Brewing, empresa de Mar del Plata, Buenos Aires, Argentina.

El objetivo es construir un ecommerce propio, completamente customizable, con arquitectura hexagonal para permitir trabajo paralelo entre varios desarrolladores y diferentes stacks.

El negocio necesita vender:

- Cervezas de distintos estilos
- Merchandising
- Productos especiales o ploteados

Los productos especiales deben poder publicarse en el catalogo, pero su precio puede quedar "a determinar" entre cliente y proveedor.

El diferencial principal del producto sera un sistema de recompensas y fidelidad, donde los clientes con mayores compras reciban premios mensuales, por ejemplo:

- Ordenes de compra
- Merchandising
- Cerveza

## Objetivo del MVP

Salir rapido con una base estable y escalable que permita:

- Carga correcta de productos
- Gestion de stock
- Gestion de clientes
- Compra online end to end
- Base lista para fidelizacion

El MVP no debe intentar resolver todo el negocio desde el dia uno. Primero hay que asegurar la operacion core del ecommerce.

La documentacion del proyecto puede incluir capacidades futuras para no perder vision de roadmap, pero esas capacidades deben quedar explicitamente marcadas como "futuro" y no como alcance confirmado de la primera salida.

En este proyecto, la pasarela de pago online forma parte obligatoria del MVP. Sin una pasarela funcionando, el ecommerce no cumple su objetivo comercial.

## Principios del proyecto

- Arquitectura hexagonal real, con separacion clara entre dominio, aplicacion, infraestructura y API
- Reglas de negocio en dominio, no en controladores ni repositorios
- Persistencia confiable con migraciones reales
- Soporte para trabajo paralelo por equipos
- Modelo extensible para futuras integraciones de pagos, envios, marketing y reportes
- Fidelizacion pensada desde el inicio, aunque salga en una primera version controlada

## Alcance funcional inicial

### 1. Catalogo

- Productos de cerveza
- Productos de merchandising
- Productos especiales/ploteados
- Categorias y subcategorias
- Variantes cuando aplique
- Imagenes
- Productos destacados
- Publicacion y despublicacion

### 2. Stock

- Stock actual por producto o variante
- Descuento de stock por compra confirmada
- Restauracion de stock por cancelacion
- Alertas de bajo stock
- Historial de movimientos

### 3. Clientes

- Registro
- Login
- Perfil
- Direcciones
- Historial de pedidos
- Roles basicos: ADMIN y CLIENTE

### 4. Checkout y pedidos

- Carrito persistente
- Creacion de pedido
- Validacion de stock
- Calculo de subtotal, descuentos y envio
- Estados del pedido
- Gestion administrativa de pedidos

### 5. Promociones y cupones

- Cupones de descuento
- Reglas por producto especifico
- Reglas por categoria
- Reglas por subcategoria o tipo de cerveza
- Descuentos porcentuales
- Descuentos de monto fijo
- Promociones del tipo 2x1
- Validaciones de vigencia, combinacion y uso

### 6. Fidelidad

- Acumulacion de puntos por compra
- Ranking mensual
- Segmentacion por nivel o tier
- Campanas mensuales de premios
- Historial de recompensas entregadas

## Tipos de producto

### Cervezas

Cada producto de cerveza deberia soportar al menos:

- Nombre
- Estilo
- Formato
- Volumen
- Precio
- Stock
- Imagenes
- Descripcion

### Merchandising

Cada producto de merchandising deberia soportar:

- Nombre
- Tipo
- Variantes, por ejemplo talle o color
- Precio
- Stock por variante
- Imagenes

### Especiales o ploteados

Cada producto especial deberia soportar:

- Nombre
- Descripcion
- Imagenes
- Estado de publicacion
- Modalidad de precio

Modalidades sugeridas:

- Precio fijo
- Precio a consultar
- Cotizacion personalizada

Para el MVP, lo mas simple y util es soportar "precio fijo" y "precio a consultar".

## Modelo inicial de promociones y cupones

El sistema debe soportar promociones que puedan aplicarse con suficiente flexibilidad comercial.

### Casos de negocio relevantes

- 10 por ciento de descuento en merchandising
- 15 por ciento de descuento en todas las cervezas
- 5 por ciento de descuento solo en cervezas IPA
- 2x1 en cervezas lager
- descuento en un producto especifico

### Niveles de aplicacion

Las promociones deberian poder aplicarse a:

- un producto especifico
- una categoria completa
- una subcategoria
- un tipo de cerveza
- una seleccion definida manualmente

### Tipos de beneficio

El motor de promociones deberia contemplar al menos:

- porcentaje de descuento
- monto fijo de descuento
- envio bonificado o con descuento
- compra X lleva Y
- 2x1

### Reglas minimas

- fecha de inicio y fin
- cupon manual o promocion automatica
- limite total de usos
- limite por cliente
- combinable o no combinable
- prioridad de aplicacion
- estado: borrador, activo, pausado, vencido

### Recomendacion de salida

Dado que hay tiempo suficiente, el MVP debe incluir desde el inicio:

- porcentaje
- monto fijo
- alcance por producto, categoria, subcategoria y tipo de cerveza
- validacion de vigencia
- limite de uso
- cupon manual
- promocion automatica
- 2x1
- compra X lleva Y

La prioridad tecnica pasa a ser disenar bien el motor de promociones para que estas reglas no queden hardcodeadas en pedidos o catalogo.

## Arquitectura propuesta

### Modulos de dominio

- `catalog`
- `inventory`
- `customer`
- `order`
- `loyalty`
- `shared`

### Responsabilidad de cada modulo

`catalog`
- productos
- variantes
- categorias
- imagenes

`inventory`
- stock
- movimientos de stock
- reglas de reserva y descuento

`customer`
- usuarios
- perfiles
- direcciones
- autenticacion a nivel de aplicacion

`order`
- carrito
- checkout
- pedidos
- estados
- descuentos sobre orden

`loyalty`
- puntos
- ranking
- premios
- campanas mensuales

`shared`
- money
- ids
- excepciones
- tiempo

### Puertos principales

- repositorios
- autenticacion y tokens
- notificaciones
- almacenamiento de imagenes
- pagos
- tareas programadas para cierres mensuales de fidelidad

## Roadmap por fases

## Fase 0 - Definicion y estabilizacion

Objetivo:
cerrar alcance del MVP y corregir la base tecnica actual para que el proyecto sea confiable.

Entregables:

- backlog priorizado
- decisiones de negocio del MVP
- modelo de dominios
- contratos API iniciales
- migraciones reales de base de datos
- correccion de inconsistencias actuales de persistencia, productos y pedidos
- estrategia minima de testing

## Fase 1 - Catalogo

Objetivo:
tener alta, baja, modificacion y consulta de productos correctamente implementados.

Entregables:

- ABM de productos
- categorias y subcategorias
- variantes
- productos especiales con precio a consultar
- filtros basicos
- productos destacados
- carga de imagenes

## Fase 2 - Stock

Objetivo:
controlar inventario de forma centralizada.

Entregables:

- stock por producto o variante
- ajuste manual de stock
- deduccion por compra
- restauracion por cancelacion
- alertas de bajo stock
- historial de movimientos

## Fase 3 - Clientes

Objetivo:
construir la base de usuarios del ecommerce.

Entregables:

- registro
- login con JWT
- perfil de cliente
- direcciones
- historial de pedidos
- permisos por rol

## Fase 4 - Carrito y pedidos

Objetivo:
habilitar el flujo de compra completo.

Entregables:

- carrito persistente
- checkout
- creacion de pedidos
- validacion de stock al confirmar
- calculo de totales
- estados del pedido
- panel admin de gestion

## Fase 5 - Promociones y cupones

Objetivo:
permitir acciones comerciales configurables que impacten directamente en conversion y ventas.

Entregables:

- cupones manuales
- descuentos porcentuales
- descuentos de monto fijo
- reglas por producto
- reglas por categoria
- reglas por tipo de cerveza
- reglas por subcategoria
- validacion de vigencia
- limites de uso
- promociones automaticas
- 2x1
- compra X lleva Y
- reglas de combinacion y prioridad

## Fase 6 - Fidelidad v1

Objetivo:
implementar la primera version del diferencial del negocio sin bloquear la salida del ecommerce.

Modelo sugerido para v1:

- puntos acumulados por orden pagada
- ranking mensual de clientes
- tiers de fidelidad, por ejemplo Bronce, Plata y Oro
- premios mensuales configurables
- entrega manual o semiautomatica desde admin

Entregables:

- ledger de puntos
- regla de acumulacion
- ranking mensual
- administracion de premios
- historial de premios entregados

## Fase 7 - Personalizacion e integraciones

Objetivo:
expandir la capacidad comercial del ecommerce.

Entregables:

- banners y destacados configurables
- configuracion de home y secciones
- notificaciones
- reportes
- automatizacion de recompensas

## Fase 8 - Integracion de pagos y cierre del MVP

Objetivo:
incorporar la pasarela de pago online obligatoria para que el ecommerce quede operable de punta a punta.

Entregables:

- integracion con MercadoPago como pasarela principal
- generacion de preferencia o sesion de pago
- confirmacion de pago
- actualizacion de estado del pedido
- manejo de pagos rechazados o pendientes
- webhooks o mecanismo equivalente de conciliacion
- trazabilidad basica de intentos de pago

## MVP recomendado

El MVP recomendado incluye:

- autenticacion y clientes
- catalogo
- productos especiales con precio a consultar
- stock
- carrito
- checkout
- pedidos
- cupones y promociones completas
- pasarela de pago online con MercadoPago
- admin basico
- fidelidad v1 con puntos y ranking mensual

No deberia incluir en la primera salida:

- automatizaciones complejas de premios
- integraciones avanzadas con AFIP
- personalizacion visual extrema desde backend

## Alcance futuro documentado

Estos puntos pueden aparecer en la documentacion actual como parte del roadmap futuro, aunque no esten dentro del MVP confirmado:

- integraciones avanzadas con AFIP
- automatizaciones mas complejas de recompensas
- integraciones de notificaciones y marketing
- reportes y analitica mas avanzados

## Modelo inicial del sistema de fidelidad

### Regla base

- cada orden pagada suma puntos
- los puntos dependen del monto total o subtotal
- el ranking se calcula por mes calendario

### Premios mensuales

Cada mes se define una campana con:

- periodo
- reglas
- premios disponibles
- cantidad de ganadores
- criterios de desempate

### Tipos de premio

- orden de compra
- cerveza
- merchandising

### Estrategia de salida

Para la primera version:

- acumulacion automatica
- ranking automatico
- asignacion de premio manual o semiautomatica

Esto reduce riesgo y permite validar negocio antes de automatizar todo.

## Recomendaciones tecnicas inmediatas

- corregir la persistencia de pedidos y sus items
- corregir el mapeo entre enums de dominio y persistencia
- reemplazar migraciones baseline vacias por migraciones reales
- agregar tests de dominio y tests de integracion de API
- alinear README y documentacion con el estado real del proyecto
- endurecer seguridad basica, especialmente credenciales iniciales y bootstrap admin
- modelar promociones como dominio separado para evitar mezclar reglas comerciales con pedidos

## Orden de prioridad de negocio

1. Base tecnica estable
2. Catalogo
3. Stock
4. Clientes
5. Carrito y pedidos
6. Promociones y cupones
7. Fidelidad v1
8. Pasarela de pago online con MercadoPago
9. Personalizacion avanzada
10. Integraciones adicionales

## Propuesta de distribucion de trabajo

### Equipo A

- catalogo
- productos
- variantes
- imagenes

### Equipo B

- stock
- movimientos
- reglas de disponibilidad

### Equipo C

- clientes
- autenticacion
- perfiles
- direcciones

### Equipo D

- carrito
- pedidos
- checkout
- estados

### Equipo E

- promociones
- cupones
- reglas comerciales

### Equipo F

- fidelidad
- puntos
- ranking
- recompensas

## Criterio de exito del MVP

El MVP sera exitoso si Bristol puede:

- administrar su catalogo sin depender de terceros
- vender online productos normales y productos especiales
- controlar stock de forma confiable
- registrar y gestionar clientes
- procesar pedidos de punta a punta
- premiar a sus mejores compradores con una primera version util del sistema de fidelidad

## Siguiente nivel de detalle sugerido

Luego de aprobar este plan, el siguiente paso recomendado es generar:

- roadmap de 6 a 8 semanas
- backlog por epicas y user stories
- modelo de datos inicial
- definicion de APIs por modulo
- criterios de aceptacion del MVP
