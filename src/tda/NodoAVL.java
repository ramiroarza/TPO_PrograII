package tda;

public class NodoAVL {
    int dato;
    int altura;
    NodoAVL izquierdo;
    NodoAVL derecho;

    public NodoAVL(int dato) {
        this.dato = dato;
        this.altura = 1;
        this.izquierdo = null;
        this.derecho = null;
    }
}
