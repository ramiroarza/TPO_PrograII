package tda;

public class Grafo<T> implements IGrafo<T> {

    private T[] vertices;
    private int[][] matriz;
    private int cantidad;
    private int capacidad;
    private boolean dirigido;

    @SuppressWarnings("unchecked")
    public Grafo(int capacidad, boolean dirigido) {
        this.capacidad = capacidad;
        this.dirigido = dirigido;
        this.cantidad = 0;
        this.vertices = (T[]) new Object[capacidad];
        this.matriz = new int[capacidad][capacidad];
    }

    @Override
    public void insertarVertice(T vertice) {
        if (cantidad == capacidad) { System.out.println("No se pueden insertar mas vertices."); return; }
        if (existeVertice(vertice)) { System.out.println("El vertice ya existe."); return; }
        vertices[cantidad] = vertice;
        cantidad++;
    }

    @Override
    public void eliminarVertice(T vertice) {
        int pos = obtenerIndice(vertice);
        if (pos == -1) { System.out.println("El vertice no existe."); return; }
        for (int i = pos; i < cantidad - 1; i++) vertices[i] = vertices[i + 1];
        for (int i = pos; i < cantidad - 1; i++)
            for (int j = 0; j < cantidad; j++) matriz[i][j] = matriz[i + 1][j];
        for (int j = pos; j < cantidad - 1; j++)
            for (int i = 0; i < cantidad; i++) matriz[i][j] = matriz[i][j + 1];
        cantidad--;
        vertices[cantidad] = null;
        for (int i = 0; i < capacidad; i++) { matriz[cantidad][i] = 0; matriz[i][cantidad] = 0; }
    }

    @Override
    public void insertarArista(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) { System.out.println("Uno de los vertices no existe."); return; }
        matriz[o][d] = 1;
        if (!dirigido) matriz[d][o] = 1;
    }

    @Override
    public void eliminarArista(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) { System.out.println("Uno de los vertices no existe."); return; }
        matriz[o][d] = 0;
        if (!dirigido) matriz[d][o] = 0;
    }

    @Override
    public boolean existeVertice(T vertice) { return obtenerIndice(vertice) != -1; }

    @Override
    public boolean existeArista(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) return false;
        return matriz[o][d] == 1;
    }

    private int obtenerIndice(T vertice) {
        for (int i = 0; i < cantidad; i++)
            if (vertices[i].equals(vertice)) return i;
        return -1;
    }

    @Override
    public void mostrarVertices() {
        System.out.print("Sectores: ");
        for (int i = 0; i < cantidad; i++) System.out.print(vertices[i] + " ");
        System.out.println();
    }

    @Override
    public void mostrarMatriz() {
        System.out.println("Mapa del deposito:");
        System.out.print("    ");
        for (int i = 0; i < cantidad; i++) System.out.printf("%-5s", vertices[i]);
        System.out.println();
        for (int i = 0; i < cantidad; i++) {
            System.out.printf("%-5s", vertices[i]);
            for (int j = 0; j < cantidad; j++) System.out.printf("%-5d", matriz[i][j]);
            System.out.println();
        }
    }

    // BFS usando Cola<Integer> y Conjunto propios, sin java.util
    @Override
    public String[] bfs(T origen, T destino) {
        int posOrigen = obtenerIndice(origen);
        int posDestino = obtenerIndice(destino);
        if (posOrigen == -1 || posDestino == -1) {
            System.out.println("Uno de los sectores no existe en el grafo.");
            return null;
        }

        int[] padre = new int[capacidad];
        for (int i = 0; i < capacidad; i++) padre[i] = -1;

        Conjunto visitados = new Conjunto(capacidad);
        Cola<Integer> cola = new Cola<>();

        visitados.insertar(posOrigen);
        cola.encolar(posOrigen);

        while (!cola.estaVacia()) {
            int actual = cola.desencolar();
            if (actual == posDestino) return reconstruirCamino(padre, posOrigen, posDestino);
            for (int v = 0; v < cantidad; v++) {
                if (matriz[actual][v] == 1 && visitados.pertenece(v) == -1) {
                    visitados.insertar(v);
                    padre[v] = actual;
                    cola.encolar(v);
                }
            }
        }

        System.out.println("No hay camino entre " + origen + " y " + destino);
        return null;
    }

    private String[] reconstruirCamino(int[] padre, int origen, int destino) {
        int largo = 0;
        int actual = destino;
        while (actual != -1) {
            largo++;
            if (actual == origen) break;
            actual = padre[actual];
        }
        String[] camino = new String[largo];
        actual = destino;
        for (int i = largo - 1; i >= 0; i--) {
            camino[i] = vertices[actual].toString();
            if (padre[actual] != -1) actual = padre[actual];
        }
        return camino;
    }
}
