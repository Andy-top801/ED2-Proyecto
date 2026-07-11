/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RutasDeMenorCosto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jherargonzales
 * @param <T>
 */
public class Grafo_Pesado <T extends Comparable<T>> { 
    
    // PROPIEDADES 
    protected List<T> listaDeVertices; 
    protected List<List<AdyacenteConPeso>> listasDeAdyacencias;
    protected static final int NRO_VERTICE_INVALIDO = -1 ;
    
    
    
    //METODOS 
    
    //CONSTRUCTOR sin parametros *
    public Grafo_Pesado(){
        listaDeVertices = new ArrayList<>();
        listasDeAdyacencias = new ArrayList<>();
    }
    
    //CONSTRUCTOR CON parametro*
    public Grafo_Pesado(Iterable<T> vertices){ //iterable : se usa solo para manejar la lista no para anadir ni modificar la lista
        this();
        for (T unVertice : vertices) {
            insertarVertice(unVertice);
        }
    }
    
    
    // ------------------------------------ METODOS CON LOS VERTICES --------------------------------------------------------------//
    
    
    public void insertarVertice(T unVertice) {
        if (existeVertice(unVertice)) {
            throw new IllegalArgumentException("VERTICE YA EXISTE EN EL GRAFO");
        }
        listaDeVertices.add(unVertice); // ANADIMOS EL VERTICE A LA LISTA DE VERTICES
        List<AdyacenteConPeso> listaDeAdyacenciasDelVertice = new ArrayList<>(); // CREAMOS UNA  INSTANCIA DE LA LISTA DE ADYACENCIAS DEL NUEVO VERTICE
        listasDeAdyacencias.add(listaDeAdyacenciasDelVertice); // ANADIMOS LA INSTANCIA A LA LISTAS DE LISTAS DE ADYACENCIAS 
    }
    
    public void eliminarVertice(T unVertice){
        validarVertice(unVertice);
        int nroVerticeAEliminar = getNroDeVertice(unVertice);
        listaDeVertices.remove(nroVerticeAEliminar);
        listasDeAdyacencias.remove(nroVerticeAEliminar);
        AdyacenteConPeso adyacenteAEliminar = new AdyacenteConPeso(nroVerticeAEliminar);
        for ( List<AdyacenteConPeso>adyacentesDeUnVertice : listasDeAdyacencias) { // en cada iteracion obtenemos la lista de adyacencias de unVertice 
           adyacentesDeUnVertice.remove(adyacenteAEliminar); // el remove usa el equals de nuestra clase adyacenteConPeso 
            for( AdyacenteConPeso adyacenteEnTurno : adyacentesDeUnVertice){
                int indiceActual = adyacenteEnTurno.getIndiceVertice();
                if (indiceActual > nroVerticeAEliminar) {
                    adyacenteEnTurno.setIndiceVertice(indiceActual - 1);
                }
            }
        }
    }
 
    
    public int gradoDelVertice(T unvertice){
        validarVertice(unvertice);
        int nroDelVertice = getNroDeVertice(unvertice);
        List<AdyacenteConPeso> adyacentesDelVertice = listasDeAdyacencias.get(nroDelVertice);
        return adyacentesDelVertice.size();
    }
   
    
    public boolean existeVertice(T unVertice){
        if (unVertice == null) {
            throw new NullPointerException("Vertice no puede ser nulo");
        }
        return getNroDeVertice(unVertice) != NRO_VERTICE_INVALIDO; 
    }
    
    
    protected void validarVertice(T unVertice){
        if (unVertice == null) {
            throw new NullPointerException("Vertice no puede ser nulo");
        }
        if (getNroDeVertice(unVertice) == NRO_VERTICE_INVALIDO) {
            throw new IllegalArgumentException("VERTICE NO ENCONTRADO");
        }
    }
    
    public int getNroDeVertice(T unVertice){
        for (int i = 0; i < listaDeVertices.size(); i++) {
            T verticeEnTurno = listaDeVertices.get(i);
            if (verticeEnTurno.compareTo(unVertice) == 0) {
                return i;
            }
        }
        return NRO_VERTICE_INVALIDO;
    }
    
    public int cantidadDeVertices(){ 
        return listaDeVertices.size();
    }
    
    public Iterable<T> getVertices(){
        List<T> copiaDeVertices = new ArrayList<>();
        for (T unVertice : listaDeVertices) {
            copiaDeVertices.add(unVertice);
        }
        return copiaDeVertices;
    }
    
    public T getVerticePorNro(int nroVertice){
        if (nroVertice < 0 || nroVertice >= listaDeVertices.size()) {
            throw  new IllegalArgumentException("NRO DE VERTICE INVALIDO");
        }
        return this.listaDeVertices.get(nroVertice);
    }
    
    // ------------------------------------ METODOS CON LAS ARISTAS O ADYACENCIAS --------------------------------------------------------------//
    
    
    
    public void insertarArista(T verticeOrigen, T verticeDestino, double peso){
        
        validarVertice(verticeOrigen);
        validarVertice(verticeDestino);
        
        if (existeAdyacencia(verticeOrigen, verticeDestino)) {
            throw new IllegalArgumentException("ARISTA YA EXISTE EN EL GRAFO");
        }
        
        int nroVerticeOrigen = getNroDeVertice(verticeOrigen);
        int nroVerticeDestino = getNroDeVertice(verticeDestino);
        
        AdyacenteConPeso adyacenciaAInsertar = new AdyacenteConPeso(nroVerticeDestino, peso);
        List<AdyacenteConPeso> adyacenciasDelVerticeOrigen = listasDeAdyacencias.get(nroVerticeOrigen);
        adyacenciasDelVerticeOrigen.add(adyacenciaAInsertar);
        Collections.sort(adyacenciasDelVerticeOrigen);

        if ( verticeOrigen != verticeDestino) {
            adyacenciaAInsertar = new AdyacenteConPeso(nroVerticeOrigen, peso);
            List<AdyacenteConPeso> adyacenciasDelVerticeDestino = listasDeAdyacencias.get(nroVerticeDestino);
            adyacenciasDelVerticeDestino.add(adyacenciaAInsertar);
            Collections.sort(adyacenciasDelVerticeDestino);
        }
        
    }
    
    public void eliminarArista(T verticeOrigen, T verticeDestino){
        validarVertice(verticeOrigen);
        validarVertice(verticeDestino);
        
        if (!existeAdyacencia(verticeOrigen, verticeDestino)) {
            throw new IllegalArgumentException("NO EXISTE ARISTA");
        }
        
        int nroVerticeOrigen = getNroDeVertice(verticeOrigen);
        int nroVerticeDestino = getNroDeVertice(verticeDestino);
        
        AdyacenteConPeso adyacenciaAEliminar = new AdyacenteConPeso(nroVerticeDestino);
   
        List<AdyacenteConPeso> adyacenciasDelVerticeOrigen  = listasDeAdyacencias.get(nroVerticeOrigen);
        adyacenciasDelVerticeOrigen.remove(adyacenciaAEliminar);
        
        if (verticeOrigen != verticeDestino) {
            adyacenciaAEliminar = new AdyacenteConPeso(nroVerticeOrigen);
            List<AdyacenteConPeso> adyacenciasDelVerticeDestino  = listasDeAdyacencias.get(nroVerticeDestino);
            adyacenciasDelVerticeDestino.remove(adyacenciaAEliminar);
        }    
        
    }
    

    public boolean existeAdyacencia(T verticeOrigen, T verticeDestino){
        validarVertice(verticeOrigen);
        validarVertice(verticeDestino);
        int nroVerticeOrigen = getNroDeVertice(verticeOrigen);
        int nroVerticeDestino = getNroDeVertice(verticeDestino);
        List<AdyacenteConPeso> AdyacentesDelOrigen = listasDeAdyacencias.get(nroVerticeOrigen); //obtenemos la lista de de adyacencias del origen 
        AdyacenteConPeso adyacente = new AdyacenteConPeso(nroVerticeDestino);
        return AdyacentesDelOrigen.contains(adyacente);

    }
    
    public Iterable<AdyacenteConPeso> adyacentesDelVertice(T elvertice){
        validarVertice(elvertice);
        List<AdyacenteConPeso> adyacentesDelVertice = new ArrayList<>();
        int nroDelVertice = getNroDeVertice(elvertice);
        List<AdyacenteConPeso> adyacenciasDelVertice = listasDeAdyacencias.get(nroDelVertice);
        
        for (AdyacenteConPeso adyacenciaEnTurno : adyacenciasDelVertice){
            AdyacenteConPeso copiaAdyacencia = new AdyacenteConPeso(adyacenciaEnTurno.getIndiceVertice(), adyacenciaEnTurno.getPeso());
            adyacentesDelVertice.add(copiaAdyacencia);
        }
        
        return adyacentesDelVertice;
        
    }
    
    public int cantidadDeAristas(){
        int contAdyacencias = 0;
        int contLazos = 0;
        int nroVertice = 0;
        for (List<AdyacenteConPeso> adyacenciasEnTurno : listasDeAdyacencias) {
            
            for (AdyacenteConPeso adyacencia : adyacenciasEnTurno) {
                int indiceActual = adyacencia.getIndiceVertice();
                if (indiceActual == nroVertice ) {
                    contLazos++;
                } else {
                    contAdyacencias++;
                }
       
            }
            nroVertice++;
        }
        
        int total = (contAdyacencias/2) + contLazos;  // al contar 2 veces la adyacencia en ambos sentidos de los vertices se debe contar solo la mitad
        return total;
    }
    
    
    public double getPeso(T verticeOrigen, T VerticeDestino){
        validarVertice(verticeOrigen);
        validarVertice(VerticeDestino);
        
        if (!existeAdyacencia(verticeOrigen, VerticeDestino)) {
            throw new IllegalArgumentException("No existe adyacencia");
        }
        
        int nroDelVerticeOrigen = getNroDeVertice(verticeOrigen);
        int nroDelVerticeDestino = getNroDeVertice(VerticeDestino);
        
        List<AdyacenteConPeso> adyacenciasDelVerticeOrigen = listasDeAdyacencias.get(nroDelVerticeOrigen);
        
        for (AdyacenteConPeso adyacenciaEnTurno : adyacenciasDelVerticeOrigen){
            int indiceActual = adyacenciaEnTurno.getIndiceVertice();
            
            if (indiceActual == nroDelVerticeDestino) {
               return adyacenciaEnTurno.getPeso();
            }
        }
        
        return 0 ;
        
    }
    
    public void setPeso(T verticeOrigen, T VerticeDestino, double peso){
        validarVertice(verticeOrigen);
        validarVertice(VerticeDestino);
        
        if (!existeAdyacencia(verticeOrigen, VerticeDestino)) {
            throw new IllegalArgumentException("No existe adyacencia");
        }
        
        int nroDelVerticeOrigen = getNroDeVertice(verticeOrigen);
        int nroDelVerticeDestino = getNroDeVertice(VerticeDestino);
      
        List<AdyacenteConPeso> adyacenciasDelVerticeOrigen = listasDeAdyacencias.get(nroDelVerticeOrigen);
        
        for (AdyacenteConPeso adyacenciaEnTurno : adyacenciasDelVerticeOrigen){
            int indiceActual = adyacenciaEnTurno.getIndiceVertice();
            
            if (indiceActual == nroDelVerticeDestino) {
                adyacenciaEnTurno.setPeso(peso);
                break;
            }
        }
        
        if (verticeOrigen != VerticeDestino) {
            List<AdyacenteConPeso> adyacenciasDelVerticeDestino = listasDeAdyacencias.get(nroDelVerticeDestino);
        
            for (AdyacenteConPeso adyacenciaEnTurno : adyacenciasDelVerticeDestino){
                int indiceActual = adyacenciaEnTurno.getIndiceVertice();
            
                if (indiceActual == nroDelVerticeOrigen) {
                    adyacenciaEnTurno.setPeso(peso);
                    break;
                }
            }
        }
        
        
    }
    
    
    
    
    
    
    
    // ---------------------------------------- OTROS METODOS ---------------------------------------------------------------
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listaDeVertices.size(); i++) {
            // Mostramos el vértice actual
            sb.append("[").append(listaDeVertices.get(i)).append("] -> ");
        
            List<AdyacenteConPeso> adyacentes = listasDeAdyacencias.get(i);
            for (int j = 0; j < adyacentes.size(); j++) {
                AdyacenteConPeso ady = adyacentes.get(j);
                // Obtenemos el nombre del vértice destino usando su índice
                T nombreDestino = listaDeVertices.get(ady.getIndiceVertice());
            
                sb.append(nombreDestino)
                .append(" (p: ").append(ady.getPeso()).append(")");
            
                // Coma decorativa entre adyacentes
                if (j < adyacentes.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
