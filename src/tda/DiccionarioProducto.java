package tda;

public class DiccionarioProducto<T> implements IDiccionarioProducto<T> {
    private EntradaDiccionario<T>[] entradas;
    private int cantidad;
    private int dimension;

    @SuppressWarnings("unchecked")
    public DiccionarioProducto(int dimension) {
        this.dimension = dimension;
        this.entradas = new EntradaDiccionario[dimension];
        this.cantidad = 0;
    }

    @Override
    public boolean insertar(String codigo, String nombre, T valor) {
        if (cantidad == dimension) { System.out.println("Diccionario lleno."); return false; }
        if (existePorCodigo(codigo)) { System.out.println("Ya existe un producto con el codigo '" + codigo + "'."); return false; }
        if (existePorNombre(nombre)) { System.out.println("Ya existe un producto con el nombre '" + nombre + "'."); return false; }
        entradas[cantidad] = new EntradaDiccionario<>(new ClaveProducto(codigo, nombre), valor);
        cantidad++;
        System.out.println("Insertado: (" + codigo + " : " + nombre + ") -> " + valor);
        return true;
    }

    private int buscarPosicion(String busqueda) {
        for (int i = 0; i < cantidad; i++)
            if (entradas[i].clave.coincide(busqueda)) return i;
        return -1;
    }

    @Override
    public T recuperar(String busqueda) {
        int pos = buscarPosicion(busqueda);
        if (pos == -1) { System.out.println("No se encontro ningun producto con: '" + busqueda + "'."); return null; }
        return entradas[pos].valor;
    }

    @Override
    public boolean modificar(String busqueda, T nuevoValor) {
        int pos = buscarPosicion(busqueda);
        if (pos == -1) { System.out.println("No se encontro el producto: '" + busqueda + "'."); return false; }
        entradas[pos].valor = nuevoValor;
        System.out.println("Modificado: " + entradas[pos].clave + " -> " + nuevoValor);
        return true;
    }

    @Override
    public boolean eliminar(String busqueda) {
        int pos = buscarPosicion(busqueda);
        if (pos == -1) { System.out.println("No se encontro el producto: '" + busqueda + "'."); return false; }
        System.out.println("Eliminado: " + entradas[pos].clave);
        for (int i = pos; i < cantidad - 1; i++) entradas[i] = entradas[i + 1];
        entradas[cantidad - 1] = null;
        cantidad--;
        return true;
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        for (int i = 0; i < cantidad; i++)
            if (entradas[i].clave.getCodigo().equalsIgnoreCase(codigo)) return true;
        return false;
    }

    @Override
    public boolean existePorNombre(String nombre) {
        for (int i = 0; i < cantidad; i++)
            if (entradas[i].clave.getNombre().equalsIgnoreCase(nombre)) return true;
        return false;
    }

    @Override
    public boolean existe(String busqueda) { return buscarPosicion(busqueda) != -1; }

    @Override
    public boolean estaVacio() { return cantidad == 0; }

    @Override
    public int tamanio() { return cantidad; }

    @Override
    public void mostrar() {
        if (estaVacio()) { System.out.println("El diccionario esta vacio."); return; }
        System.out.println("=== Productos registrados ===");
        for (int i = 0; i < cantidad; i++)
            System.out.println("  " + entradas[i].clave + " -> " + entradas[i].valor);
    }
}
