package servicio;

import modelo.Producto;

public class GestorPicking {

    private GestorStock gestorStock;
    private GestorRutas gestorRutas;
    private static final String ENTRADA = "Entrada";

    public GestorPicking(GestorStock gestorStock, GestorRutas gestorRutas) {
        this.gestorStock = gestorStock;
        this.gestorRutas = gestorRutas;
    }

    public void armarRuta(String[] codigos) {
        if (codigos == null || codigos.length == 0) { System.out.println("No se indicaron productos."); return; }
        if (!gestorRutas.existeSector(ENTRADA)) {
            System.out.println("No existe el sector \"" + ENTRADA + "\". Registralo y conectalo antes de armar la ruta.");
            return;
        }

        String[] sectores = new String[codigos.length];
        Producto[] prods = new Producto[codigos.length];
        int cant = 0;
        for (int i = 0; i < codigos.length; i++) {
            Producto p = gestorStock.recuperar(codigos[i]);
            if (p == null) { System.out.println("Producto no encontrado: " + codigos[i] + " (se ignora)"); continue; }
            prods[cant] = p;
            sectores[cant] = p.getUbicacion();
            cant++;
        }
        if (cant == 0) { System.out.println("Ninguno de los productos existe."); return; }

        boolean[] visitado = new boolean[cant];
        String actual = ENTRADA;
        int distanciaTotal = 0;
        int recolectados = 0;

        System.out.println("\n--- Ruta de picking (desde " + ENTRADA + ") ---");
        while (recolectados < cant) {
            int idxMasCercano = -1;
            int mejorDist = -1;
            for (int i = 0; i < cant; i++) {
                if (visitado[i]) continue;
                int d = gestorRutas.distanciaEntre(actual, sectores[i]);
                if (d < 0) continue;
                if (mejorDist == -1 || d < mejorDist) { mejorDist = d; idxMasCercano = i; }
            }
            if (idxMasCercano == -1) {
                System.out.println("Quedaron productos inalcanzables desde " + actual + ":");
                for (int i = 0; i < cant; i++)
                    if (!visitado[i]) System.out.println("  -> " + prods[i] + " (sector: " + sectores[i] + ")");
                break;
            }
            String destino = sectores[idxMasCercano];
            System.out.println((recolectados + 1) + ") " + actual + " -> " + destino +
                    "  (dist " + mejorDist + ")  | " + prods[idxMasCercano]);
            distanciaTotal += mejorDist;
            visitado[idxMasCercano] = true;
            actual = destino;
            recolectados++;
        }
        System.out.println("Productos recolectados: " + recolectados + " de " + cant);
        System.out.println("Distancia total recorrida: " + distanciaTotal);
    }
}