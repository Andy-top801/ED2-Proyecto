package ClasificacionDeViajes;

public class RankingDeLugares {
    private MonticuloMaximo<Lugar> ranking;

    public RankingDeLugares() {
        ranking = new MonticuloMaximo<>();
    }

    public Lugar verLugarEnPosicion(int posicionEnElRanking){
        if (posicionEnElRanking >= ranking.size() || posicionEnElRanking < 0) {
            throw new IndexOutOfBoundsException("La posicion se encuentra fuera del rango del monticulo");
        }
        return ranking.get(posicionEnElRanking);
    }

    public void insertarLugar(String nombre, int califacion){
        ranking.insertar(new Lugar(nombre, califacion));
    }

    public Lugar eliminarMasPopular(){
        return ranking.eliminarTope();
    }

    public boolean esVacio(){
        return ranking.esMonticuloVacio();
    }

    public int size(){
        return ranking.size();
    }

    public Lugar mostrarLugarMasPopular(){
        return ranking.verTope();
    }

    public void mostrarRankingCompleto(){
        // Se usa un montículo temporal para no destruir el ranking original
        MonticuloMaximo<Lugar> temporal = new MonticuloMaximo<>();
        int posicion = 1;

        while (!ranking.esMonticuloVacio()) {
            Lugar lugar = ranking.eliminarTope();
            System.out.println(posicion + ". " + lugar.getNombre()
                    + " - Calificacion: " + lugar.getCalifacion());
            temporal.insertar(lugar);
            posicion++;
        }

        // Restaurar el ranking original
        while (!temporal.esMonticuloVacio()) {
            ranking.insertar(temporal.eliminarTope());
        }
    }

    public void actualizarCalificacion(int posicion, int nuevaCalificacion){
        Lugar lugar = ranking.get(posicion);
        lugar.setCalifacion(nuevaCalificacion);
        ranking.reconstruir();
    }
    
}