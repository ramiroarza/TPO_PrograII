package tda;

public class ColaPrioridad<T> implements IColaPrioridad<T> {

    private T[] elementos;
    private int[] prioridades;
    private int cantidad;
    private int capacidad;

    @SuppressWarnings("unchecked")
    public ColaPrioridad() {
        this.capacidad = 16;
        this.cantidad = 0;
        this.elementos = (T[]) new Object[capacidad];
        this.prioridades = new int[capacidad];
    }

    @Override
    public void encolar(T elemento, int prioridad) {
        if (cantidad == capacidad) redimensionar();
        elementos[cantidad] = elemento;
        prioridades[cantidad] = prioridad;
        flotar(cantidad);
        cantidad++;
    }

    @Override
    public T desencolar() {
        if (estaVacia()) return null;
        T raiz = elementos[0];
        cantidad--;
        elementos[0] = elementos[cantidad];
        prioridades[0] = prioridades[cantidad];
        elementos[cantidad] = null;
        hundir(0);
        return raiz;
    }

    @Override
    public T frente() { return estaVacia() ? null : elementos[0]; }

    @Override
    public boolean estaVacia() { return cantidad == 0; }

    @Override
    public int tamano() { return cantidad; }

    private void flotar(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (prioridades[i] < prioridades[padre]) { intercambiar(i, padre); i = padre; }
            else break;
        }
    }

    private void hundir(int i) {
        while (true) {
            int izq = 2 * i + 1, der = 2 * i + 2, menor = i;
            if (izq < cantidad && prioridades[izq] < prioridades[menor]) menor = izq;
            if (der < cantidad && prioridades[der] < prioridades[menor]) menor = der;
            if (menor == i) break;
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        T tmp = elementos[a]; elementos[a] = elementos[b]; elementos[b] = tmp;
        int p = prioridades[a]; prioridades[a] = prioridades[b]; prioridades[b] = p;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        capacidad *= 2;
        T[] ne = (T[]) new Object[capacidad];
        int[] np = new int[capacidad];
        for (int i = 0; i < cantidad; i++) { ne[i] = elementos[i]; np[i] = prioridades[i]; }
        elementos = ne;
        prioridades = np;
    }
}
