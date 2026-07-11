/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RegistroDePasajeros;

/**
 *
 * @author jherargonzales
 */
public class Boleto {
    
    private int nroBoleto;

    public Boleto(int nroBoleto, String nombrePersona, double precioDeBoleto, int Origen, int Destino) {
        this.nroBoleto = nroBoleto;
        this.nombrePersona = nombrePersona;
        this.precioDeBoleto = precioDeBoleto;
        this.Origen = Origen;
        this.Destino = Destino;
    }

    public void setNroBoleto(int nroBoleto) {
        this.nroBoleto = nroBoleto;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public void setPrecioDeBoleto(double precioDeBoleto) {
        this.precioDeBoleto = precioDeBoleto;
    }

    public void setOrigen(int Origen) {
        this.Origen = Origen;
    }

    public void setDestino(int Destino) {
        this.Destino = Destino;
    }
    private String nombrePersona;
    private double precioDeBoleto;
    private int Origen;

    public int getNroBoleto() {
        return nroBoleto;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public double getPrecioDeBoleto() {
        return precioDeBoleto;
    }

    public int getOrigen() {
        return Origen;
    }

    public int getDestino() {
        return Destino;
    }
    private int Destino;
    
    
    
    
}
