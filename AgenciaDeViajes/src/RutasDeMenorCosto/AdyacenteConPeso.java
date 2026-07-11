/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RutasDeMenorCosto;

/**
 *
 * @author jherargonzales
 */
public class AdyacenteConPeso implements Comparable<AdyacenteConPeso>{
    
    //PROPIEDADES
    
    private int indiceVertice;
    private double peso;
    
    //METODOS

    public AdyacenteConPeso(int vertice) {
        this.indiceVertice = vertice;
    }
    
    public AdyacenteConPeso(int vertice, double peso) {
        this.indiceVertice = vertice;
        this.peso = peso;
    }
    
    public int getIndiceVertice() {
        return indiceVertice;
    }
    
    public void setIndiceVertice(int indiceVertice) {
        this.indiceVertice = indiceVertice;
    }
    
    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public int compareTo(AdyacenteConPeso vert) { // comparamos los indices del vertice adyacente
        
        Integer esteVertice = this.indiceVertice;
        Integer elOtroVertice = vert.indiceVertice;
        return esteVertice.compareTo(elOtroVertice);
        
    }
     
    
    
//    Sirve para comparar si dos objetos son lógicamente iguales.
//    Por defecto: Java compara direcciones de memoria (si son el mismo objeto físico).
//    En tu caso: Has sobrescrito el método para que compare solo el indiceVertice.
//    Para qué sirve: Cuando en tu clase Grafo hagas algo como listaAdyacencias.contains(objetoAdyacente) o listaAdyacencias.remove(objetoAdyacente)
//    , Java usará este método equals para saber cuál objeto debe borrar o si ya existe, sin importar si el peso es diferente.
    @Override
    public boolean equals(Object otro) {
        
        if (otro == null) { 
            return false;
        }
        
        if (getClass() != otro.getClass()) {
            return false;
        }
        
        AdyacenteConPeso other = (AdyacenteConPeso) otro;
        return this.indiceVertice == other.indiceVertice;
        
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.indiceVertice;
        return hash;
    }
    
    
//    ¿Por qué son vitales para tu Grafo Pesado?
//
//    Imagina que quieres saber si ya existe una arista entre el vértice 1 y el vértice 2:
//
//    Creas un new AdyacenteConPeso(2, 5.0).
//
//    Llamas a listasDeAdyacencias.get(1).contains(eseObjeto).
//
//    Sin equals: Java diría "No existe", porque buscaría exactamente la misma instancia de memoria.
//
//    Con tu equals: Java revisa el indiceVertice, ve que ambos apuntan al 2, y te dice "Sí, ya existe", permitiéndote evitar duplicados.
    
} 
