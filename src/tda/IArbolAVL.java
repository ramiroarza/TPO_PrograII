package tda;

public interface IArbolAVL {
    void insertar(int dato);
    void eliminar(int dato);
    int minimo();
    NodoAVL getRaiz();
    void mostrarInorden(NodoAVL nodo);
    void mostrarPreorden(NodoAVL nodo);
    void mostrarPostorden(NodoAVL nodo);
}
