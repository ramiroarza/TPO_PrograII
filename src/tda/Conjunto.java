package tda;

// lo usamos para marcar nodos visitados en el BFS del grafo
public class Conjunto implements IConjunto {
    private int[] datos;
    private int cantidad;
    private int dimension;

    public Conjunto(int dimension) {
        this.dimension = dimension;
        this.datos = new int[dimension];
        this.cantidad = 0;
    }

    @Override
    public boolean estaVacio() { return cantidad == 0; }

    @Override
    public boolean estaLleno() { return cantidad == dimension; }

    @Override
    public void insertar(int elemento) {
        if (estaLleno()) { System.out.println("Conjunto lleno."); return; }
        if (pertenece(elemento) != -1) return;
        datos[cantidad] = elemento;
        cantidad++;
    }

    @Override
    public void eliminar(int elemento) {
        int pos = pertenece(elemento);
        if (pos == -1) { System.out.println("El elemento no existe."); return; }
        for (int i = pos; i < cantidad - 1; i++) datos[i] = datos[i + 1];
        cantidad--;
    }

    @Override
    public int pertenece(int elemento) {
        for (int i = 0; i < cantidad; i++) if (datos[i] == elemento) return i;
        return -1;
    }

    @Override
    public int tamanio() { return cantidad; }

    @Override
    public void mostrar() {
        System.out.print("{ ");
        for (int i = 0; i < cantidad; i++) System.out.print(datos[i] + " ");
        System.out.println("}");
    }
}
