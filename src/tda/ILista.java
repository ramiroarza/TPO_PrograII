package tda;

public interface ILista<T> {
    void agregarInicio(T dato);
    void agregarFinal(T dato);
    void eliminar(T dato);
    boolean buscar(T dato);
    boolean estaVacia();
    void mostrar();
}
