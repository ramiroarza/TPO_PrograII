import modelo.Movimiento;
import modelo.Pedido;
import servicio.*;
import tda.Ubicacion;

import java.util.Scanner;

public class Main {

    static GestorStock gestorStock           = new GestorStock();
    static GestorRutas gestorRutas           = new GestorRutas(20);
    static GestorInventario gestorInventario = new GestorInventario();
    static GestorExpedicion gestorExpedicion = new GestorExpedicion();
    static GestorTrazabilidad gestorTrazabilidad = new GestorTrazabilidad();

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
                    gestorStock.registrar(cod, nom, new Ubicacion(pasillo, estante, nivel));
                } catch (NumberFormatException e) { System.out.println("Estante y nivel tienen que ser numeros"); }
            }
            case "2" -> {
                System.out.print("Buscar: "); Ubicacion u = gestorStock.buscar(sc.nextLine().trim());
                if (u != null) System.out.println("Ubicacion: " + u);
            }
            case "3" -> {
                System.out.print("Codigo o nombre: "); String b = sc.nextLine().trim();
                System.out.print("Nuevo pasillo: "); String p = sc.nextLine().trim();
                try {
                    System.out.print("Nuevo estante: "); int e = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nuevo nivel: "); int n = Integer.parseInt(sc.nextLine().trim());
                    gestorStock.modificarUbicacion(b, new Ubicacion(p, e, n));
                } catch (NumberFormatException e) { System.out.println("Estante y nivel tienen que ser numeros"); }
            }
            case "4" -> { System.out.print("Codigo o nombre: "); gestorStock.eliminar(sc.nextLine().trim()); }
            case "5" -> gestorStock.mostrarTodos();
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
            case "1" -> { System.out.print("Nombre del sector: "); gestorRutas.agregarSector(sc.nextLine().trim()); }
            case "2" -> {
                System.out.print("Sector A: "); String a = sc.nextLine().trim();
                System.out.print("Sector B: "); gestorRutas.agregarPasillo(a, sc.nextLine().trim());
            }
            case "3" -> {
                System.out.print("Origen: "); String o = sc.nextLine().trim();
                System.out.print("Destino: "); gestorRutas.calcularRuta(o, sc.nextLine().trim());
            }
            case "4" -> {
                System.out.print("Sector A: "); String a = sc.nextLine().trim();
                System.out.print("Sector B: "); gestorRutas.verificarConexion(a, sc.nextLine().trim());
            }
            case "5" -> gestorRutas.mostrarMapa();
            default  -> System.out.println("Opcion no valida");
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
            case "1" -> { try { System.out.print("Cantidad: "); gestorInventario.agregarStock(Integer.parseInt(sc.nextLine().trim())); } catch (NumberFormatException e) { System.out.println("Ingresa un numero"); } }
            case "2" -> gestorInventario.verMinimoStock();
            case "3" -> gestorInventario.verStocksOrdenados();
            case "4" -> {
                try {
                    System.out.print("Stock actual: "); int v = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Stock nuevo: "); int n = Integer.parseInt(sc.nextLine().trim());
                    gestorInventario.actualizarStock(v, n);
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
                    gestorExpedicion.encolarPedido(new Pedido(id, desc, prio));
                } catch (NumberFormatException e) { System.out.println("ID y prioridad tienen que ser numeros"); }
            }
            case "2" -> gestorExpedicion.despachar();
            case "3" -> gestorExpedicion.verProximo();
            default  -> System.out.println("Opcion no valida");
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
                    gestorTrazabilidad.registrarMovimiento(new Movimiento(tipo, cod, cant, java.time.LocalDate.now().toString()));
                } catch (NumberFormatException e) { System.out.println("La cantidad tiene que ser un numero"); }
            }
            case "2" -> gestorTrazabilidad.deshacerUltimo();
            case "3" -> gestorTrazabilidad.verHistorial();
            default  -> System.out.println("Opcion no valida");
        }
    }
}
