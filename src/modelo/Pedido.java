package modelo;

public class Pedido {
    private int idPedido;
    private String descripcion;
    private int prioridad;

    public Pedido(int idPedido, String descripcion, int prioridad) {
        this.idPedido = idPedido;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    public int getIdPedido() { return idPedido; }
    public String getDescripcion() { return descripcion; }
    public int getPrioridad() { return prioridad; }

    @Override
    public String toString() {
        return "Pedido #" + idPedido + ": " + descripcion;
    }
}
