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
        Producto p = gestorStock.recuperar(m.getCodigoProducto());
        if (p == null) return false;
        int viejo = p.getCantidadStock();
        int nuevo;
        if (m.getTipo().equals("INGRESO")) nuevo = viejo + m.getCantidad();
        else if (m.getTipo().equals("EGRESO")) nuevo = viejo - m.getCantidad();
        else nuevo = viejo; // TRANSFERENCIA: no altera el stock total
        if (nuevo < 0) return false;
        historial.apilar(m);
        if (nuevo != viejo) {
            gestorInventario.actualizar(viejo, nuevo);
            p.setCantidadStock(nuevo);
            gestorStock.actualizarProducto(m.getCodigoProducto(), p);
        }
        return true;
    }

    // devuelve null si no se pudo deshacer (el metodo imprime la razon)
    public Movimiento deshacerUltimo() {
        if (historial.pilaVacia()) return null;
        Movimiento m = historial.desapilar();
        Producto p = gestorStock.recuperar(m.getCodigoProducto());
        if (p == null) {
            System.out.println("El producto '" + m.getCodigoProducto() + "' ya no existe. El movimiento fue retirado del historial.");
            return null;
        }
        int actual = p.getCantidadStock();
        int revertido;
        if (m.getTipo().equals("INGRESO")) revertido = actual - m.getCantidad();
        else if (m.getTipo().equals("EGRESO")) revertido = actual + m.getCantidad();
        else revertido = actual; // TRANSFERENCIA: no hubo cambio de stock
        if (revertido < 0) {
            historial.apilar(m);
            System.out.println("No se puede deshacer: el stock quedaria en negativo.");
            return null;
        }
        if (revertido != actual) {
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
