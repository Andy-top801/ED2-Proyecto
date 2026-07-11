package RegistroDePasajeros;

import java.util.List;

public interface Prub<T> {
    void insertar(T dato);

    void eliminar(T dato);

    T buscar(T dato);

    boolean contiene(T dato);

    int size();

    int altura();

    void vaciar();

    boolean esArbolVacio();

    int nivel();

    List<T> recorridoEnPreOrden();

    List<T> recorridoEnInOrden();

    List<T> recorridoEnPostOrden();

    List<T> recorridoPorNiveles();
}

