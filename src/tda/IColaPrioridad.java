package tda;

public interface IColaPrioridad<T> {
    void encolar(T elemento, int prioridad);
    T desencolar();
    T frente();
    boolean estaVacia();
    int tamano();
}