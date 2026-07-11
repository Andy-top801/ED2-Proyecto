package ClasificacionDeViajes;

public class Lugar implements Comparable<Lugar> {
    private int califacion;
    private String nombre;

    public Lugar(String nombre) {
        this.nombre = nombre;
        this.califacion = 0;
    }

    public Lugar(String nombre, int califacion) {
        this.nombre = nombre;
        this.califacion = califacion;
    }

    public void setCalifacion(int califacion) {
        this.califacion = califacion;
    }

    public int getCalifacion() {
        return califacion;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int compareTo(Lugar otro) {
        return Integer.compare(this.califacion, otro.califacion);
    }
    
}
