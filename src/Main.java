import modelo.Movimiento;
import modelo.Pedido;
import tda.Ubicacion;
import tda.DiccionarioProducto;
import tda.ArbolAVL;
import tda.Cola;
import tda.pila;
import tda.Grafo;

import java.util.Scanner;

public class Main {

    // instanciamos los TDAs directamente, sin gestores
    static DiccionarioProducto<Ubicacion> diccionario = new DiccionarioProducto<>(100);
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

    static void menuStock(Scanner sc) {
        System.out.println("\n-- Stock --");
        System.out.println("1. Registrar producto");
        System.out.println("2. Buscar (codigo o nombre)");
        System.out.println("3. Modificar ubicacion");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Ver todos");
        System.out.print("Opcion: ");
        switch (sc.nextLine().trim()) {
            case "1" -> {
                System.out.print("Codigo (ej PROD-001): "); String cod = sc.nextLine().trim();
                System.out.print("Nombre: "); String nom = sc.nextLine().trim();
                System.out.print("Pasillo: "); String pasillo = sc.nextLine().trim();
                try {
                    System.out.print("Estante: "); int estante = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nivel: "); int nivel = Integer.parseInt(sc.nextLine().trim());
                    if (cod.isBlank()) { System.out.println("El codigo no puede estar vacio"); break; }
                    diccionario.insertar(cod, nom, new Ubicacion(pasillo, estante, nivel));
                } catch (NumberFormatException e) { System.out.println("Estante y nivel tienen que ser numeros"); }
            }
            case "2" -> {
                System.out.print("Buscar: "); String b = sc.nextLine().trim();
                Ubicacion u = diccionario.recuperar(b);
                if (u != null) System.out.println("Ubicacion: " + u);
            }
            case "3" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                System.out.print("Nuevo pasillo: "); String p = sc.nextLine().trim();
                try {
                    System.out.print("Nuevo estante: "); int e = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nuevo nivel: "); int n = Integer.parseInt(sc.nextLine().trim());
                    diccionario.modificar(b, new Ubicacion(p, e, n));
                } catch (NumberFormatException e) { System.out.println("Estante y nivel tienen que ser numeros"); }
            }
            case "4" -> { System.out.print("Codigo o nombre: "); diccionario.eliminar(sc.nextLine().trim()); }
            case "5" -> diccionario.mostrar();
            default  -> System.out.println("Opcion no valida");
        }
    }

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
                System.out.print("Nombre del sector: ");
                String nombre = sc.nextLine().trim();
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
            case "5" -> {
                grafo.mostrarVertices();
                grafo.mostrarMatriz();
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    static void menuInventario(Scanner sc) {
        System.out.println("\n-- Inventario critico --");
        System.out.println("1. Registrar stock");
        System.out.println("2. Ver menor stock");
        System.out.println("3. Ver todos ordenados");
        System.out.println("4. Actualizar stock");
        System.out.print("Opcion: ");
        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine().trim());
                    if (cant < 0) { System.out.println("El stock no puede ser negativo"); break; }
                    inventario.insertar(cant);
                    System.out.println("Stock " + cant + " agregado al inventario critico");
                } catch (NumberFormatException e) { System.out.println("Ingresa un numero"); }
            }
            case "2" -> {
                int min = inventario.minimo();
                if (min != -1) System.out.println("Menor stock: " + min + " unidades");
            }
            case "3" -> {
                if (inventario.getRaiz() == null) { System.out.println("No hay stocks registrados"); break; }
                System.out.print("Stocks (de menor a mayor): ");
                inventario.mostrarInorden(inventario.getRaiz());
                System.out.println();
            }
            case "4" -> {
                try {
                    System.out.print("Stock actual: "); int v = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Stock nuevo: "); int n = Integer.parseInt(sc.nextLine().trim());
                    if (n < 0) { System.out.println("El stock no puede ser negativo"); break; }
                    inventario.eliminar(v);
                    inventario.insertar(n);
                    System.out.println("Stock actualizado: " + v + " -> " + n);
                } catch (NumberFormatException e) { System.out.println("Ingresa numeros validos"); }
            }
            default -> System.out.println("Opcion no valida");
        }
    }

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
                Pedido p = colaPedidos.desencolar();
                System.out.println("Despachando: " + p);
            }
            case "3" -> {
                if (colaPedidos.estaVacia()) { System.out.println("No hay pedidos en la cola"); break; }
                System.out.println("Proximo: " + colaPedidos.frente());
            }
            default -> System.out.println("Opcion no valida");
        }
    }

    static void menuTrazabilidad(Scanner sc) {
        System.out.println("\n-- Trazabilidad --");
        System.out.println("1. Registrar movimiento");
        System.out.println("2. Deshacer ultimo");
        System.out.println("3. Ver historial");
        System.out.print("Opcion: ");
        switch (sc.nextLine().trim()) {
            case "1" -> {
                try {
                    System.out.print("Tipo (INGRESO/EGRESO/TRANSFERENCIA): "); String tipo = sc.nextLine().trim().toUpperCase();
                    System.out.print("Codigo del producto: "); String cod = sc.nextLine().trim();
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine().trim());
                    historial.apilar(new Movimiento(tipo, cod, cant, java.time.LocalDate.now().toString()));
                } catch (NumberFormatException e) { System.out.println("La cantidad tiene que ser un numero"); }
            }
            case "2" -> {
                if (historial.pilaVacia()) { System.out.println("No hay movimientos para deshacer"); break; }
                Movimiento m = historial.desapilar();
                System.out.println("Movimiento deshecho: " + m);
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
