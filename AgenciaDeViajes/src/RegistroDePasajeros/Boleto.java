package RegistroDePasajeros;

/**
 *
 * @author jherargonzales
 */
public class Boleto implements Comparable<Boleto> {
    
    private int nroBoleto;
    private String nombrePersona;
    private double precioDeBoleto;
    private int Origen;
    private int Destino;

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

    @Override
    public int compareTo(Boleto otro) {
        return Integer.compare(this.nroBoleto, otro.nroBoleto);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Boleto otro = (Boleto) obj;
        return this.nroBoleto == otro.nroBoleto;
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "nro=" + nroBoleto +
                ", nombre='" + nombrePersona + '\'' +
                ", precio=" + precioDeBoleto +
                ", origen=" + Origen +
                ", destino=" + Destino +
                '}';
    }
}
