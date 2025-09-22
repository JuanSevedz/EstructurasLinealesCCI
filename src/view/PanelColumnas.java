package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import model.*;

/**
 * Panel que dibuja la pila de desposeídos en columnas
 * "y la pila por columnas"
 */
public class PanelColumnas extends JPanel {
    
    private List<Pastor> desposeidos;
    
    // Configuración visual
    private final Color COLOR_PILA_BASE = new Color(105, 105, 105); // Gris dim
    private final Color COLOR_PASTOR_DESPOSEIDO = new Color(139, 69, 19); // Marrón
    private final Color COLOR_PASTOR_CIMA = new Color(184, 134, 11); // Dorado oscuro
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_FONDO = new Color(245, 245, 220); // Beige
    private final Color COLOR_BORDE = Color.BLACK;
    
    // Configuración de tamaños
    private final int ANCHO_CARTA = 80;
    private final int ALTO_CARTA = 60;
    private final int ESPACIADO_VERTICAL = 5;
    private final int MARGEN = 20;
    private final Font FONT_NOMBRE = new Font("Arial", Font.BOLD, 10);
    private final Font FONT_RECURSOS = new Font("Arial", Font.PLAIN, 9);
    private final Font FONT_NIVEL = new Font("Arial", Font.BOLD, 12);
    
    public PanelColumnas() {
        this.desposeidos = new ArrayList<>();
        setBackground(COLOR_FONDO);
        setDoubleBuffered(true);
    }
    
    /**
     * Establece los pastores desposeídos a mostrar
     * desposeidos lista de pastores desposeídos (desde el fondo hasta la cima)
     */
    public void setDesposeidos(List<Pastor> desposeidos) {
        this.desposeidos = new ArrayList<>(desposeidos);
        repaint();
    }
    
    /**
     * Limpia el panel
     */
    public void limpiar() {
        desposeidos.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Activar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Dibujar base de la pila
        dibujarBasePila(g2d);
        
        if (desposeidos.isEmpty()) {
            dibujarPilaVacia(g2d);
        } else {
            dibujarPastoresDesposeidos(g2d);
        }
        
        // Dibujar información de la pila
        dibujarInfoPila(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Dibuja la base donde se apila a los desposeídos
     */
    private void dibujarBasePila(Graphics2D g2d) {
        int baseY = getHeight() - MARGEN;
        int baseX = getWidth() / 2 - ANCHO_CARTA / 2;
        
        // Sombra de la base
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(baseX + 3, baseY - 13, ANCHO_CARTA, 15, 5, 5);
        
        // Base principal
        g2d.setColor(COLOR_PILA_BASE);
        g2d.fillRoundRect(baseX, baseY - 15, ANCHO_CARTA, 15, 5, 5);
        
        // Borde de la base
        g2d.setColor(COLOR_PILA_BASE.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(baseX, baseY - 15, ANCHO_CARTA, 15, 5, 5);
        
        // Texto "PILA"
        g2d.setColor(COLOR_TEXTO);
        g2d.setFont(FONT_NIVEL);
        FontMetrics fm = g2d.getFontMetrics();
        String texto = "PILA";
        int textoX = baseX + (ANCHO_CARTA - fm.stringWidth(texto)) / 2;
        int textoY = baseY - 3;
        g2d.drawString(texto, textoX, textoY);
    }
    
    /**
     * Dibuja mensaje cuando la pila está vacía
     */
    private void dibujarPilaVacia(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Mensaje de pila vacía
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        FontMetrics fm = g2d.getFontMetrics();
        
        String mensaje = "Pila vacía";
        int mensajeX = centerX - fm.stringWidth(mensaje) / 2;
        int mensajeY = centerY;
        g2d.drawString(mensaje, mensajeX, mensajeY);
        
        // Descripción
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        fm = g2d.getFontMetrics();
        String desc = "\"El postrero en caer";
        String desc2 = "será el primero en salir\"";
        
        int descX = centerX - fm.stringWidth(desc) / 2;
        int desc2X = centerX - fm.stringWidth(desc2) / 2;
        g2d.drawString(desc, descX, mensajeY + 30);
        g2d.drawString(desc2, desc2X, mensajeY + 45);
    }
    
    /**
     * Dibuja todos los pastores desposeídos como cartas apiladas
     */
    private void dibujarPastoresDesposeidos(Graphics2D g2d) {
        int baseY = getHeight() - MARGEN - 15; // Posición base
        int baseX = getWidth() / 2 - ANCHO_CARTA / 2;
        
        // Dibujar desde el fondo hacia la cima
        for (int i = 0; i < desposeidos.size(); i++) {
            Pastor pastor = desposeidos.get(i);
            
            // Calcular posición Y (hacia arriba)
            int cartaY = baseY - (i + 1) * (ALTO_CARTA + ESPACIADO_VERTICAL);
            
            // Determinar si es la cima de la pila
            boolean esCima = (i == desposeidos.size() - 1);
            
            // Dibujar la carta del pastor
            dibujarCartaPastor(g2d, pastor, baseX, cartaY, i + 1, esCima);
        }
    }
    
    /**
     * Dibuja una carta individual de pastor desposeído
     */
    private void dibujarCartaPastor(Graphics2D g2d, Pastor pastor, int x, int y, int nivel, boolean esCima) {
        // Color de la carta según si es la cima
        Color colorCarta = esCima ? COLOR_PASTOR_CIMA : COLOR_PASTOR_DESPOSEIDO;
        
        // Sombra de la carta
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(x + 2, y + 2, ANCHO_CARTA, ALTO_CARTA, 8, 8);
        
        // Carta principal
        g2d.setColor(colorCarta);
        g2d.fillRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 8, 8);
        
        // Borde de la carta
        g2d.setColor(esCima ? COLOR_PASTOR_CIMA.darker() : COLOR_BORDE);
        g2d.setStroke(new BasicStroke(esCima ? 3 : 2));
        g2d.drawRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 8, 8);
        
        // Si es la cima, agregar brillo dorado
        if (esCima) {
            g2d.setColor(new Color(255, 215, 0, 100));
            g2d.fillRoundRect(x + 2, y + 2, ANCHO_CARTA - 4, ALTO_CARTA - 4, 6, 6);
        }
        
        // Dibujar información del pastor
        dibujarInfoCartaPastor(g2d, pastor, x, y, nivel, esCima);
        
        // Dibujar indicador de nivel en la pila
        dibujarIndicadorNivel(g2d, nivel, x, y, esCima);
    }
    
    /**
     * Dibuja la información del pastor en su carta
     */
    private void dibujarInfoCartaPastor(Graphics2D g2d, Pastor pastor, int x, int y, int nivel, boolean esCima) {
        g2d.setColor(COLOR_TEXTO);
        
        // Nombre del pastor
        g2d.setFont(esCima ? FONT_NOMBRE.deriveFont(Font.BOLD, 11f) : FONT_NOMBRE);
        FontMetrics fmNombre = g2d.getFontMetrics();
        String nombre = pastor.getNombre();
        
        // Recortar nombre si es muy largo
        if (nombre.length() > 10) {
            nombre = nombre.substring(0, 8) + "..";
        }
        
        int nombreX = x + (ANCHO_CARTA - fmNombre.stringWidth(nombre)) / 2;
        int nombreY = y + 15;
        g2d.drawString(nombre, nombreX, nombreY);
        
        // Recursos
        g2d.setFont(FONT_RECURSOS);
        FontMetrics fmRecursos = g2d.getFontMetrics();
        
        String doblones = "D:" + pastor.getDoblones();
        String feligreses = "F:" + pastor.getFeligreses();
        
        int doblonesX = x + (ANCHO_CARTA - fmRecursos.stringWidth(doblones)) / 2;
        int feligresesX = x + (ANCHO_CARTA - fmRecursos.stringWidth(feligreses)) / 2;
        
        g2d.drawString(doblones, doblonesX, y + 30);
        g2d.drawString(feligreses, feligresesX, y + 42);
        
        // Trato (esquina superior derecha)
        String trato = pastor.getTrato().substring(0, 1);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.setColor(esCima ? Color.BLACK : Color.LIGHT_GRAY);
        g2d.drawString(trato, x + ANCHO_CARTA - 15, y + 12);
    }
    
    /**
     * Dibuja el indicador de nivel en la pila
     */
    private void dibujarIndicadorNivel(Graphics2D g2d, int nivel, int x, int y, boolean esCima) {
        // Círculo para el nivel (esquina superior izquierda)
        Color colorNivel = esCima ? Color.YELLOW : new Color(255, 255, 255, 180);
        Color colorTextoNivel = esCima ? Color.BLACK : Color.BLACK;
        
        g2d.setColor(colorNivel);
        g2d.fillOval(x + 5, y + 5, 18, 18);
        
        g2d.setColor(colorTextoNivel);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x + 5, y + 5, 18, 18);
        
        // Número del nivel
        g2d.setFont(FONT_NIVEL.deriveFont(10f));
        FontMetrics fm = g2d.getFontMetrics();
        String nivelStr = String.valueOf(nivel);
        int nivelX = x + 14 - fm.stringWidth(nivelStr) / 2;
        int nivelY = y + 16;
        
        g2d.drawString(nivelStr, nivelX, nivelY);
    }
    
    /**
     * Dibuja información general de la pila
     */
    private void dibujarInfoPila(Graphics2D g2d) {
        if (desposeidos.isEmpty()) return;
        
        // Panel de información en la parte superior
        int panelX = 10;
        int panelY = 10;
        int panelAncho = getWidth() - 20;
        int panelAlto = 80;
        
        // Fondo del panel de información
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(panelX, panelY, panelAncho, panelAlto, 10, 10);
        g2d.setColor(COLOR_BORDE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(panelX, panelY, panelAncho, panelAlto, 10, 10);
        
        // Información de la pila
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        
        int infoX = panelX + 10;
        int infoY = panelY + 20;
        
        g2d.drawString("PILA DE DESPOSEÍDOS", infoX, infoY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        infoY += 18;
        g2d.drawString("Total pastores: " + desposeidos.size(), infoX, infoY);
        
        // Calcular totales de recursos
        int totalDoblones = desposeidos.stream().mapToInt(Pastor::getDoblones).sum();
        int totalFeligreses = desposeidos.stream().mapToInt(Pastor::getFeligreses).sum();
        
        infoY += 15;
        g2d.drawString("Doblones perdidos: " + totalDoblones, infoX, infoY);
        
        infoY += 15;
        g2d.drawString("Feligreses perdidos: " + totalFeligreses, infoX, infoY);
        
        // Información del pastor en la cima (si hay)
        if (!desposeidos.isEmpty()) {
            Pastor cima = desposeidos.get(desposeidos.size() - 1);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(new Color(184, 134, 11)); // Dorado oscuro
            
            int cimaX = panelX + panelAncho - 120;
            int cimaY = panelY + 20;
            
            g2d.drawString("EN LA CIMA:", cimaX, cimaY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.BLACK);
            
            cimaY += 15;
            g2d.drawString(cima.getNombre(), cimaX, cimaY);
            cimaY += 12;
            g2d.drawString("\"Próximo en salir\"", cimaX, cimaY);
        }
    }
    
    /**
     * Obtiene la altura mínima necesaria para mostrar todos los pastores
     */
    private int calcularAlturaMinima() {
        if (desposeidos.isEmpty()) {
            return 200; // Altura mínima para pila vacía
        }
        
        int alturaNecesaria = MARGEN * 2 + // Márgenes superior e inferior
                             15 + // Base de la pila
                             (desposeidos.size() * (ALTO_CARTA + ESPACIADO_VERTICAL)) +
                             100; // Espacio para información superior
        
        return Math.max(400, alturaNecesaria);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(280, calcularAlturaMinima());
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(250, 400);
    }
    
    /**
     * Método para obtener información sobre qué pastor está en cierta posición
     * Útil para interacciones del mouse (futuras extensiones)
     */
    public Pastor getPastorEnPosicion(Point punto) {
        if (desposeidos.isEmpty()) return null;
        
        int baseY = getHeight() - MARGEN - 15;
        int baseX = getWidth() / 2 - ANCHO_CARTA / 2;
        
        for (int i = 0; i < desposeidos.size(); i++) {
            int cartaY = baseY - (i + 1) * (ALTO_CARTA + ESPACIADO_VERTICAL);
            
            Rectangle areaCarta = new Rectangle(baseX, cartaY, ANCHO_CARTA, ALTO_CARTA);
            
            if (areaCarta.contains(punto)) {
                return desposeidos.get(i);
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene el índice del pastor en la cima de la pila
     */
    public int getIndiceCima() {
        return desposeidos.isEmpty() ? -1 : desposeidos.size() - 1;
    }
    
    /**
     * Verifica si la pila tiene pastores
     */
    public boolean tienePastores() {
        return !desposeidos.isEmpty();
    }
    
    /**
     * Obtiene el número de pastores en la pila
     */
    public int getNumeroPastores() {
        return desposeidos.size();
    }
}