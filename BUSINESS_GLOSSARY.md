# Business Glossary

## Objetivo

Este glosario define el lenguaje comun del proyecto Bristol Brewing para reducir ambiguedades entre negocio, producto, backend, frontend y QA.

## Terminos comerciales

### Ecommerce

Canal digital propio de Bristol Brewing donde los clientes pueden explorar productos, registrarse, comprar y participar del sistema de fidelidad.

### Catalogo

Conjunto de productos publicados para la venta o consulta.

### Producto

Unidad comercial publicada en el catalogo.

Puede ser:

- cerveza
- merchandising
- especial o ploteado

### Variante

Version especifica de un producto que cambia algun atributo comercial o logistico, por ejemplo talle, color, formato o presentacion.

### Categoria

Agrupacion principal de productos.

Categorias iniciales sugeridas:

- cerveza
- merchandising
- especiales

### Subcategoria

Subdivision comercial dentro de una categoria.

Ejemplos:

- six pack
- remera
- vaso
- evento

### Tipo de cerveza

Clasificacion cervecera utilizada para navegacion, promociones y reporting.

Ejemplos:

- IPA
- Lager
- Stout
- Porter
- Amber

### Producto especial

Producto o servicio publicado en el catalogo cuyo precio puede quedar sujeto a cotizacion o consulta.

Ejemplo:

- ploteado

### Precio fijo

Precio definido y visible que permite compra directa.

### Precio a consultar

Modalidad comercial donde el producto esta visible pero no tiene precio cerrado para compra inmediata.

## Terminos de inventario

### Stock

Cantidad disponible para venta de un producto o variante.

### Reserva de stock

Bloqueo temporal de unidades para evitar sobreventa durante una etapa del flujo de compra.

### Descuento de stock

Reduccion efectiva del stock al confirmarse una condicion de negocio, por ejemplo una orden pagada.

### Restauracion de stock

Reposicion de unidades al inventario cuando una orden se cancela o revierte.

### Movimiento de stock

Registro auditable de una alteracion del inventario.

Tipos comunes:

- ingreso
- ajuste manual
- reserva
- descuento por venta
- restauracion por cancelacion

### Bajo stock

Estado en el que un producto o variante cae por debajo del umbral configurado para alerta.

## Terminos de clientes

### Cliente

Usuario final que compra o interactua con el ecommerce.

### Administrador

Usuario interno con permisos de gestion sobre catalogo, promociones, pedidos y configuracion.

### Perfil

Conjunto de datos personales y comerciales del cliente.

### Direccion

Ubicacion asociada al cliente para entrega o facturacion.

## Terminos de compra

### Carrito

Contenedor temporal de productos seleccionados antes del checkout.

### Item de carrito

Linea individual del carrito con producto, variante, cantidad y precio de referencia.

### Checkout

Proceso de confirmacion de compra donde se validan direccion, stock, descuentos y total final.

### Pedido

Registro formal de la compra realizada por el cliente.

### Item de pedido

Linea individual dentro de un pedido.

### Estado del pedido

Situacion operativa de una orden dentro del ciclo comercial.

Estados iniciales sugeridos:

- pendiente
- pagado
- procesando
- enviado
- entregado
- cancelado

### Total del pedido

Monto final a pagar luego de aplicar promociones, descuentos y costo de envio.

## Terminos de promociones

### Promocion

Regla comercial que modifica el precio o beneficio de compra bajo ciertas condiciones.

### Cupon

Codigo manual que el cliente ingresa para intentar activar una promocion.

### Promocion automatica

Promocion que se aplica sin necesidad de ingresar un codigo.

### Alcance de promocion

Define sobre que conjunto aplica una promocion.

Puede ser:

- producto especifico
- categoria
- subcategoria
- tipo de cerveza
- seleccion manual
- orden completa

### Beneficio

Resultado economico de una promocion.

Tipos iniciales:

- porcentaje
- monto fijo
- envio bonificado
- 2x1
- compra X lleva Y

### 2x1

Promocion donde por cada dos unidades elegibles, una se bonifica segun la regla definida.

### Compra X lleva Y

Promocion donde la compra de una cierta cantidad o conjunto habilita una bonificacion o descuento sobre otra unidad o producto.

### Vigencia

Periodo de tiempo en el que una promocion puede aplicarse.

### Combinabilidad

Regla que determina si una promocion puede coexistir con otras promociones en la misma compra.

### Prioridad

Orden de evaluacion o resolucion cuando varias promociones compiten entre si.

### Redencion

Uso efectivo de una promocion o cupon en una compra.

## Terminos de fidelidad

### Fidelidad

Sistema que recompensa a clientes por su comportamiento historico de compra.

### Punto

Unidad de valor interna acumulada por el cliente segun reglas del programa de fidelidad.

### Cuenta de fidelidad

Registro asociado al cliente que concentra saldo y movimientos de puntos.

### Transaccion de puntos

Movimiento individual de acreditacion o debito de puntos.

### Ranking mensual

Ordenamiento de clientes segun compras o puntos dentro de un mes calendario.

### Tier

Nivel de cliente dentro del programa de fidelidad.

Ejemplos:

- Bronce
- Plata
- Oro

### Campana de recompensas

Configuracion mensual o periodica que define premios, reglas y ganadores.

### Premio

Beneficio entregado a clientes por fidelidad o desempeno.

Ejemplos:

- orden de compra
- cerveza
- merchandising

## Reglas semanticas importantes

### Promocion no es fidelidad

Una promocion modifica el precio actual de compra.

La fidelidad recompensa el historial de compra del cliente.

### Producto no es variante

El producto es el concepto comercial principal.

La variante es una version particular vendible de ese producto.

### Pedido no es carrito

El carrito es temporal y editable.

El pedido es una transaccion registrada.

### Stock no es catalogo

El catalogo describe que se vende.

El stock describe cuanto hay disponible.

### Precio a consultar no es producto sin precio

Es una modalidad comercial valida que requiere flujo propio de negocio.

## Terminos tecnicos de apoyo al negocio

### MVP

Version minima viable del producto con valor real para Bristol Brewing y operacion comercial funcional.

### Dominio

Area de negocio con reglas, lenguaje y responsabilidades propias.

### Caso de uso

Operacion de aplicacion que coordina una accion del negocio.

### Puerto

Interfaz que define una dependencia externa desde el dominio o aplicacion.

### Adaptador

Implementacion tecnica de un puerto, por ejemplo JPA, JWT o almacenamiento de imagenes.
