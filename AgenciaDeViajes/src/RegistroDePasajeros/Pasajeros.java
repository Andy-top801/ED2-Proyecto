package RegistroDePasajeros;

import java.util.List;

/**
 *
 * @author Andy
 */
public class Pasajeros {
    private final ArbolB<Boleto> registro;

    public Pasajeros(int orden) {
        registro = new ArbolB<Boleto>(orden) {};
    }

    public void nuevoPasajero(Boleto nuevoBoleto) {
        registro.insertar(nuevoBoleto);
    }

    public void cancelarVuelo(Boleto retirarBoleto) {
        registro.eliminar(retirarBoleto);
    }

    public Boleto buscarBoleto(Boleto boleto) {
        return registro.buscar(boleto);
    }

    public boolean contieneBoleto(Boleto boleto) {
        return registro.contiene(boleto);
    }

    public int cantidadDePasajeros() {
        return registro.size();
    }

    public boolean esVacio() {
        return registro.esArbolVacio();
    }

    public List<Boleto> listarPasajerosEnOrden() {
        return registro.recorridoEnInOrden();
    }

    public List<Boleto> listarPasajerosPorNiveles() {
        return registro.recorridoPorNiveles();
    }

    public void mostrarArbol() {
        System.out.println(registro.toString());
    }
}
