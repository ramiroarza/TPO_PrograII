package tda;

public class EntradaDiccionario<T> {
    ClaveProducto clave;
    T valor;
    EntradaDiccionario<T> siguiente;

    public EntradaDiccionario(ClaveProducto clave, T valor) {
        this.clave = clave;
        this.valor = valor;
        this.siguiente = null;
    }
}
