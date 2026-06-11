package tda;

public interface IPila<T> {
    void apilar(T dato);
    T desapilar();
    T tope();
    int tamano();
    boolean pilaVacia();
    void mostrar(); // para ver el historial sin desapilar
}
