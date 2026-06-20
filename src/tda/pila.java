package tda;

public class pila<T> implements IPila<T> {
    private ListaDoble<T> lista;

    public pila() {
        this.lista = new ListaDoble<>();
    }

    @Override
    public void apilar(T dato) {
        lista.insertarFinal(dato); // el tope es siempre el fin de la lista
    }

    @Override
    public T desapilar() {
        if (pilaVacia()) { System.out.println("La pila esta vacia."); return null; }
        T dato = lista.getFin().dato;
        lista.eliminarFinal();
        return dato;
    }

    @Override
    public T tope() {
        if (pilaVacia()) { System.out.println("Pila vacia."); return null; }
        return lista.getFin().dato;
    }

    @Override
    public int tamano() { return lista.getCantidad(); }

    @Override
    public boolean pilaVacia() { return lista.estaVacia(); }

    @Override
    public void mostrar() {
        if (pilaVacia()) { System.out.println("Pila vacia."); return; }
        lista.mostrarAtras(); // del tope (fin) hacia el inicio: ultimo movimiento primero
    }
}
