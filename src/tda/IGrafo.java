package tda;

public interface IGrafo<T> {
    void insertarVertice(T vertice);
    void eliminarVertice(T vertice);
    void insertarArista(T origen, T destino, int peso);
    void eliminarArista(T origen, T destino);
    boolean existeVertice(T vertice);
    boolean existeArista(T origen, T destino);
    void mostrarVertices();
    void mostrarGrafo();
    String[] bfs(T origen, T destino);
    String[] dijkstra(T origen, T destino);
    int distancia(T origen, T destino);
}