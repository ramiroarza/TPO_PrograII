package tda;

public class EntradaDiccionario<T> {
    ClaveProducto clave;
    T valor;

    public EntradaDiccionario(ClaveProducto clave, T valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
