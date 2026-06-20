package tda;

public class Arista {
    int destino;
    int peso;

    public Arista(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public int getDestino() { return destino; }
    public int getPeso() { return peso; }
}
