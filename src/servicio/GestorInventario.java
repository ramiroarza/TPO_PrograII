package servicio;

import tda.ArbolAVL;
import tda.Lista;

public class GestorInventario {
    private ArbolAVL arbol = new ArbolAVL();

    public void agregarProducto(int stock) { arbol.insertar(stock); }
    public void eliminarProducto(int stock) { arbol.eliminar(stock); }

    public void actualizar(int stockViejo, int stockNuevo) {
        arbol.eliminar(stockViejo);
        arbol.insertar(stockNuevo);
    }

    public int obtenerMinimo() { return arbol.minimo(); }
    public boolean estaVacio() { return arbol.getRaiz() == null; }

    public void mostrarOrdenado() {
        if (arbol.getRaiz() == null) { System.out.println("No hay productos registrados"); return; }
        System.out.print("Stocks (de menor a mayor): ");
        arbol.mostrarInorden(arbol.getRaiz());
        System.out.println();
    }

    public Lista<Integer> stocksBajoUmbral(int umbral) { return arbol.stocksMenoresOIgual(umbral); }
}