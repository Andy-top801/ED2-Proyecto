package ClasificacionDeViajes;

import java.util.ArrayList;
import java.util.List;

public abstract class Monticulo<T extends Comparable<T>> {
    private List<T> elementos;

    public Monticulo(){
        elementos = new ArrayList<>();
    }

    public Monticulo(List<T> lista){
        elementos = new ArrayList<>(lista);
        construirMonticulo();
    }

    public void insertar(T datoInsertar){
        if (datoInsertar == null){
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        elementos.add(datoInsertar);
        flotar(size()-1);

    }

    public boolean esMonticuloVacio(){
        return elementos.isEmpty();
    }

    public T verTope(){
        if (esMonticuloVacio()){
            throw new IllegalArgumentException("El monticulo se encuentra vacio");
        }
        return elementos.get(0);
    }

    public T get(int posicion){
        if (posicion < 0 || posicion >= size()){
            throw new IndexOutOfBoundsException("La posicion se encuentra fuera del rango del monticulo");
        }
        return elementos.get(posicion);
    }

    public int size(){
        return elementos.size();
    }

    public T eliminarTope(){
        if (esMonticuloVacio()){
            throw new IllegalArgumentException("El monticulo se encuentra vacio");
        }
        T tope = elementos.get(0);
        T ultimo = elementos.remove(size()-1);
        if (!esMonticuloVacio()){
            elementos.set(0,ultimo);
            hundir(0);
        }
        return tope;
    }

    protected abstract boolean seNecesitaIntercambiar(T datoDelPadre, T datoDelHijo);
    //el metodo nos indicara en cada clase individual si hay o no que intercambiar
    //ademas que realizara la verificacion de que ese dato no sea nulo para poder cambiar

    private void intercambiar(int posElementoA,int posElementoB){
        T datoA = elementos.get(posElementoA);
        T datoB = elementos.get(posElementoB);
        elementos.set(posElementoA,datoB);
        elementos.set(posElementoB,datoA);
    }

    private int getPosicionPadre(int posElemento){
        return (posElemento-1)/2;
    }

    private int getPosicionHijoDerecho(int posElemento){
        return (posElemento*2)+2;
    }

    private int getPosicionHijoIzquierdo(int posElemento){
        return (posElemento*2)+1;
    }

    private void flotar(int posElemento){
        while (posElemento > 0 ){
            int padre = getPosicionPadre(posElemento);
            T datoHijo = elementos.get(posElemento);
            T datoPadre = elementos.get(padre);
            if (seNecesitaIntercambiar(datoPadre,datoHijo)) {
                intercambiar(posElemento,padre);
                posElemento = padre;
            } else {
                posElemento = -1;
            }
        }
    }

    private void hundir(int posElementoPadre){
        int posNuevoPadre = posElementoPadre;
        while (posElementoPadre < size()){
            int hijoIzquierdo = getPosicionHijoIzquierdo(posElementoPadre);
            int hijoDerecho = getPosicionHijoDerecho(posElementoPadre);
            if (hijoIzquierdo < size() &&
                    seNecesitaIntercambiar(elementos.get(posNuevoPadre),elementos.get(hijoIzquierdo))) {
                posNuevoPadre = hijoIzquierdo;
            }
            if (hijoDerecho < size() &&
                    seNecesitaIntercambiar(elementos.get(posNuevoPadre),elementos.get(hijoDerecho))) {
                posNuevoPadre = hijoDerecho;
            }
            if (posElementoPadre != posNuevoPadre) {
                intercambiar(posElementoPadre,posNuevoPadre);
                posElementoPadre = posNuevoPadre;
            } else {
                posElementoPadre = size();
            }
        }
    }

    private void construirMonticulo(){
        for (int i = size()/2 -1; i >= 0; i--){
            hundir(i);
        }
    }

    public void reconstruir(){
        construirMonticulo();
    }

}
