package model;

/**
 * Clase auxiliar para encapsular estadísticas de recursos
 */
public class EstadisticasRecursos {
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
