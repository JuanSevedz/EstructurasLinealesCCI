package model;

/**
 * Encapsula la configuración inicial del juego
 * "siendo de sabios moderar el gentío y limitar el número de estos líderes espirituales"
 */
public class ConfiguracionRueda {
    private final int numPastores;
    private final int valorN;
    private final int doblonesIniciales;
    private final int feligresesIniciales;
    private final boolean recursosAleatorios;
    
    /**
     * Constructor básico con valores por defecto
     * @param numPastores número de pastores en el juego
     * @param valorN número de posiciones a contar para eliminación
     */
    public ConfiguracionRueda(int numPastores, int valorN) {
        this.numPastores = Math.max(2, Math.min(20, numPastores)); // Entre 2 y 20
        this.valorN = Math.max(1, Math.min(numPastores - 1, valorN)); // Entre 1 y numPastores-1
        this.doblonesIniciales = 300; // Valor por defecto
        this.feligresesIniciales = 150; // Valor por defecto
        this.recursosAleatorios = true; // Por defecto aleatorios
    }
    
    /**
     * Constructor completo
     * @param numPastores número de pastores
     * @param valorN valor de n para conteo
     * @param doblonesIniciales doblones base (si no son aleatorios)
     * @param feligresesIniciales feligreses base (si no son aleatorios)
     * @param recursosAleatorios si los recursos deben ser aleatorios
     */
    public ConfiguracionRueda(int numPastores, int valorN, int doblonesIniciales, 
                             int feligresesIniciales, boolean recursosAleatorios) {
        
        this.numPastores = Math.max(2, Math.min(20, numPastores));
        this.valorN = Math.max(1, Math.min(numPastores - 1, valorN));
        this.doblonesIniciales = Math.max(50, Math.min(1000, doblonesIniciales));
        this.feligresesIniciales = Math.max(25, Math.min(500, feligresesIniciales));
        this.recursosAleatorios = recursosAleatorios;
    }
    
    // Getters
    public int getNumPastores() {
        return numPastores;
    }
    
    public int getValorN() {
        return valorN;
    }
    
    public int getDoblonesIniciales() {
        return doblonesIniciales;
    }
    
    public int getFeligresesIniciales() {
        return feligresesIniciales;
    }
    
    public boolean isRecursosAleatorios() {
        return recursosAleatorios;
    }
    
    /**
     * Valida que la configuración sea correcta
     * @return true si la configuración es válida
     */
    public boolean esValida() {
        return numPastores >= 2 && 
               numPastores <= 20 &&
               valorN >= 1 && 
               valorN < numPastores &&
               doblonesIniciales > 0 &&
               feligresesIniciales > 0;
    }
    
    /**
     * Obtiene una descripción de la configuración
     */
    public String getDescripcion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pastores: ").append(numPastores).append("\n");
        sb.append("Valor N: ").append(valorN).append("\n");
        sb.append("Recursos: ");
        
        if (recursosAleatorios) {
            sb.append("Aleatorios");
        } else {
            sb.append("Fijos (D:").append(doblonesIniciales)
              .append(", F:").append(feligresesIniciales).append(")");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("ConfiguracionRueda[pastores=%d, n=%d, aleatorio=%s]", 
                           numPastores, valorN, recursosAleatorios);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ConfiguracionRueda that = (ConfiguracionRueda) obj;
        return numPastores == that.numPastores &&
               valorN == that.valorN &&
               doblonesIniciales == that.doblonesIniciales &&
               feligresesIniciales == that.feligresesIniciales &&
               recursosAleatorios == that.recursosAleatorios;
    }
    
    @Override
    public int hashCode() {
        int result = numPastores;
        result = 31 * result + valorN;
        result = 31 * result + doblonesIniciales;
        result = 31 * result + feligresesIniciales;
        result = 31 * result + (recursosAleatorios ? 1 : 0);
        return result;
    }
}