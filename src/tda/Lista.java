package tda;

public class Lista<T> implements ILista<T> {
    Nodo<T> cabeza;

    public Lista() {
        this.cabeza = null;
    }

    @Override
    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
    }

    @Override
    public void agregarFinal(T dato) {
        if (estaVacia()) { agregarInicio(dato); return; }
        Nodo<T> nuevo = new Nodo<>(dato);
        Nodo<T> aux = cabeza;
        while (aux.getSiguiente() != null) aux = aux.getSiguiente();
        aux.setSiguiente(nuevo);
    }

    @Override
    public void eliminar(T dato) {
        if (estaVacia()) return;
        if (cabeza.getDato().equals(dato)) { cabeza = cabeza.getSiguiente(); return; }
        Nodo<T> aux = cabeza;
        while (aux.getSiguiente() != null && !aux.getSiguiente().getDato().equals(dato))
            aux = aux.getSiguiente();
        if (aux.getSiguiente() != null)
            aux.setSiguiente(aux.getSiguiente().getSiguiente());
    }

    @Override
    public boolean buscar(T dato) {
        Nodo<T> aux = cabeza;
        while (aux != null) {
            if (aux.getDato().equals(dato)) return true;
            aux = aux.getSiguiente();
        }
        return false;
    }

    @Override
    public boolean estaVacia() { return cabeza == null; }

    @Override
    public void mostrar() {
        if (estaVacia()) { System.out.println("Lista vacia."); return; }
        Nodo<T> aux = cabeza;
        while (aux != null) { System.out.print(aux.getDato() + " -> "); aux = aux.getSiguiente(); }
        System.out.println("null");
    }
}
