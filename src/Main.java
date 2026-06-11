import modelo.Movimiento;
import modelo.Pedido;
import modelo.Producto;
import tda.DiccionarioProducto;
import tda.ArbolAVL;
import tda.Cola;
import tda.pila;
import tda.Grafo;

import java.util.Scanner; // para leer la consola

public class Main {

    // diccionario guarda el Producto completo (codigo, nombre, stock, ubicacion)
    static DiccionarioProducto<Producto> diccionario = new DiccionarioProducto<>(100);

    // el AVL se alimenta del stock de cada producto registrado
    static ArbolAVL inventario = new ArbolAVL();

    static Cola<Pedido> colaPedidos = new Cola<>();
    static pila<Movimiento> historial = new pila<>();
    static Grafo<String> grafo = new Grafo<>(20, false);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion = -1;
        System.out.println("=== Centro Logistico de Distribucion ===");

        while (opcion != 0) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Stock de productos");
            System.out.println("2. Rutas del deposito");
            System.out.println("3. Inventario critico");
            System.out.println("4. Expedicion de pedidos");
            System.out.println("5. Trazabilidad");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            try { opcion = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingresa un numero"); continue; }

            switch (opcion) {
                case 1 -> menuStock(sc);
                case 2 -> menuRutas(sc);
                case 3 -> menuInventario(sc);
                case 4 -> menuExpedicion(sc);
                case 5 -> menuTrazabilidad(sc);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion no valida");
            }
        }
        sc.close();
    }

    // ─── STOCK ───────────────────────────────────────────────────────
    static void menuStock(Scanner sc) {
        System.out.println("\n-- Stock --");
        System.out.println("1. Registrar producto");
        System.out.println("2. Buscar (codigo o nombre)");
        System.out.println("3. Modificar ubicacion");
        System.out.println("4. Modificar stock");
        System.out.println("5. Eliminar producto");
        System.out.println("6. Ver todos");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Codigo (ej PROD-001): "); String cod = sc.nextLine().trim();
                    System.out.print("Nombre: "); String nom = sc.nextLine().trim();
                    System.out.print("Stock inicial: "); int stock = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Ubicacion (ej Pasillo A Estante 2 Nivel 1): "); String ubi = sc.nextLine().trim();

                    if (cod.isBlank()) { System.out.println("El codigo no puede estar vacio"); break; }
                    if (stock < 0) { System.out.println("El stock no puede ser negativo"); break; }

                    Producto p = new Producto(cod, nom, stock, ubi);
                    diccionario.insertar(cod, nom, p);
                    // al registrar el producto tambien insertamos su stock en el AVL
                    inventario.insertar(stock);
                } catch (NumberFormatException e) { System.out.println("El stock tiene que ser un numero"); }
            }
            case "2" -> {
                System.out.print("Buscar (codigo o nombre): ");
                Producto p = diccionario.recuperar(sc.nextLine().trim());
                if (p != null) System.out.println("Encontrado: " + p);
            }
            case "3" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                Producto p = diccionario.recuperar(b);
                if (p == null) break;
                System.out.print("Nueva ubicacion: "); String nuevaUbi = sc.nextLine().trim();
                p.setUbicacion(nuevaUbi);
                diccionario.modificar(b, p);
                System.out.println("Ubicacion actualizada: " + p);
            }
            case "4" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                Producto p = diccionario.recuperar(b);
                if (p == null) break;
                try {
                    System.out.print("Nuevo stock: "); int nuevoStock = Integer.parseInt(sc.nextLine().trim());
                    if (nuevoStock < 0) { System.out.println("El stock no puede ser negativo"); break; }
                    // actualizamos el AVL: sacamos el viejo, insertamos el nuevo
                    inventario.eliminar(p.getCantidadStock());
                    inventario.insertar(nuevoStock);
                    p.setCantidadStock(nuevoStock);
                    diccionario.modificar(b, p);
                    System.out.println("Stock actualizado: " + p);
                } catch (NumberFormatException e) { System.out.println("Ingresa un numero valido"); }
            }
            case "5" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                Producto p = diccionario.recuperar(b);
                if (p == null) break;
                // al eliminar el producto tambien sacamos su stock del AVL
                inventario.eliminar(p.getCantidadStock());
                diccionario.eliminar(b);
            }
            case "6" -> diccionario.mostrar();
            default  -> System.out.println("Opcion no valida");
        }
    }

    // ─── RUTAS ───────────────────────────────────────────────────────
    static void menuRutas(Scanner sc) {
        System.out.println("\n-- Rutas --");
        System.out.println("1. Agregar sector");
        System.out.println("2. Agregar pasillo entre sectores");
        System.out.println("3. Calcular ruta mas corta (BFS)");
        System.out.println("4. Verificar conexion directa");
        System.out.println("5. Ver mapa");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                System.out.print("Nombre del sector: "); String nombre = sc.nextLine().trim();
                grafo.insertarVertice(nombre);
                System.out.println("Sector registrado: " + nombre);
            }
            case "2" -> {
                System.out.print("Sector A: "); String a = sc.nextLine().trim();
                System.out.print("Sector B: "); String b = sc.nextLine().trim();
                if (!grafo.existeVertice(a) || !grafo.existeVertice(b)) {
                    System.out.println("Uno de los sectores no existe, registralo primero");
                } else {
                    grafo.insertarArista(a, b);
                    System.out.println("Pasillo agregado: " + a + " <-> " + b);
                }
            }
            case "3" -> {
                System.out.print("Origen: "); String o = sc.nextLine().trim();
                System.out.print("Destino: "); String d = sc.nextLine().trim();
                String[] camino = grafo.bfs(o, d);
                if (camino != null) {
                    System.out.print("Ruta mas corta (" + (camino.length - 1) + " pasillos): ");
                    for (int i = 0; i < camino.length; i++) {
                        System.out.print(camino[i]);
                        if (i < camino.length - 1) System.out.print(" -> ");
                    }
                    System.out.println();
                }
            }
            case "4" -> {
                System.out.print("Sector A: "); String a = sc.nextLine().trim();
                System.out.print("Sector B: "); String b = sc.nextLine().trim();
                boolean ok = grafo.existeArista(a, b);
                System.out.println(a + " y " + b + (ok ? " estan conectados directamente" : " no tienen conexion directa"));
            }
            case "5" -> { grafo.mostrarVertices(); grafo.mostrarMatriz(); }
            default  -> System.out.println("Opcion no valida");
        }
    }

    // ─── INVENTARIO ──────────────────────────────────────────────────
    // el AVL ya se actualiza solo desde menuStock cuando registras o modificas un producto
    // aca solo consultamos
    static void menuInventario(Scanner sc) {
        System.out.println("\n-- Inventario critico --");
        System.out.println("1. Ver producto con menor stock");
        System.out.println("2. Ver todos los stocks ordenados");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                int min = inventario.minimo();
                if (min == -1) break;
                // buscamos el producto que tiene ese stock para mostrar el nombre tambien
                // recorremos el diccionario para encontrarlo
                System.out.println("Menor stock registrado: " + min + " unidades");
            }
            case "2" -> {
                if (inventario.getRaiz() == null) { System.out.println("No hay productos registrados"); break; }
                System.out.print("Stocks (de menor a mayor): ");
                inventario.mostrarInorden(inventario.getRaiz());
                System.out.println();
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── EXPEDICION ──────────────────────────────────────────────────
    static void menuExpedicion(Scanner sc) {
        System.out.println("\n-- Expedicion --");
        System.out.println("1. Encolar pedido");
        System.out.println("2. Despachar proximo");
        System.out.println("3. Ver proximo sin despachar");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("ID: "); int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Descripcion: "); String desc = sc.nextLine().trim();
                    System.out.print("Prioridad (1-3): "); int prio = Integer.parseInt(sc.nextLine().trim());
                    colaPedidos.encolar(new Pedido(id, desc, prio));
                } catch (NumberFormatException e) { System.out.println("ID y prioridad tienen que ser numeros"); }
            }
            case "2" -> {
                if (colaPedidos.estaVacia()) { System.out.println("No hay pedidos pendientes"); break; }
                System.out.println("Despachando: " + colaPedidos.desencolar());
            }
            case "3" -> {
                if (colaPedidos.estaVacia()) { System.out.println("No hay pedidos en la cola"); break; }
                System.out.println("Proximo: " + colaPedidos.frente());
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── TRAZABILIDAD ────────────────────────────────────────────────
    static void menuTrazabilidad(Scanner sc) {
        System.out.println("\n-- Trazabilidad --");
        System.out.println("1. Registrar movimiento");
        System.out.println("2. Deshacer ultimo movimiento");
        System.out.println("3. Ver historial");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Tipo (INGRESO/EGRESO/TRANSFERENCIA): "); String tipo = sc.nextLine().trim().toUpperCase();
                    if (!tipo.equals("INGRESO") && !tipo.equals("EGRESO") && !tipo.equals("TRANSFERENCIA")) {
                        System.out.println("Tipo invalido, usa INGRESO, EGRESO o TRANSFERENCIA");
                        break;
                    }
                    System.out.print("Codigo del producto: "); String cod = sc.nextLine().trim();
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine().trim());
                    if (cant <= 0) { System.out.println("La cantidad tiene que ser mayor a cero"); break; }
                    System.out.print("Fecha (ej 2026-06-11): "); String fecha = sc.nextLine().trim();

                    Movimiento m = new Movimiento(tipo, cod, cant, fecha);
                    historial.apilar(m);

                    // si el producto existe, actualizamos su stock segun el tipo de movimiento
                    Producto p = diccionario.recuperar(cod);
                    if (p != null) {
                        int stockViejo = p.getCantidadStock();
                        int stockNuevo = tipo.equals("INGRESO") ? stockViejo + cant : stockViejo - cant;
                        if (stockNuevo < 0) { System.out.println("No hay suficiente stock para ese egreso"); historial.desapilar(); break; }
                        inventario.eliminar(stockViejo);
                        inventario.insertar(stockNuevo);
                        p.setCantidadStock(stockNuevo);
                        diccionario.modificar(cod, p);
                        System.out.println("Stock actualizado: " + p);
                    }
                } catch (NumberFormatException e) { System.out.println("La cantidad tiene que ser un numero"); }
            }
            case "2" -> {
                if (historial.pilaVacia()) { System.out.println("No hay movimientos para deshacer"); break; }
                Movimiento m = historial.desapilar();
                System.out.println("Deshaciendo: " + m);

                // revertimos el efecto en el stock del producto
                Producto p = diccionario.recuperar(m.getCodigoProducto());
                if (p != null) {
                    int stockActual = p.getCantidadStock();
                    // si el movimiento fue un ingreso, revertir = restar; si fue egreso, revertir = sumar
                    int stockRevertido = m.getTipo().equals("INGRESO") ? stockActual - m.getCantidad() : stockActual + m.getCantidad();
                    inventario.eliminar(stockActual);
                    inventario.insertar(stockRevertido);
                    p.setCantidadStock(stockRevertido);
                    diccionario.modificar(m.getCodigoProducto(), p);
                    System.out.println("Stock revertido: " + p);
                }
            }
            case "3" -> {
                if (historial.pilaVacia()) { System.out.println("No hay movimientos registrados"); break; }
                System.out.println("Historial (ultimo primero):");
                historial.mostrar();
            }
            default -> System.out.println("Opcion no valida");
        }
    }
}
