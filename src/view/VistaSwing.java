package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementación Swing de la vista del juego
 * "con traza visible y de buen manejo que pinte la mesa por círculos y la pila por columnas"
 */
public class VistaSwing implements VistaJuego {
    
    // Componentes principales
    private JFrame ventanaPrincipal;
    private PanelCircular panelMesa;
    private PanelColumnas panelPila;
    private JPanel panelControl;
    private JTextArea areaInfo;
    private JTextArea areaEstadisticas;
    
    // Controles de acción
    private JButton btnArrimarDerecha;
    private JButton btnArrimarIzquierda;
    private JButton btnSacarOlvido;
    private JButton btnMeterMano;
    private JButton btnNuevoJuego;
    
    // Estado actual
    private EstadoRueda estadoActual;
    private CompletableFuture<AccionPastor> futuraAccion;
    private CompletableFuture<Boolean> futuraDireccion;
    
    // Configuración visual
    private final Color COLOR_MESA = new Color(139, 69, 19); // Marrón
    private final Color COLOR_PASTOR_NORMAL = new Color(70, 130, 180); // Azul acero
    private final Color COLOR_PASTOR_ACTUAL = new Color(255, 215, 0); // Dorado
    private final Color COLOR_PASTOR_RICO = new Color(50, 205, 50); // Verde lima
    private final Color COLOR_PASTOR_POBRE = new Color(220, 20, 60); // Carmesí
    private final Color COLOR_PILA = new Color(105, 105, 105); // Gris dim
    
    public VistaSwing() {
        // Constructor vacío, la inicialización se hace en inicializar()
    }
    
    @Override
    public void inicializar(int numPastores, int valorN) {
        SwingUtilities.invokeLater(() -> {
            crearVentanaPrincipal(numPastores, valorN);
            configurarComponentes();
            configurarEventos();
            ventanaPrincipal.setVisible(true);
        });
    }
    
    /**
     * Crea la ventana principal con todos sus componentes
     */
    private void crearVentanaPrincipal(int numPastores, int valorN) {
        ventanaPrincipal = new JFrame("Juego de Pastores - Círculo de Josefo Modificado");
        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaPrincipal.setLayout(new BorderLayout());
        ventanaPrincipal.setSize(1200, 800);
        ventanaPrincipal.setLocationRelativeTo(null);
        
        // Panel superior - Información del juego
        JPanel panelSuperior = crearPanelSuperior(numPastores, valorN);
        ventanaPrincipal.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Mesa circular y pila
        JPanel panelCentral = crearPanelCentral();
        ventanaPrincipal.add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - Controles
        panelControl = crearPanelControl();
        ventanaPrincipal.add(panelControl, BorderLayout.SOUTH);
        
        // Panel lateral - Información y estadísticas
        JPanel panelLateral = crearPanelLateral();
        ventanaPrincipal.add(panelLateral, BorderLayout.EAST);
    }
    
    /**
     * Crea el panel superior con información del juego
     */
    private JPanel crearPanelSuperior(int numPastores, int valorN) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Configuración del Juego"));
        
        panel.add(new JLabel("Pastores iniciales: " + numPastores));
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Valor N (conteo): " + valorN));
        panel.add(Box.createHorizontalStrut(20));
        
        JLabel lblTurno = new JLabel("Turno: 0");
        lblTurno.setFont(lblTurno.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblTurno);
        
        return panel;
    }
    
    /**
     * Crea el panel central con mesa y pila
     */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Mesa circular (lado izquierdo)
        panelMesa = new PanelCircular();
        panelMesa.setPreferredSize(new Dimension(500, 500));
        panelMesa.setBorder(BorderFactory.createTitledBorder("Mesa Redonda"));
        
        // Pila de desposeídos (lado derecho)
        panelPila = new PanelColumnas();
        panelPila.setPreferredSize(new Dimension(300, 500));
        panelPila.setBorder(BorderFactory.createTitledBorder("Pila de Desposeídos"));
        
        panel.add(panelMesa, BorderLayout.CENTER);
        panel.add(panelPila, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea el panel de controles
     */
    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Acciones Disponibles"));
        
        btnArrimarDerecha = new JButton("Arrimar Guadaña →");
        btnArrimarIzquierda = new JButton("← Arrimar Guadaña");
        btnSacarOlvido = new JButton("Sacar del Olvido");
        btnMeterMano = new JButton("Meter Mano en Faltriquera");
        btnNuevoJuego = new JButton("Nuevo Juego");
        
        // Colores y estilos
        btnArrimarDerecha.setBackground(new Color(255, 99, 71));
        btnArrimarIzquierda.setBackground(new Color(255, 99, 71));
        btnSacarOlvido.setBackground(new Color(60, 179, 113));
        btnMeterMano.setBackground(new Color(255, 165, 0));
        btnNuevoJuego.setBackground(new Color(100, 149, 237));
        
        panel.add(btnArrimarIzquierda);
        panel.add(btnArrimarDerecha);
        panel.add(btnSacarOlvido);
        panel.add(btnMeterMano);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(btnNuevoJuego);
        
        return panel;
    }
    
    /**
     * Crea el panel lateral con información
     */
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(250, 0));
        
        // Área de información del turno
        areaInfo = new JTextArea(8, 20);
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaInfo.setBorder(BorderFactory.createTitledBorder("Información del Turno"));
        
        // Área de estadísticas
        areaEstadisticas = new JTextArea(12, 20);
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        areaEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas"));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.NORTH);
        panel.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Configura los componentes iniciales
     */
    private void configurarComponentes() {
        habilitarBotones(false);
        areaInfo.setText("Esperando inicio del juego...");
        areaEstadisticas.setText("Estadísticas aparecerán aquí");
    }
    
    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        btnArrimarDerecha.addActionListener(e -> completarAccion(AccionPastor.ARRIMAR_GUADAÑA_DERECHA));
        btnArrimarIzquierda.addActionListener(e -> completarAccion(AccionPastor.ARRIMAR_GUADAÑA_IZQUIERDA));
        btnSacarOlvido.addActionListener(e -> completarAccion(AccionPastor.SACAR_DEL_OLVIDO));
        btnMeterMano.addActionListener(e -> completarAccion(AccionPastor.METER_MANO_FALTRIQUERA));
    }
    
    /**
     * Completa una acción cuando el usuario hace clic
     */
    private void completarAccion(AccionPastor accion) {
        if (futuraAccion != null && !futuraAccion.isDone()) {
            futuraAccion.complete(accion);
        }
    }
    
    @Override
    public void actualizarEstado(EstadoRueda estado) {
        this.estadoActual = estado;
        SwingUtilities.invokeLater(() -> {
            pintarMesa(estado.getPastoresEnMesa(), estado.getPosicionPastorActual());
            pintarPila(estado.getPastoresDesposeidos());
            mostrarTurno(estado.getPastorConTurno(), estado.isPuedeHurtar(), 
                        estado.isPuedeRescatar(), estado.getTurno());
            mostrarEstadisticas(estado);
            
            if (estado.isDanzaTerminada()) {
                mostrarReyFinal(estado.getGanador());
            }
        });
    }
    
    @Override
    public void pintarMesa(List<Pastor> pastores, int posicionActual) {
        SwingUtilities.invokeLater(() -> {
            panelMesa.setPastores(pastores, posicionActual);
            panelMesa.repaint();
        });
    }
    
    @Override
    public void pintarPila(List<Pastor> desposeidos) {
        SwingUtilities.invokeLater(() -> {
            panelPila.setDesposeidos(desposeidos);
            panelPila.repaint();
        });
    }
    
    @Override
    public void mostrarTurno(Pastor pastorActual, boolean puedeHurtar, boolean puedeRescatar, int turno) {
        SwingUtilities.invokeLater(() -> {
            if (pastorActual == null) return;
            
            StringBuilder info = new StringBuilder();
            info.append("=== TURNO ").append(turno).append(" ===\n");
            info.append("Pastor: ").append(pastorActual.getNombre()).append("\n");
            info.append("Doblones: ").append(pastorActual.getDoblones()).append("\n");
            info.append("Feligreses: ").append(pastorActual.getFeligreses()).append("\n");
            info.append("Trato: ").append(pastorActual.getTrato()).append("\n\n");
            
            info.append("Acciones disponibles:\n");
            info.append("• Arrimar guadaña\n");
            if (puedeRescatar) info.append("• Sacar del olvido\n");
            if (puedeHurtar) info.append("• Hurto piadoso\n");
            
            areaInfo.setText(info.toString());
            
            // Actualizar título de ventana
            ventanaPrincipal.setTitle("Juego de Pastores - Turno " + turno + 
                                    " (" + pastorActual.getNombre() + ")");
        });
    }
    
    @Override
    public void mostrarEstadisticas(EstadoRueda estado) {
        SwingUtilities.invokeLater(() -> {
            EstadisticasRecursos estatsMesa = estado.getEstadisticasRecursosMesa();
            EstadisticasRecursos estatsPila = estado.getEstadisticasRecursosPila();
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== EN LA MESA ===\n");
            stats.append("Pastores: ").append(estatsMesa.getNumPastores()).append("\n");
            stats.append("Doblones: ").append(estatsMesa.getTotalDoblones()).append("\n");
            stats.append("Feligreses: ").append(estatsMesa.getTotalFeligreses()).append("\n");
            stats.append("Promedio D: ").append(String.format("%.1f", estatsMesa.getPromedioDoblones())).append("\n");
            stats.append("Promedio F: ").append(String.format("%.1f", estatsMesa.getPromedioFeligreses())).append("\n\n");
            
            stats.append("=== DESPOSEÍDOS ===\n");
            stats.append("Pastores: ").append(estatsPila.getNumPastores()).append("\n");
            stats.append("Doblones: ").append(estatsPila.getTotalDoblones()).append("\n");
            stats.append("Feligreses: ").append(estatsPila.getTotalFeligreses()).append("\n\n");
            
            stats.append("=== MÁS RICO ===\n");
            if (estado.getMasRico() != null) {
                Pastor rico = estado.getMasRico();
                stats.append(rico.getNombre()).append("\n");
                stats.append("D: ").append(rico.getDoblones()).append(" F: ").append(rico.getFeligreses()).append("\n\n");
            }
            
            stats.append("=== MÁS POBRE ===\n");
            if (estado.getMasPobre() != null) {
                Pastor pobre = estado.getMasPobre();
                stats.append(pobre.getNombre()).append("\n");
                stats.append("D: ").append(pobre.getDoblones()).append(" F: ").append(pobre.getFeligreses()).append("\n");
            }
            
            areaEstadisticas.setText(stats.toString());
        });
    }
    
    @Override
    public AccionPastor pedirAccion(List<AccionPastor> accionesDisponibles) {
        SwingUtilities.invokeLater(() -> {
            // Habilitar solo los botones de acciones disponibles
            btnArrimarDerecha.setEnabled(accionesDisponibles.contains(AccionPastor.ARRIMAR_GUADAÑA_DERECHA));
            btnArrimarIzquierda.setEnabled(accionesDisponibles.contains(AccionPastor.ARRIMAR_GUADAÑA_IZQUIERDA));
            btnSacarOlvido.setEnabled(accionesDisponibles.contains(AccionPastor.SACAR_DEL_OLVIDO));
            btnMeterMano.setEnabled(accionesDisponibles.contains(AccionPastor.METER_MANO_FALTRIQUERA));
        });
        
        // Crear future para esperar acción del usuario
        futuraAccion = new CompletableFuture<>();
        
        try {
            return futuraAccion.get(); // Bloquea hasta que el usuario haga clic
        } catch (Exception e) {
            return AccionPastor.ARRIMAR_GUADAÑA_DERECHA; // Default
        } finally {
            SwingUtilities.invokeLater(() -> habilitarBotones(false));
        }
    }
    
    @Override
    public boolean pedirDireccion() {
        // Para simplificar, las direcciones ya están en las acciones
        // Este método se puede usar para casos especiales
        return true; // Default: derecha
    }
    
    @Override
    public void mostrarDeguello(Pastor eliminado, Pastor eliminador, boolean direccion) {
        SwingUtilities.invokeLater(() -> {
            String dir = direccion ? "derecha" : "izquierda";
            String mensaje = String.format("¡DEGÜELLO EJECUTADO!\n\n%s eliminó a %s\n(mirando hacia la %s)", 
                                         eliminador.getNombre(), eliminado.getNombre(), dir);
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "Degüello", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    @Override
    public void mostrarRescate(Pastor rescatado, Pastor rescatador) {
        SwingUtilities.invokeLater(() -> {
            String mensaje = String.format("¡RESCATE EXITOSO!\n\n%s rescató a %s del olvido\n\"Como Lázaro al llamado\"", 
                                         rescatador.getNombre(), rescatado.getNombre());
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "Rescate", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public void mostrarHurtoPiadoso(Pastor ladron, Pastor victima, int doblonesRobados, int feligresesRobados) {
        SwingUtilities.invokeLater(() -> {
            String mensaje = String.format("¡HURTO PIADOSO!\n\n%s le quitó la tercia parte a %s\n" +
                                         "Botín: %d doblones, %d feligreses", 
                                         ladron.getNombre(), victima.getNombre(), 
                                         doblonesRobados, feligresesRobados);
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "Hurto Piadoso", JOptionPane.PLAIN_MESSAGE);
        });
    }
    
    @Override
    public void mostrarReyFinal(Pastor ganador) {
        SwingUtilities.invokeLater(() -> {
            if (ganador == null) return;
            
            String mensaje = String.format("¡JUEGO TERMINADO!\n\n" +
                                         "REY DE BURLAS Y VERAS:\n%s\n\n" +
                                         "Dueño de bolsas y conciencias\n" +
                                         "Doblones finales: %d\n" +
                                         "Feligreses finales: %d", 
                                         ganador.getNombre(), ganador.getDoblones(), ganador.getFeligreses());
            
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
            habilitarBotones(false);
        });
    }
    
    @Override
    public void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(ventanaPrincipal, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public boolean pedirConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(ventanaPrincipal, mensaje, "Confirmación", 
                                           JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    @Override
    public ConfiguracionRueda solicitarConfiguracion() {
        // Diálogo de configuración inicial
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JSpinner spinnerPastores = new JSpinner(new SpinnerNumberModel(10, 2, 20, 1));
        JSpinner spinnerN = new JSpinner(new SpinnerNumberModel(3, 1, 19, 1));
        JCheckBox chkAleatorio = new JCheckBox("Recursos aleatorios", true);
        
        panel.add(new JLabel("Número de pastores:"));
        panel.add(spinnerPastores);
        panel.add(new JLabel("Valor N (conteo):"));
        panel.add(spinnerN);
        panel.add(new JLabel("Configuración:"));
        panel.add(chkAleatorio);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Configuración del Juego", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            int pastores = (Integer) spinnerPastores.getValue();
            int n = (Integer) spinnerN.getValue();
            return new ConfiguracionRueda(pastores, n, 300, 150, chkAleatorio.isSelected());
        }
        
        return null; // Usuario canceló
    }
    
    @Override
    public void limpiar() {
        SwingUtilities.invokeLater(() -> {
            panelMesa.limpiar();
            panelPila.limpiar();
            areaInfo.setText("");
            areaEstadisticas.setText("");
            habilitarBotones(false);
        });
    }
    
    @Override
    public void habilitarInteraccion(boolean habilitado) {
        SwingUtilities.invokeLater(() -> habilitarBotones(habilitado));
    }
    
    /**
     * Habilita o deshabilita todos los botones de acción
     */
    private void habilitarBotones(boolean habilitado) {
        btnArrimarDerecha.setEnabled(habilitado);
        btnArrimarIzquierda.setEnabled(habilitado);
        btnSacarOlvido.setEnabled(habilitado);
        btnMeterMano.setEnabled(habilitado);
    }
    
    @Override
    public void resaltarPastor(Pastor pastor, TipoResaltado tipo) {
        // Implementar resaltado específico si es necesario
        SwingUtilities.invokeLater(() -> panelMesa.repaint());
    }
    
    @Override
    public void animarEliminacion(Pastor pastor) {
        // Implementar animación simple
        SwingUtilities.invokeLater(() -> {
            // Parpadeo o efecto visual
            panelMesa.repaint();
        });
    }
    
    @Override
    public void animarRescate(Pastor pastor) {
        // Implementar animación de rescate
        SwingUtilities.invokeLater(() -> panelMesa.repaint());
    }
    
    @Override
    public void actualizarRecursosPastor(Pastor pastor) {
        // Actualización específica de recursos
        SwingUtilities.invokeLater(() -> panelMesa.repaint());
    }
    
    @Override
    public void cerrar() {
        SwingUtilities.invokeLater(() -> {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.dispose();
            }
        });
    }
}