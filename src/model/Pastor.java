package model;


/**
 * Representa un pastor de almas con su caudal contante y sonante,
 * su grey de fieles y su trato o negocio.
 */
public class Pastor {
    private int id;
    private String nombre;
    private int doblones;          // "caudal contante y sonante"
    private int feligreses;        // "grey de fieles que le siguen"
    private String trato;          // "mesmo trato o negocio"
    private boolean enMesa;        // si está actualmente en la mesa redonda
    
    /**
     * Constructor para crear un nuevo pastor
     * id identificador único del pastor
     * nombre nombre del pastor
     * doblones cantidad inicial de doblones
     * feligreses cantidad inicial de feligreses
     * trato tipo de negocio o oficio del pastor
     */
    public Pastor(int id, String nombre, int doblones, int feligreses, String trato) {
        this.id = id;
        this.nombre = nombre;
        this.doblones = doblones;
        this.feligreses = feligreses;
        this.trato = trato;
        this.enMesa = true;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getDoblones() {
        return doblones;
    }
    
    public int getFeligreses() {
        return feligreses;
    }
    
    public String getTrato() {
        return trato;
    }
    
    public boolean isEnMesa() {
        return enMesa;
    }
    
    // Setters
    public void setDoblones(int doblones) {
        this.doblones = Math.max(0, doblones); // no pueden ser negativos
    }
    
    public void setFeligreses(int feligreses) {
        this.feligreses = Math.max(0, feligreses); // no pueden ser negativos
    }
    
    public void setEnMesa(boolean enMesa) {
        this.enMesa = enMesa;
    }
    
    /**
     * Traspasa todos los recursos de este pastor a otro
     * "traspasándole su gente y su tesoro"
     * destino el pastor que recibirá los recursos
     */
    public void traspasarRecursos(Pastor destino) {
        if (destino != null) {
            destino.doblones += this.doblones;
            destino.feligreses += this.feligreses;
            this.doblones = 0;
            this.feligreses = 0;
        }
    }
    
    /**
     * Recibe la mitad de los recursos especificados
     * "dándole la mitad cabal de sus fieles y de su riqueza"
     * doblones cantidad de doblones a recibir la mitad
     * feligreses cantidad de feligreses a recibir la mitad
     */
    public void recibirMitad(int doblones, int feligreses) {
        this.doblones += doblones / 2;
        this.feligreses += feligreses / 2;
    }
    
    /**
     * Transfiere una cantidad específica de recursos a otro pastor
     * destino pastor que recibirá los recursos
     * doblones cantidad de doblones a transferir
     * feligreses cantidad de feligreses a transferir
     */
    public void transferirRecursos(Pastor destino, int doblones, int feligreses) {
        if (destino != null && this.doblones >= doblones && this.feligreses >= feligreses) {
            destino.doblones += doblones;
            destino.feligreses += feligreses;
            this.doblones -= doblones;
            this.feligreses -= feligreses;
        }
    }
    
    /**
     * Verifica si este pastor es más rico que otro
     * otro el pastor a comparar
     * @return true si este pastor tiene más doblones
     */
    public boolean esMasRico(Pastor otro) {
        return otro != null && this.doblones > otro.doblones;
    }
    
    /**
     * Verifica si este pastor es más pobre que otro
     * otro el pastor a comparar
     * @return true si este pastor tiene menos doblones
     */
    public boolean esMasPobre(Pastor otro) {
        return otro != null && this.doblones < otro.doblones;
    }
    
    /**
     * Verifica si este pastor tiene menos feligreses que otro
     * "eligiendo al de menor grey"
     * otro el pastor a comparar
     * @return true si este pastor tiene menos feligreses
     */
    public boolean tieneMenosFeligreses(Pastor otro) {
        return otro != null && this.feligreses < otro.feligreses;
    }
    
    /**
     * Verifica si este pastor es del mismo trato que otro
     * "compañero del mesmo trato o negocio"
     * otro el pastor a comparar
     * @return true si ambos pastores tienen el mismo trato
     */
    public boolean esDelMismoTrato(Pastor otro) {
        return otro != null && this.trato.equals(otro.trato);
    }
    
    /**
     * Aplica el "hurto piadoso" - roba la tercia parte de los recursos del más rico
     * "llevarse de ella la tercia parte, tanto en ovejas como en moneda"
     * victima el pastor más rico del que se robará
     * @return true si el hurto se realizó exitosamente
     */
    public boolean aplicarHurtoPiadoso(Pastor victima) {
        if (victima != null && victima.doblones > 0 && victima.feligreses > 0) {
            int doblonesRobados = victima.doblones / 3;
            int feligresesRobados = victima.feligreses / 3;
            
            this.doblones += doblonesRobados;
            this.feligreses += feligresesRobados;
            
            victima.doblones -= doblonesRobados;
            victima.feligreses -= feligresesRobados;
            
            return true;
        }
        return false;
    }
    
    /**
     * Calcula el total de recursos (doblones + feligreses) para comparaciones
     * @return suma total de doblones y feligreses
     */
    public int getTotalRecursos() {
        return doblones + feligreses;
    }
    
    @Override
    public String toString() {
        return String.format("Pastor %s [ID: %d, Trato: %s, Doblones: %d, Feligreses: %d, En Mesa: %s]",
                nombre, id, trato, doblones, feligreses, enMesa ? "Sí" : "No");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pastor pastor = (Pastor) obj;
        return id == pastor.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}