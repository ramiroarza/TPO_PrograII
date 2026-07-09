# Centro Logístico de Distribución Avanzada

**Trabajo Práctico Obligatorio — Programación II (2026)**
Alternativa C · **Entrega Final: mejoras de arquitectura y nuevas funcionalidades**

**Integrantes:** Ramiro Arzamendia Martínez · Román Galante

---

Aplicación de consola en Java que modela la gestión de un centro logístico de distribución. El sistema centraliza la información del depósito y resuelve, de forma eficiente, las operaciones de localización de stock, optimización de rutas de recolección, control de inventario crítico, gestión de la línea de expedición y trazabilidad de movimientos. Cada módulo está construido sobre un Tipo de Dato Abstracto (TDA) genérico implementado desde cero, sin recurrir a las colecciones de `java.util`.

Sobre esa base, la **entrega final** incorpora cinco mejoras que amplían los TDA existentes (grafo y árbol AVL) con nuevos métodos e integran los módulos entre sí para resolver problemas reales de un centro de distribución. Ver la sección [Mejoras de la entrega final](#mejoras-de-la-entrega-final).

---

## Tabla de contenidos

- [Contexto y problema](#contexto-y-problema)
- [Objetivos](#objetivos)
- [Estructuras de datos y TDAs](#estructuras-de-datos-y-tdas)
- [Mejoras de la entrega final](#mejoras-de-la-entrega-final)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [Cómo ejecutar](#cómo-ejecutar)
- [Datos de ejemplo](#datos-de-ejemplo)
- [Requisitos no funcionales](#requisitos-no-funcionales)
- [Decisiones de diseño](#decisiones-de-diseño)

---

## Contexto y problema

Los centros logísticos modernos gestionan grandes volúmenes de productos, operarios, camiones y pedidos en forma simultánea. En un depósito administrado manualmente, operaciones como encontrar un producto, calcular la ruta de recolección o identificar los artículos con stock mínimo requieren recorrer listas y planillas de forma secuencial. Ese enfoque no escala: a medida que crece el volumen, el tiempo de respuesta aumenta de forma lineal y los errores humanos se multiplican.

El problema se descompone en necesidades concretas, cada una resuelta con la estructura de datos más adecuada:

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

**Objetivos de la entrega final.**

- Incorporar un nuevo TDA genérico (**cola de prioridad** / montículo binario) reutilizable por varios módulos.
- Ampliar el **grafo** con el cálculo de rutas de menor peso (**Dijkstra**), aprovechando el peso de los pasillos.
- Ampliar el **árbol AVL** con una consulta por rango para generar **alertas de reposición**.
- Integrar los módulos de Stock y Rutas en una funcionalidad de **ruteo de picking**.
- Incorporar una **búsqueda tolerante a errores** de tipeo mediante distancia de edición.

---

## Estructuras de datos y TDAs

### TDAs principales — uno por funcionalidad

| Funcionalidad | TDA | Clase | Justificación |
|---|---|---|---|
| Localización de stock | Diccionario | `DiccionarioProducto<T>` | Recupera un producto por código o por nombre sin recorridos lineales innecesarios. |
| Optimización de rutas | Grafo | `Grafo<T>` | Modela sectores como vértices y pasillos como aristas. BFS calcula el camino de menor cantidad de pasillos; Dijkstra, el de menor peso. |
| Inventario crítico | Árbol AVL | `ArbolAVL` | Mantiene los stocks ordenados; el mínimo es accesible en O(log n) y habilita consultas por rango. |
| Línea de expedición | Cola | `Cola<T>` | Garantiza FIFO estricto; encolar y desencolar en O(1) con lista enlazada interna. |
| Trazabilidad | Pila | `pila<T>` | El último movimiento queda en el tope; deshacer con `desapilar()` en O(1). |
| Priorización / rutas | Cola de prioridad | `ColaPrioridad<T>` | Montículo binario (min-heap) que entrega siempre el elemento de menor prioridad; motor de Dijkstra y del ruteo de picking. |

### TDAs de soporte interno

| TDA | Clase | Rol |
|---|---|---|
| Lista enlazada simple | `Lista<T>` | Estructura base genérica; nodos con `Nodo<T>`. |
| Lista doblemente enlazada | `ListaDoble<T>` | Estructura de recorrido bidireccional; nodos con `NodoDoble<T>`. |
| Conjunto | `Conjunto` | Marca los nodos ya visitados durante el BFS, evitando ciclos. |
| Clave / Entrada de diccionario | `ClaveProducto`, `EntradaDiccionario` | Clave compuesta (código + nombre) y par clave-valor del diccionario. |

Todas las estructuras se programan contra su propia interfaz (`IArbolAVL`, `ICola`, `IColaPrioridad`, `IPila`, `IGrafo`, `IDiccionarioProducto`, `IConjunto`, `ILista`, `IListaDoble`), separando el contrato de su implementación.

---

## Mejoras de la entrega final

Como el proyecto ya incorporaba todos los TDA solicitados originalmente, las mejoras consisten en **ampliar TDA existentes con nuevos métodos relevantes** (grafo y árbol AVL) y en **incorporar un nuevo TDA reutilizable** junto a funcionalidades que integran los módulos.

### 1. Cola de prioridad (nuevo TDA)

Montículo binario (min-heap) genérico implementado sobre arreglos propios (`ColaPrioridad<T>` / `IColaPrioridad<T>`). Entrega siempre el elemento de menor prioridad en O(log n), sin usar `Comparator` ni ninguna clase de `java.util`. Es la base de las mejoras 2 y 3.

### 2. Ruta de menor peso — Dijkstra *(ampliación del TDA Grafo)*

Se agregaron al grafo los métodos `dijkstra(origen, destino)` e `distancia(origen, destino)`. Mientras el BFS original calcula el camino con **menor cantidad de pasillos**, Dijkstra calcula el de **menor peso total**, aprovechando el peso de cada `Arista` (que antes se almacenaba pero no se usaba para rutear). El BFS se conserva intacto; ambos algoritmos conviven.

### 3. Ruteo de picking *(integración Stock + Rutas)*

`GestorPicking` recibe `GestorStock` y `GestorRutas` y, dada una lista de códigos de producto, arma el recorrido para recolectarlos todos partiendo del sector `Entrada`. Resuelve la ubicación de cada producto vía Stock, mide distancias con Dijkstra y ordena las paradas con la heurística del **vecino más cercano**. Se usa una heurística porque el recorrido óptimo multiparada es el Problema del Viajante (TSP), de complejidad NP-difícil. Para mantener la coherencia, `GestorStock` **crea automáticamente el sector** en el grafo si la ubicación de un producto todavía no existe.

### 4. Búsqueda por similitud *(algoritmo)*

`GestorStock.buscarSimilares(texto, umbral)` encuentra productos aunque el texto tenga errores de tipeo, midiendo la **distancia de Levenshtein** contra el código y el nombre de cada producto y ordenando por cercanía. Es un **algoritmo**, no un TDA: se implementa con una matriz de programación dinámica `int[][]`.

### 5. Alertas de reposición *(ampliación del TDA Árbol AVL)*

Se amplió el AVL con `stocksMenoresOIgual(umbral)`, una consulta por rango que, mediante un recorrido inorden con **poda**, devuelve los stocks por debajo de un umbral en O(log n + k). Sobre ella, `GestorInventario.stocksBajoUmbral(...)` y el puente en `GestorStock` muestran qué productos necesitan reposición. Le da al AVL un segundo uso más allá de obtener el mínimo.

---

## Estructura del proyecto

```
TPO_PrograII/
└── src/
    ├── Main.java                 # menú de consola que integra todos los módulos
    ├── modelo/                   # capa de dominio
    │   ├── Producto.java         # codigo, nombre, cantidadStock, ubicacion
    │   ├── Pedido.java           # idPedido, descripcion, prioridad
    │   └── Movimiento.java       # tipo, codigoProducto, cantidad, fecha
    ├── servicio/                 # capa de lógica de negocio
    │   ├── GestorStock.java      # CRUD de productos, búsqueda por similitud, puente de reposición
    │   ├── GestorInventario.java # consultas de stock sobre el AVL (mínimo y por umbral)
    │   ├── GestorRutas.java      # operaciones sobre el grafo (BFS y Dijkstra)
    │   ├── GestorPicking.java    # ruteo de picking (integra Stock + Rutas)
    │   ├── GestorExpedicion.java # gestión de la cola de pedidos
    │   └── GestorTrazabilidad.java # historial de movimientos sobre la pila
    └── tda/                      # capa de estructuras de datos e interfaces
        ├── DiccionarioProducto.java / IDiccionarioProducto.java
        ├── ClaveProducto.java / EntradaDiccionario.java
        ├── ArbolAVL.java / IArbolAVL.java / NodoAVL.java
        ├── Cola.java / ICola.java
        ├── ColaPrioridad.java / IColaPrioridad.java
        ├── pila.java / IPila.java
        ├── Grafo.java / IGrafo.java / Arista.java
        ├── Lista.java / ILista.java / Nodo.java
        ├── ListaDoble.java / IListaDoble.java / NodoDoble.java
        └── Conjunto.java / IConjunto.java
```

La aplicación se organiza en tres capas: **modelo** (objetos del dominio), **servicio** (lógica de negocio que conecta el menú con los TDAs) y **tda** (estructuras genéricas). La clase `Main` integra las tres capas y contiene el menú de consola.

---

## Funcionalidades

El menú principal ofrece seis módulos, cada uno apoyado en una o varias estructuras. Al iniciar, el sistema carga automáticamente un juego de datos de ejemplo (ver [Datos de ejemplo](#datos-de-ejemplo)).

### 1. Stock de productos — *Diccionario + Árbol AVL*

Gestiona el catálogo. El diccionario permite buscar indistintamente por código o por nombre, y cada cambio de stock se refleja también en el árbol AVL.

- Registrar producto (código, nombre, stock inicial y ubicación)
- Buscar por código o nombre
- Modificar ubicación
- Modificar stock
- Eliminar producto
- Ver todos los productos
- Stock total del depósito
- **Buscar por similitud** — encuentra productos por parecido de código o nombre, tolerando errores de tipeo *(mejora 4)*

### 2. Rutas del depósito — *Grafo (BFS + Dijkstra)*

Modela el depósito como un grafo no dirigido: los sectores son vértices y los pasillos, aristas con peso.

- Agregar sector
- Agregar pasillo entre dos sectores (con peso)
- Calcular la ruta más corta por cantidad de pasillos (BFS)
- Verificar conexión directa entre dos sectores
- Ver el mapa (vértices y lista de adyacencia)
- **Calcular ruta por peso (Dijkstra)** — camino de menor distancia total *(mejora 2)*

### 3. Inventario crítico — *Árbol AVL*

Consulta el estado del stock a partir del árbol, que mantiene los valores ordenados.

- Ver el menor stock registrado
- Ver todos los stocks ordenados de menor a mayor (recorrido inorden)
- **Alertas de reposición** — productos con stock por debajo de un umbral *(mejora 5)*

### 4. Expedición de pedidos — *Cola (FIFO)*

Administra los pedidos respetando el orden de llegada.

- Encolar un pedido
- Despachar el próximo pedido
- Ver el próximo pedido sin despacharlo
- Contador de pedidos en cola

### 5. Trazabilidad — *Pila (LIFO)*

Registra los movimientos de inventario y permite revertir el último.

- Registrar movimiento (`INGRESO`, `EGRESO` o `TRANSFERENCIA`)
- Deshacer el último movimiento, revirtiendo su efecto sobre el stock
- Ver el historial completo (del más reciente al más antiguo)
- Contador de movimientos registrados

### 6. Ruteo de picking — *Stock + Rutas + Cola de prioridad* *(mejora 3)*

Dada una lista de códigos de producto, arma el recorrido para recolectarlos todos desde `Entrada`, minimizando la distancia con la heurística del vecino más cercano sobre las distancias calculadas por Dijkstra.

## Cómo ejecutar

**Requisitos:** JDK 21 (el proyecto se compila y prueba con JDK 21; requiere al menos JDK 17 por el uso de `switch` con expresiones flecha `->`). Opcionalmente, IntelliJ IDEA.

**Desde IntelliJ IDEA:** abrir la carpeta del proyecto y ejecutar la clase `Main`.

**Desde la terminal**, ubicado en la raíz del proyecto:

```bash
javac -d out -sourcepath src src/Main.java src/modelo/*.java src/servicio/*.java src/tda/*.java
java -cp out Main
```

---

## Datos de ejemplo

Al iniciar el sistema, se cargan **automáticamente** los siguientes datos de ejemplo, de modo que todas las funcionalidades quedan listas para probar sin ingresar nada a mano:

- **6 sectores:** `Entrada`, `A`, `B`, `C`, `D`, `E`.
- **8 pasillos con peso:** `Entrada-A(4)`, `Entrada-B(3)`, `A-B(2)`, `A-C(5)`, `B-D(6)`, `C-D(4)`, `C-E(4)`, `D-E(3)`.
- **6 productos:** `EL-001 Guantes`, `EL-002 Cinta`, `EL-003 Tornillos`, `EL-004 Guantez`, `EL-005 Casco`, `EL-006 Caja` (con stocks variados; `Guantes` y `Guantez` son parecidos a propósito).
- **2 pedidos** en la línea de expedición y **2 movimientos** de trazabilidad.

Sugerencias para probar cada mejora con esos datos:

| Mejora | Menú | Prueba | Resultado esperado |
|---|---|---|---|
| Dijkstra | Rutas → 6 | `Entrada` a `E` | `Entrada -> B -> D -> E`, distancia 12 |
| BFS (comparación) | Rutas → 3 | `Entrada` a `E` | `Entrada -> A -> C -> E`, 3 pasillos |
| Picking | Ruteo de picking → 6 | `EL-001,EL-002,EL-003` | recorrido de distancia total 12 |
| Similitud | Stock → 8 | texto `guantes`, umbral `1` | encuentra `Guantes` (dif 0) y `Guantez` (dif 1) |
| Reposición | Inventario → 3 | umbral `5` | lista los productos con stock ≤ 5 |

---

## Requisitos no funcionales

- **Eficiencia.** Las operaciones críticas (buscar producto, encolar, deshacer, consultar el mínimo, ruta de menor peso, consulta por rango) se resuelven con la estructura adecuada en lugar de recorridos lineales.
- **Robustez.** El sistema valida entradas inválidas (código, nombre y ubicación vacíos, stock negativo, prioridad fuera de rango, fecha vacía, claves duplicadas, pasillos duplicados, operaciones sobre estructuras vacías) e informa el error sin interrumpir la ejecución.
- **Legibilidad.** El código separa con claridad las capas de modelo, servicio y TDAs, usa nombres descriptivos e incluye comentarios en los métodos complejos.
- **Reutilización.** Los TDAs se implementan con generics (`<T>`) para poder usarse con cualquier tipo de objeto sin duplicar código; la cola de prioridad, por ejemplo, se reutiliza en el grafo y en el ruteo de picking.

---

## Decisiones de diseño

- **Programación contra interfaces.** Cada TDA se implementa a partir de su interfaz, separando el contrato de su implementación concreta.
- **Búsqueda flexible en el diccionario.** La clase `ClaveProducto` permite recuperar un producto tanto por su código como por su nombre, sin distinguir mayúsculas de minúsculas, validando duplicados en ambas dimensiones al insertar.
- **Sincronización entre diccionario y AVL.** Al registrar, modificar o eliminar un producto, su stock se actualiza también en el árbol AVL, de modo que las consultas de inventario crítico siempre reflejan el estado real.
- **BFS y Dijkstra conviven en el grafo.** El BFS (sobre `Cola` y `Conjunto`) resuelve el camino con menos pasillos; Dijkstra (sobre `ColaPrioridad`) resuelve el de menor peso usando el valor de cada `Arista`. Son respuestas a preguntas distintas y por eso se conservan ambos.
- **Cola de prioridad como montículo binario.** Se eligió un min-heap sobre arreglos por su O(log n) en inserción y extracción del mínimo, frente al O(n) de buscar el mínimo en una lista. No usa `Comparator`; la prioridad se recibe como entero explícito.
- **Heurística en el picking.** El recorrido óptimo que visita todas las paradas es el Problema del Viajante (TSP), NP-difícil; por eso se usa la heurística del vecino más cercano, que da una solución de muy buena calidad en tiempo razonable.
- **Enlace ubicación–sector.** Para que el picking siempre pueda ubicar un producto en el mapa, `GestorStock` crea automáticamente el sector en el grafo si no existe. El sector nace aislado hasta que se le agreguen pasillos.
- **Levenshtein como algoritmo.** La búsqueda por similitud no es un TDA sino un algoritmo de distancia de edición resuelto con una matriz de programación dinámica `int[][]`.
- **Consulta por rango con poda en el AVL.** La consulta de stocks bajo umbral aprovecha el orden del árbol para descartar subárboles completos, logrando O(log n + k) en lugar de recorrer todo el árbol.
- **Deshacer con pila.** El historial de movimientos se modela con una pila (LIFO), lo que permite revertir el último movimiento de forma natural y devolver el stock a su estado anterior.