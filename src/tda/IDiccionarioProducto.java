package tda;

public interface IDiccionarioProducto<T> {
    boolean insertar(String codigo, String nombre, T valor);
    boolean eliminar(String busqueda);
    boolean modificar(String busqueda, T nuevoValor);
    T recuperar(String busqueda);
    boolean existe(String busqueda);
    boolean existePorCodigo(String codigo);
    boolean existePorNombre(String nombre);
    boolean estaVacio();
    int tamanio();
    void mostrar();
}
