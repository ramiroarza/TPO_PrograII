package modelo;

public class Producto {
    private String codigo;
    private String nombre;
    private int cantidadStock;

    public Producto(String codigo, String nombre, int cantidadStock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidadStock = cantidadStock;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(int cant) { this.cantidadStock = cant; }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " | stock: " + cantidadStock;
    }
}
