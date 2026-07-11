/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RutasDeMenorCosto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlPredecesores {

    // ATRIBUTO
    private int[] predecesores;

    // CONSTRUCTOR
    public ControlPredecesores(int nroVertices) {
        if (nroVertices <= 0) {
            throw new IllegalArgumentException("La cantidad de vértices debe ser mayor a 0");
        }
        
        predecesores = new int[nroVertices];
        
        // Inicializamos todos los predecesores en -1 (NRO_VERTICE_INVALIDO)
        for (int i = 0; i < nroVertices; i++) {
            predecesores[i] = -1;
        }
    }

    // MÉTODOS SIMPLES
    public int getPredecesor(int posVertice) {
        return predecesores[posVertice];
    }

    public void setPredecesor(int posVertice, int posPredecesor) {
        predecesores[posVertice] = posPredecesor;
    }

    // EL MÉTODO ESTRELLA
    /**
     * Reconstruye el camino más corto desde el origen hasta el destino
     * siguiendo las "migas de pan" que dejó el algoritmo.
     */
    public List<Integer> recuperarCamino(int posDestino) {
        List<Integer> camino = new ArrayList<>();
        int verticeActual = posDestino;

        // Retrocedemos mientras el vértice actual tenga un predecesor válido
        while (verticeActual != -1) {
            camino.add(verticeActual);
            verticeActual = predecesores[verticeActual]; // Saltamos al vértice anterior
        }

        // Como el camino se armó desde el Destino hacia el Origen, lo invertimos
        Collections.reverse(camino);
        
        return camino;
    }
}
