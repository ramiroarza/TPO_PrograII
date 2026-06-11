package tda;

public class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public void setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
    public void setDato(T dato) { this.dato = dato; }
    public T getDato() { return dato; }
    public Nodo<T> getSiguiente() { return siguiente; }
}
