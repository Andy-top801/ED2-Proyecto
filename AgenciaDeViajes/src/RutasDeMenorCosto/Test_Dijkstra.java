/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package RutasDeMenorCosto;


import java.util.List;

/**
 *
 * @author jherargonzales
 */
public class Test_Dijkstra {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("--- INICIANDO PRUEBA AVANZADA DEL ALGORITMO DE DIJKSTRA ---");
        
        // 1. Creamos el grafo gigante
        Grafo_Pesado<String> miGrafo = new Grafo_Pesado<>();
        
        // 2. Insertamos 8 vértices
        String[] vertices = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String v : vertices) {
            miGrafo.insertarVertice(v);
        }
        
        // 3. Insertamos una red compleja de aristas con pesos engañosos
        miGrafo.insertarArista("A", "B", 4.0);
        miGrafo.insertarArista("A", "C", 2.0);
        miGrafo.insertarArista("B", "C", 1.0);  // Un "atajo" barato entre B y C
        miGrafo.insertarArista("B", "D", 5.0);
        miGrafo.insertarArista("C", "D", 8.0);  // Camino directo C-D es muy caro
        miGrafo.insertarArista("C", "E", 10.0); // Camino directo C-E es carísimo
        miGrafo.insertarArista("B", "E", 2.0);  // Mejor ir por B para llegar a E
        miGrafo.insertarArista("D", "F", 6.0);
        miGrafo.insertarArista("E", "F", 2.0);
        miGrafo.insertarArista("D", "G", 4.0);
        miGrafo.insertarArista("F", "G", 3.0);
        miGrafo.insertarArista("E", "H", 3.0);
        miGrafo.insertarArista("G", "H", 1.0);
        miGrafo.insertarArista("F", "H", 6.0);
        
        System.out.println(miGrafo.toString());
        
        System.out.println("Grafo complejo construido exitosamente.\n");
        
        // 4. Instanciamos y ejecutamos Dijkstra
        Dijkstra<String> dijkstra = new Dijkstra<>(miGrafo);
        String origen = "A";
        
        System.out.println("Ejecutando Dijkstra desde el vértice origen: [" + origen + "]\n");
        dijkstra.ejecutar(origen);
        
        // 5. Mostramos los resultados hacia los destinos más lejanos
        mostrarResultado(dijkstra, "F");
        mostrarResultado(dijkstra, "G");
        mostrarResultado(dijkstra, "H"); // El destino final más lejano
        
        System.out.println("--- PRUEBA FINALIZADA ---");
    }
    
    // Método auxiliar para imprimir bonito
    private static void mostrarResultado(Dijkstra<String> algoritmo, String destino) {
        double costo = algoritmo.obtenerCostoMinimoHacia(destino);
        List<String> camino = algoritmo.obtenerCaminoMasCorto(destino);
        
        System.out.println("Destino [" + destino + "]:");
        System.out.println("  -> Costo Mínimo: " + costo);
        System.out.println("  -> Ruta Óptima : " + camino);
        System.out.println("------------------------------------");
    }
    
}
