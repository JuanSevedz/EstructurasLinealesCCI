package view;

import java.util.List;
import model.*;

/**
 * Interfaz que define el contrato para cualquier implementación de vista del juego
 * "con traza visible y de buen manejo que pinte la mesa por círculos y la pila por columnas"
 */
public interface VistaJuego {
    
    /**
     * Actualiza la visualización completa del juego con el nuevo estado
     * estado estado actual del juego
     */
    void actualizarEstado(EstadoRueda estado);
    
    /**
     * Pinta la mesa circular con los pastores
     * "que pinte la mesa por círculos"
     * pastores lista de pastores en la mesa
     * posicionActual posición del pastor que tiene el turno
     */
    void pintarMesa(List<Pastor> pastores, int posicionActual);
    
    /**
     * Pinta la pila de desposeídos en columnas
     * "y la pila por columnas"
     * desposeidos lista de pastores desposeídos (desde el fondo hasta la cima)
     */
    void pintarPila(List<Pastor> desposeidos);
    
    /**
     * Muestra información del turno actual
     * pastorActual pastor que tiene el turno
     * puedeHurtar si puede hacer hurto piadoso
     * puedeRescatar si puede rescatar de la pila
     * turno número de turno actual
     */
    void mostrarTurno(Pastor pastorActual, boolean puedeHurtar, boolean puedeRescatar, int turno);
    
    /**
     * Muestra el resultado de una eliminación (degüello)
     * "arrimar la guadaña y segar otra cabeza"
     * eliminado pastor que fue eliminado
     * eliminador pastor que realizó la eliminación
     * direccion dirección en que se miró (true=derecha, false=izquierda)
     */
    void mostrarDeguello(Pastor eliminado, Pastor eliminador, boolean direccion);
    
    /**
     * Muestra el resultado de un rescate
     * "sacar del olvido al que encima de todos yace"
     * rescatado pastor que fue rescatado
     * rescatador pastor que realizó el rescate
     */
    void mostrarRescate(Pastor rescatado, Pastor rescatador);
    
    /**
     * Muestra el resultado del hurto piadoso
     * "meter mano en la faltriquera del más rico"
     * ladron pastor que realizó el hurto (más pobre)
     * victima pastor que fue robado (más rico)
     * doblonesRobados cantidad de doblones robados
     * feligresesRobados cantidad de feligreses robados
     */
    void mostrarHurtoPiadoso(Pastor ladron, Pastor victima, int doblonesRobados, int feligresesRobados);
    
    /**
     * Muestra al ganador final del juego
     * "rey de burlas y veras, dueño de bolsas y conciencias"
     * ganador el pastor ganador
     */
    void mostrarReyFinal(Pastor ganador);
    
    /**
     * Solicita al usuario que elija una acción
     * accionesDisponibles lista de acciones que puede tomar
     * @return la acción elegida por el usuario
     */
    AccionPastor pedirAccion(List<AccionPastor> accionesDisponibles);
    
    /**
     * Solicita al usuario que elija una dirección (para arrimar guadaña)
     * @return true para derecha, false para izquierda
     */
    boolean pedirDireccion();
    
    /**
     * Muestra un mensaje de error al usuario
     * mensaje mensaje de error a mostrar
     */
    void mostrarError(String mensaje);
    
    /**
     * Muestra un mensaje informativo al usuario
     * mensaje mensaje informativo
     */
    void mostrarMensaje(String mensaje);
    
    /**
     * Muestra estadísticas del juego
     * estadoActual estado actual para calcular estadísticas
     */
    void mostrarEstadisticas(EstadoRueda estadoActual);
    
    /**
     * Solicita confirmación al usuario para una acción importante
     * mensaje mensaje de confirmación
     * @return true si el usuario confirma, false en caso contrario
     */
    boolean pedirConfirmacion(String mensaje);
    
    /**
     * Inicializa la vista con la configuración del juego
     * numPastores número inicial de pastores
     * valorN valor de n para el conteo
     */
    void inicializar(int numPastores, int valorN);
    
    /**
     * Limpia la vista para un nuevo juego
     */
    void limpiar();
    
    /**
     * Muestra la pantalla de configuración inicial
     * @return configuración elegida por el usuario
     */
    ConfiguracionRueda solicitarConfiguracion();
    
    /**
     * Habilita o deshabilita la interacción del usuario
     * habilitado true para habilitar, false para deshabilitar
     */
    void habilitarInteraccion(boolean habilitado);
    
    /**
     * Resalta un pastor específico en la vista
     * pastor pastor a resaltar
     * tipo tipo de resaltado (actual, rico, pobre, etc.)
     */
    void resaltarPastor(Pastor pastor, TipoResaltado tipo);
    
    /**
     * Muestra una animación de eliminación
     * pastor pastor siendo eliminado
     */
    void animarEliminacion(Pastor pastor);
    
    /**
     * Muestra una animación de rescate
     * pastor pastor siendo rescatado
     */
    void animarRescate(Pastor pastor);
    
    /**
     * Actualiza solo la información de recursos sin repintar todo
     * pastor pastor cuya información cambió
     */
    void actualizarRecursosPastor(Pastor pastor);
    
    /**
     * Cierra la vista y libera recursos
     */
    void cerrar();
}

/**
 * Tipos de resaltado para pastores en la vista
 */
enum TipoResaltado {
    TURNO_ACTUAL("Pastor con turno"),
    MAS_RICO("Pastor más rico"),
    MAS_POBRE("Pastor más pobre"), 
    CANDIDATO_ELIMINACION("Candidato a eliminación"),
    RECIEN_RESCATADO("Recién rescatado"),
    VICTIMA_HURTO("Víctima de hurto"),
    NORMAL("Sin resaltado especial");
    
    private final String descripcion;
    
    TipoResaltado(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}