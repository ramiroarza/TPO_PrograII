package tda;

public class DiccionarioProducto<T> implements IDiccionarioProducto<T> {
    private EntradaDiccionario<T>[] porCodigo;
    private EntradaDiccionario<T>[] porNombre;
    private int cantidad;
    private int dimension;

    @SuppressWarnings("unchecked")
    public DiccionarioProducto(int dimension) {
        this.dimension = dimension <= 0 ? 31 : dimension;
        this.porCodigo = new EntradaDiccionario[this.dimension];
        this.porNombre = new EntradaDiccionario[this.dimension];
        this.cantidad = 0;
    }


    private int hash(String clave) {
        int suma = 0;
        String c = clave.toLowerCase();
        for (int i = 0; i < c.length(); i++) suma += c.charAt(i);
        return Math.abs(suma) % dimension;
    }

    @Override
    public boolean insertar(String codigo, String nombre, T valor) {
        if (existePorCodigo(codigo)) { System.out.println("Ya existe un producto con el codigo '" + codigo + "'."); return false; }
        if (existePorNombre(nombre)) { System.out.println("Ya existe un producto con el nombre '" + nombre + "'."); return false; }

        ClaveProducto clave = new ClaveProducto(codigo, nombre);

        int hc = hash(codigo);
        EntradaDiccionario<T> eCod = new EntradaDiccionario<>(clave, valor);
        eCod.siguiente = porCodigo[hc];
        porCodigo[hc] = eCod;

        int hn = hash(nombre);
        EntradaDiccionario<T> eNom = new EntradaDiccionario<>(clave, valor);
        eNom.siguiente = porNombre[hn];
        porNombre[hn] = eNom;

        cantidad++;
        System.out.println("Insertado: (" + codigo + " : " + nombre + ") -> " + valor);
        return true;
    }

    private EntradaDiccionario<T> buscarPorCodigo(String codigo) {
        if (codigo == null) return null;
        EntradaDiccionario<T> actual = porCodigo[hash(codigo)];
        while (actual != null) {
            if (actual.clave.getCodigo().equalsIgnoreCase(codigo)) return actual;
            actual = actual.siguiente;
        }
        return null;
    }

    private EntradaDiccionario<T> buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        EntradaDiccionario<T> actual = porNombre[hash(nombre)];
        while (actual != null) {
            if (actual.clave.getNombre().equalsIgnoreCase(nombre)) return actual;
            actual = actual.siguiente;
        }
        return null;
    }

    @Override
    public T recuperar(String busqueda) {
        EntradaDiccionario<T> e = buscarPorCodigo(busqueda);
        if (e == null) e = buscarPorNombre(busqueda); // si no es codigo, probamos por nombre
        if (e == null) { System.out.println("No se encontro ningun producto con: '" + busqueda + "'."); return null; }
        return e.valor;
    }

    @Override
    public boolean modificar(String busqueda, T nuevoValor) {
        EntradaDiccionario<T> eCod = buscarPorCodigo(busqueda);
        EntradaDiccionario<T> eNom = buscarPorNombre(busqueda);
        if (eCod == null && eNom == null) { System.out.println("No se encontro el producto: '" + busqueda + "'."); return false; }
        ClaveProducto clave = eCod != null ? eCod.clave : eNom.clave;
        EntradaDiccionario<T> porC = buscarPorCodigo(clave.getCodigo());
        EntradaDiccionario<T> porN = buscarPorNombre(clave.getNombre());
        if (porC != null) porC.valor = nuevoValor;
        if (porN != null) porN.valor = nuevoValor;
        System.out.println("Modificado: " + clave + " -> " + nuevoValor);
        return true;
    }

    @Override
    public boolean eliminar(String busqueda) {
        EntradaDiccionario<T> e = buscarPorCodigo(busqueda);
        if (e == null) e = buscarPorNombre(busqueda);
        if (e == null) { System.out.println("No se encontro el producto: '" + busqueda + "'."); return false; }
        ClaveProducto clave = e.clave;
        quitarDeTabla(porCodigo, hash(clave.getCodigo()), clave.getCodigo(), true);
        quitarDeTabla(porNombre, hash(clave.getNombre()), clave.getNombre(), false);
        cantidad--;
        System.out.println("Eliminado: " + clave);
        return true;
    }

    private void quitarDeTabla(EntradaDiccionario<T>[] tabla, int pos, String clave, boolean porCod) {
        EntradaDiccionario<T> actual = tabla[pos], anterior = null;
        while (actual != null) {
            String campo = porCod ? actual.clave.getCodigo() : actual.clave.getNombre();
            if (campo.equalsIgnoreCase(clave)) {
                if (anterior == null) tabla[pos] = actual.siguiente;
                else anterior.siguiente = actual.siguiente;
                return;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
    }

    @Override
    public boolean existePorCodigo(String codigo) { return buscarPorCodigo(codigo) != null; }

    @Override
    public boolean existePorNombre(String nombre) { return buscarPorNombre(nombre) != null; }

    @Override
    public boolean existe(String busqueda) { return buscarPorCodigo(busqueda) != null || buscarPorNombre(busqueda) != null; }

    @Override
    public boolean estaVacio() { return cantidad == 0; }

    @Override
    public int tamanio() { return cantidad; }

    @Override
    public void mostrar() {
        if (estaVacio()) { System.out.println("El diccionario esta vacio."); return; }
        System.out.println("=== Productos registrados ===");
        for (int i = 0; i < dimension; i++) {
            EntradaDiccionario<T> actual = porCodigo[i];
            while (actual != null) {
                System.out.println("  " + actual.clave + " -> " + actual.valor);
                actual = actual.siguiente;
            }
        }
    }

    @Override
    public T valorEn(int indice) {
        if (indice < 0 || indice >= cantidad) return null;
        int contados = 0;
        for (int i = 0; i < dimension; i++) {
            EntradaDiccionario<T> actual = porCodigo[i];
            while (actual != null) {
                if (contados == indice) return actual.valor;
                contados++;
                actual = actual.siguiente;
            }
        }
        return null;
    }
}
