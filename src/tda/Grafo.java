package tda;

public class Grafo<T> implements IGrafo<T> {

    private T[] vertices;
    private Lista<Arista>[] adyacencia;
    private int cantidad;
    private int capacidad;
    private boolean dirigido;

    @SuppressWarnings("unchecked")
    public Grafo(int capacidad, boolean dirigido) {
        this.capacidad = capacidad;
        this.dirigido = dirigido;
        this.cantidad = 0;
        this.vertices = (T[]) new Object[capacidad];
        this.adyacencia = new Lista[capacidad];
    }

    @Override
    public void insertarVertice(T vertice) {
        if (cantidad == capacidad) { System.out.println("No se pueden insertar mas vertices."); return; }
        if (existeVertice(vertice)) { System.out.println("El vertice ya existe."); return; }
        vertices[cantidad] = vertice;
        adyacencia[cantidad] = new Lista<>();
        cantidad++;
    }

    @Override
    public void eliminarVertice(T vertice) {
        int pos = obtenerIndice(vertice);
        if (pos == -1) { System.out.println("El vertice no existe."); return; }
        for (int i = 0; i < cantidad; i++) {
            if (i == pos) continue;
            quitarAristaHacia(i, pos);
        }
        for (int i = pos; i < cantidad - 1; i++) {
            vertices[i] = vertices[i + 1];
            adyacencia[i] = adyacencia[i + 1];
        }
        cantidad--;
        vertices[cantidad] = null;
        adyacencia[cantidad] = null;
        for (int i = 0; i < cantidad; i++) reindexar(adyacencia[i], pos);
    }

    @Override
    public void insertarArista(T origen, T destino, int peso) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) { System.out.println("Uno de los vertices no existe."); return; }
        if (buscarArista(o, d) == null) adyacencia[o].agregarFinal(new Arista(d, peso));
        if (!dirigido && buscarArista(d, o) == null) adyacencia[d].agregarFinal(new Arista(o, peso));
    }


    public void insertarArista(T origen, T destino) { insertarArista(origen, destino, 1); }

    @Override
    public void eliminarArista(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) { System.out.println("Uno de los vertices no existe."); return; }
        quitarAristaHacia(o, d);
        if (!dirigido) quitarAristaHacia(d, o);
    }

    @Override
    public boolean existeVertice(T vertice) { return obtenerIndice(vertice) != -1; }

    @Override
    public boolean existeArista(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) return false;
        return buscarArista(o, d) != null;
    }

    private int obtenerIndice(T vertice) {
        for (int i = 0; i < cantidad; i++)
            if (vertices[i].equals(vertice)) return i;
        return -1;
    }

    private Arista buscarArista(int origen, int destino) {
        Nodo<Arista> aux = adyacencia[origen].cabeza;
        while (aux != null) {
            if (aux.getDato().getDestino() == destino) return aux.getDato();
            aux = aux.getSiguiente();
        }
        return null;
    }

    private void quitarAristaHacia(int origen, int destino) {
        Arista a = buscarArista(origen, destino);
        if (a != null) adyacencia[origen].eliminar(a);
    }

    private void reindexar(Lista<Arista> lista, int pos) {
        Nodo<Arista> aux = lista.cabeza;
        while (aux != null) {
            if (aux.getDato().destino > pos) aux.getDato().destino--;
            aux = aux.getSiguiente();
        }
    }

    @Override
    public void mostrarVertices() {
        System.out.print("Sectores: ");
        for (int i = 0; i < cantidad; i++) System.out.print(vertices[i] + " ");
        System.out.println();
    }

    @Override
    public void mostrarGrafo() {
        System.out.println("Mapa del deposito (lista de adyacencia):");
        for (int i = 0; i < cantidad; i++) {
            System.out.print("  " + vertices[i] + " -> ");
            Nodo<Arista> aux = adyacencia[i].cabeza;
            if (aux == null) System.out.print("(sin conexiones)");
            while (aux != null) {
                System.out.print(vertices[aux.getDato().getDestino()] + "(peso " + aux.getDato().getPeso() + ") ");
                aux = aux.getSiguiente();
            }
            System.out.println();
        }
    }

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
            Nodo<Arista> aux = adyacencia[actual].cabeza;
            while (aux != null) {
                int v = aux.getDato().getDestino();
                if (visitados.pertenece(v) == -1) {
                    visitados.insertar(v);
                    padre[v] = actual;
                    cola.encolar(v);
                }
                aux = aux.getSiguiente();
            }
        }

        System.out.println("No hay camino entre " + origen + " y " + destino);
        return null;
    }

    @Override
    public String[] dijkstra(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) {
            System.out.println("Uno de los sectores no existe en el grafo.");
            return null;
        }
        int[] padre = new int[capacidad];
        int[] dist = calcularDistancias(o, padre);
        if (dist[d] == Integer.MAX_VALUE) {
            System.out.println("No hay camino entre " + origen + " y " + destino);
            return null;
        }
        return reconstruirCamino(padre, o, d);
    }

    @Override
    public int distancia(T origen, T destino) {
        int o = obtenerIndice(origen), d = obtenerIndice(destino);
        if (o == -1 || d == -1) return -1;
        int[] dist = calcularDistancias(o, null);
        return dist[d] == Integer.MAX_VALUE ? -1 : dist[d];
    }

    private int[] calcularDistancias(int origen, int[] padre) {
        int[] dist = new int[capacidad];
        boolean[] listo = new boolean[capacidad];
        for (int i = 0; i < capacidad; i++) {
            dist[i] = Integer.MAX_VALUE;
            if (padre != null) padre[i] = -1;
        }
        dist[origen] = 0;

        ColaPrioridad<Integer> pq = new ColaPrioridad<>();
        pq.encolar(origen, 0);

        while (!pq.estaVacia()) {
            int u = pq.desencolar();
            if (listo[u]) continue;
            listo[u] = true;
            Nodo<Arista> aux = adyacencia[u].cabeza;
            while (aux != null) {
                int v = aux.getDato().getDestino();
                int peso = aux.getDato().getPeso();
                if (!listo[v] && dist[u] != Integer.MAX_VALUE && dist[u] + peso < dist[v]) {
                    dist[v] = dist[u] + peso;
                    if (padre != null) padre[v] = u;
                    pq.encolar(v, dist[v]);
                }
                aux = aux.getSiguiente();
            }
        }
        return dist;
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
