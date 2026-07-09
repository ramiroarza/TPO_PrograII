package servicio;

import modelo.Producto;
import tda.DiccionarioProducto;
import tda.Lista;

public class GestorStock {
    private DiccionarioProducto<Producto> dic;
    private GestorInventario gestorInventario;
    private GestorRutas gestorRutas;

    public GestorStock(GestorInventario gestorInventario, GestorRutas gestorRutas) {
        this.dic = new DiccionarioProducto<>(100);
        this.gestorInventario = gestorInventario;
        this.gestorRutas = gestorRutas;
    }

    public boolean registrar(Producto p) {
        if (p == null || p.getCodigo() == null || p.getCodigo().isBlank()) return false;
        if (p.getCantidadStock() < 0) return false;
        boolean ok = dic.insertar(p.getCodigo(), p.getNombre(), p);
        if (ok) gestorInventario.agregarProducto(p.getCantidadStock());
        return ok;
    }

    public Producto buscar(String busqueda) { return dic.recuperar(busqueda); }

    public boolean modificarUbicacion(String busqueda, String nuevaUbicacion) {
        Producto p = dic.recuperar(busqueda);
        if (p == null) return false;
        p.setUbicacion(nuevaUbicacion);
        return dic.modificar(busqueda, p);
    }

    public boolean modificarStock(String busqueda, int nuevoStock) {
        if (nuevoStock < 0) return false;
        Producto p = dic.recuperar(busqueda);
        if (p == null) return false;
        gestorInventario.actualizar(p.getCantidadStock(), nuevoStock);
        p.setCantidadStock(nuevoStock);
        return dic.modificar(busqueda, p);
    }

    public boolean eliminar(String busqueda) {
        Producto p = dic.recuperar(busqueda);
        if (p == null) return false;
        gestorInventario.eliminarProducto(p.getCantidadStock());
        return dic.eliminar(busqueda);
    }

    public int stockTotal() {
        int total = 0;
        for (int i = 0; i < dic.tamanio(); i++) {
            Producto p = dic.valorEn(i);
            if (p != null) total += p.getCantidadStock();
        }
        return total;
    }

    public void mostrarProductosConStock(int stock) {
        for (int i = 0; i < dic.tamanio(); i++) {
            Producto p = dic.valorEn(i);
            if (p != null && p.getCantidadStock() == stock) System.out.println("  -> " + p);
        }
    }

    public void buscarSimilares(String texto, int umbral) {
        if (texto == null || texto.isBlank()) { System.out.println("Texto de busqueda vacio."); return; }
        String t = texto.toLowerCase();
        int n = dic.tamanio();
        Producto[] candidatos = new Producto[n];
        int[] distancias = new int[n];
        int cant = 0;

        for (int i = 0; i < n; i++) {
            Producto p = dic.valorEn(i);
            if (p == null) continue;
            int dCod = levenshtein(t, p.getCodigo().toLowerCase());
            int dNom = levenshtein(t, p.getNombre().toLowerCase());
            int d = Math.min(dCod, dNom);
            if (d <= umbral) { candidatos[cant] = p; distancias[cant] = d; cant++; }
        }

        if (cant == 0) {
            System.out.println("No se encontraron productos similares a \"" + texto + "\" (umbral " + umbral + ").");
            return;
        }

        for (int i = 0; i < cant - 1; i++) {
            int min = i;
            for (int j = i + 1; j < cant; j++) if (distancias[j] < distancias[min]) min = j;
            if (min != i) {
                int td = distancias[i]; distancias[i] = distancias[min]; distancias[min] = td;
                Producto tp = candidatos[i]; candidatos[i] = candidatos[min]; candidatos[min] = tp;
            }
        }

        System.out.println("Resultados similares a \"" + texto + "\":");
        for (int i = 0; i < cant; i++) System.out.println("  (dif " + distancias[i] + ") " + candidatos[i]);
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int costo = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int borrar = dp[i - 1][j] + 1;
                int insertar = dp[i][j - 1] + 1;
                int sustituir = dp[i - 1][j - 1] + costo;
                dp[i][j] = Math.min(Math.min(borrar, insertar), sustituir);
            }
        }
        return dp[a.length()][b.length()];
    }


    public void mostrarAlertasReposicion(int umbral) {
        Lista<Integer> bajos = gestorInventario.stocksBajoUmbral(umbral);
        if (bajos.estaVacia()) { System.out.println("No hay productos con stock <= " + umbral + "."); return; }
        System.out.print("Niveles de stock en alerta (consulta O(log n + k) sobre el AVL): ");
        bajos.mostrar();
        System.out.println("Productos que necesitan reposicion (stock <= " + umbral + "):");
        for (int i = 0; i < dic.tamanio(); i++) {
            Producto p = dic.valorEn(i);
            if (p != null && p.getCantidadStock() <= umbral) System.out.println("  [!] " + p);
        }
    }

    public int cantidadProductos() { return dic.tamanio(); }
    public boolean estaVacio() { return dic.estaVacio(); }
    public void mostrar() { dic.mostrar(); }

    public Producto recuperar(String codigo) { return dic.recuperarSilencioso(codigo); }
    public boolean actualizarProducto(String busqueda, Producto p) { return dic.modificar(busqueda, p); }
}