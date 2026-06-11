package modelo;

public class Producto {
    private String codigo;
    private String nombre;
    private int cantidadStock;
    private String ubicacion; // ej: "Pasillo A | Estante 2 | Nivel 1"

    public Producto(String codigo, String nombre, int cantidadStock, String ubicacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidadStock = cantidadStock;
        this.ubicacion = ubicacion;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCantidadStock() { return cantidadStock; }
    public String getUbicacion() { return ubicacion; }

    public void setCantidadStock(int cantidadStock) { this.cantidadStock = cantidadStock; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " | stock: " + cantidadStock + " | " + ubicacion;
    }
}
