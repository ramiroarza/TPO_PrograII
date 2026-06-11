package tda;

public class pila<T> implements IPila<T> {
    // lista enlazada para generics sin limite fijo de tamaño
    private Nodo<T> tope;
    private int cantidad;

    public pila() {
        this.tope = null;
        this.cantidad = 0;
    }

    @Override
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        cantidad++;
    }

    @Override
    public T desapilar() {
        if (pilaVacia()) { System.out.println("La pila esta vacia."); return null; }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        cantidad--;
        return dato;
    }

    @Override
    public T tope() {
        if (pilaVacia()) { System.out.println("Pila vacia."); return null; }
        return tope.getDato();
    }

    @Override
    public int tamano() { return cantidad; }

    @Override
    public boolean pilaVacia() { return tope == null; }

    @Override
    public void mostrar() {
        if (pilaVacia()) { System.out.println("Pila vacia."); return; }
        Nodo<T> aux = tope;
        while (aux != null) {
            System.out.println("  " + aux.getDato());
            aux = aux.getSiguiente();
        }
    }
}
