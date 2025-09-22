package model;

import java.util.*;

/**
 * Representa la pila de los desposeídos donde van los pastores eliminados
 * "enviándolo de cabeza a la pila de los desposeídos, donde el postrero en caer será el primero en salir"
 */
public class PilaDesposeidos {
    private Stack<Pastor> pila;
    
    /**
     * Constructor de la pila de desposeídos
     */
    public PilaDesposeidos() {
        this.pila = new Stack<>();
    }
    
    /**
     * Echa un pastor a la pila de desposeídos
     * "enviándolo de cabeza a la pila de los desposeídos"
     * pastor el pastor a echar a la pila
     */
    public void echarAPila(Pastor pastor) {
        if (pastor != null) {
            pastor.setEnMesa(false);
            pila.push(pastor);
        }
    }
    
    /**
     * Saca al último pastor de la pila (LIFO)
     * "sacar del olvido al que encima de todos yace en la pila"
     * @return el pastor que estaba en la cima de la pila, o null si está vacía
     */
    public Pastor sacarDePila() {
        if (!pila.isEmpty()) {
            Pastor rescatado = pila.pop();
            rescatado.setEnMesa(true);
            return rescatado;
        }
        return null;
    }
    
    /**
     * Verifica si la pila está vacía
     * "si aconteciere que la susodicha pila no esté vacía"
     * @return true si la pila está vacía
     */
    public boolean estaVacia() {
        return pila.isEmpty();
    }
    
    /**
     * Obtiene el pastor que está en la cima sin sacarlo
     * "al que encima de todos yace en la pila"
     * @return el pastor en la cima, o null si está vacía
     */
    public Pastor obtenerPostrero() {
        if (!pila.isEmpty()) {
            return pila.peek();
        }
        return null;
    }
    
    /**
     * Obtiene el tamaño actual de la pila
     * @return número de pastores desposeídos
     */
    public int obtenerTamaño() {
        return pila.size();
    }
    
    /**
     * Obtiene una copia de todos los pastores desposeídos
     * Para visualización (desde el fondo hasta la cima)
     * @return lista de pastores desposeídos ordenados desde el primero hasta el último
     */
    public List<Pastor> obtenerDesposeidos() {
        return new ArrayList<>(pila);
    }
    
    /**
     * Obtiene una lista de pastores desposeídos desde la cima hacia abajo
     * Para visualización en columnas (el último en entrar arriba)
     * @return lista de pastores desposeídos desde la cima
     */
    public List<Pastor> obtenerDesposeidosOrdenInverso() {
        List<Pastor> lista = new ArrayList<>(pila);
        Collections.reverse(lista);
        return lista;
    }
    
    /**
     * Busca un pastor específico en la pila
     * id id del pastor a buscar
     * @return true si el pastor está en la pila
     */
    public boolean contienePastor(int id) {
        for (Pastor pastor : pila) {
            if (pastor.getId() == id) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la posición de un pastor en la pila (0 = fondo, size-1 = cima)
     * id id del pastor
     * @return posición del pastor, o -1 si no está
     */
    public int obtenerPosicion(int id) {
        for (int i = 0; i < pila.size(); i++) {
            if (pila.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Calcula el total de recursos en la pila (para estadísticas)
     * @return array con [totalDoblones, totalFeligreses]
     */
    public int[] calcularRecursosTotales() {
        int totalDoblones = 0;
        int totalFeligreses = 0;
        
        for (Pastor pastor : pila) {
            totalDoblones += pastor.getDoblones();
            totalFeligreses += pastor.getFeligreses();
        }
        
        return new int[]{totalDoblones, totalFeligreses};
    }
    
    /**
     * Obtiene información detallada de los pastores en la pila
     * @return mapa con estadísticas por trato
     */
    public Map<String, Integer> obtenerEstadisticasPorTrato() {
        Map<String, Integer> estadisticas = new HashMap<>();
        
        for (Pastor pastor : pila) {
            String trato = pastor.getTrato();
            estadisticas.put(trato, estadisticas.getOrDefault(trato, 0) + 1);
        }
        
        return estadisticas;
    }
    
    /**
     * Restaura un pastor específico de la pila (para casos especiales)
     * NOTA: Solo usar si las reglas del juego lo permiten
     * id id del pastor a restaurar
     * @return el pastor restaurado, o null si no se encontró
     */
    public Pastor restaurarPastorEspecifico(int id) {
        for (int i = 0; i < pila.size(); i++) {
            if (pila.get(i).getId() == id) {
                Pastor pastor = pila.remove(i);
                pastor.setEnMesa(true);
                return pastor;
            }
        }
        return null;
    }
    
    /**
     * Vacía completamente la pila (para reiniciar juego)
     */
    public void vaciarPila() {
        while (!pila.isEmpty()) {
            Pastor pastor = pila.pop();
            pastor.setEnMesa(false);
        }
    }
    
    /**
     * Obtiene el pastor que lleva más tiempo en la pila (el del fondo)
     * "el postrero en caer será el primero en salir"
     * @return el pastor del fondo de la pila
     */
    public Pastor obtenerPrimeroEnCaer() {
        if (!pila.isEmpty()) {
            return pila.get(0); // El primer elemento (fondo de la pila)
        }
        return null;
    }
    
    /**
     * Obtiene el pastor que acaba de llegar a la pila (la cima)
     * @return el último pastor que entró a la pila
     */
    public Pastor obtenerUltimoEnCaer() {
        return obtenerPostrero();
    }
    
    /**
     * Verifica si la pila tiene capacidad (por si hay límites)
     * maxCapacidad capacidad máxima permitida
     * @return true si puede agregar más pastores
     */
    public boolean tieneCapacidad(int maxCapacidad) {
        return pila.size() < maxCapacidad;
    }
    
    @Override
    public String toString() {
        return String.format("PilaDesposeidos[tamaño=%d]", pila.size());
    }
    
    /**
     * Obtiene una representación visual de la pila para la interfaz
     * NOTA: Este método será usado por la vista, no por la lógica de negocio
     * @return string con representación visual en columnas
     */
    public String obtenerRepresentacionVisual() {
        if (pila.isEmpty()) {
            return "║     PILA VACÍA     ║";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════╗\n");
        
        // Mostrar desde la cima hacia abajo
        for (int i = pila.size() - 1; i >= 0; i--) {
            Pastor pastor = pila.get(i);
            sb.append("║ ").append(String.format("%-17s", pastor.getNombre())).append(" ║\n");
            sb.append("║ D:%-4d F:%-8d ║\n", pastor.getDoblones(), pastor.getFeligreses());
            if (i > 0) sb.append("╠═══════════════════╣\n");
        }
        
        sb.append("╚═══════════════════╝");
        return sb.toString();
    }
}