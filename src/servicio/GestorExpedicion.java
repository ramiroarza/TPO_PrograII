package servicio;

import modelo.Pedido;
import tda.Cola;

public class GestorExpedicion {
    private Cola<Pedido> cola = new Cola<>();

    public void encolarPedido(Pedido p) { cola.encolar(p); }
    public Pedido despachar() { return cola.estaVacia() ? null : cola.desencolar(); }
    public Pedido proximoPedido() { return cola.estaVacia() ? null : cola.frente(); }
    public int pedidosEnCola() { return cola.tamano(); }
    public boolean estaVacia() { return cola.estaVacia(); }
}
