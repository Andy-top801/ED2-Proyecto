package RutasDeMenorCosto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy
 * @param <T>
 */

public class Prim<T extends Comparable<T>> {

    protected Grafo_Pesado<T> elGrafo;
    protected ControlMarcados marcados;
    protected ControlCostos costos;
    protected ControlPredecesores predecesores;
    protected static final int NRO_VERTICE_INVALIDO = -1;

    public Prim(Grafo_Pesado<T> unGrafo) {
        this.elGrafo = unGrafo;
    }

    public void ejecutar(T verticeOrigen) {

        // PASO 1: VALIDACIÓN E INICIALIZACIÓN DE ESTRUCTURAS
        int posOrigen = elGrafo.getNroDeVertice(verticeOrigen);
        if (posOrigen == NRO_VERTICE_INVALIDO) {
            throw new IllegalArgumentException("El vértice de origen no existe en el grafo.");
        }
        int nroVertices = elGrafo.cantidadDeVertices();
        // Instanciamos nuestras 3 estructuras de control
        this.marcados = new ControlMarcados(nroVertices);
        this.costos = new ControlCostos(nroVertices, posOrigen); // Origen inicia en 0, resto en Infinito
        this.predecesores = new ControlPredecesores(nroVertices); // Todos inician en -1
        // PASO 2: BUCLE PRINCIPAL
        while (!marcados.estanTodosMarcados()) {
            // PASO 3: SELECCIÓN DEL VÉRTICE CON MENOR COSTO DE CONEXIÓN
            int posVerticeActual = costos.obtenerVerticeMenorCostoNoMarcado(marcados);
            // Si devuelve -1, los vértices restantes son inalcanzables (grafo no conexo). Terminamos.
            if (posVerticeActual == NRO_VERTICE_INVALIDO) {
                break;
            }
            // PASO 4: AGREGAR EL VÉRTICE AL MST (marcarlo como procesado)
            marcados.marcarVertice(posVerticeActual);
            // PASO 5: EXPLORAR LOS VECINOS (ADYACENTES) DEL VÉRTICE RECIÉN AGREGADO AL MST
            T verticeActual = elGrafo.getVerticePorNro(posVerticeActual);
            Iterable<AdyacenteConPeso> adyacentes = elGrafo.adyacentesDelVertice(verticeActual);
            for (AdyacenteConPeso adyacente : adyacentes) {
                int posVecino = adyacente.getIndiceVertice();
                double pesoArista = adyacente.getPeso();
                // Solo evaluamos vecinos que NO estén ya en el MST
                if (!marcados.estaVerticeMarcado(posVecino)) {
                    // PASO 6: ACTUALIZACIÓN (la diferencia clave con Dijkstra)
                    if (pesoArista < costos.getCosto(posVecino)) {
                        // ¡Actualizamos! Esta arista es la conexión más barata al MST
                        costos.setCosto(posVecino, pesoArista);                    // Guardamos el peso de la arista
                        predecesores.setPredecesor(posVecino, posVerticeActual);   // Recordamos desde dónde lo conectamos
                    }
                }
            }
        }
    }

    public double obtenerCostoTotalMST() {
        double costoTotal = 0.0;
        int nroVertices = elGrafo.cantidadDeVertices();
        for (int i = 0; i < nroVertices; i++) {
            // Solo sumamos vértices que tienen predecesor (todos excepto el origen)
            if (predecesores.getPredecesor(i) != NRO_VERTICE_INVALIDO) {
                costoTotal += costos.getCosto(i);
            }
        }
        return costoTotal;
    }

    public List<String> obtenerAristasMST() {
        List<String> aristas = new ArrayList<>();
        int nroVertices = elGrafo.cantidadDeVertices();
        for (int i = 0; i < nroVertices; i++) {
            int predecesor = predecesores.getPredecesor(i);
            if (predecesor != NRO_VERTICE_INVALIDO) {
                T verticeOrigen = elGrafo.getVerticePorNro(predecesor);
                T verticeDestino = elGrafo.getVerticePorNro(i);
                double peso = costos.getCosto(i);
                aristas.add(verticeOrigen + " -- " + verticeDestino + " (peso: " + peso + ")");
            }
        }
        return aristas;
    }

    /**
     * Devuelve el grafo resultante que representa el Árbol de Expansión Mínima.
     * Crea un nuevo Grafo_Pesado con los mismos vértices y solo las aristas del MST.
     */
    public Grafo_Pesado<T> obtenerGrafoMST() {
        Grafo_Pesado<T> mst = new Grafo_Pesado<>(elGrafo.getVertices());
        int nroVertices = elGrafo.cantidadDeVertices();
        for (int i = 0; i < nroVertices; i++) {
            int predecesor = predecesores.getPredecesor(i);
            if (predecesor != NRO_VERTICE_INVALIDO) {
                T verticeOrigen = elGrafo.getVerticePorNro(predecesor);
                T verticeDestino = elGrafo.getVerticePorNro(i);
                double peso = costos.getCosto(i);
                mst.insertarArista(verticeOrigen, verticeDestino, peso);
            }
        }
        return mst;
    }

    public void mostrarResultadoMST() {
        System.out.println("========== ÁRBOL DE EXPANSIÓN MÍNIMA (PRIM) ==========");
        System.out.println("Aristas del MST:");
        List<String> aristas = obtenerAristasMST();
        for (String arista : aristas) {
            System.out.println("  " + arista);
        }
        System.out.println("Costo total del MST: " + obtenerCostoTotalMST());
        System.out.println("======================================================");
    }
}
