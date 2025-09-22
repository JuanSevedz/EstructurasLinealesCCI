package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import model.*;

/**
 * Panel que dibuja la mesa redonda con los pastores en círculo
 * "que pinte la mesa por círculos"
 */
public class PanelCircular extends JPanel {
    
    private List<Pastor> pastores;
    private int posicionActual;
    private Pastor masRico;
    private Pastor masPobre;
    
    // Configuración visual
    private final Color COLOR_MESA = new Color(139, 69, 19); // Marrón mesa
    private final Color COLOR_PASTOR_NORMAL = new Color(70, 130, 180); // Azul acero
    private final Color COLOR_PASTOR_ACTUAL = new Color(255, 215, 0); // Dorado
    private final Color COLOR_PASTOR_RICO = new Color(50, 205, 50); // Verde lima
    private final Color COLOR_PASTOR_POBRE = new Color(220, 20, 60); // Carmesí
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_FONDO = new Color(245, 245, 220); // Beige
    
    // Configuración de tamaños
    private final int RADIO_MESA = 180;
    private final int RADIO_PASTOR = 35;
    private final int RADIO_PASTOR_ACTUAL = 45;
    private final Font FONT_NOMBRE = new Font("Arial", Font.BOLD, 10);
    private final Font FONT_RECURSOS = new Font("Arial", Font.PLAIN, 8);
    
    public PanelCircular() {
        this.pastores = new ArrayList<>();
        this.posicionActual = -1;
        setBackground(COLOR_FONDO);
        setDoubleBuffered(true);
    }
    
    /**
     * Establece los pastores a mostrar en la mesa
     * pastores lista de pastores
     * posicionActual posición del pastor con turno (-1 si no hay)
     */
    public void setPastores(List<Pastor> pastores, int posicionActual) {
        this.pastores = new ArrayList<>(pastores);
        this.posicionActual = posicionActual;
        calcularPastoresEspeciales();
        repaint();
    }
    
    /**
     * Calcula quién es el más rico y más pobre para resaltarlos
     */
    private void calcularPastoresEspeciales() {
        if (pastores.isEmpty()) {
            masRico = null;
            masPobre = null;
            return;
        }
        
        masRico = pastores.get(0);
        masPobre = pastores.get(0);
        
        for (Pastor pastor : pastores) {
            if (pastor.getDoblones() > masRico.getDoblones()) {
                masRico = pastor;
            }
            if (pastor.getDoblones() < masPobre.getDoblones()) {
                masPobre = pastor;
            }
        }
    }
    
    /**
     * Limpia el panel
     */
    public void limpiar() {
        pastores.clear();
        posicionActual = -1;
        masRico = null;
        masPobre = null;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Activar antialiasing para mejor calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Calcular centro del panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Dibujar mesa redonda
        dibujarMesa(g2d, centerX, centerY);
        
        // Dibujar pastores si los hay
        if (!pastores.isEmpty()) {
            dibujarPastores(g2d, centerX, centerY);
        } else {
            dibujarMensajeVacio(g2d, centerX, centerY);
        }
        
        // Dibujar leyenda
        dibujarLeyenda(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Dibuja la mesa circular
     */
    private void dibujarMesa(Graphics2D g2d, int centerX, int centerY) {
        // Sombra de la mesa
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(centerX - RADIO_MESA + 5, centerY - RADIO_MESA + 5, 
                    RADIO_MESA * 2, RADIO_MESA * 2);
        
        // Mesa principal
        g2d.setColor(COLOR_MESA);
        g2d.fillOval(centerX - RADIO_MESA, centerY - RADIO_MESA, 
                    RADIO_MESA * 2, RADIO_MESA * 2);
        
        // Borde de la mesa
        g2d.setColor(COLOR_MESA.darker());
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centerX - RADIO_MESA, centerY - RADIO_MESA, 
                    RADIO_MESA * 2, RADIO_MESA * 2);
        
        // Círculo interior (decorativo)
        g2d.setColor(COLOR_MESA.brighter());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centerX - RADIO_MESA + 20, centerY - RADIO_MESA + 20, 
                    (RADIO_MESA - 20) * 2, (RADIO_MESA - 20) * 2);
    }
    
    /**
     * Dibuja todos los pastores alrededor de la mesa
     */
    private void dibujarPastores(Graphics2D g2d, int centerX, int centerY) {
        int numPastores = pastores.size();
        
        for (int i = 0; i < numPastores; i++) {
            Pastor pastor = pastores.get(i);
            
            // Calcular posición angular (empezar desde arriba y seguir en sentido horario)
            double angulo = (2 * Math.PI * i / numPastores) - (Math.PI / 2);
            
            // Calcular posición del pastor
            int pastorX = centerX + (int) (RADIO_MESA * Math.cos(angulo));
            int pastorY = centerY + (int) (RADIO_MESA * Math.sin(angulo));
            
            // Determinar si es el pastor actual
            boolean esActual = (i == posicionActual);
            
            // Dibujar el pastor
            dibujarPastor(g2d, pastor, pastorX, pastorY, esActual, i);
        }
    }
    
    /**
     * Dibuja un pastor individual
     */
    private void dibujarPastor(Graphics2D g2d, Pastor pastor, int x, int y, boolean esActual, int posicion) {
        // Determinar radio y color según el estado del pastor
        int radio = esActual ? RADIO_PASTOR_ACTUAL : RADIO_PASTOR;
        Color colorPastor = determinarColorPastor(pastor, esActual);
        
        // Dibujar sombra
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(x - radio + 3, y - radio + 3, radio * 2, radio * 2);
        
        // Dibujar círculo del pastor
        g2d.setColor(colorPastor);
        g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);
        
        // Borde del pastor
        g2d.setColor(colorPastor.darker());
        g2d.setStroke(new BasicStroke(esActual ? 3 : 2));
        g2d.drawOval(x - radio, y - radio, radio * 2, radio * 2);
        
        // Si es el pastor actual, agregar anillo dorado
        if (esActual) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - radio - 5, y - radio - 5, (radio + 5) * 2, (radio + 5) * 2);
        }
        
        // Dibujar información del pastor
        dibujarInfoPastor(g2d, pastor, x, y, radio, esActual);
        
        // Dibujar número de posición
        dibujarNumeroPosicion(g2d, posicion + 1, x, y, radio);
    }
    
    /**
     * Determina el color del pastor según su estado
     */
    private Color determinarColorPastor(Pastor pastor, boolean esActual) {
        if (esActual) {
            return COLOR_PASTOR_ACTUAL;
        } else if (pastor.equals(masRico)) {
            return COLOR_PASTOR_RICO;
        } else if (pastor.equals(masPobre)) {
            return COLOR_PASTOR_POBRE;
        } else {
            return COLOR_PASTOR_NORMAL;
        }
    }
    
    /**
     * Dibuja la información del pastor (nombre y recursos)
     */
    private void dibujarInfoPastor(Graphics2D g2d, Pastor pastor, int x, int y, int radio, boolean esActual) {
        g2d.setColor(COLOR_TEXTO);
        
        // Nombre del pastor
        g2d.setFont(esActual ? FONT_NOMBRE.deriveFont(12f) : FONT_NOMBRE);
        FontMetrics fmNombre = g2d.getFontMetrics();
        String nombre = pastor.getNombre();
        
        // Recortar nombre si es muy largo
        if (nombre.length() > 8) {
            nombre = nombre.substring(0, 8) + "...";
        }
        
        int nombreX = x - fmNombre.stringWidth(nombre) / 2;
        int nombreY = y - 5;
        g2d.drawString(nombre, nombreX, nombreY);
        
        // Recursos (doblones y feligreses)
        g2d.setFont(FONT_RECURSOS);
        FontMetrics fmRecursos = g2d.getFontMetrics();
        
        String doblones = "D:" + pastor.getDoblones();
        String feligreses = "F:" + pastor.getFeligreses();
        
        int doblonesX = x - fmRecursos.stringWidth(doblones) / 2;
        int feligresesX = x - fmRecursos.stringWidth(feligreses) / 2;
        
        g2d.drawString(doblones, doblonesX, y + 5);
        g2d.drawString(feligreses, feligresesX, y + 15);
        
        // Trato/oficio
        String trato = pastor.getTrato().substring(0, 1); // Primera letra
        g2d.setFont(FONT_NOMBRE.deriveFont(Font.BOLD, 14f));
        FontMetrics fmTrato = g2d.getFontMetrics();
        int tratoX = x - fmTrato.stringWidth(trato) / 2;
        int tratoY = y - 15;
        
        // Fondo para la letra del trato
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 8, y - 22, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.drawString(trato, tratoX, tratoY);
    }
    
    /**
     * Dibuja el número de posición del pastor
     */
    private void dibujarNumeroPosicion(Graphics2D g2d, int numero, int x, int y, int radio) {
        // Círculo pequeño para el número
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + radio - 10, y - radio, 20, 20);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x + radio - 10, y - radio, 20, 20);
        
        // Número
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String numStr = String.valueOf(numero);
        int numX = x + radio - fm.stringWidth(numStr) / 2;
        int numY = y - radio + fm.getAscent() - 2;
        
        g2d.drawString(numStr, numX, numY);
    }
    
    /**
     * Dibuja mensaje cuando no hay pastores
     */
    private void dibujarMensajeVacio(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        
        String mensaje = "Mesa vacía";
        FontMetrics fm = g2d.getFontMetrics();
        int mensajeX = centerX - fm.stringWidth(mensaje) / 2;
        int mensajeY = centerY + fm.getAscent() / 2;
        
        g2d.drawString(mensaje, mensajeX, mensajeY);
    }
    
    /**
     * Dibuja la leyenda de colores
     */
    private void dibujarLeyenda(Graphics2D g2d) {
        int x = 10;
        int y = 20;
        int tamaño = 15;
        int espacioY = 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Pastor actual
        g2d.setColor(COLOR_PASTOR_ACTUAL);
        g2d.fillOval(x, y, tamaño, tamaño);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Pastor actual", x + tamaño + 5, y + tamaño - 2);
        
        // Más rico
        y += espacioY;
        g2d.setColor(COLOR_PASTOR_RICO);
        g2d.fillOval(x, y, tamaño, tamaño);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Más rico", x + tamaño + 5, y + tamaño - 2);
        
        // Más pobre
        y += espacioY;
        g2d.setColor(COLOR_PASTOR_POBRE);
        g2d.fillOval(x, y, tamaño, tamaño);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Más pobre", x + tamaño + 5, y + tamaño - 2);
        
        // Normal
        y += espacioY;
        g2d.setColor(COLOR_PASTOR_NORMAL);
        g2d.fillOval(x, y, tamaño, tamaño);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Normal", x + tamaño + 5, y + tamaño - 2);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(400, 400);
    }
}