package Interface;

import RegistroDePasajeros.Boleto;
import RegistroDePasajeros.Pasajeros;
import RutasDeMenorCosto.Dijkstra;
import RutasDeMenorCosto.Grafo_Pesado;
import RutasDeMenorCosto.Prim;
import ClasificacionDeViajes.Lugar;
import ClasificacionDeViajes.RankingDeLugares;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulario principal de la Agencia de Viajes — Bolivia.
 *
 * Pesta\u00f1a 1: Compra de Pasaje      (vista del PASAJERO)
 * Pesta\u00f1a 2: Recepci\u00f3n             (vista del RECEPCIONISTA)
 * Pesta\u00f1a 3: Rutas de Vuelo        (Grafo Pesado + Dijkstra + Prim)
 * Pesta\u00f1a 4: Ranking de Destinos   (Mont\u00edculo M\u00e1ximo)
 *
 * @author Andy
 */
public class Formulario extends javax.swing.JFrame {

    // ======================== MODELOS DE DATOS ========================
    private Pasajeros registroPasajerosGlobal;
    private java.util.Map<String, Pasajeros> registrosPorRuta;
    private Grafo_Pesado<String> grafo;
    private Dijkstra<String> dijkstra;
    private Prim<String> prim;
    private RankingDeLugares ranking;

    // ======================== DATOS DE BOLIVIA ========================
    private static final String[] DEPARTAMENTOS = {
        "La Paz", "Cochabamba", "Santa Cruz", "Oruro",
        "Potosi", "Tarija", "Sucre", "Trinidad", "Cobija"
    };

    // ======================== ESTADO INTERNO ========================
    private int siguienteBoleto = 1000;
    private double precioCalculado = 0;
    private List<String> rutaCalculada = null;
    private String origenSeleccionado = null;
    private String destinoSeleccionado = null;
    private String ultimoDestinoComprado = null;

    // ================ COMPONENTES — TAB 1: COMPRA ================
    private JTextField txtNombreCompra;
    private JComboBox<String> cmbOrigenCompra;
    private JComboBox<String> cmbDestinoCompra;
    private JSpinner spnFeedback;
    private JTextArea txtResultadosCompra;

    // ================ COMPONENTES — TAB 2: RECEPCI\u00d3N ================
    private JTextField txtBoletoRecepcion;
    private JComboBox<String> cmbOrigenFiltro;
    private JComboBox<String> cmbDestinoFiltro;
    private JTextArea txtResultadosRecepcion;

    // ================ COMPONENTES — TAB 3: RUTAS ================
    private JTextField txtNuevoVertice;
    private JTextField txtPesoArista;
    private JComboBox<String> cmbOrigenArista;
    private JComboBox<String> cmbDestinoArista;
    private JComboBox<String> cmbOrigenDijkstra;
    private JComboBox<String> cmbDestinoDijkstra;
    private JTextArea txtResultadosRutas;

    // ================ COMPONENTES — TAB 4: RANKING ================
    private JTextField txtNombreLugar;
    private JSpinner spnCalificacion;
    private JSpinner spnPosicion;
    private JSpinner spnNuevaCalificacion;
    private JTextArea txtResultadosRanking;

    // ======================== COLORES DE ACENTO ========================
    private static final Color AZUL  = new Color(70, 130, 210);
    private static final Color ROJO  = new Color(190, 70, 70);
    private static final Color VERDE = new Color(60, 160, 110);
    private static final Color AMBAR = new Color(200, 155, 50);

    // ======================== FUENTES ========================
    private static final Font FONT_SECCION = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    // ================================================================
    //  CONSTRUCTOR
    // ================================================================
    public Formulario() {
        initModelos();
        cargarDatosBolivia();
        initUI();
        actualizarCombosVertices();   // poblar combos de Tab 3
    }

    private void initModelos() {
        registroPasajerosGlobal = new Pasajeros(3);
        registrosPorRuta = new java.util.HashMap<>();
        grafo   = new Grafo_Pesado<>();
        ranking = new RankingDeLugares();
    }

    /**
     * Pre-carga los 9 departamentos de Bolivia como v\u00e9rtices del grafo,
     * las rutas a\u00e9reas con sus costos en Bs., y el ranking inicial
     * de calificaciones de cada departamento.
     */
    private void cargarDatosBolivia() {
        // ---- V\u00e9rtices (departamentos) ----
        for (String dept : DEPARTAMENTOS) {
            grafo.insertarVertice(dept);
        }

        // ---- Aristas (rutas a\u00e9reas con costo en Bs.) ----
        grafo.insertarArista("La Paz",      "Cochabamba",  350.0);
        grafo.insertarArista("La Paz",      "Oruro",       200.0);
        grafo.insertarArista("La Paz",      "Santa Cruz",  500.0);
        grafo.insertarArista("La Paz",      "Sucre",       420.0);
        grafo.insertarArista("La Paz",      "Trinidad",    550.0);
        grafo.insertarArista("La Paz",      "Cobija",      650.0);
        grafo.insertarArista("Cochabamba",  "Santa Cruz",  300.0);
        grafo.insertarArista("Cochabamba",  "Sucre",       280.0);
        grafo.insertarArista("Cochabamba",  "Trinidad",    420.0);
        grafo.insertarArista("Santa Cruz",  "Trinidad",    350.0);
        grafo.insertarArista("Santa Cruz",  "Tarija",      400.0);
        grafo.insertarArista("Santa Cruz",  "Sucre",       320.0);
        grafo.insertarArista("Sucre",       "Potosi",      150.0);
        grafo.insertarArista("Sucre",       "Tarija",      280.0);
        grafo.insertarArista("Oruro",       "Potosi",      200.0);
        grafo.insertarArista("Potosi",      "Tarija",      350.0);
        grafo.insertarArista("Trinidad",    "Cobija",      450.0);

        // ---- Ranking inicial de departamentos ----
        ranking.insertarLugar("Santa Cruz",  95);
        ranking.insertarLugar("La Paz",      92);
        ranking.insertarLugar("Cochabamba",  90);
        ranking.insertarLugar("Trinidad",    88);
        ranking.insertarLugar("Tarija",      86);
        ranking.insertarLugar("Potosi",      83);
        ranking.insertarLugar("Sucre",       81);
        ranking.insertarLugar("Oruro",       78);
        ranking.insertarLugar("Cobija",      75);
    }

    // ================================================================
    //  INICIALIZACI\u00d3N DE LA INTERFAZ
    // ================================================================
    private void initUI() {
        setTitle("Agencia de Viajes Bolivia \u2014 Sistema de Gesti\u00f3n");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 760);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 660));

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 6));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblTitulo = new JLabel("AGENCIA DE VIAJES \u2014 BOLIVIA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pestanas.addTab("Compra de Pasaje",    crearPanelCompra());
        pestanas.addTab("Recepci\u00f3n",            crearPanelRecepcion());
        pestanas.addTab("Rutas de Vuelo",      crearPanelRutas());
        pestanas.addTab("Ranking de Destinos", crearPanelRanking());

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(pestanas,  BorderLayout.CENTER);
        setContentPane(panelPrincipal);
    }

    // ================================================================
    //  TAB 1 — COMPRA DE PASAJE  (vista del PASAJERO)
    // ================================================================
    private JPanel crearPanelCompra() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));

        // ---- Datos del Pasaje ----
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(crearBorde("Datos del Pasaje", AZUL));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelDatos.add(etiqueta("Nombre del Pasajero:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtNombreCompra = new JTextField(20);
        panelDatos.add(txtNombreCompra, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelDatos.add(etiqueta("Departamento Origen:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbOrigenCompra = new JComboBox<>(DEPARTAMENTOS);
        panelDatos.add(cmbOrigenCompra, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelDatos.add(etiqueta("Departamento Destino:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbDestinoCompra = new JComboBox<>(DEPARTAMENTOS);
        cmbDestinoCompra.setSelectedIndex(2);  // Santa Cruz por defecto
        panelDatos.add(cmbDestinoCompra, gbc);

        // ---- Botones de compra ----
        JPanel panelBtnCompra = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 5));
        panelBtnCompra.setBorder(crearBorde("Acciones", AZUL));
        JButton btnCalcular = boton("Calcular Ruta y Precio");
        JButton btnDirecto  = boton("Vuelo Directo");
        JButton btnComprar  = boton("Comprar Pasaje");
        panelBtnCompra.add(btnCalcular);
        panelBtnCompra.add(btnDirecto);
        panelBtnCompra.add(btnComprar);

        // ---- Feedback / Calificaci\u00f3n ----
        JPanel panelFeedback = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelFeedback.setBorder(crearBorde("Calificar Destino (despu\u00e9s de comprar)", AMBAR));
        panelFeedback.add(etiqueta("Su calificaci\u00f3n (1-100):"));
        spnFeedback = new JSpinner(new SpinnerNumberModel(80, 1, 100, 1));
        panelFeedback.add(spnFeedback);
        JButton btnCalificar = boton("Enviar Calificaci\u00f3n");
        panelFeedback.add(btnCalificar);

        panelControles.add(panelDatos);
        panelControles.add(panelBtnCompra);
        panelControles.add(panelFeedback);

        // ---- Resultados ----
        txtResultadosCompra = new JTextArea();
        txtResultadosCompra.setEditable(false);
        txtResultadosCompra.setFont(FONT_MONO);
        JScrollPane scroll = new JScrollPane(txtResultadosCompra);
        scroll.setBorder(crearBorde("Informaci\u00f3n del Pasaje", AZUL));

        // ---- Eventos ----
        btnCalcular.addActionListener(e  -> accionCalcularRuta());
        btnDirecto.addActionListener(e   -> accionVueloDirecto());
        btnComprar.addActionListener(e   -> accionComprarPasaje());
        btnCalificar.addActionListener(e -> accionCalificarDestino());

        JPanel panelInf = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar = boton("Limpiar");
        btnLimpiar.addActionListener(e -> txtResultadosCompra.setText(""));
        panelInf.add(btnLimpiar);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelInf, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  TAB 2 — RECEPCI\u00d3N  (vista del RECEPCIONISTA de la aerol\u00ednea)
    // ================================================================
    private JPanel crearPanelRecepcion() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));

        // ---- Buscar pasajero ----
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelBuscar.setBorder(crearBorde("Verificar Pasajero", ROJO));
        panelBuscar.add(etiqueta("Nro. Boleto:"));
        txtBoletoRecepcion = new JTextField(10);
        panelBuscar.add(txtBoletoRecepcion);
        JButton btnBuscar    = boton("Buscar");
        JButton btnConfirmar = boton("Confirmar Vuelo");
        JButton btnCancelar  = boton("Cancelar Vuelo");
        panelBuscar.add(btnBuscar);
        panelBuscar.add(btnConfirmar);
        panelBuscar.add(btnCancelar);

        // ---- Gesti\u00f3n del registro ----
        JPanel panelGestion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelGestion.setBorder(crearBorde("Gesti\u00f3n del Registro (\u00c1rbol B)", ROJO));
        
        String[] deptosFiltro = new String[DEPARTAMENTOS.length + 1];
        deptosFiltro[0] = "Todos";
        System.arraycopy(DEPARTAMENTOS, 0, deptosFiltro, 1, DEPARTAMENTOS.length);
        
        cmbOrigenFiltro = new JComboBox<>(deptosFiltro);
        cmbDestinoFiltro = new JComboBox<>(deptosFiltro);
        
        panelGestion.add(etiqueta("Origen:"));
        panelGestion.add(cmbOrigenFiltro);
        panelGestion.add(etiqueta("Destino:"));
        panelGestion.add(cmbDestinoFiltro);

        JButton btnMostrarArbol = boton("Mostrar \u00c1rbol");
        JButton btnInOrden      = boton("Recorrido InOrden");
        JButton btnPorNiveles   = boton("Por Niveles");
        JButton btnCantidad     = boton("Cantidad");
        panelGestion.add(btnMostrarArbol);
        panelGestion.add(btnInOrden);
        panelGestion.add(btnPorNiveles);
        panelGestion.add(btnCantidad);

        panelControles.add(panelBuscar);
        panelControles.add(panelGestion);

        // ---- Resultados ----
        txtResultadosRecepcion = new JTextArea();
        txtResultadosRecepcion.setEditable(false);
        txtResultadosRecepcion.setFont(FONT_MONO);
        JScrollPane scroll = new JScrollPane(txtResultadosRecepcion);
        scroll.setBorder(crearBorde("Resultados", ROJO));

        // ---- Eventos ----
        btnBuscar.addActionListener(e       -> accionBuscarPasajero());
        btnConfirmar.addActionListener(e    -> accionConfirmarVuelo());
        btnCancelar.addActionListener(e     -> accionCancelarVuelo());
        btnMostrarArbol.addActionListener(e -> accionMostrarArbolR());
        btnInOrden.addActionListener(e      -> accionInOrdenR());
        btnPorNiveles.addActionListener(e   -> accionPorNivelesR());
        btnCantidad.addActionListener(e     -> accionCantidadR());

        JPanel panelInf = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar = boton("Limpiar");
        btnLimpiar.addActionListener(e -> txtResultadosRecepcion.setText(""));
        panelInf.add(btnLimpiar);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelInf, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  TAB 3 — RUTAS DE VUELO  (Grafo Pesado + Dijkstra + Prim)
    // ================================================================
    private JPanel crearPanelRutas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));

        // ---- V\u00e9rtices ----
        JPanel panelVertices = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelVertices.setBorder(crearBorde("Gesti\u00f3n de V\u00e9rtices (Departamentos)", VERDE));
        panelVertices.add(etiqueta("V\u00e9rtice:"));
        txtNuevoVertice = new JTextField(12);
        panelVertices.add(txtNuevoVertice);
        JButton btnAgregarV  = boton("Agregar");
        JButton btnEliminarV = boton("Eliminar");
        JButton btnMostrarG  = boton("Mostrar Grafo");
        panelVertices.add(btnAgregarV);
        panelVertices.add(btnEliminarV);
        panelVertices.add(Box.createHorizontalStrut(20));
        panelVertices.add(btnMostrarG);

        // ---- Aristas ----
        JPanel panelAristas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelAristas.setBorder(crearBorde("Gesti\u00f3n de Aristas (Rutas)", VERDE));
        panelAristas.add(etiqueta("Origen:"));
        cmbOrigenArista = new JComboBox<>();
        cmbOrigenArista.setPreferredSize(new Dimension(130, 25));
        panelAristas.add(cmbOrigenArista);
        panelAristas.add(etiqueta("Destino:"));
        cmbDestinoArista = new JComboBox<>();
        cmbDestinoArista.setPreferredSize(new Dimension(130, 25));
        panelAristas.add(cmbDestinoArista);
        panelAristas.add(etiqueta("Peso (Bs.):"));
        txtPesoArista = new JTextField(6);
        panelAristas.add(txtPesoArista);
        JButton btnAgregarA  = boton("Agregar Arista");
        JButton btnEliminarA = boton("Eliminar Arista");
        panelAristas.add(btnAgregarA);
        panelAristas.add(btnEliminarA);

        // ---- Algoritmos ----
        JPanel panelAlgo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelAlgo.setBorder(crearBorde("Algoritmos (Dijkstra / Prim)", VERDE));
        panelAlgo.add(etiqueta("Origen:"));
        cmbOrigenDijkstra = new JComboBox<>();
        cmbOrigenDijkstra.setPreferredSize(new Dimension(130, 25));
        panelAlgo.add(cmbOrigenDijkstra);
        JButton btnDijkstra = boton("Ejecutar Dijkstra");
        panelAlgo.add(btnDijkstra);
        panelAlgo.add(Box.createHorizontalStrut(8));
        panelAlgo.add(etiqueta("Destino:"));
        cmbDestinoDijkstra = new JComboBox<>();
        cmbDestinoDijkstra.setPreferredSize(new Dimension(130, 25));
        panelAlgo.add(cmbDestinoDijkstra);
        JButton btnRuta = boton("Mostrar Ruta");
        panelAlgo.add(btnRuta);
        panelAlgo.add(Box.createHorizontalStrut(16));
        JButton btnPrimBtn = boton("Ejecutar Prim");
        JButton btnMST     = boton("Mostrar MST");
        panelAlgo.add(btnPrimBtn);
        panelAlgo.add(btnMST);

        panelControles.add(panelVertices);
        panelControles.add(panelAristas);
        panelControles.add(panelAlgo);

        // ---- Resultados ----
        txtResultadosRutas = new JTextArea();
        txtResultadosRutas.setEditable(false);
        txtResultadosRutas.setFont(FONT_MONO);
        JScrollPane scroll = new JScrollPane(txtResultadosRutas);
        scroll.setBorder(crearBorde("Resultados", VERDE));

        // ---- Eventos ----
        btnAgregarV.addActionListener(e  -> accionAgregarVertice());
        btnEliminarV.addActionListener(e -> accionEliminarVertice());
        btnMostrarG.addActionListener(e  -> accionMostrarGrafo());
        btnAgregarA.addActionListener(e  -> accionAgregarArista());
        btnEliminarA.addActionListener(e -> accionEliminarArista());
        btnDijkstra.addActionListener(e  -> accionEjecutarDijkstra());
        btnRuta.addActionListener(e      -> accionMostrarRutaDijkstra());
        btnPrimBtn.addActionListener(e   -> accionEjecutarPrim());
        btnMST.addActionListener(e       -> accionMostrarMST());

        JPanel panelInf = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar = boton("Limpiar");
        btnLimpiar.addActionListener(e -> txtResultadosRutas.setText(""));
        panelInf.add(btnLimpiar);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelInf, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  TAB 4 — RANKING DE DESTINOS  (Mont\u00edculo M\u00e1ximo)
    // ================================================================
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));

        // ---- Insertar lugar ----
        JPanel panelInsertar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelInsertar.setBorder(crearBorde("Insertar Lugar", AMBAR));
        panelInsertar.add(etiqueta("Nombre:"));
        txtNombreLugar = new JTextField(15);
        panelInsertar.add(txtNombreLugar);
        panelInsertar.add(etiqueta("Calificaci\u00f3n:"));
        spnCalificacion = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        panelInsertar.add(spnCalificacion);
        JButton btnInsertarL = boton("Insertar Lugar");
        panelInsertar.add(btnInsertarL);

        // ---- Acciones ----
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelAcciones.setBorder(crearBorde("Acciones del Ranking", AMBAR));
        JButton btnVerPop     = boton("Ver M\u00e1s Popular");
        JButton btnElimPop    = boton("Eliminar M\u00e1s Popular");
        JButton btnMostrarRnk = boton("Mostrar Ranking Completo");
        JButton btnTotal      = boton("Total de Lugares");
        panelAcciones.add(btnVerPop);
        panelAcciones.add(btnElimPop);
        panelAcciones.add(btnMostrarRnk);
        panelAcciones.add(btnTotal);

        // ---- Actualizar calificaci\u00f3n ----
        JPanel panelActualizar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelActualizar.setBorder(crearBorde("Actualizar Calificaci\u00f3n (posici\u00f3n en el mont\u00edculo)", AMBAR));
        panelActualizar.add(etiqueta("Posici\u00f3n:"));
        spnPosicion = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        panelActualizar.add(spnPosicion);
        panelActualizar.add(etiqueta("Nueva Calificaci\u00f3n:"));
        spnNuevaCalificacion = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        panelActualizar.add(spnNuevaCalificacion);
        JButton btnActualizar = boton("Actualizar");
        panelActualizar.add(btnActualizar);

        panelControles.add(panelInsertar);
        panelControles.add(panelAcciones);
        panelControles.add(panelActualizar);

        // ---- Resultados ----
        txtResultadosRanking = new JTextArea();
        txtResultadosRanking.setEditable(false);
        txtResultadosRanking.setFont(FONT_MONO);
        JScrollPane scroll = new JScrollPane(txtResultadosRanking);
        scroll.setBorder(crearBorde("Resultados", AMBAR));

        // ---- Eventos ----
        btnInsertarL.addActionListener(e  -> accionInsertarLugar());
        btnVerPop.addActionListener(e     -> accionVerMasPopular());
        btnElimPop.addActionListener(e    -> accionEliminarMasPopular());
        btnMostrarRnk.addActionListener(e -> accionMostrarRankingCompleto());
        btnTotal.addActionListener(e      -> accionTotalLugares());
        btnActualizar.addActionListener(e -> accionActualizarCalificacion());

        JPanel panelInf = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar = boton("Limpiar");
        btnLimpiar.addActionListener(e -> txtResultadosRanking.setText(""));
        panelInf.add(btnLimpiar);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelInf, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  ACCIONES — TAB 1: COMPRA DE PASAJE
    // ================================================================

    /** Calcula la ruta \u00f3ptima y el precio usando Dijkstra. */
    private void accionCalcularRuta() {
        try {
            String origen  = (String) cmbOrigenCompra.getSelectedItem();
            String destino = (String) cmbDestinoCompra.getSelectedItem();

            if (origen.equals(destino)) {
                mostrarError("El departamento de origen y destino no pueden ser el mismo.");
                return;
            }

            Dijkstra<String> dTemp = new Dijkstra<>(grafo);
            dTemp.ejecutar(origen);

            precioCalculado     = dTemp.obtenerCostoMinimoHacia(destino);
            rutaCalculada       = dTemp.obtenerCaminoMasCorto(destino);
            origenSeleccionado  = origen;
            destinoSeleccionado = destino;

            appendCompra("\n========================================\n");
            appendCompra("   RUTA Y PRECIO CALCULADOS\n");
            appendCompra("========================================\n");
            appendCompra("  Origen:    " + origen + "\n");
            appendCompra("  Destino:   " + destino + "\n");
            appendCompra("  Ruta:      " + String.join(" -> ", rutaCalculada) + "\n");
            appendCompra("  Precio:    Bs. " + precioCalculado + "\n");
            appendCompra("========================================\n");
            appendCompra("\nPresione 'Comprar Pasaje' para confirmar.\n\n");
        } catch (Exception ex) {
            mostrarError("Error al calcular ruta: " + ex.getMessage());
        }
    }

    /** Verifica si existe un vuelo directo entre el origen y destino y lo selecciona. */
    private void accionVueloDirecto() {
        try {
            String origen  = (String) cmbOrigenCompra.getSelectedItem();
            String destino = (String) cmbDestinoCompra.getSelectedItem();

            if (origen.equals(destino)) {
                mostrarError("El departamento de origen y destino no pueden ser el mismo.");
                return;
            }

            if (!grafo.existeAdyacencia(origen, destino)) {
                mostrarError("No existe un vuelo directo entre " + origen + " y " + destino + ".");
                return;
            }

            precioCalculado     = grafo.getPeso(origen, destino);
            rutaCalculada       = new ArrayList<>();
            rutaCalculada.add(origen);
            rutaCalculada.add(destino);
            origenSeleccionado  = origen;
            destinoSeleccionado = destino;

            appendCompra("\n========================================\n");
            appendCompra("   VUELO DIRECTO SELECCIONADO\n");
            appendCompra("========================================\n");
            appendCompra("  Origen:    " + origen + "\n");
            appendCompra("  Destino:   " + destino + "\n");
            appendCompra("  Ruta:      " + origen + " -> " + destino + "\n");
            appendCompra("  Precio:    Bs. " + precioCalculado + "\n");
            appendCompra("========================================\n");
            appendCompra("\nPresione 'Comprar Pasaje' para confirmar.\n\n");
        } catch (Exception ex) {
            mostrarError("Error al buscar vuelo directo: " + ex.getMessage());
        }
    }

    /** Registra la compra del pasaje: crea un Boleto y lo inserta en el \u00c1rbol B. */
    private void accionComprarPasaje() {
        try {
            String nombre = txtNombreCompra.getText().trim();
            if (nombre.isEmpty()) {
                mostrarError("Ingrese su nombre completo.");
                return;
            }
            if (precioCalculado <= 0 || rutaCalculada == null) {
                mostrarError("Primero calcule la ruta y precio.");
                return;
            }

            int nroBoleto = siguienteBoleto++;
            int origenInt  = obtenerIndiceDepartamento(origenSeleccionado)  + 1;
            int destinoInt = obtenerIndiceDepartamento(destinoSeleccionado) + 1;

            Boleto boleto = new Boleto(nroBoleto, nombre, precioCalculado, origenInt, destinoInt);
            registroPasajerosGlobal.nuevoPasajero(boleto);
            
            String claveRuta = origenSeleccionado + "-" + destinoSeleccionado;
            Pasajeros registroRuta = registrosPorRuta.getOrDefault(claveRuta, new Pasajeros(3));
            registroRuta.nuevoPasajero(boleto);
            registrosPorRuta.put(claveRuta, registroRuta);

            appendCompra("\n+========================================+\n");
            appendCompra("|    PASAJE COMPRADO EXITOSAMENTE        |\n");
            appendCompra("+========================================+\n");
            appendCompra("  Nro. Boleto: " + nroBoleto + "\n");
            appendCompra("  Pasajero:    " + nombre + "\n");
            appendCompra("  Origen:      " + origenSeleccionado + "\n");
            appendCompra("  Destino:     " + destinoSeleccionado + "\n");
            appendCompra("  Ruta:        " + String.join(" -> ", rutaCalculada) + "\n");
            appendCompra("  Precio:      Bs. " + precioCalculado + "\n");
            appendCompra("+========================================+\n");
            appendCompra("\n>> Guarde su numero de boleto: " + nroBoleto + "\n");
            appendCompra(">> Puede calificar su destino abajo.\n\n");

            ultimoDestinoComprado = destinoSeleccionado;

            // Limpiar estado
            precioCalculado = 0;
            rutaCalculada = null;
            txtNombreCompra.setText("");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * El pasajero califica su destino. La calificaci\u00f3n se promedia
     * con la calificaci\u00f3n actual del lugar en el ranking, actualizando
     * el mont\u00edculo m\u00e1ximo autom\u00e1ticamente.
     */
    private void accionCalificarDestino() {
        if (ultimoDestinoComprado == null) {
            mostrarError("Primero debe comprar un pasaje para poder calificar el destino.");
            return;
        }
        int feedback = (int) spnFeedback.getValue();

        int pos = encontrarPosicionLugar(ultimoDestinoComprado);
        if (pos != -1) {
            Lugar lugar = ranking.verLugarEnPosicion(pos);
            int calAnterior = lugar.getCalifacion();
            int nuevaCalif  = (calAnterior + feedback) / 2;
            ranking.actualizarCalificacion(pos, nuevaCalif);

            appendCompra("[OK] Calificacion enviada para " + ultimoDestinoComprado + "\n");
            appendCompra("     Calificacion anterior: " + calAnterior
                    + " -> Nueva: " + nuevaCalif + "\n");
            appendCompra("     (Vea el ranking actualizado en la pestana 'Ranking de Destinos')\n\n");
        } else {
            appendCompra("[!] No se encontro " + ultimoDestinoComprado + " en el ranking.\n");
        }
        ultimoDestinoComprado = null;
    }

    // ================================================================
    //  ACCIONES — TAB 2: RECEPCI\u00d3N
    // ================================================================

    /** Busca un pasajero por n\u00famero de boleto y muestra sus datos. */
    private void accionBuscarPasajero() {
        try {
            int nro = Integer.parseInt(txtBoletoRecepcion.getText().trim());
            Boleto encontrado = registroPasajerosGlobal.buscarBoleto(new Boleto(nro, "", 0, 0, 0));
            if (encontrado != null) {
                appendRecepcion("\n+======================================+\n");
                appendRecepcion("|     PASAJERO ENCONTRADO              |\n");
                appendRecepcion("+======================================+\n");
                appendRecepcion("  Nro. Boleto: " + encontrado.getNroBoleto() + "\n");
                appendRecepcion("  Nombre:      " + encontrado.getNombrePersona() + "\n");
                appendRecepcion("  Origen:      " + obtenerNombreDepartamento(encontrado.getOrigen()) + "\n");
                appendRecepcion("  Destino:     " + obtenerNombreDepartamento(encontrado.getDestino()) + "\n");
                appendRecepcion("  Precio:      Bs. " + encontrado.getPrecioDeBoleto() + "\n");
                appendRecepcion("+======================================+\n\n");
            } else {
                appendRecepcion("[>>] Boleto #" + nro + " NO encontrado en el sistema.\n\n");
            }
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un numero de boleto valido.");
        }
    }

    /**
     * El recepcionista confirma si el pasajero est\u00e1 registrado y
     * puede abordar el vuelo.
     */
    private void accionConfirmarVuelo() {
        try {
            int nro = Integer.parseInt(txtBoletoRecepcion.getText().trim());
            boolean existe = registroPasajerosGlobal.contieneBoleto(new Boleto(nro, "", 0, 0, 0));

            if (existe) {
                Boleto b = registroPasajerosGlobal.buscarBoleto(new Boleto(nro, "", 0, 0, 0));
                appendRecepcion("\n+======================================+\n");
                appendRecepcion("|     VUELO CONFIRMADO                 |\n");
                appendRecepcion("+======================================+\n");
                appendRecepcion("  Boleto:    #" + nro + "\n");
                appendRecepcion("  Pasajero:  " + b.getNombrePersona() + "\n");
                appendRecepcion("  Ruta:      " + obtenerNombreDepartamento(b.getOrigen())
                        + " -> " + obtenerNombreDepartamento(b.getDestino()) + "\n");
                appendRecepcion("  Estado:    AUTORIZADO PARA ABORDAR\n");
                appendRecepcion("+======================================+\n\n");
            } else {
                appendRecepcion("\n+======================================+\n");
                appendRecepcion("|     VUELO NO CONFIRMADO              |\n");
                appendRecepcion("+======================================+\n");
                appendRecepcion("  Boleto #" + nro + " NO REGISTRADO\n");
                appendRecepcion("  Estado:  NO AUTORIZADO PARA ABORDAR\n");
                appendRecepcion("+======================================+\n\n");
            }
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un numero de boleto valido.");
        }
    }

    /** El recepcionista cancela el vuelo de un pasajero (elimina del \u00c1rbol B). */
    private void accionCancelarVuelo() {
        try {
            int nro = Integer.parseInt(txtBoletoRecepcion.getText().trim());
            Boleto b = registroPasajerosGlobal.buscarBoleto(new Boleto(nro, "", 0, 0, 0));
            if (b == null) {
                mostrarError("El boleto #" + nro + " no existe.");
                return;
            }
            
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Cancelar el vuelo del boleto #" + nro + "?",
                    "Confirmar cancelacion", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (opcion == JOptionPane.YES_OPTION) {
                registroPasajerosGlobal.cancelarVuelo(new Boleto(nro, "", 0, 0, 0));
                
                String origenStr = obtenerNombreDepartamento(b.getOrigen());
                String destinoStr = obtenerNombreDepartamento(b.getDestino());
                String claveRuta = origenStr + "-" + destinoStr;
                
                if (registrosPorRuta.containsKey(claveRuta)) {
                    registrosPorRuta.get(claveRuta).cancelarVuelo(b);
                }
                
                appendRecepcion("[X] Vuelo cancelado para boleto #" + nro + "\n\n");
            }
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un numero de boleto valido.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // -- Operaciones del \u00c1rbol B (Recepcionista) --

    private Pasajeros obtenerRegistroSeleccionado() {
        String origen = (String) cmbOrigenFiltro.getSelectedItem();
        String destino = (String) cmbDestinoFiltro.getSelectedItem();
        
        if (origen.equals("Todos") || destino.equals("Todos")) {
            return registroPasajerosGlobal;
        }
        
        String clave = origen + "-" + destino;
        return registrosPorRuta.getOrDefault(clave, new Pasajeros(3));
    }

    private String obtenerNombreRegistroSeleccionado() {
        String origen = (String) cmbOrigenFiltro.getSelectedItem();
        String destino = (String) cmbDestinoFiltro.getSelectedItem();
        
        if (origen.equals("Todos") || destino.equals("Todos")) {
            return "GLOBAL (Todos los pasajeros y destinos)";
        }
        return "RUTA: " + origen + " -> " + destino;
    }

    private void accionMostrarArbolR() {
        Pasajeros registro = obtenerRegistroSeleccionado();
        String nombreReg = obtenerNombreRegistroSeleccionado();
        
        if (registro.esVacio()) {
            appendRecepcion("No hay pasajeros registrados para: " + nombreReg + ".\n");
            return;
        }
        String arbol = capturarSalida(() -> registro.mostrarArbol());
        appendRecepcion("\n=== Estructura del \u00c1rbol B [" + nombreReg + "] ===\n" + arbol + "\n");
    }

    private void accionInOrdenR() {
        Pasajeros registro = obtenerRegistroSeleccionado();
        String nombreReg = obtenerNombreRegistroSeleccionado();
        
        if (registro.esVacio()) {
            appendRecepcion("No hay pasajeros registrados para: " + nombreReg + ".\n");
            return;
        }
        List<Boleto> lista = registro.listarPasajerosEnOrden();
        appendRecepcion("\n=== Pasajeros en orden [" + nombreReg + "] ===\n");
        for (Boleto b : lista) {
            appendRecepcion("  #" + b.getNroBoleto() + " | " + b.getNombrePersona()
                    + " | " + obtenerNombreDepartamento(b.getOrigen())
                    + " -> " + obtenerNombreDepartamento(b.getDestino())
                    + " | Bs. " + b.getPrecioDeBoleto() + "\n");
        }
        appendRecepcion("\n");
    }

    private void accionPorNivelesR() {
        Pasajeros registro = obtenerRegistroSeleccionado();
        String nombreReg = obtenerNombreRegistroSeleccionado();
        
        if (registro.esVacio()) {
            appendRecepcion("No hay pasajeros registrados para: " + nombreReg + ".\n");
            return;
        }
        try {
            List<Boleto> lista = registro.listarPasajerosPorNiveles();
            appendRecepcion("\n=== Pasajeros por niveles [" + nombreReg + "] ===\n");
            for (Boleto b : lista) {
                appendRecepcion("  #" + b.getNroBoleto() + " | " + b.getNombrePersona()
                        + " | " + obtenerNombreDepartamento(b.getOrigen())
                        + " -> " + obtenerNombreDepartamento(b.getDestino())
                        + " | Bs. " + b.getPrecioDeBoleto() + "\n");
            }
            appendRecepcion("\n");
        } catch (IllegalArgumentException ex) {
            appendRecepcion("No hay pasajeros registrados para: " + nombreReg + ".\n");
        }
    }

    private void accionCantidadR() {
        Pasajeros registro = obtenerRegistroSeleccionado();
        String nombreReg = obtenerNombreRegistroSeleccionado();
        appendRecepcion("Pasajeros registrados en [" + nombreReg + "]: " + registro.cantidadDePasajeros() + "\n");
    }

    // ================================================================
    //  ACCIONES — TAB 3: RUTAS DE VUELO
    // ================================================================

    private void accionAgregarVertice() {
        try {
            String vertice = txtNuevoVertice.getText().trim();
            if (vertice.isEmpty()) { mostrarError("Ingrese un nombre de vertice."); return; }
            grafo.insertarVertice(vertice);
            actualizarCombosVertices();
            txtNuevoVertice.setText("");
            appendRutas("[OK] Vertice '" + vertice + "' agregado.\n");
        } catch (IllegalArgumentException ex) { mostrarError(ex.getMessage()); }
    }

    private void accionEliminarVertice() {
        try {
            String vertice = txtNuevoVertice.getText().trim();
            if (vertice.isEmpty()) { mostrarError("Ingrese el nombre del vertice."); return; }
            grafo.eliminarVertice(vertice);
            actualizarCombosVertices();
            txtNuevoVertice.setText("");
            dijkstra = null; prim = null;
            appendRutas("[X]  Vertice '" + vertice + "' eliminado.\n");
        } catch (IllegalArgumentException | NullPointerException ex) { mostrarError(ex.getMessage()); }
    }

    private void accionMostrarGrafo() {
        if (grafo.cantidadDeVertices() == 0) {
            appendRutas("El grafo esta vacio.\n"); return;
        }
        appendRutas("\n=== Red de Rutas Aereas ===\n");
        appendRutas("Departamentos: " + grafo.cantidadDeVertices()
                + "  |  Rutas: " + grafo.cantidadDeAristas() + "\n");
        appendRutas(grafo.toString() + "\n");
    }

    private void accionAgregarArista() {
        try {
            String origen  = (String) cmbOrigenArista.getSelectedItem();
            String destino = (String) cmbDestinoArista.getSelectedItem();
            if (origen == null || destino == null) { mostrarError("Seleccione origen y destino."); return; }
            double peso = Double.parseDouble(txtPesoArista.getText().trim());
            grafo.insertarArista(origen, destino, peso);
            txtPesoArista.setText("");
            dijkstra = null; prim = null;
            appendRutas("[OK] Ruta: " + origen + " <-> " + destino + " (Bs. " + peso + ")\n");
        } catch (NumberFormatException ex) {
            mostrarError("El precio debe ser un numero valido.");
        } catch (IllegalArgumentException ex) { mostrarError(ex.getMessage()); }
    }

    private void accionEliminarArista() {
        try {
            String origen  = (String) cmbOrigenArista.getSelectedItem();
            String destino = (String) cmbDestinoArista.getSelectedItem();
            if (origen == null || destino == null) { mostrarError("Seleccione origen y destino."); return; }
            grafo.eliminarArista(origen, destino);
            dijkstra = null; prim = null;
            appendRutas("[X]  Ruta eliminada: " + origen + " <-> " + destino + "\n");
        } catch (IllegalArgumentException ex) { mostrarError(ex.getMessage()); }
    }

    private void accionEjecutarDijkstra() {
        try {
            String origen = (String) cmbOrigenDijkstra.getSelectedItem();
            if (origen == null) { mostrarError("Seleccione un vertice de origen."); return; }
            dijkstra = new Dijkstra<>(grafo);
            dijkstra.ejecutar(origen);
            appendRutas("\n[OK] Dijkstra ejecutado desde '" + origen + "'.\n");
            appendRutas("     Seleccione un destino y presione 'Mostrar Ruta'.\n\n");
        } catch (Exception ex) { mostrarError("Error Dijkstra: " + ex.getMessage()); }
    }

    private void accionMostrarRutaDijkstra() {
        try {
            if (dijkstra == null) { mostrarAdvertencia("Primero ejecute Dijkstra."); return; }
            String destino = (String) cmbDestinoDijkstra.getSelectedItem();
            if (destino == null) { mostrarError("Seleccione un destino."); return; }
            double costo = dijkstra.obtenerCostoMinimoHacia(destino);
            List<String> camino = dijkstra.obtenerCaminoMasCorto(destino);
            appendRutas("\n=== Ruta mas corta hacia '" + destino + "' ===\n");
            appendRutas("  Costo minimo: Bs. " + costo + "\n");
            appendRutas("  Ruta optima:  " + String.join(" -> ", camino) + "\n\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void accionEjecutarPrim() {
        try {
            String origen = (String) cmbOrigenDijkstra.getSelectedItem();
            if (origen == null) { mostrarError("Seleccione un vertice de origen."); return; }
            prim = new Prim<>(grafo);
            prim.ejecutar(origen);
            appendRutas("\n[OK] Prim ejecutado desde '" + origen + "'.\n\n");
        } catch (Exception ex) { mostrarError("Error Prim: " + ex.getMessage()); }
    }

    private void accionMostrarMST() {
        try {
            if (prim == null) { mostrarAdvertencia("Primero ejecute Prim."); return; }
            String resultado = capturarSalida(() -> prim.mostrarResultadoMST());
            appendRutas("\n" + resultado + "\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void actualizarCombosVertices() {
        String[] vertices = obtenerVerticesComoArray();
        cmbOrigenArista.setModel(new DefaultComboBoxModel<>(vertices));
        cmbDestinoArista.setModel(new DefaultComboBoxModel<>(vertices));
        cmbOrigenDijkstra.setModel(new DefaultComboBoxModel<>(vertices));
        cmbDestinoDijkstra.setModel(new DefaultComboBoxModel<>(vertices));
    }

    private String[] obtenerVerticesComoArray() {
        ArrayList<String> lista = new ArrayList<>();
        for (String v : grafo.getVertices()) { lista.add(v); }
        return lista.toArray(new String[0]);
    }

    // ================================================================
    //  ACCIONES — TAB 4: RANKING DE DESTINOS
    // ================================================================

    private void accionInsertarLugar() {
        try {
            String nombre = txtNombreLugar.getText().trim();
            if (nombre.isEmpty()) { mostrarError("Ingrese el nombre del lugar."); return; }
            int cal = (int) spnCalificacion.getValue();
            ranking.insertarLugar(nombre, cal);
            txtNombreLugar.setText("");
            appendRanking("[OK] Lugar insertado: " + nombre + " (Calificacion: " + cal + ")\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void accionVerMasPopular() {
        try {
            if (ranking.esVacio()) { appendRanking("El ranking esta vacio.\n"); return; }
            Lugar lugar = ranking.mostrarLugarMasPopular();
            appendRanking("[*] Mas popular: " + lugar.getNombre()
                    + " (Calificacion: " + lugar.getCalifacion() + ")\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void accionEliminarMasPopular() {
        try {
            if (ranking.esVacio()) { appendRanking("El ranking esta vacio.\n"); return; }
            Lugar lugar = ranking.eliminarMasPopular();
            appendRanking("[X] Eliminado: " + lugar.getNombre()
                    + " (Calificacion: " + lugar.getCalifacion() + ")\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void accionMostrarRankingCompleto() {
        try {
            if (ranking.esVacio()) { appendRanking("El ranking esta vacio.\n"); return; }
            String resultado = capturarSalida(() -> ranking.mostrarRankingCompleto());
            appendRanking("\n=== Ranking de Destinos (mayor a menor) ===\n"
                    + resultado + "\n");
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    private void accionTotalLugares() {
        appendRanking("Total de lugares en el ranking: " + ranking.size() + "\n");
    }

    private void accionActualizarCalificacion() {
        try {
            int pos = (int) spnPosicion.getValue();
            int cal = (int) spnNuevaCalificacion.getValue();
            ranking.actualizarCalificacion(pos, cal);
            appendRanking("[OK] Posicion " + pos + " actualizada a calificacion " + cal + "\n");
        } catch (IndexOutOfBoundsException ex) {
            mostrarError("Posicion fuera de rango. Rango valido: 0 a " + (ranking.size() - 1));
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    // ================================================================
    //  UTILIDADES
    // ================================================================

    /** Captura System.out de un Runnable y devuelve el texto. */
    private String capturarSalida(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try { accion.run(); }
        finally { System.setOut(original); }
        return baos.toString();
    }

    /** Devuelve el \u00edndice (0-based) de un departamento en el array. */
    private int obtenerIndiceDepartamento(String nombre) {
        for (int i = 0; i < DEPARTAMENTOS.length; i++) {
            if (DEPARTAMENTOS[i].equals(nombre)) return i;
        }
        return -1;
    }

    /** Convierte el n\u00famero de departamento (1-based) a nombre. */
    private String obtenerNombreDepartamento(int numero) {
        if (numero >= 1 && numero <= DEPARTAMENTOS.length) {
            return DEPARTAMENTOS[numero - 1];
        }
        return "Desconocido (" + numero + ")";
    }

    /**
     * Busca un lugar por nombre en el ranking (mont\u00edculo)
     * recorriendo todas las posiciones.
     * @return posici\u00f3n (0-based) o -1 si no se encuentra.
     */
    private int encontrarPosicionLugar(String nombre) {
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.verLugarEnPosicion(i).getNombre().equals(nombre)) {
                return i;
            }
        }
        return -1;
    }

    // -- F\u00e1bricas de componentes --

    private TitledBorder crearBorde(String titulo, Color color) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 1),
                " " + titulo + " ",
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_SECCION, color);
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private JButton boton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    // -- Di\u00e1logos --

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // -- Append con auto-scroll --

    private void appendCompra(String t) {
        txtResultadosCompra.append(t);
        txtResultadosCompra.setCaretPosition(txtResultadosCompra.getDocument().getLength());
    }

    private void appendRecepcion(String t) {
        txtResultadosRecepcion.append(t);
        txtResultadosRecepcion.setCaretPosition(txtResultadosRecepcion.getDocument().getLength());
    }

    private void appendRutas(String t) {
        txtResultadosRutas.append(t);
        txtResultadosRutas.setCaretPosition(txtResultadosRutas.getDocument().getLength());
    }

    private void appendRanking(String t) {
        txtResultadosRanking.append(t);
        txtResultadosRanking.setCaretPosition(txtResultadosRanking.getDocument().getLength());
    }

    // ================================================================
    //  MAIN
    // ================================================================

    /**
     * @param args argumentos de l\u00ednea de comandos
     */
    public static void main(String args[]) {
        /* Establecer Nimbus Look and Feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Formulario.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Crear y mostrar el formulario */
        java.awt.EventQueue.invokeLater(() -> new Formulario().setVisible(true));
    }
}
