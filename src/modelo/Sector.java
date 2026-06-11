package modelo;

public class Sector {
    private int idSector;
    private String nombre;

    public Sector(int idSector, String nombre) {
        this.idSector = idSector;
        this.nombre = nombre;
    }

    public int getIdSector() { return idSector; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return "Sector " + idSector + ": " + nombre; }
}
