import modelo.Movimiento;
import modelo.Pedido;
import modelo.Producto;
import servicio.GestorInventario;
import servicio.GestorStock;
import servicio.GestorExpedicion;
import servicio.GestorTrazabilidad;
import servicio.GestorRutas;
import servicio.GestorPicking;

import java.util.Scanner;

public class Main {

    // orden de construccion importante: inventario -> rutas -> stock -> trazabilidad -> picking
    static GestorInventario   gestorInventario   = new GestorInventario();
    static GestorRutas        gestorRutas        = new GestorRutas();
    static GestorStock        gestorStock        = new GestorStock(gestorInventario, gestorRutas);
    static GestorTrazabilidad gestorTrazabilidad = new GestorTrazabilidad(gestorStock, gestorInventario);
    static GestorExpedicion   gestorExpedicion   = new GestorExpedicion();
    static GestorPicking      gestorPicking      = new GestorPicking(gestorStock, gestorRutas);

    static boolean datosCargados = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion = -1;
        System.out.println("=== Centro Logistico de Distribucion ===");
        cargarDatosDeEjemplo(); // carga automatica de datos de ejemplo al iniciar

        while (opcion != 0) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Stock de productos");
            System.out.println("2. Rutas del deposito");
            System.out.println("3. Inventario critico");
            System.out.println("4. Expedicion de pedidos");
            System.out.println("5. Trazabilidad");
            System.out.println("6. Ruteo de picking");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            try { opcion = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("\n Error, ingrese un numero"); continue; }

            switch (opcion) {
                case 1 -> menuStock(sc);
                case 2 -> menuRutas(sc);
                case 3 -> menuInventario(sc);
                case 4 -> menuExpedicion(sc);
                case 5 -> menuTrazabilidad(sc);
                case 6 -> menuPicking(sc);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion no valida");
            }
        }
        sc.close();
    }

    // ─── DATOS DE EJEMPLO / CASOS DE PRUEBA ──────────────────────────
    // carga un deposito completo para probar las 5 mejoras sin tener que tipear todo
    static void cargarDatosDeEjemplo() {
        if (datosCargados) { System.out.println("Los datos de ejemplo ya fueron cargados."); return; }

        // 1) mapa del deposito: sectores (vertices) y pasillos con peso (aristas)
        String[] sectores = {"Entrada", "A", "B", "C", "D", "E"};
        for (String s : sectores) gestorRutas.agregarSector(s);
        gestorRutas.agregarPasillo("Entrada", "A", 4);
        gestorRutas.agregarPasillo("Entrada", "B", 3);
        gestorRutas.agregarPasillo("A", "B", 2);
        gestorRutas.agregarPasillo("A", "C", 5);
        gestorRutas.agregarPasillo("B", "D", 6);
        gestorRutas.agregarPasillo("C", "D", 4);
        gestorRutas.agregarPasillo("C", "E", 4);
        gestorRutas.agregarPasillo("D", "E", 3);

        // 2) productos: la ubicacion coincide con un sector ya creado; stock variado para las alertas
        //    "Guantes" y "Guantez" son parecidos a proposito, para probar la busqueda por similitud
        gestorStock.registrar(new Producto("EL-001", "Guantes", 2, "B"));
        gestorStock.registrar(new Producto("EL-002", "Cinta", 50, "D"));
        gestorStock.registrar(new Producto("EL-003", "Tornillos", 1, "E"));
        gestorStock.registrar(new Producto("EL-004", "Guantez", 30, "A"));
        gestorStock.registrar(new Producto("EL-005", "Casco", 8, "C"));
        gestorStock.registrar(new Producto("EL-006", "Caja", 4, "A"));

        // 3) pedidos en la linea de expedicion
        gestorExpedicion.encolarPedido(new Pedido(101, "Kit de seguridad", 1));
        gestorExpedicion.encolarPedido(new Pedido(102, "Reposicion pasillo A", 2));

        // 4) movimientos de trazabilidad (modifican el stock y quedan en el historial)
        gestorTrazabilidad.registrar(new Movimiento("INGRESO", "EL-002", 10, "2026-06-10"));
        gestorTrazabilidad.registrar(new Movimiento("EGRESO", "EL-005", 3, "2026-06-11"));

        datosCargados = true;
        System.out.println("Datos de ejemplo cargados: 6 sectores, 8 pasillos, 6 productos, 2 pedidos, 2 movimientos.");
        System.out.println("Sugerencias para probar las mejoras:");
        System.out.println("  - Rutas op.6:      Dijkstra Entrada -> E (deberia dar Entrada->B->D->E, distancia 12)");
        System.out.println("  - Rutas op.3:      BFS Entrada -> E (deberia dar Entrada->A->C->E, 3 pasillos)");
        System.out.println("  - Picking op.6:    EL-001,EL-002,EL-003");
        System.out.println("  - Stock op.8:      texto 'guantes', umbral 1 (encuentra Guantes y Guantez)");
        System.out.println("  - Inventario op.3: umbral 5 (alertas de reposicion via AVL)");
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
        System.out.println("7. Stock total del deposito");
        System.out.println("8. Buscar por similitud");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Codigo (ej PROD-001): "); String cod = sc.nextLine().trim();
                    if (cod.isBlank()) { System.out.println("\n El codigo no puede estar vacio"); break; }
                    System.out.print("Nombre: "); String nom = sc.nextLine().trim();
                    if (nom.isBlank()) { System.out.println("\n El nombre no puede estar vacio"); break; }
                    System.out.print("Stock inicial: "); int stock = Integer.parseInt(sc.nextLine().trim());
                    if (stock < 0) { System.out.println("\n El stock no puede ser negativo"); break; }
                    System.out.print("Ubicacion (ej Pasillo A Estante 2 Nivel 1): "); String ubi = sc.nextLine().trim();
                    if (ubi.isBlank()) { System.out.println("\n La ubicacion no puede estar vacia"); break; }
                    Producto p = new Producto(cod, nom, stock, ubi);
                    if (gestorStock.registrar(p)) System.out.println("Producto registrado: " + p);
                } catch (NumberFormatException e) { System.out.println("\n El stock tiene que ser un numero"); }
            }
            case "2" -> {
                System.out.print("Buscar (codigo o nombre): ");
                Producto p = gestorStock.buscar(sc.nextLine().trim());
                if (p != null) System.out.println("Encontrado: " + p);
            }
            case "3" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                System.out.print("Nueva ubicacion: "); String nuevaUbi = sc.nextLine().trim();
                if (gestorStock.modificarUbicacion(b, nuevaUbi)) System.out.println("Ubicacion actualizada");
            }
            case "4" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                try {
                    System.out.print("Nuevo stock: "); int nuevoStock = Integer.parseInt(sc.nextLine().trim());
                    if (nuevoStock < 0) { System.out.println("\n El stock no puede ser negativo"); break; }
                    if (gestorStock.modificarStock(b, nuevoStock)) System.out.println("Stock actualizado");
                } catch (NumberFormatException e) { System.out.println("Ingresa un numero valido"); }
            }
            case "5" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                if (gestorStock.eliminar(b)) System.out.println("Producto eliminado");
            }
            case "6" -> gestorStock.mostrar();
            case "7" -> {
                if (gestorStock.estaVacio()) { System.out.println("\n No hay productos registrados"); break; }
                System.out.println("Stock total del deposito: " + gestorStock.stockTotal() +
                        " unidades (" + gestorStock.cantidadProductos() + " productos)");
            }
            case "8" -> {
                System.out.print("Texto a buscar: "); String txt = sc.nextLine().trim();
                try {
                    System.out.print("Umbral de diferencia (ej 2): "); int umbral = Integer.parseInt(sc.nextLine().trim());
                    gestorStock.buscarSimilares(txt, umbral);
                } catch (NumberFormatException e) { System.out.println("El umbral tiene que ser un numero"); }
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── RUTAS ───────────────────────────────────────────────────────
    static void menuRutas(Scanner sc) {
        System.out.println("\n-- Rutas --");
        System.out.println("1. Agregar sector");
        System.out.println("2. Agregar pasillo entre sectores");
        System.out.println("3. Calcular ruta mas corta (BFS, por tramos)");
        System.out.println("4. Verificar conexion directa");
        System.out.println("5. Ver mapa");
        System.out.println("6. Calcular ruta por peso (Dijkstra)");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                System.out.print("Nombre del sector: "); String nombre = sc.nextLine().trim();
                if (nombre.isBlank()) { System.out.println("\n El nombre no puede estar vacio"); break; }
                gestorRutas.agregarSector(nombre);
                System.out.println("Sector registrado: " + nombre);
            }
            case "2" -> {
                System.out.print("Sector A: "); String a = sc.nextLine().trim();
                System.out.print("Sector B: "); String b = sc.nextLine().trim();
                try {
                    System.out.print("Peso del pasillo: "); int peso = Integer.parseInt(sc.nextLine().trim());
                    if (gestorRutas.hayConexion(a, b)) System.out.println("Ya existe un pasillo entre " + a + " y " + b);
                    else if (gestorRutas.agregarPasillo(a, b, peso)) System.out.println("Pasillo agregado: " + a + " <-> " + b);
                    else System.out.println("Uno de los sectores no existe, registralo primero");
                } catch (NumberFormatException e) { System.out.println("El peso tiene que ser un numero"); }
            }
            case "3" -> {
                System.out.print("Origen: "); String o = sc.nextLine().trim();
                System.out.print("Destino: "); String d = sc.nextLine().trim();
                String[] camino = gestorRutas.calcularRuta(o, d);
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
                boolean ok = gestorRutas.hayConexion(a, b);
                System.out.println(a + " y " + b + (ok ? " estan conectados directamente" : " no tienen conexion directa"));
            }
            case "5" -> gestorRutas.mostrarMapa();
            case "6" -> {
                System.out.print("Origen: "); String o = sc.nextLine().trim();
                System.out.print("Destino: "); String d = sc.nextLine().trim();
                String[] camino = gestorRutas.calcularRutaMasCorta(o, d);
                if (camino != null) {
                    System.out.print("Ruta de menor peso (" + gestorRutas.distanciaEntre(o, d) + " de distancia): ");
                    for (int i = 0; i < camino.length; i++) {
                        System.out.print(camino[i]);
                        if (i < camino.length - 1) System.out.print(" -> ");
                    }
                    System.out.println();
                }
            }
            default  -> System.out.println("Opcion no valida");
        }
    }

    // ─── INVENTARIO ──────────────────────────────────────────────────
    static void menuInventario(Scanner sc) {
        System.out.println("\n-- Inventario critico --");
        System.out.println("1. Ver producto con menor stock");
        System.out.println("2. Ver todos los stocks ordenados");
        System.out.println("3. Alertas de reposicion (stock bajo umbral)");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                if (gestorInventario.estaVacio()) { System.out.println("\n No hay productos registrados"); break; }
                int min = gestorInventario.obtenerMinimo();
                System.out.println("Menor stock registrado: " + min + " unidades");
                gestorStock.mostrarProductosConStock(min);
            }
            case "2" -> gestorInventario.mostrarOrdenado();
            case "3" -> {
                if (gestorInventario.estaVacio()) { System.out.println("\n No hay productos registrados"); break; }
                try {
                    System.out.print("Umbral de stock: "); int umbral = Integer.parseInt(sc.nextLine().trim());
                    gestorStock.mostrarAlertasReposicion(umbral);
                } catch (NumberFormatException e) { System.out.println("El umbral tiene que ser un numero"); }
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── EXPEDICION ──────────────────────────────────────────────────
    static void menuExpedicion(Scanner sc) {
        System.out.println("\n-- Expedicion --");
        System.out.println("Pedidos en cola: " + gestorExpedicion.pedidosEnCola());
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
                    if (prio < 1 || prio > 3) { System.out.println("\n La prioridad debe ser 1, 2 o 3"); break; }
                    gestorExpedicion.encolarPedido(new Pedido(id, desc, prio));
                    System.out.println("Pedido encolado");
                } catch (NumberFormatException e) { System.out.println("\n ID y prioridad tienen que ser numeros"); }
            }
            case "2" -> {
                if (gestorExpedicion.estaVacia()) { System.out.println("\n No hay pedidos pendientes"); break; }
                System.out.println("Despachando: " + gestorExpedicion.despachar());
            }
            case "3" -> {
                if (gestorExpedicion.estaVacia()) { System.out.println("\n No hay pedidos en la cola"); break; }
                System.out.println("Proximo: " + gestorExpedicion.proximoPedido());
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── TRAZABILIDAD ────────────────────────────────────────────────
    static void menuTrazabilidad(Scanner sc) {
        System.out.println("\n-- Trazabilidad --");
        System.out.println("Movimientos registrados: " + gestorTrazabilidad.cantidadMovimientos());
        System.out.println("1. Registrar movimiento");
        System.out.println("2. Deshacer ultimo movimiento");
        System.out.println("3. Ver historial");
        System.out.print("Opcion: ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Tipo (INGRESO/EGRESO/TRANSFERENCIA): "); String tipo = sc.nextLine().trim().toUpperCase();
                    if (!tipo.equals("INGRESO") && !tipo.equals("EGRESO") && !tipo.equals("TRANSFERENCIA")) {
                        System.out.println("Tipo invalido, usa INGRESO, EGRESO o TRANSFERENCIA"); break;
                    }
                    System.out.print("Codigo del producto: "); String cod = sc.nextLine().trim();
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine().trim());
                    if (cant <= 0) { System.out.println("\n La cantidad tiene que ser mayor a cero"); break; }
                    System.out.print("Fecha (ej 2026-06-11): "); String fecha = sc.nextLine().trim();
                    if (fecha.isBlank()) { System.out.println("\n La fecha no puede estar vacia"); break; }
                    Movimiento m = new Movimiento(tipo, cod, cant, fecha);
                    if (gestorTrazabilidad.registrar(m)) System.out.println("Movimiento registrado: " + m);
                    else System.out.println("\n No se pudo registrar: producto no encontrado o stock insuficiente para el egreso");
                } catch (NumberFormatException e) { System.out.println("\n La cantidad tiene que ser un numero"); }
            }
            case "2" -> {
                if (gestorTrazabilidad.estaVacio()) { System.out.println("No hay movimientos para deshacer"); break; }
                Movimiento m = gestorTrazabilidad.deshacerUltimo();
                if (m == null) System.out.println("\n No se puede deshacer: revertir ese ingreso dejaria el stock en negativo");
                else System.out.println("Deshaciendo: " + m);
            }
            case "3" -> {
                if (gestorTrazabilidad.estaVacio()) { System.out.println("No hay movimientos registrados"); break; }
                System.out.println("Historial (ultimo primero):");
                gestorTrazabilidad.verHistorial();
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    // ─── PICKING ─────────────────────────────────────────────────────
    static void menuPicking(Scanner sc) {
        System.out.println("\n-- Ruteo de picking --");
        if (gestorStock.estaVacio()) { System.out.println("\n No hay productos registrados"); return; }
        System.out.print("Codigos de los productos del pedido (separados por coma): ");
        String linea = sc.nextLine().trim();
        if (linea.isBlank()) { System.out.println("No se ingresaron codigos"); return; }
        String[] codigos = linea.split(",");
        for (int i = 0; i < codigos.length; i++) codigos[i] = codigos[i].trim();
        gestorPicking.armarRuta(codigos);
    }
}