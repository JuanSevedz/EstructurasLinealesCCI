package model;

/**
 * Enumeración que representa las acciones que puede tomar un pastor en su turno.
 * Se usa desde el controlador y la vista para estandarizar las opciones disponibles.
 */
public enum AccionPastor {
    /** Arrimar la guadaña hacia la derecha (degollar a la derecha). */
    ARRIMAR_GUADAÑA_DERECHA,
    /** Arrimar la guadaña hacia la izquierda (degollar a la izquierda). */
    ARRIMAR_GUADAÑA_IZQUIERDA,
    /** Sacar del olvido (rescatar) al último pastor de la pila de desposeídos. */
    SACAR_DEL_OLVIDO,
    /** Meter mano en la faltriquera del más rico (solo si quien actúa es el más pobre). */
    METER_MANO_FALTRIQUERA
}
