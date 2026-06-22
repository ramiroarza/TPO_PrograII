package servicio;

import tda.Grafo;

public class GestorRutas {
    private Grafo<String> grafo = new Grafo<>(20, false);

    public void agregarSector(String nombre) { grafo.insertarVertice(nombre); }

    public boolean agregarPasillo(String a, String b, int peso) {
        if (!grafo.existeVertice(a) || !grafo.existeVertice(b)) return false;
        grafo.insertarArista(a, b, peso);
        return true;
    }

    public String[] calcularRuta(String origen, String destino) { return grafo.bfs(origen, destino); }
    public boolean hayConexion(String a, String b) { return grafo.existeArista(a, b); }
    public boolean existeSector(String nombre) { return grafo.existeVertice(nombre); }
    public void mostrarMapa() { grafo.mostrarVertices(); grafo.mostrarGrafo(); }
}
