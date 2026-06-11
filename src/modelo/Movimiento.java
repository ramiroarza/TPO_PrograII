package modelo;

// tipos validos: INGRESO, EGRESO, TRANSFERENCIA
public class Movimiento {
    private String tipo;
    private String codigoProducto;
    private int cantidad;
    private String fecha;

    public Movimiento(String tipo, String codigoProducto, int cantidad, String fecha) {
        this.tipo = tipo;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public String getTipo() { return tipo; }
    public String getCodigoProducto() { return codigoProducto; }
    public int getCantidad() { return cantidad; }
    public String getFecha() { return fecha; }

    @Override
    public String toString() {
        return tipo + " | " + codigoProducto + " | cant: " + cantidad + " | " + fecha;
    }
}
