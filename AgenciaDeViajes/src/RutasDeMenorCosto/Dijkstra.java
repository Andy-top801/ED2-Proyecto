/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RutasDeMenorCosto;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jherargonzales
 * @param <T>
 */
public class Dijkstra<T extends Comparable<T>> {
    
    protected Grafo_Pesado<T> elGrafo;
    protected ControlMarcados marcados;
    protected ControlCostos costos;
    protected ControlPredecesores predecesores;
    protected static final int NRO_VERTICE_INVALIDO = -1 ;

    // CONSTRUCTOR
    public Dijkstra(Grafo_Pesado<T> unGrafo) {
        this.elGrafo = unGrafo;
    }

    
    public void ejecutar(T verticeOrigen) {
        
        // PASO 1: VALIDACIÓN E INICIALIZACIÓN DE ESTRUCTURAS
        elGrafo.validarVertice(verticeOrigen);
        int posOrigen = elGrafo.getNroDeVertice(verticeOrigen);
        int nroVertices = elGrafo.cantidadDeVertices();
        // Instanciamos nuestras 3 estructuras de control
        this.marcados = new ControlMarcados(nroVertices);
        this.costos = new ControlCostos(nroVertices, posOrigen); // Origen inicia en 0, resto en Infinito
        this.predecesores = new ControlPredecesores(nroVertices); // Todos inician en -1

        // PASO 2: BUCLE PRINCIPAL
        do {

            // PASO 3: SELECCIÓN DEL VÉRTICE ACTIVO (El más barato disponible)
            // Delegamos la búsqueda al metodo de la clase ControlCostos
            int posVerticeActual = costos.obtenerVerticeMenorCostoNoMarcado(marcados);

            // Si devuelve -1, significa que los vértices restantes son inalcanzables (costo infinito). Terminamos.
            if (posVerticeActual == NRO_VERTICE_INVALIDO) {
                break;
            }

            // PASO 4: MARCAR EL VÉRTICE COMO PROCESADO
            // Garantizamos que ya encontramos el camino más corto definitivo hacia él y no volvemos a visitarlo
            marcados.marcarVertice(posVerticeActual);

            // PASO 5: EXPLORAR LOS VECINOS (ADYACENTES) DEL VÉRTICE ACTUAL
            T verticeActual = elGrafo.getVerticePorNro(posVerticeActual);
            Iterable<AdyacenteConPeso> adyacentes = elGrafo.adyacentesDelVertice(verticeActual);

            for (AdyacenteConPeso adyacenteEnTurno : adyacentes) {
                int posAdyacenteEnTurno = adyacenteEnTurno.getIndiceVertice();
                double pesoArista = adyacenteEnTurno.getPeso();

                // Solo calculamos si el vecino NO está marcado
                if (!marcados.estaVerticeMarcado(posAdyacenteEnTurno)) {
                    
                    // PASO 6: ACTUALIZACIÓN 
                    // Calculamos: ¿Cuánto costaría llegar a este vecino si paso por el vértice actual?
                    double costoAcumulado = costos.getCosto(posVerticeActual) + pesoArista;

                    // Si el nuevo costo calculado es MEJOR (más barato) que el que ya teníamos anotado...
                    if (costoAcumulado < costos.getCosto(posAdyacenteEnTurno)) {
                        
                        // ¡Actualizamos las libretas!
                        costos.setCosto(posAdyacenteEnTurno, costoAcumulado);              // Guardamos el nuevo costo
                        predecesores.setPredecesor(posAdyacenteEnTurno, posVerticeActual); // Guardamos el predecesor
                    }
                }
            }
            // Repetimos el proceso hasta que todos los vértices alcanzables estén marcados
        } while (!marcados.estanTodosMarcados());
    }

    
    /**
     * Devuelve la ruta exacta (lista de vértices) desde el origen hasta el destino.
     */
    public List<T> obtenerCaminoMasCorto(T verticeDestino) {
        int posDestino = elGrafo.getNroDeVertice(verticeDestino);
        if (posDestino == NRO_VERTICE_INVALIDO) {
            throw new IllegalArgumentException("El vértice destino no existe.");
        }

        // Le pedimos a ControlPredecesores que nos arme la ruta de índices 
        List<Integer> caminoIndices = predecesores.recuperarCamino(posDestino);
        List<T> caminoVertices = new ArrayList<>();

        
        for (int indice : caminoIndices) {
            caminoVertices.add(elGrafo.getVerticePorNro(indice));
        }

        return caminoVertices;
    }

    /**
     * Devuelve el costo total para llegar al destino.
     */
    public double obtenerCostoMinimoHacia(T verticeDestino) {
        int posDestino = elGrafo.getNroDeVertice(verticeDestino);
        if (posDestino == NRO_VERTICE_INVALIDO) {
            throw new IllegalArgumentException("El vértice destino no existe.");
        }
        return costos.getCosto(posDestino);
    }
}
