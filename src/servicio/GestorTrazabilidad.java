package servicio;

import modelo.Movimiento;
import modelo.Producto;
import tda.pila;

public class GestorTrazabilidad {
    private pila<Movimiento> historial = new pila<>();
    private GestorStock gestorStock;
    private GestorInventario gestorInventario;

    public GestorTrazabilidad(GestorStock gestorStock, GestorInventario gestorInventario) {
        this.gestorStock = gestorStock;
        this.gestorInventario = gestorInventario;
    }

    public boolean registrar(Movimiento m) {
        if (m == null) return false;
        historial.apilar(m);
        Producto p = gestorStock.recuperar(m.getCodigoProducto());
        if (p != null) {
            int viejo = p.getCantidadStock();
            int nuevo = m.getTipo().equals("INGRESO") ? viejo + m.getCantidad() : viejo - m.getCantidad();
            if (nuevo < 0) { historial.desapilar(); return false; } // egreso invalido: revertimos el apilado
            gestorInventario.actualizar(viejo, nuevo);
            p.setCantidadStock(nuevo);
            gestorStock.actualizarProducto(m.getCodigoProducto(), p);
        }
        return true;
    }

    public Movimiento deshacerUltimo() {
        if (historial.pilaVacia()) return null;
        Movimiento m = historial.desapilar();
        Producto p = gestorStock.recuperar(m.getCodigoProducto());
        if (p != null) {
            int actual = p.getCantidadStock();
            int revertido = m.getTipo().equals("INGRESO") ? actual - m.getCantidad() : actual + m.getCantidad();
            gestorInventario.actualizar(actual, revertido);
            p.setCantidadStock(revertido);
            gestorStock.actualizarProducto(m.getCodigoProducto(), p);
        }
        return m;
    }

    public int cantidadMovimientos() { return historial.tamano(); }
    public boolean estaVacio() { return historial.pilaVacia(); }
    public void verHistorial() { historial.mostrar(); }
}
