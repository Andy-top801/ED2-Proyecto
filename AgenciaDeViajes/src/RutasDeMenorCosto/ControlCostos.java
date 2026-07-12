package RutasDeMenorCosto;

public class ControlCostos {

    private double[] costos;

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

    public double getCosto(int posVertice) {
        return costos[posVertice];
    }

    public void setCosto(int posVertice, double nuevoCosto) {
        costos[posVertice] = nuevoCosto;
    }

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
