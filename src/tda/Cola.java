package tda;

public class Cola<T> implements ICola<T> {
    // frente = donde se desencola, fin = donde se encola
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int cantidad;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.cantidad = 0;
    }

    @Override
    public void encolar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) { frente = nuevo; fin = nuevo; }
        else { fin.setSiguiente(nuevo); fin = nuevo; }
        cantidad++;
    }

    @Override
    public T desencolar() {
        if (estaVacia()) { System.out.println("La cola esta vacia."); return null; }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) fin = null;
        cantidad--;
        return dato;
    }

    @Override
    public T frente() {
        if (estaVacia()) { System.out.println("Cola vacia."); return null; }
        return frente.getDato();
    }

    @Override
    public boolean estaVacia() { return frente == null; }

    @Override
    public int tamano() { return cantidad; }
}
