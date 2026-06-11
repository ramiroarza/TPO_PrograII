package tda;

// representa donde esta fisicamente un producto en el deposito
public class Ubicacion {
    private String pasillo;
    private int estante;
    private int nivel;

    public Ubicacion(String pasillo, int estante, int nivel) {
        this.pasillo = pasillo;
        this.estante = estante;
        this.nivel = nivel;
    }

    public String getPasillo() { return pasillo; }
    public int getEstante() { return estante; }
    public int getNivel() { return nivel; }
    public void setPasillo(String pasillo) { this.pasillo = pasillo; }
    public void setEstante(int estante) { this.estante = estante; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    @Override
    public String toString() {
        return "Pasillo " + pasillo + " | Estante " + estante + " | Nivel " + nivel;
    }
}
