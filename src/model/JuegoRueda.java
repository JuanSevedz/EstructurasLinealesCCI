package model;

import java.util.*;

/**
 * Controlador principal del juego de la rueda de pastores
 * "prosigue la rueda sin desfallecer"
 */
public class JuegoRueda {
    private Mesa mesa;
    private PilaDesposeidos pilaDesposeidos;
    private boolean juegoTerminado;
    private int turno;
    private String[] tiposOficio = {"COMERCIANTE", "ARTESANO", "AGRICULTOR", "GANADERO", "BANQUERO"};
    private Random random;
    
    /**
     * Constructor del juego
     * numPastores número de pastores iniciales
     * n número de posiciones a contar para eliminación
     */
    public JuegoRueda(int numPastores, int n) {
        this.mesa = new Mesa(numPastores, n);
        this.pilaDesposeidos = new PilaDesposeidos();
        this.juegoTerminado = false;
        this.turno = 0;
        this.random = new Random();
        
        inicializarPastores(numPastores);
    }
    
    /**
     * Inicializa los pastores con valores aleatorios
     */
    private void inicializarPastores(int numPastores) {
        String[] nombres = {"Fray Ambrosio", "Don Rodrigo", "Padre Benito", "Mosén García", 
                           "Capellán Ruiz", "Abad Martín", "Prior Fernández", "Canónigo López", 
                           "Vicario Sánchez", "Deán Jiménez"};
        
        for (int i = 0; i < numPastores; i++) {
            String nombre = i < nombres.length ? nombres[i] : "Pastor " + (i + 1);
            int doblones = random.nextInt(500) + 100; // Entre 100 y 600 doblones
            int feligreses = random.nextInt(200) + 50; // Entre 50 y 250 feligreses
            String trato = tiposOficio[i % tiposOficio.length];
            
            Pastor pastor = new Pastor(i + 1, nombre, doblones, feligreses, trato);
            mesa.sentarPastor(pastor);
        }
        
        // Reorganizar para cumplir reglas de vecindad
        mesa.reorganizarCorro();
    }
    
    /**
     * Inicia la danza del juego
     * "empiece la danza aquel pastor que más doblones guarda en sus arcas"
     */
    public void empezarDanza() {
        if (mesa.contarPastores() <= 1) {
            juegoTerminado = true;
            return;
        }
        
        mesa.empezarConMasRico();
        juegoTerminado = false;
        turno = 1;
    }
    
    /**
     * Procesa un turno completo del juego
     * accion la acción que decide tomar el pastor actual
     * derecha true si mira a la derecha, false a la izquierda
     * @return resultado del turno
     */
    public ResultadoTurno tomarTurno(AccionPastor accion, boolean derecha) {
        if (juegoTerminado) {
            return new ResultadoTurno(false, "El juego ya terminó", null);
        }
        
        Pastor pastorActual = mesa.obtenerPastorActual();
        if (pastorActual == null) {
            juegoTerminado = true;
            return new ResultadoTurno(false, "No hay pastor actual", null);
        }
        
        ResultadoTurno resultado = null;
        
        switch (accion) {
            case ARRIMAR_GUADAÑA_DERECHA:
                resultado = arrimarGuadaña(true);
                break;
            case ARRIMAR_GUADAÑA_IZQUIERDA:
                resultado = arrimarGuadaña(false);
                break;
            case SACAR_DEL_OLVIDO:
                resultado = sacarDelOlvido();
                break;
            case METER_MANO_FALTRIQUERA:
                resultado = meterManoEnFaltriquera();
                break;
            default:
                resultado = new ResultadoTurno(false, "Acción no válida", null);
        }
        
        if (resultado.isExitoso()) {
            // Verificar fin del juego
            if (mesa.quedaUnSolo()) {
                juegoTerminado = true;
            } else {
                // Pasar al siguiente turno
                mesa.darVuelta();
                turno++;
                
                // Reorganizar si es necesario
                if (!mesa.verificarVecindad()) {
                    mesa.reorganizarCorro();
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Arrima la guadaña para segar una cabeza
     * "arrimar la guadaña y segar otra cabeza de vecino"
     * derecha dirección a mirar
     * @return resultado de la eliminación
     */
    public ResultadoTurno arrimarGuadaña(boolean derecha) {
        List<Pastor> vecinos = mesa.mirarHacia(derecha, mesa.getN());
        
        if (vecinos.isEmpty()) {
            return new ResultadoTurno(false, "No hay vecinos en esa dirección", null);
        }
        
        // Encontrar al de menor grey
        Pastor menorGrey = mesa.encontrarMenorGrey(vecinos);
        if (menorGrey == null) {
            return new ResultadoTurno(false, "No se pudo encontrar pastor a eliminar", null);
        }
        
        // Traspasar recursos al pastor actual
        Pastor pastorActual = mesa.obtenerPastorActual();
        menorGrey.traspasarRecursos(pastorActual);
        
        // Sacar de la mesa y echar a la pila
        int posicion = mesa.obtenerPastoresEnMesa().indexOf(menorGrey);
        Pastor eliminado = mesa.sacarPastor(posicion);
        pilaDesposeidos.echarAPila(eliminado);
        
        String mensaje = String.format("%s eliminado", eliminado.getNombre());
        
        return new ResultadoTurno(true, mensaje, eliminado);
    }
    
    /**
     * Saca del olvido al que encima de todos yace
     * "sacar del olvido al que encima de todos yace en la pila"
     * @return resultado del rescate
     */
    public ResultadoTurno sacarDelOlvido() {
        if (pilaDesposeidos.estaVacia()) {
            return new ResultadoTurno(false, "La pila de desposeídos está vacía", null);
        }
        
        Pastor pastorActual = mesa.obtenerPastorActual();
        Pastor rescatado = pilaDesposeidos.sacarDePila();
        
        if (rescatado == null) {
            return new ResultadoTurno(false, "No se pudo rescatar al pastor", null);
        }
        
        // Darle la mitad de los recursos del pastor actual
        int mitadDoblones = pastorActual.getDoblones() / 2;
        int mitadFeligreses = pastorActual.getFeligreses() / 2;
        
        pastorActual.setDoblones(pastorActual.getDoblones() - mitadDoblones);
        pastorActual.setFeligreses(pastorActual.getFeligreses() - mitadFeligreses);
        
        rescatado.setDoblones(rescatado.getDoblones() + mitadDoblones);
        rescatado.setFeligreses(rescatado.getFeligreses() + mitadFeligreses);
        
        // Sentar al rescatado en la mesa
        mesa.sentarPastor(rescatado);
        
        String mensaje = String.format("%s rescatado", rescatado.getNombre());
        
        return new ResultadoTurno(true, mensaje, rescatado);
    }
    
    /**
     * Aplica el hurto piadoso - meter mano en la faltriquera del más rico
     * "meter mano en la faltriquera del más rico, y llevarse de ella la tercia parte"
     * @return resultado del hurto
     */
    public ResultadoTurno meterManoEnFaltriquera() {
        Pastor pastorActual = mesa.obtenerPastorActual();
        Pastor masRico = mesa.encontrarMasRico();
        Pastor masPobre = mesa.encontrarMasPobre();
        
        // Verificar que el pastor actual es el más pobre
        if (!pastorActual.equals(masPobre)) {
            return new ResultadoTurno(false, "Solo el más pobre puede meter mano en la faltriquera", null);
        }
        
        if (pastorActual.equals(masRico)) {
            return new ResultadoTurno(false, "No puedes robarte a ti mismo", null);
        }
        
        // Aplicar el hurto piadoso
        boolean exitoso = pastorActual.aplicarHurtoPiadoso(masRico);
        
        if (!exitoso) {
            return new ResultadoTurno(false, "No se pudo realizar el hurto piadoso", null);
        }
        
        String mensaje = String.format("%s hurtó a %s", pastorActual.getNombre(), masRico.getNombre());
        
        return new ResultadoTurno(true, mensaje, masRico);
    }
    
    /**
     * Verifica si el juego ha terminado
     * "concluirá el juego cuando, rotos los demás eslabones, quede en la redonda pista un solo pastor"
     * @return true si el juego terminó
     */
    public boolean verificarFinDanza() {
        return juegoTerminado || mesa.quedaUnSolo();
    }
    
    /**
     * Obtiene el ganador del juego
     * "rey de burlas y veras, dueño de bolsas y conciencias"
     * @return el pastor ganador, o null si el juego no ha terminado
     */
    public Pastor obtenerReyDeBurlasYVeras() {
        if (verificarFinDanza() && mesa.contarPastores() == 1) {
            return mesa.obtenerPastoresEnMesa().get(0);
        }
        return null;
    }
    
    /**
     * Reinicia el juego con los mismos parámetros
     */
    public void reiniciarRueda() {
        int numPastores = mesa.contarPastores() + pilaDesposeidos.obtenerTamaño();
        int n = mesa.getN();
        
        // Limpiar estado actual
        pilaDesposeidos.vaciarPila();
        mesa = new Mesa(numPastores, n);
        
        // Reinicializar
        inicializarPastores(numPastores);
        juegoTerminado = false;
        turno = 0;
    }
    
    /**
     * Obtiene el estado actual del juego
     * @return estado completo del juego
     */
    public EstadoRueda obtenerEstadoRueda() {
        Pastor pastorActual = mesa.obtenerPastorActual();
        boolean puedeHurtar = false;
        boolean puedeRescatar = !pilaDesposeidos.estaVacia();
        
        // Verificar si puede hacer hurto piadoso
        if (pastorActual != null) {
            Pastor masPobre = mesa.encontrarMasPobre();
            puedeHurtar = pastorActual.equals(masPobre) && mesa.contarPastores() > 1;
        }
        
        return new EstadoRueda(
            pastorActual,
            mesa.obtenerPastoresEnMesa(),
            pilaDesposeidos.obtenerDesposeidos(),
            puedeHurtar,
            puedeRescatar,
            juegoTerminado,
            turno
        );
    }
    
    /**
     * Obtiene las acciones disponibles para el pastor actual
     */
    public List<AccionPastor> obtenerAccionesDisponibles() {
        List<AccionPastor> acciones = new ArrayList<>();
        
        if (juegoTerminado) {
            return acciones;
        }
        
        // Siempre puede arrimar la guadaña en ambas direcciones
        acciones.add(AccionPastor.ARRIMAR_GUADAÑA_DERECHA);
        acciones.add(AccionPastor.ARRIMAR_GUADAÑA_IZQUIERDA);
        
        // Puede rescatar si la pila no está vacía
        if (!pilaDesposeidos.estaVacia()) {
            acciones.add(AccionPastor.SACAR_DEL_OLVIDO);
        }
        
        // Puede hurtar si es el más pobre
        Pastor pastorActual = mesa.obtenerPastorActual();
        if (pastorActual != null && pastorActual.equals(mesa.encontrarMasPobre()) && mesa.contarPastores() > 1) {
            acciones.add(AccionPastor.METER_MANO_FALTRIQUERA);
        }
        
        return acciones;
    }
    
    // Getters
    public Mesa getMesa() {
        return mesa;
    }
    
    public PilaDesposeidos getPilaDesposeidos() {
        return pilaDesposeidos;
    }
    
    public int getTurno() {
        return turno;
    }
    
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    
    @Override
    public String toString() {
        return String.format("JuegoRueda[turno=%d, pastores=%d, desposeidos=%d, terminado=%s]", 
                           turno, mesa.contarPastores(), pilaDesposeidos.obtenerTamaño(), juegoTerminado);
    }
}


/**
 * Clase para encapsular el resultado de un turno
 */
class ResultadoTurno {
    private boolean exitoso;
    private String mensaje;
    private Pastor pastorAfectado;
    
    public ResultadoTurno(boolean exitoso, String mensaje, Pastor pastorAfectado) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.pastorAfectado = pastorAfectado;
    }
    
    // Getters y setters
    public boolean isExitoso() { return exitoso; }
    public String getMensaje() { return mensaje; }
    public Pastor getPastorAfectado() { return pastorAfectado; }
    
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}