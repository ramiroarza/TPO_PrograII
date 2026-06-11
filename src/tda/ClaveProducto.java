package tda;

public class ClaveProducto {
    private String codigo;
    private String nombre;

    public ClaveProducto(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }

    // permite buscar por codigo O por nombre indistintamente
    public boolean coincide(String busqueda) {
        return codigo.equalsIgnoreCase(busqueda) || nombre.equalsIgnoreCase(busqueda);
    }

    @Override
    public String toString() { return "(" + codigo + " : " + nombre + ")"; }
}
