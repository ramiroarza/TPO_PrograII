package tda;

public class ArbolAVL implements IArbolAVL {
    private NodoAVL raiz;

    public ArbolAVL() {
        this.raiz = null;
    }

    @Override
    public NodoAVL getRaiz() { return raiz; }

    private int altura(NodoAVL nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private int obtenerBalance(NodoAVL nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private NodoAVL rotarDerecha(NodoAVL y) {
        NodoAVL x = y.izquierdo;
        NodoAVL temp = x.derecho;
        x.derecho = y;
        y.izquierdo = temp;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        return x;
    }

    private NodoAVL rotarIzquierda(NodoAVL x) {
        NodoAVL y = x.derecho;
        NodoAVL temp = y.izquierdo;
        y.izquierdo = x;
        x.derecho = temp;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        return y;
    }

    @Override
    public void insertar(int dato) {
        raiz = insertarRec(raiz, dato);
    }

    private NodoAVL insertarRec(NodoAVL nodo, int dato) {
        if (nodo == null) return new NodoAVL(dato);
        if (dato < nodo.dato) nodo.izquierdo = insertarRec(nodo.izquierdo, dato);
        else if (dato > nodo.dato) nodo.derecho = insertarRec(nodo.derecho, dato);
        else { nodo.cantidad++; return nodo; } // duplicado: no creamos nodo, aumentamos el contador

        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        int balance = obtenerBalance(nodo);

        if (balance > 1 && dato < nodo.izquierdo.dato) return rotarDerecha(nodo);
        if (balance < -1 && dato > nodo.derecho.dato) return rotarIzquierda(nodo);
        if (balance > 1 && dato > nodo.izquierdo.dato) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }
        if (balance < -1 && dato < nodo.derecho.dato) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    @Override
    public void eliminar(int dato) {
        raiz = eliminarRec(raiz, dato);
    }

    private NodoAVL eliminarRec(NodoAVL nodo, int dato) {
        if (nodo == null) return null;
        if (dato < nodo.dato) nodo.izquierdo = eliminarRec(nodo.izquierdo, dato);
        else if (dato > nodo.dato) nodo.derecho = eliminarRec(nodo.derecho, dato);
        else {
            if (nodo.cantidad > 1) { nodo.cantidad--; return nodo; } // aun quedan productos con este stock
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho == null) return nodo.izquierdo;
            NodoAVL suc = nodo.derecho;
            while (suc.izquierdo != null) suc = suc.izquierdo;
            nodo.dato = suc.dato;
            nodo.cantidad = suc.cantidad; // heredamos el contador del sucesor
            suc.cantidad = 1;             // forzamos la baja fisica real del sucesor
            nodo.derecho = eliminarRec(nodo.derecho, suc.dato);
        }
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        int b = obtenerBalance(nodo);
        if (b > 1 && obtenerBalance(nodo.izquierdo) >= 0) return rotarDerecha(nodo);
        if (b > 1 && obtenerBalance(nodo.izquierdo) < 0) { nodo.izquierdo = rotarIzquierda(nodo.izquierdo); return rotarDerecha(nodo); }
        if (b < -1 && obtenerBalance(nodo.derecho) <= 0) return rotarIzquierda(nodo);
        if (b < -1 && obtenerBalance(nodo.derecho) > 0) { nodo.derecho = rotarDerecha(nodo.derecho); return rotarIzquierda(nodo); }
        return nodo;
    }

    @Override
    public int minimo() {
        if (raiz == null) { System.out.println("El arbol esta vacio."); return -1; }
        NodoAVL aux = raiz;
        while (aux.izquierdo != null) aux = aux.izquierdo;
        return aux.dato;
    }

    @Override
    public void mostrarInorden(NodoAVL nodo) {
        if (nodo == null) return;
        mostrarInorden(nodo.izquierdo);
        for (int i = 0; i < nodo.cantidad; i++) System.out.print(nodo.dato + " ");
        mostrarInorden(nodo.derecho);
    }

    @Override
    public void mostrarPreorden(NodoAVL nodo) {
        if (nodo == null) return;
        System.out.print(nodo.dato + " ");
        mostrarPreorden(nodo.izquierdo);
        mostrarPreorden(nodo.derecho);
    }

    @Override
    public void mostrarPostorden(NodoAVL nodo) {
        if (nodo == null) return;
        mostrarPostorden(nodo.izquierdo);
        mostrarPostorden(nodo.derecho);
        System.out.print(nodo.dato + " ");
    }
}
