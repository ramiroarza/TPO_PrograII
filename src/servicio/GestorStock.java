package servicio;

import modelo.Producto;
import tda.DiccionarioProducto;

public class GestorStock {
    private DiccionarioProducto<Producto> dic;
    private GestorInventario gestorInventario;

    public GestorStock(GestorInventario gestorInventario) {
        this.dic = new DiccionarioProducto<>(100);
        this.gestorInventario = gestorInventario;
    }

    public boolean registrar(Producto p) {
        if (p == null || p.getCodigo() == null || p.getCodigo().isBlank()) return false;
        if (p.getCantidadStock() < 0) return false;
        boolean ok = dic.insertar(p.getCodigo(), p.getNombre(), p);
        if (ok) gestorInventario.agregarProducto(p.getCantidadStock()); // solo si se registro de verdad
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

    public int cantidadProductos() { return dic.tamanio(); }
    public boolean estaVacio() { return dic.estaVacio(); }
    public void mostrar() { dic.mostrar(); }

    // usados por GestorTrazabilidad
    public Producto recuperar(String codigo) { return dic.recuperar(codigo); }
    public boolean actualizarProducto(String busqueda, Producto p) { return dic.modificar(busqueda, p); }
}
