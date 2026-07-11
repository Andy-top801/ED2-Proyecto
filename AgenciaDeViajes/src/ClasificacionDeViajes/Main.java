package ClasificacionDeViajes;

public class Main {
    public static void main(String[] args) {
        RankingDeLugares ranking = new RankingDeLugares();

        // Insertar lugares con sus calificaciones
        ranking.insertarLugar("La Paz", 92);
        ranking.insertarLugar("Cochabamba", 90);
        ranking.insertarLugar("Santa Cruz", 95);
        ranking.insertarLugar("Oruro", 78);
        ranking.insertarLugar("Potosi", 83);
        ranking.insertarLugar("Tarija", 86);
        ranking.insertarLugar("Chuquisaca", 81);
        ranking.insertarLugar("Beni", 88);
        ranking.insertarLugar("Pando", 75);

        System.out.println("=== Ranking de Lugares ===");
        System.out.println("Total de lugares: " + ranking.size());

        // Mostrar el lugar mas popular sin eliminarlo
        Lugar masPopular = ranking.mostrarLugarMasPopular();
        System.out.println("\nLugar mas popular: " + masPopular.getNombre()
                + " (Calificacion: " + masPopular.getCalifacion() + ")");

        // Mostrar ranking completo de mayor a menor calificacion
        System.out.println("\n=== Ranking completo (de mayor a menor) ===");
        ranking.mostrarRankingCompleto();

        System.out.println("\n=== Prueba de monticulo vacio ===");
        System.out.println("Esta vacio: " + ranking.esVacio());

        System.out.println("\n=== Actualizando calificacion de posicion 8 a 100 ===");
        ranking.actualizarCalificacion(8, 100);
        ranking.mostrarRankingCompleto();

        // Verificar que lanza excepcion al intentar eliminar de un monticulo vacio
        try {
            ranking.eliminarMasPopular();
        } catch (IllegalArgumentException e) {
            System.out.println("Excepcion capturada correctamente: " + e.getMessage());
        }
    }
}
