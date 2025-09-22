package model;

import java.util.*;

/**
 * Encapsula el estado completo del juego en un momento dado
 * Sirve como puente entre la lógica de negocio y la vista
 */
public class EstadoRueda {
    private final Pastor pastorConTurno;
    private final List<Pastor> pastoresEnMesa;
    private final List<Pastor> pastoresDesposeidos;
    private final boolean puedeHurtar;
    private final boolean puedeRescatar;
    private final boolean danzaTerminada;
    private final int turno;
    private final Pastor masRico;
    private final Pastor masPobre;
    private final int valorN;
    
    /**
     * Constructor del estado de la rueda
     * @param pastorConTurno pastor que tiene el turno actual
     * @param pastoresEnMesa lista de pastores actualmente en la mesa
     * @param pastoresDesposeidos lista de pastores en la pila
     * @param puedeHurtar si el pastor actual puede hacer hurto piadoso
     * @param puedeRescatar si se puede rescatar de la pila
     * @param danzaTerminada si el juego ha terminado
     * @param turno número de turno actual
     */
    public EstadoRueda(Pastor pastorConTurno, List<Pastor> pastoresEnMesa, 
                       List<Pastor> pastoresDesposeidos, boolean puedeHurtar, 
                       boolean puedeRescatar, boolean danzaTerminada, int turno) {
        
        this.pastorConTurno = pastorConTurno;
        this.pastoresEnMesa = new ArrayList<>(pastoresEnMesa); // copia defensiva
        this.pastoresDesposeidos = new ArrayList<>(pastoresDesposeidos); // copia defensiva
        this.puedeHurtar = puedeHurtar;
        this.puedeRescatar = puedeRescatar;
        this.danzaTerminada = danzaTerminada;
        this.turno = turno;
        
        // Calcular información adicional útil para la vista
        this.masRico = calcularMasRico();
        this.masPobre = calcularMasPobre();
        this.valorN = 0; // Se establecerá externamente si es necesario
    }
    
    /**
     * Constructor completo con valor N
     */
    public EstadoRueda(Pastor pastorConTurno, List<Pastor> pastoresEnMesa, 
                       List<Pastor> pastoresDesposeidos, boolean puedeHurtar, 
                       boolean puedeRescatar, boolean danzaTerminada, int turno, int valorN) {
        
        this.pastorConTurno = pastorConTurno;
        this.pastoresEnMesa = new ArrayList<>(pastoresEnMesa);
        this.pastoresDesposeidos = new ArrayList<>(pastoresDesposeidos);
        this.puedeHurtar = puedeHurtar;
        this.puedeRescatar = puedeRescatar;
        this.danzaTerminada = danzaTerminada;
        this.turno = turno;
        this.valorN = valorN;
        
        this.masRico = calcularMasRico();
        this.masPobre = calcularMasPobre();
    }
    
    /**
     * Calcula quién es el pastor más rico actualmente en la mesa
     */
    private Pastor calcularMasRico() {
        if (pastoresEnMesa.isEmpty()) {
            return null;
        }
        
        Pastor masRico = pastoresEnMesa.get(0);
        for (Pastor pastor : pastoresEnMesa) {
            if (pastor.getDoblones() > masRico.getDoblones()) {
                masRico = pastor;
            }
        }
        return masRico;
    }
    
    /**
     * Calcula quién es el pastor más pobre actualmente en la mesa
     */
    private Pastor calcularMasPobre() {
        if (pastoresEnMesa.isEmpty()) {
            return null;
        }
        
        Pastor masPobre = pastoresEnMesa.get(0);
        for (Pastor pastor : pastoresEnMesa) {
            if (pastor.getDoblones() < masPobre.getDoblones()) {
                masPobre = pastor;
            }
        }
        return masPobre;
    }
    
    // Getters básicos
    public Pastor getPastorConTurno() {
        return pastorConTurno;
    }
    
    public List<Pastor> getPastoresEnMesa() {
        return new ArrayList<>(pastoresEnMesa); // copia defensiva
    }
    
    public List<Pastor> getPastoresDesposeidos() {
        return new ArrayList<>(pastoresDesposeidos); // copia defensiva
    }
    
    public boolean isPuedeHurtar() {
        return puedeHurtar;
    }
    
    public boolean isPuedeRescatar() {
        return puedeRescatar;
    }
    
    public boolean isDanzaTerminada() {
        return danzaTerminada;
    }
    
    public int getTurno() {
        return turno;
    }
    
    public Pastor getMasRico() {
        return masRico;
    }
    
    public Pastor getMasPobre() {
        return masPobre;
    }
    
    public int getValorN() {
        return valorN;
    }
    
    // Métodos de conveniencia para la vista
    
    /**
     * Obtiene el número de pastores en la mesa
     */
    public int getNumPastoresEnMesa() {
        return pastoresEnMesa.size();
    }
    
    /**
     * Obtiene el número de pastores desposeídos
     */
    public int getNumPastoresDesposeidos() {
        return pastoresDesposeidos.size();
    }
    
    /**
     * Verifica si hay pastores en la pila
     */
    public boolean hayDesposeidos() {
        return !pastoresDesposeidos.isEmpty();
    }
    
    /**
     * Obtiene el último pastor desposeído (cima de la pila)
     */
    public Pastor getUltimoDesposeido() {
        if (pastoresDesposeidos.isEmpty()) {
            return null;
        }
        return pastoresDesposeidos.get(pastoresDesposeidos.size() - 1);
    }
    
    /**
     * Obtiene el primer pastor desposeído (fondo de la pila)
     */
    public Pastor getPrimerDesposeido() {
        if (pastoresDesposeidos.isEmpty()) {
            return null;
        }
        return pastoresDesposeidos.get(0);
    }
    
    /**
     * Verifica si un pastor específico está en la mesa
     */
    public boolean pastorEstaEnMesa(int idPastor) {
        return pastoresEnMesa.stream().anyMatch(p -> p.getId() == idPastor);
    }
    
    /**
     * Verifica si un pastor específico está desposeído
     */
    public boolean pastorEstaDesposeido(int idPastor) {
        return pastoresDesposeidos.stream().anyMatch(p -> p.getId() == idPastor);
    }
    
    /**
     * Obtiene la posición de un pastor en la mesa
     */
    public int getPosicionEnMesa(int idPastor) {
        for (int i = 0; i < pastoresEnMesa.size(); i++) {
            if (pastoresEnMesa.get(i).getId() == idPastor) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Obtiene la posición del pastor con turno en la mesa
     */
    public int getPosicionPastorActual() {
        if (pastorConTurno == null) {
            return -1;
        }
        return getPosicionEnMesa(pastorConTurno.getId());
    }
    
    /**
     * Calcula estadísticas de recursos en la mesa
     */
    public EstadisticasRecursos getEstadisticasRecursosMesa() {
        int totalDoblones = pastoresEnMesa.stream().mapToInt(Pastor::getDoblones).sum();
        int totalFeligreses = pastoresEnMesa.stream().mapToInt(Pastor::getFeligreses).sum();
        
        return new EstadisticasRecursos(totalDoblones, totalFeligreses, pastoresEnMesa.size());
    }
    
    /**
     * Calcula estadísticas de recursos en la pila
     */
    public EstadisticasRecursos getEstadisticasRecursosPila() {
        int totalDoblones = pastoresDesposeidos.stream().mapToInt(Pastor::getDoblones).sum();
        int totalFeligreses = pastoresDesposeidos.stream().mapToInt(Pastor::getFeligreses).sum();
        
        return new EstadisticasRecursos(totalDoblones, totalFeligreses, pastoresDesposeidos.size());
    }
    
    /**
     * Obtiene un resumen de los tratos en la mesa
     */
    public Map<String, Integer> getResumenTratosMesa() {
        Map<String, Integer> resumen = new HashMap<>();
        for (Pastor pastor : pastoresEnMesa) {
            String trato = pastor.getTrato();
            resumen.put(trato, resumen.getOrDefault(trato, 0) + 1);
        }
        return resumen;
    }
    
    /**
     * Obtiene un resumen de los tratos en la pila
     */
    public Map<String, Integer> getResumenTratosPila() {
        Map<String, Integer> resumen = new HashMap<>();
        for (Pastor pastor : pastoresDesposeidos) {
            String trato = pastor.getTrato();
            resumen.put(trato, resumen.getOrDefault(trato, 0) + 1);
        }
        return resumen;
    }
    
    /**
     * Verifica si el pastor actual es el más pobre
     */
    public boolean pastorActualEsMasPobre() {
        return pastorConTurno != null && pastorConTurno.equals(masPobre);
    }
    
    /**
     * Verifica si el pastor actual es el más rico
     */
    public boolean pastorActualEsMasRico() {
        return pastorConTurno != null && pastorConTurno.equals(masRico);
    }
    
    /**
     * Obtiene el ganador si el juego terminó
     */
    public Pastor getGanador() {
        if (danzaTerminada && pastoresEnMesa.size() == 1) {
            return pastoresEnMesa.get(0);
        }
        return null;
    }
    
    /**
     * Verifica si hay un ganador
     */
    public boolean hayGanador() {
        return getGanador() != null;
    }
    
    @Override
    public String toString() {
        return String.format("EstadoRueda[turno=%d, enMesa=%d, desposeidos=%d, terminado=%s, pastor=%s]",
                           turno, pastoresEnMesa.size(), pastoresDesposeidos.size(), 
                           danzaTerminada, pastorConTurno != null ? pastorConTurno.getNombre() : "null");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        EstadoRueda that = (EstadoRueda) obj;
        return turno == that.turno && 
               danzaTerminada == that.danzaTerminada &&
               Objects.equals(pastorConTurno, that.pastorConTurno) &&
               pastoresEnMesa.size() == that.pastoresEnMesa.size() &&
               pastoresDesposeidos.size() == that.pastoresDesposeidos.size();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(turno, danzaTerminada, pastorConTurno, 
                           pastoresEnMesa.size(), pastoresDesposeidos.size());
    }
}

/**
 * Clase auxiliar para encapsular estadísticas de recursos
 */
class EstadisticasRecursos {
    private final int totalDoblones;
    private final int totalFeligreses;
    private final int numPastores;
    
    public EstadisticasRecursos(int totalDoblones, int totalFeligreses, int numPastores) {
        this.totalDoblones = totalDoblones;
        this.totalFeligreses = totalFeligreses;
        this.numPastores = numPastores;
    }
    
    public int getTotalDoblones() { return totalDoblones; }
    public int getTotalFeligreses() { return totalFeligreses; }
    public int getNumPastores() { return numPastores; }
    public int getTotalRecursos() { return totalDoblones + totalFeligreses; }
    
    public double getPromedioDoblones() {
        return numPastores > 0 ? (double) totalDoblones / numPastores : 0;
    }
    
    public double getPromedioFeligreses() {
        return numPastores > 0 ? (double) totalFeligreses / numPastores : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Recursos[doblones=%d, feligreses=%d, pastores=%d]", 
                           totalDoblones, totalFeligreses, numPastores);
    }
}