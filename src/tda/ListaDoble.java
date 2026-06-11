package tda;

public class ListaDoble<T> implements IListaDoble<T> {
    private NodoDoble<T> inicio;
    private NodoDoble<T> fin;
    private int cantidad;

    public ListaDoble() {
        this.inicio = null;
        this.fin = null;
        this.cantidad = 0;
    }

    @Override
    public void insertarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) { inicio = nuevo; fin = nuevo; }
        else { nuevo.siguiente = inicio; inicio.anterior = nuevo; inicio = nuevo; }
        cantidad++;
    }

    @Override
    public void insertarFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) { inicio = nuevo; fin = nuevo; }
        else { fin.siguiente = nuevo; nuevo.anterior = fin; fin = nuevo; }
        cantidad++;
    }

    @Override
    public void mostrarAdelante() {
        NodoDoble<T> actual = inicio;
        while (actual != null) { System.out.print(actual.dato + " "); actual = actual.siguiente; }
        System.out.println();
    }

    @Override
    public void mostrarAtras() {
        NodoDoble<T> actual = fin;
        while (actual != null) { System.out.print(actual.dato + " "); actual = actual.anterior; }
        System.out.println();
    }

    @Override
    public boolean buscar(T dato) {
        NodoDoble<T> actual = inicio;
        while (actual != null) {
            if (actual.dato.equals(dato)) return true;
            actual = actual.siguiente;
        }
        return false;
    }

    @Override
    public void eliminarInicio() {
        if (estaVacia()) { System.out.println("Lista vacia."); return; }
        if (inicio == fin) { inicio = null; fin = null; }
        else { inicio = inicio.siguiente; inicio.anterior = null; }
        cantidad--;
    }

    @Override
    public void eliminarFinal() {
        if (estaVacia()) { System.out.println("Lista vacia."); return; }
        if (inicio == fin) { inicio = null; fin = null; }
        else { fin = fin.anterior; fin.siguiente = null; }
        cantidad--;
    }

    @Override
    public void eliminarPorValor(T dato) {
        if (estaVacia()) { System.out.println("Lista vacia."); return; }
        NodoDoble<T> actual = inicio;
        while (actual != null && !actual.dato.equals(dato)) actual = actual.siguiente;
        if (actual == null) { System.out.println("El dato no esta en la lista."); return; }
        if (actual == inicio) eliminarInicio();
        else if (actual == fin) eliminarFinal();
        else { actual.anterior.siguiente = actual.siguiente; actual.siguiente.anterior = actual.anterior; cantidad--; }
    }

    @Override
    public boolean estaVacia() { return inicio == null && fin == null; }

    public int getCantidad() { return cantidad; }
    public NodoDoble<T> getInicio() { return inicio; }
    public NodoDoble<T> getFin() { return fin; }
}
