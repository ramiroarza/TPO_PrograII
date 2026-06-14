# Centro Logístico de Distribución Avanzada

**Trabajo Práctico Obligatorio — Programación II (2026)**
Etapa 1: Propuesta y Diseño — Alternativa C

**Integrantes:** Ramiro Arzamendia Martínez · Román Galante

---

Aplicación de consola en Java que modela la gestión de un centro logístico de distribución. El sistema centraliza la información del depósito y resuelve, de forma eficiente, las operaciones de localización de stock, optimización de rutas de recolección, control de inventario crítico, gestión de la línea de expedición y trazabilidad de movimientos. Cada módulo está construido sobre un Tipo de Dato Abstracto (TDA) genérico implementado desde cero, sin recurrir a las colecciones de `java.util`.

---

## Tabla de contenidos

- [Contexto y problema](#contexto-y-problema)
- [Objetivos](#objetivos)
- [Estructuras de datos y TDAs](#estructuras-de-datos-y-tdas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [Cómo ejecutar](#cómo-ejecutar)
- [Ejemplo de uso](#ejemplo-de-uso)
- [Requisitos no funcionales](#requisitos-no-funcionales)
- [Decisiones de diseño](#decisiones-de-diseño)

---

## Contexto y problema

Los centros logísticos modernos gestionan grandes volúmenes de productos, operarios, camiones y pedidos en forma simultánea. En un depósito administrado manualmente, operaciones como encontrar un producto, calcular la ruta de recolección o identificar los artículos con stock mínimo requieren recorrer listas y planillas de forma secuencial. Ese enfoque no escala: a medida que crece el volumen, el tiempo de respuesta aumenta de forma lineal y los errores humanos se multiplican.

El problema se descompone en cinco necesidades concretas, cada una resuelta con la estructura de datos más adecuada:

- **Localización lenta de productos** → indexación con un diccionario.
- **Recorridos subóptimos de recolección** → cálculo de caminos sobre un grafo.
- **Detección tardía de quiebres de stock** → orden por existencias con un árbol AVL.
- **Despacho desordenado** → atención por orden de llegada con una cola (FIFO).
- **Falta de trazabilidad** → historial reversible con una pila (LIFO).

---

## Objetivos

**Objetivo general.** Diseñar e implementar en Java un sistema de gestión para un centro logístico que integre múltiples TDAs para resolver, de forma eficiente, las operaciones del depósito.

**Objetivos específicos.**

- Implementar un **diccionario con clave compuesta** (código + nombre) que recupere un producto buscando indistintamente por cualquiera de los dos campos.
- Modelar la red de pasillos como un **grafo no dirigido** y calcular, mediante recorrido por anchura (**BFS**), el camino de menor cantidad de pasillos entre dos sectores.
- Mantener los stocks ordenados en un **árbol AVL**, de modo que el menor sea accesible en O(log n) con garantía de balanceo.
- Gestionar la línea de expedición con una **cola** que respete estrictamente el orden de llegada (FIFO), con encolar y desencolar en O(1).
- Registrar los movimientos de mercadería en una **pila**, permitiendo revertir el último ante un error operativo.
- Aplicar **generics de Java (`<T>`)** en los TDAs para garantizar su reutilización con cualquier tipo de dato.

---

## Estructuras de datos y TDAs

### TDAs principales — uno por funcionalidad

| Funcionalidad | TDA | Clase | Justificación |
|---|---|---|---|
| Localización de stock | Diccionario | `DiccionarioProducto<T>` | Recupera un producto por código o por nombre sin recorridos lineales innecesarios. |
| Optimización de rutas | Grafo | `Grafo<T>` | Modela sectores como vértices y pasillos como aristas. BFS calcula el camino de menor cantidad de pasillos. |
| Inventario crítico | Árbol AVL | `ArbolAVL` | Mantiene los stocks ordenados; el mínimo es accesible en O(log n) gracias al balanceo automático. |
| Línea de expedición | Cola | `Cola<T>` | Garantiza FIFO estricto; encolar y desencolar en O(1) con lista enlazada interna. |
| Trazabilidad | Pila | `pila<T>` | El último movimiento queda en el tope; deshacer con `desapilar()` en O(1). |

### TDAs de soporte interno

| TDA | Clase | Rol |
|---|---|---|
| Lista enlazada simple | `Lista<T>` | Estructura base genérica; nodos con `Nodo<T>`. |
| Lista doblemente enlazada | `ListaDoble<T>` | Estructura de recorrido bidireccional; nodos con `NodoDoble<T>`. |
| Conjunto | `Conjunto` | Marca los nodos ya visitados durante el BFS, evitando ciclos. |
| Clave / Entrada de diccionario | `ClaveProducto`, `EntradaDiccionario` | Clave compuesta (código + nombre) y par clave-valor del diccionario. |

Todas las estructuras se programan contra su propia interfaz (`IArbolAVL`, `ICola`, `IPila`, `IGrafo`, `IDiccionarioProducto`, `IConjunto`, `ILista`, `IListaDoble`), separando el contrato de su implementación.

---

## Estructura del proyecto

```
TPO_PrograII/
└── src/
    ├── Main.java                 # menú de consola que integra todos los módulos
    ├── modelo/                   # capa de dominio
    │   ├── Producto.java         # codigo, nombre, cantidadStock, ubicacion
    │   ├── Pedido.java           # idPedido, descripcion, prioridad
    │   ├── Movimiento.java       # tipo, codigoProducto, cantidad, fecha
    │   └── Sector.java           # idSector, nombre
    └── tda/                      # capa de estructuras de datos e interfaces
        ├── DiccionarioProducto.java / IDiccionarioProducto.java
        ├── ClaveProducto.java / EntradaDiccionario.java
        ├── ArbolAVL.java / IArbolAVL.java / NodoAVL.java
        ├── Cola.java / ICola.java
        ├── pila.java / IPila.java
        ├── Grafo.java / IGrafo.java
        ├── Lista.java / ILista.java / Nodo.java
        ├── ListaDoble.java / IListaDoble.java / NodoDoble.java
        ├── Conjunto.java / IConjunto.java
        └── Ubicacion.java
```

La aplicación se organiza en dos capas: **modelo** (objetos del dominio) y **tda** (estructuras genéricas). La clase `Main` integra ambas capas y contiene el menú de consola junto con la lógica de cada módulo.

---

## Funcionalidades

El menú principal ofrece cinco módulos, cada uno apoyado en una estructura distinta.

### 1. Stock de productos — *Diccionario + Árbol AVL*

Gestiona el catálogo. El diccionario permite buscar indistintamente por código o por nombre, y cada cambio de stock se refleja también en el árbol AVL.

- Registrar producto (código, nombre, stock inicial y ubicación)
- Buscar por código o nombre
- Modificar ubicación
- Modificar stock
- Eliminar producto
- Ver todos los productos
- **Stock total del depósito** — suma el stock de todos los productos registrados

### 2. Rutas del depósito — *Grafo (BFS)*

Modela el depósito como un grafo no dirigido: los sectores son vértices y los pasillos, aristas.

- Agregar sector
- Agregar pasillo entre dos sectores
- Calcular la ruta más corta entre dos sectores (BFS)
- Verificar conexión directa entre dos sectores
- Ver el mapa (vértices y matriz de adyacencia)

### 3. Inventario crítico — *Árbol AVL*

Consulta el estado del stock a partir del árbol, que mantiene los valores ordenados.

- Ver el menor stock registrado
- Ver todos los stocks ordenados de menor a mayor (recorrido inorden)

### 4. Expedición de pedidos — *Cola (FIFO)*

Administra los pedidos respetando el orden de llegada.

- Encolar un pedido
- Despachar el próximo pedido
- Ver el próximo pedido sin despacharlo
- **Contador de pedidos en cola** — muestra cuántos pedidos quedan pendientes

### 5. Trazabilidad — *Pila (LIFO)*

Registra los movimientos de inventario y permite revertir el último.

- Registrar movimiento (`INGRESO`, `EGRESO` o `TRANSFERENCIA`), actualizando el stock del producto
- Deshacer el último movimiento, revirtiendo su efecto sobre el stock
- Ver el historial completo (del más reciente al más antiguo)
- **Contador de movimientos registrados** — muestra cuántos movimientos hay en el historial

---

## Cómo ejecutar

**Requisitos:** JDK 17 o superior (el proyecto usa `switch` con expresiones flecha `->`). Opcionalmente, IntelliJ IDEA.

**Desde IntelliJ IDEA:** abrir la carpeta del proyecto y ejecutar la clase `Main`.

**Desde la terminal**, ubicado en `src`:

```bash
javac -d out Main.java modelo/*.java tda/*.java
java -cp out Main
```

---

## Ejemplo de uso

```
=== Centro Logistico de Distribucion ===

--- Menu ---
1. Stock de productos
2. Rutas del deposito
3. Inventario critico
4. Expedicion de pedidos
5. Trazabilidad
0. Salir
Opcion: 1

-- Stock --
1. Registrar producto
...
7. Stock total del deposito
Opcion: 7
Stock total del deposito: 80 unidades (2 productos)
```

---

## Requisitos no funcionales

- **Eficiencia.** Las operaciones críticas (buscar producto, encolar, deshacer, consultar el mínimo) se resuelven con la estructura adecuada en lugar de recorridos lineales.
- **Robustez.** El sistema valida entradas inválidas (códigos vacíos, stock negativo, claves duplicadas, operaciones sobre estructuras vacías) e informa el error sin interrumpir la ejecución.
- **Legibilidad.** El código separa con claridad las capas de modelo y de TDAs, usa nombres descriptivos e incluye comentarios en los métodos complejos.
- **Reutilización.** Los TDAs se implementan con generics (`<T>`) para poder usarse con cualquier tipo de objeto sin duplicar código.

---

## Decisiones de diseño

- **Programación contra interfaces.** Cada TDA se implementa a partir de su interfaz, separando el contrato de su implementación concreta.
- **Búsqueda flexible en el diccionario.** La clase `ClaveProducto` permite recuperar un producto tanto por su código como por su nombre, sin distinguir mayúsculas de minúsculas, validando duplicados en ambas dimensiones al insertar.
- **Sincronización entre diccionario y AVL.** Al registrar, modificar o eliminar un producto, su stock se actualiza también en el árbol AVL, de modo que las consultas de inventario crítico siempre reflejan el estado real.
- **Ruta más corta con BFS.** Como las aristas del grafo no tienen peso, el recorrido por anchura garantiza encontrar el camino con la menor cantidad de pasillos entre dos sectores. El BFS se apoya en una `Cola` para el orden de exploración y en un `Conjunto` para registrar los nodos ya visitados.
- **Deshacer con pila.** El historial de movimientos se modela con una pila (LIFO), lo que permite revertir el último movimiento de forma natural y devolver el stock a su estado anterior.
