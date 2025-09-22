package app;

import view.VistaSwing;

import javax.swing.SwingUtilities;

import controller.ControladorJuego;

/**
 * Clase Main para iniciar la aplicación Swing del juego.
 * Crea la vista Swing y el controlador, establece la vinculación básica y muestra la ventana.
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     * argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaSwing vista = new VistaSwing();
            // Crear el controlador y pasarle la vista
            ControladorJuego controlador = new ControladorJuego(vista);
            // Establecer la referencia del controlador en la vista
            vista.setControlador(controlador);

            // La UI se muestra automáticamente en inicializar()
            // Primero inicializamos la vista
            vista.inicializar(10, 2);

            // Luego intentamos iniciar el controlador
            try {
                controlador.iniciar(10, 2);
            } catch (IllegalArgumentException e) {
                // Mostrar error con la vista si algo falla
                try {
                    vista.mostrarError("No se pudo iniciar el juego: " + e.getMessage());
                } catch (Exception ex) {
                    // Ignorar: evita que la app crashee
                }
            }
        });
    }
}
