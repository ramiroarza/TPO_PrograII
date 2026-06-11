package tda;

public interface IConjunto {
    boolean estaVacio();
    boolean estaLleno();
    void insertar(int elemento);
    void eliminar(int elemento);
    int pertenece(int elemento);
    int tamanio();
    void mostrar();
}
