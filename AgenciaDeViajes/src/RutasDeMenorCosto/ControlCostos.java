/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RutasDeMenorCosto;

/**
 *
 * @author jherargonzales
 */
public class ControlCostos {
    // ATRIBUTO
    private double[] costos;

    // CONSTRUCTOR
    public ControlCostos(int nroVertices, int posOrigen) {
        if (nroVertices <= 0) {
            throw new IllegalArgumentException("La cantidad de vértices debe ser mayor a 0");
        }
        
        costos = new double[nroVertices];
        
        // Inicializamos todos los costos al "Infinito" de Java
        for (int i = 0; i < nroVertices; i++) {
            costos[i] = Double.POSITIVE_INFINITY; 
        }
        
        // El costo al vértice de origen siempre es 0
        costos[posOrigen] = 0.0;
    }

    // MÉTODOS SIMPLES
    public double getCosto(int posVertice) {
        return costos[posVertice];
    }

    public void setCosto(int posVertice, double nuevoCosto) {
        costos[posVertice] = nuevoCosto;
    }

    // "En cada paso, de todos los caminos que conozco, siempre debo elegir el más barato que aún no haya visitado"
    /**
     * Busca el vértice que tenga el costo acumulado más bajo, 
     * asegurándose de que no haya sido procesado (marcado) aún.
     */
    public int obtenerVerticeMenorCostoNoMarcado(ControlMarcados marcados) {
        double menorCosto = Double.POSITIVE_INFINITY;
        int verticeMenor = -1; // -1 indica que no se encontró ninguno (Grafo_Pesado.NRO_VERTICE_INVALIDO)

        for (int i = 0; i < costos.length; i++) {
            // Si el vértice NO está marcado Y su costo es menor al que tengo guardado
            if (!marcados.estaVerticeMarcado(i) && costos[i] < menorCosto) {
                menorCosto = costos[i];
                verticeMenor = i;
            }
        }
        return verticeMenor;
    }
}
