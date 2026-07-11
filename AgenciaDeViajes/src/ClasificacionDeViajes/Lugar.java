/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClasificacionDeViajes;

/**
 *
 * @author jherargonzales
 */
public class Lugar {
    private int califacion;
    private String nombre;

    public void setCalifacion(int califacion) {
        this.califacion = califacion;
    }

    public int getCalifacion() {
        return califacion;
    }

    public String getNombre() {
        return nombre;
    }

    public Lugar( String nombre) {
        this.nombre = nombre;
    }
    
}
