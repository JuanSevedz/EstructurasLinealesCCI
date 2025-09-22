package model;

import java.util.*;

/**
 * Representa la mesa redonda donde se sientan los pastores
 * "En una redonda mesa, cual si fuera torno del hado"
 */
public class Mesa {
    private ArrayList<Pastor> pastores;
    private int posicionActual;  // posición del pastor que tiene el turno
    private int n;              // número de posiciones a contar para eliminación
    
    /**
     * Constructor de la mesa redonda
     * @param numPastores número inicial de pastores
     * @param n número de posiciones a contar en cada eliminación
     */
    public Mesa(int numPastores, int n) {
        this.pastores = new ArrayList<>(numPastores);
        this.posicionActual = 0;
        this.n = n;
    }
    
    /**
     * Sienta un pastor en la mesa
     * "siéntanse, por ventura y a la buena de Dios, varios pastores"
     * @param pastor el pastor a sentar en la mesa
     */
    public void sentarPastor(Pastor pastor) {
        if (pastor != null) {
            pastores.add(pastor);
            pastor.setEnMesa(true);
        }
    }
    
    /**
     * Saca un pastor de la mesa en una posición específica
     * "darle pasaporte de la rueda"
     * @param posicion la posición del pastor a sacar
     * @return el pastor eliminado, o null si la posición es inválida
     */
    public Pastor sacarPastor(int posicion) {
        if (posicion >= 0 && posicion < pastores.size()) {
            Pastor eliminado = pastores.remove(posicion);
            eliminado.setEnMesa(false);
            
            // Ajustar posición actual si es necesario
            if (posicionActual >= pastores.size() && !pastores.isEmpty()) {
                posicionActual = 0;
            } else if (posicion < posicionActual) {
                posicionActual--;
            }
            
            return eliminado;
        }
        return null;
    }
    
    /**
     * Obtiene el pastor que actualmente tiene el turno
     * @return el pastor actual, o null si no hay pastores
     */
    public Pastor obtenerPastorActual() {
        if (pastores.isEmpty() || posicionActual >= pastores.size()) {
            return null;
        }
        return pastores.get(posicionActual);
    }
    
    /**
     * Mira hacia una dirección y obtiene los n pastores más próximos
     * "entre cuantos vecinos se le cuenten por más próximos hasta un número n"
     * @param derecha true para mirar a la derecha, false para la izquierda
     * @param cantidad número de pastores a considerar
     * @return lista de pastores en la dirección especificada
     */
    public List<Pastor> mirarHacia(boolean derecha, int cantidad) {
        List<Pastor> vecinos = new ArrayList<>();
        if (pastores.isEmpty() || cantidad <= 0) {
            return vecinos;
        }
        
        int tamaño = pastores.size();
        int direccion = derecha ? 1 : -1;
        
        for (int i = 1; i <= Math.min(cantidad, tamaño - 1); i++) {
            int indice = (posicionActual + (i * direccion) + tamaño) % tamaño;
            vecinos.add(pastores.get(indice));
        }
        
        return vecinos;
    }
    
    /**
     * Encuentra al pastor más rico de la mesa
     * "aquel pastor que más doblones guarda en sus arcas"
     * @return el pastor más rico, o null si no hay pastores
     */
    public Pastor encontrarMasRico() {
        if (pastores.isEmpty()) {
            return null;
        }
        
        Pastor masRico = pastores.get(0);
        for (Pastor pastor : pastores) {
            if (pastor.getDoblones() > masRico.getDoblones()) {
                masRico = pastor;
            }
        }
        return masRico;
    }
    
    /**
     * Encuentra al pastor más pobre de la mesa
     * "si en alguna vuelta el que manda es el más pobre de la compañía"
     * @return el pastor más pobre, o null si no hay pastores
     */
    public Pastor encontrarMasPobre() {
        if (pastores.isEmpty()) {
            return null;
        }
        
        Pastor masPobre = pastores.get(0);
        for (Pastor pastor : pastores) {
            if (pastor.getDoblones() < masPobre.getDoblones()) {
                masPobre = pastor;
            }
        }
        return masPobre;
    }
    
    /**
     * Encuentra al pastor con menos feligreses entre una lista
     * "señala al más escaso en feligreses"
     * @param candidatos lista de pastores candidatos
     * @return el pastor con menos feligreses
     */
    public Pastor encontrarMenorGrey(List<Pastor> candidatos) {
        if (candidatos == null || candidatos.isEmpty()) {
            return null;
        }
        
        Pastor menorGrey = candidatos.get(0);
        for (Pastor pastor : candidatos) {
            if (pastor.getFeligreses() < menorGrey.getFeligreses()) {
                menorGrey = pastor;
            }
        }
        return menorGrey;
    }
    
    /**
     * Reorganiza el corro para cumplir con la cortesía de vecindad
     * "se verifique y, si menester fuere, se reorganice el corro"
     */
    public void reorganizarCorro() {
        if (pastores.size() < 2) {
            return;
        }
        
        boolean necesitaReorganizar = true;
        int intentos = 0;
        int maxIntentos = pastores.size() * 2; // evitar bucle infinito
        
        while (necesitaReorganizar && intentos < maxIntentos) {
            necesitaReorganizar = false;
            
            for (int i = 0; i < pastores.size(); i++) {
                int siguienteIndice = (i + 1) % pastores.size();
                Pastor actual = pastores.get(i);
                Pastor siguiente = pastores.get(siguienteIndice);
                
                // Verificar si hay conflicto (mismo trato a la derecha)
                if (actual.esDelMismoTrato(siguiente)) {
                    // Buscar una posición válida para el siguiente
                    boolean intercambioRealizado = false;
                    
                    for (int j = 0; j < pastores.size(); j++) {
                        if (j != i && j != siguienteIndice) {
                            Pastor candidato = pastores.get(j);
                            
                            // Verificar si intercambiar resuelve el problema
                            if (!actual.esDelMismoTrato(candidato) && 
                                !puedeGenerarConflicto(siguienteIndice, j)) {
                                
                                // Realizar intercambio
                                Collections.swap(pastores, siguienteIndice, j);
                                intercambioRealizado = true;
                                necesitaReorganizar = true;
                                break;
                            }
                        }
                    }
                    
                    if (intercambioRealizado) {
                        break; // Reiniciar verificación
                    }
                }
            }
            intentos++;
        }
        
        // Actualizar posición actual si es necesario
        ajustarPosicionActual();
    }
    
    /**
     * Verifica si intercambiar dos pastores puede generar nuevos conflictos
     */
    private boolean puedeGenerarConflicto(int pos1, int pos2) {
        Pastor pastor1 = pastores.get(pos1);
        Pastor pastor2 = pastores.get(pos2);
        
        // Verificar vecinos de pos2 con pastor1
        int anteriorPos2 = (pos2 - 1 + pastores.size()) % pastores.size();
        int siguientePos2 = (pos2 + 1) % pastores.size();
        
        if (anteriorPos2 != pos1 && siguientePos2 != pos1) {
            Pastor anteriorPos2Pastor = pastores.get(anteriorPos2);
            
            if (anteriorPos2Pastor.esDelMismoTrato(pastor1)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si la vecindad actual cumple las reglas
     * "Guárdese una severa cortesía de vecindad"
     * @return true si no hay conflictos de vecindad
     */
    public boolean verificarVecindad() {
        if (pastores.size() < 2) {
            return true;
        }
        
        for (int i = 0; i < pastores.size(); i++) {
            int siguienteIndice = (i + 1) % pastores.size();
            Pastor actual = pastores.get(i);
            Pastor siguiente = pastores.get(siguienteIndice);
            
            if (actual.esDelMismoTrato(siguiente)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Verifica si queda un solo pastor en la mesa
     * "quede en la redonda pista un solo pastor"
     * @return true si solo queda un pastor
     */
    public boolean quedaUnSolo() {
        return pastores.size() == 1;
    }
    
    /**
     * Da la vuelta al siguiente pastor en el turno
     * "designa al siguiente en tomar la palabra y moverse"
     */
    public void darVuelta() {
        if (!pastores.isEmpty()) {
            posicionActual = (posicionActual + 1) % pastores.size();
        }
    }
    
    /**
     * Establece la posición del pastor más rico para empezar
     * "empiece la danza aquel pastor que más doblones guarda"
     */
    public void empezarConMasRico() {
        Pastor masRico = encontrarMasRico();
        if (masRico != null) {
            posicionActual = pastores.indexOf(masRico);
        }
    }
    
    /**
     * Ajusta la posición actual después de reorganizaciones
     */
    private void ajustarPosicionActual() {
        if (posicionActual >= pastores.size() && !pastores.isEmpty()) {
            posicionActual = 0;
        }
    }
    
    // Getters
    public List<Pastor> obtenerPastoresEnMesa() {
        return new ArrayList<>(pastores);
    }
    
    public int contarPastores() {
        return pastores.size();
    }
    
    public int getPosicionActual() {
        return posicionActual;
    }
    
    public int getN() {
        return n;
    }
    
    public boolean estaVacia() {
        return pastores.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("Mesa[pastores=%d, posicion=%d, n=%d]", 
                           pastores.size(), posicionActual, n);
    }
}