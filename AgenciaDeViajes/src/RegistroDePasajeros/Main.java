package RegistroDePasajeros;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  PRUEBAS DEL REGISTRO DE PASAJEROS (Arbol B)");
        System.out.println("=============================================\n");

        // Crear registro con orden 3 (arbol B de orden 3: max 2 datos por nodo)
        Pasajeros registro = new Pasajeros(3);

        // =============================================
        // PRUEBA 1: Insertar pasajeros
        // =============================================
        System.out.println("=== PRUEBA 1: Insertar pasajeros ===\n");

        Boleto b1 = new Boleto(101, "Juan Perez", 350.0, 1, 5);
        Boleto b2 = new Boleto(205, "Maria Lopez", 420.0, 2, 7);
        Boleto b3 = new Boleto(150, "Carlos Mamani", 280.0, 3, 1);
        Boleto b4 = new Boleto(310, "Ana Quispe", 500.0, 5, 9);
        Boleto b5 = new Boleto(75, "Pedro Rojas", 310.0, 4, 2);
        Boleto b6 = new Boleto(250, "Laura Vargas", 460.0, 6, 3);
        Boleto b7 = new Boleto(180, "Diego Flores", 390.0, 1, 8);
        Boleto b8 = new Boleto(400, "Sofia Mendoza", 550.0, 7, 4);
        Boleto b9 = new Boleto(50, "Roberto Gutierrez", 200.0, 2, 6);

        registro.nuevoPasajero(b1);
        System.out.println("Insertado: " + b1);
        registro.nuevoPasajero(b2);
        System.out.println("Insertado: " + b2);
        registro.nuevoPasajero(b3);
        System.out.println("Insertado: " + b3);
        registro.nuevoPasajero(b4);
        System.out.println("Insertado: " + b4);
        registro.nuevoPasajero(b5);
        System.out.println("Insertado: " + b5);
        registro.nuevoPasajero(b6);
        System.out.println("Insertado: " + b6);
        registro.nuevoPasajero(b7);
        System.out.println("Insertado: " + b7);
        registro.nuevoPasajero(b8);
        System.out.println("Insertado: " + b8);
        registro.nuevoPasajero(b9);
        System.out.println("Insertado: " + b9);

        System.out.println("\nCantidad de pasajeros: " + registro.cantidadDePasajeros());

        // =============================================
        // PRUEBA 2: Estructura del arbol
        // =============================================
        System.out.println("\n=== PRUEBA 2: Estructura del arbol B ===\n");
        registro.mostrarArbol();

        // =============================================
        // PRUEBA 3: Buscar boletos
        // =============================================
        System.out.println("=== PRUEBA 3: Buscar boletos ===\n");

        // Buscar un boleto existente (por nroBoleto ya que compareTo usa eso)
        Boleto busqueda1 = new Boleto(150, "", 0, 0, 0);
        Boleto encontrado1 = registro.buscarBoleto(busqueda1);
        System.out.println("Buscar boleto 150: " + (encontrado1 != null ? encontrado1 : "No encontrado"));

        Boleto busqueda2 = new Boleto(400, "", 0, 0, 0);
        Boleto encontrado2 = registro.buscarBoleto(busqueda2);
        System.out.println("Buscar boleto 400: " + (encontrado2 != null ? encontrado2 : "No encontrado"));

        // Buscar un boleto que no existe
        Boleto busqueda3 = new Boleto(999, "", 0, 0, 0);
        Boleto encontrado3 = registro.buscarBoleto(busqueda3);
        System.out.println("Buscar boleto 999: " + (encontrado3 != null ? encontrado3 : "No encontrado"));

        // =============================================
        // PRUEBA 4: Contiene boleto
        // =============================================
        System.out.println("\n=== PRUEBA 4: Contiene boleto ===\n");

        System.out.println("Contiene boleto 205: " + registro.contieneBoleto(new Boleto(205, "", 0, 0, 0)));
        System.out.println("Contiene boleto 999: " + registro.contieneBoleto(new Boleto(999, "", 0, 0, 0)));

        // =============================================
        // PRUEBA 5: Recorrido InOrden (datos ordenados)
        // =============================================
        System.out.println("\n=== PRUEBA 5: Recorrido InOrden (debe estar ordenado) ===\n");

        List<Boleto> enOrden = registro.listarPasajerosEnOrden();
        for (Boleto b : enOrden) {
            System.out.println("  " + b);
        }

        // =============================================
        // PRUEBA 6: Recorrido por niveles
        // =============================================
        System.out.println("\n=== PRUEBA 6: Recorrido por niveles ===\n");

        List<Boleto> porNiveles = registro.listarPasajerosPorNiveles();
        for (Boleto b : porNiveles) {
            System.out.println("  " + b);
        }

        // =============================================
        // PRUEBA 7: Eliminar pasajeros
        // =============================================
        System.out.println("\n=== PRUEBA 7: Eliminar pasajeros ===\n");

        // Eliminar una hoja
        System.out.println("Eliminando boleto 50 (Roberto Gutierrez)...");
        registro.cancelarVuelo(new Boleto(50, "", 0, 0, 0));
        System.out.println("Cantidad despues de eliminar: " + registro.cantidadDePasajeros());
        registro.mostrarArbol();

        // Eliminar un nodo interno
        System.out.println("Eliminando boleto 150 (Carlos Mamani)...");
        registro.cancelarVuelo(new Boleto(150, "", 0, 0, 0));
        System.out.println("Cantidad despues de eliminar: " + registro.cantidadDePasajeros());
        registro.mostrarArbol();

        // Eliminar otro
        System.out.println("Eliminando boleto 310 (Ana Quispe)...");
        registro.cancelarVuelo(new Boleto(310, "", 0, 0, 0));
        System.out.println("Cantidad despues de eliminar: " + registro.cantidadDePasajeros());
        registro.mostrarArbol();

        // =============================================
        // PRUEBA 8: Verificar orden despues de eliminaciones
        // =============================================
        System.out.println("=== PRUEBA 8: InOrden despues de eliminaciones ===\n");

        List<Boleto> enOrdenDespues = registro.listarPasajerosEnOrden();
        for (Boleto b : enOrdenDespues) {
            System.out.println("  " + b);
        }

        // =============================================
        // PRUEBA 9: Intentar insertar duplicado
        // =============================================
        System.out.println("\n=== PRUEBA 9: Insertar duplicado ===\n");

        try {
            registro.nuevoPasajero(new Boleto(205, "Duplicado", 100.0, 1, 1));
            System.out.println("ERROR: No deberia haber llegado aqui");
        } catch (IllegalArgumentException e) {
            System.out.println("Excepcion capturada correctamente: " + e.getMessage());
        }

        // =============================================
        // PRUEBA 10: Intentar eliminar un boleto inexistente
        // =============================================
        System.out.println("\n=== PRUEBA 10: Eliminar boleto inexistente ===\n");

        try {
            registro.cancelarVuelo(new Boleto(999, "", 0, 0, 0));
            System.out.println("ERROR: No deberia haber llegado aqui");
        } catch (IllegalArgumentException e) {
            System.out.println("Excepcion capturada correctamente: " + e.getMessage());
        }

        // =============================================
        // PRUEBA 11: Estado final
        // =============================================
        System.out.println("\n=== PRUEBA 11: Estado final ===\n");

        System.out.println("Cantidad de pasajeros: " + registro.cantidadDePasajeros());
        System.out.println("Esta vacio: " + registro.esVacio());
        System.out.println("\nArbol final:");
        registro.mostrarArbol();

        System.out.println("=============================================");
        System.out.println("  TODAS LAS PRUEBAS FINALIZADAS");
        System.out.println("=============================================");
    }
}
