package tda;

public interface ICola<T> {
    void encolar(T elemento);
    T desencolar();
    T frente();
    boolean estaVacia();
    int tamano();
}
