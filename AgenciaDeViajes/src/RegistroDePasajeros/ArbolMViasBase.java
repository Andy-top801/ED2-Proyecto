package RegistroDePasajeros;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andy
 * @param <T>
 */
public abstract class ArbolMViasBase<T extends Comparable<T>> {

    protected static class NodoMVias<T> {
        List<T> datos;
        List<NodoMVias<T>> hijos;

        NodoMVias(int orden) {
            datos = new ArrayList<>();
            hijos = new ArrayList<>();
            for (int i = 0; i < orden - 1; i++) {
                datos.add((T) DATO_VACIO);
                hijos.add(NODO_VACIO);
            }
            hijos.add(NODO_VACIO);
        }

        NodoMVias(int orden, T primerDato) {
            this(orden); // hace referencia al constructor NodoMVias(int orden)
            datos.set(0, primerDato);
            /**
             * Por qué no un add y por qué un set??, porque en el constructor llenamos los
             * datos con valores nulos
             * y necesitamos remplazar los valores que ya fueron rellenados en esa posición
             */
        }
    }

    protected NodoMVias<T> raiz;
    protected int orden;
    protected static final NodoMVias DATO_VACIO = null;
    protected static final NodoMVias NODO_VACIO = null;
    protected static final int POSICION_INVALIDA = -1;
    protected static final int ORDEN_MINIMO = 3;

    protected boolean esArbolVacio(){
        return esNodoVacio(raiz);
    }

    protected boolean esNodoVacio(NodoMVias<T> elNodo) {
        return elNodo == NODO_VACIO;
    }

    protected NodoMVias<T> crearNodo(T dato) {
        return new NodoMVias<>(this.orden, dato);
    }

    protected boolean esDatoVacio(T elDato) {
        return elDato == DATO_VACIO;
    }

    protected int nroDeDatosNoVacios(NodoMVias<T> elNodo) {
        int cantidadPosicion = 0;
        while (cantidadPosicion < elNodo.datos.size() &&
                !esDatoVacio(elNodo.datos.get(cantidadPosicion))) {
            cantidadPosicion++;
        }
        return cantidadPosicion;
    }

    protected boolean esNodoHoja(NodoMVias<T> elNodo) {
        for (int i = 0; i <= nroDeDatosNoVacios(elNodo); i++) {
            if (!esNodoVacio(elNodo.hijos.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected boolean estanLosDatosLlenos(NodoMVias<T> nodoEnTurno) {
        return nroDeDatosNoVacios(nodoEnTurno) >= orden - 1;
    }

    protected int obtenerPosicionDeDatoEnNodo(NodoMVias<T> nodoActual, T dato) {
        // ESCUCHAR AUDIOS DEL INGENIERO
        for (int i = 0; i < nroDeDatosNoVacios(nodoActual); i++) {
            if (dato.equals(nodoActual.datos.get(i))) {
                return i;
            }
        }
        return POSICION_INVALIDA;
        /**
         * ¿CUANDO USAMOS equals?
         * ¿CUANDO USAMOS compareTo?
         * Cuando son objetos.
         * ¿CUANDO USAMOS ==?
         * Cuando son del mismo tipo de objeto.
         */
    }

    protected int obtenerPosicionPorDondeBajar(NodoMVias<T> nodoActual, T dato) {
        for (int i = 0; i < nroDeDatosNoVacios(nodoActual); i++) {
            if (dato.compareTo(nodoActual.datos.get(i)) < 0) {
                return i;
            }
        }
        return nroDeDatosNoVacios(nodoActual);
    }
}