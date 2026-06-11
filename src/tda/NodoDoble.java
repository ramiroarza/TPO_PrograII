package tda;

public class NodoDoble<T> {
    T dato;
    NodoDoble<T> anterior;
    NodoDoble<T> siguiente;

    public NodoDoble(T dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }
}
