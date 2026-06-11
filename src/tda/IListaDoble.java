package tda;

public interface IListaDoble<T> {
    void insertarInicio(T dato);
    void insertarFinal(T dato);
    void mostrarAdelante();
    void mostrarAtras();
    boolean buscar(T dato);
    void eliminarInicio();
    void eliminarFinal();
    void eliminarPorValor(T dato);
    boolean estaVacia();
}
