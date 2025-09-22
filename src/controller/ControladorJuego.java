package controller;

import model.AccionPastor;
import model.Mesa;
import model.Pastor;
import model.PilaDesposeidos;
import view.VistaJuego;

import java.util.List;
import java.util.ArrayList;

/**
 * Controlador principal del juego. Coordina la Mesa, la Pila de desposeídos y la Vista.
 * <p>
 * Este controlador usa los métodos públicos definidos en las clases del modelo (Mesa, Pastor, PilaDesposeidos).
 * </p>
 */
public class ControladorJuego {

    private final VistaJuego vista;
    private Mesa mesa;
    private PilaDesposeidos pilaDesposeidos;
    private boolean juegoTerminado = false;
    private int nVecinos = 1;
    private int turno = 0;

    /**
     * Crea un controlador asociado a una vista. No inicia el juego automáticamente;
     * llame a {iniciar(int,int)} para crear la mesa y comenzar.
     *
     * vista vista que implementa la interfaz {VistaJuego}
     * IllegalArgumentException si la vista es null
     */
    public ControladorJuego(VistaJuego vista) {
        if (vista == null) {
            throw new IllegalArgumentException("La vista no puede ser null");
        }
        this.vista = vista;
    }

    /**
     * Inicializa la mesa y la pila con los parámetros suministrados y comienza el ciclo
     * principal en un hilo separado para no bloquear la UI.
     *
     * numPastores número de pastores iniciales (>= 2)
     * n           número de vecinos a considerar para degüello (1 <= n < numPastores)
     * IllegalArgumentException si los parámetros no cumplen condiciones
     */
    public void iniciar(int numPastores, int n) {
        if (numPastores < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 pastores.");
        }
        if (n < 1 || n >= numPastores) {
            throw new IllegalArgumentException("El valor de n debe ser >=1 y menor que numPastores.");
        }
        this.nVecinos = n;
        // Construcción de la mesa y pila — se asume que Mesa tiene un constructor (numPastores, n)
        this.mesa = new Mesa(numPastores, n);
        this.pilaDesposeidos = new PilaDesposeidos();

        // Mostrar estado inicial en la vista
        actualizarVista(0);

        // Iniciar el bucle de turnos en un hilo separado para no bloquear la UI
        new Thread(this::bucleTurnos, "Rueda-Turnos").start();
    }

    /** Bucle principal de turnos. Termina cuando queda un solo pastor en la mesa. */
    private void bucleTurnos() {
        try {
            // El que inicia: el más rico (si la Mesa tiene un método, se podría usar; aquí buscamos en la lista)
            List<Pastor> lista = mesa.obtenerPastoresEnMesa();
            int posicionActual = 0;
            int maxD = -1;
            for (int i = 0; i < lista.size(); i++) {
                Pastor p = lista.get(i);
                if (p.getDoblones() > maxD) {
                    maxD = p.getDoblones();
                    posicionActual = i;
                }
            }

            while (!mesa.quedaUnSolo() && !juegoTerminado) {
                turno++;
                Pastor actual = mesa.obtenerPastorActual();
                if (actual == null) {
                    // sincronizar con la lista por si la implementación de Mesa usa posición interna
                    if (lista.size() > 0) {
                        actual = lista.get(posicionActual % lista.size());
                    } else {
                        vista.mostrarError("Error: No hay pastores en la mesa.");
                        break;
                    }
                }

                // Mostrar turno en la vista
                vista.mostrarTurno(actual, puedeHurtar(actual), !pilaDesposeidos.estaVacia(), turno);

                // Crear lista de acciones disponibles
                List<AccionPastor> accionesDisponibles = new ArrayList<>();
                accionesDisponibles.add(AccionPastor.ARRIMAR_GUADAÑA_DERECHA);
                accionesDisponibles.add(AccionPastor.ARRIMAR_GUADAÑA_IZQUIERDA);
                
                // Si la pila no está vacía, se puede rescatar
                if (!pilaDesposeidos.estaVacia()) {
                    accionesDisponibles.add(AccionPastor.SACAR_DEL_OLVIDO);
                }
                
                // Si puede hurtar (es el más pobre), puede meter mano
                if (puedeHurtar(actual)) {
                    accionesDisponibles.add(AccionPastor.METER_MANO_FALTRIQUERA);
                }
                
                AccionPastor accion = vista.pedirAccion(accionesDisponibles);
                if (accion == null) {
                    // En caso de que la vista no proporcione acción, tomamos una por defecto: degüello a la derecha
                    accion = AccionPastor.ARRIMAR_GUADAÑA_DERECHA;
                }

                switch (accion) {
                    case SACAR_DEL_OLVIDO:
                        if (!pilaDesposeidos.estaVacia()) {
                            Pastor rescatado = pilaDesposeidos.sacarDePila();
                            if (rescatado != null) {
                                // la regla dice: el actual da la mitad cabal de sus fieles y riqueza
                                int mitD = actual.getDoblones() / 2;
                                int mitF = actual.getFeligreses() / 2;
                                // El actual entrega la mitad
                                actual.setDoblones(actual.getDoblones() - mitD);
                                actual.setFeligreses(actual.getFeligreses() - mitF);
                                // El rescatado recibe la mitad
                                rescatado.recibirMitad(mitD, mitF);
                                // Sentar nuevamente a rescatado en la mesa (método público de Mesa)
                                mesa.sentarPastor(rescatado);
                                vista.mostrarRescate(rescatado, actual);
                            }
                        } else {
                            vista.mostrarError("La pila está vacía: no es posible rescatar.");
                        }
                        break;

                    case METER_MANO_FALTRIQUERA:
                        if (puedeHurtar(actual)) {
                            Pastor masRico = mesa.encontrarMasRico();
                            if (masRico != null && !masRico.equals(actual)) {
                                int roboD = masRico.getDoblones() / 3;
                                int roboF = masRico.getFeligreses() / 3;
                                masRico.setDoblones(masRico.getDoblones() - roboD);
                                masRico.setFeligreses(masRico.getFeligreses() - roboF);
                                actual.setDoblones(actual.getDoblones() + roboD);
                                actual.setFeligreses(actual.getFeligreses() + roboF);
                                vista.mostrarHurtoPiadoso(actual, masRico, roboD, roboF);
                            }
                        } else {
                            vista.mostrarError("No puede hurtar: no es el pastor más pobre.");
                        }
                        break;

                    case ARRIMAR_GUADAÑA_DERECHA:
                    case ARRIMAR_GUADAÑA_IZQUIERDA:
                        boolean derecha = (accion == AccionPastor.ARRIMAR_GUADAÑA_DERECHA);
                        List<Pastor> vecinos = mesa.mirarHacia(derecha, nVecinos);
                        if (vecinos == null || vecinos.isEmpty()) {
                            vista.mostrarError("No hay vecinos en la dirección seleccionada.");
                            break;
                        }
                        // Elegir entre los vecinos al de menor feligreses
                        Pastor victima = vecinos.get(0);
                        for (Pastor c : vecinos) {
                            if (c.tieneMenosFeligreses(victima)) {
                                victima = c;
                            }
                        }
                        // El victima pasa sus recursos al actual y va a la pila
                        victima.traspasarRecursos(actual);
                        // Sacar victima de la mesa (buscar su posición)
                        List<Pastor> current = mesa.obtenerPastoresEnMesa();
                        int pos = -1;
                        for (int i = 0; i < current.size(); i++) {
                            if (current.get(i).equals(victima)) {
                                pos = i;
                                break;
                            }
                        }
                        if (pos != -1) {
                            mesa.sacarPastor(pos);
                            pilaDesposeidos.echarAPila(victima);
                            vista.mostrarDeguello(victima, actual, derecha);
                            // Tras cada eliminación se debe reorganizar el corro para que a la derecha no haya mismo trato
                            mesa.reorganizarCorro();
                        } else {
                            vista.mostrarError("Error interno: no se encontró la víctima en la mesa.");
                        }
                        break;

                    default:
                        vista.mostrarError("Acción no reconocida por el controlador.");
                }

                // Actualizar la vista después de la acción
                // buscar posición actual si aplica y pasarla a pintarMesa
                List<Pastor> listaActual = mesa.obtenerPastoresEnMesa();
                int posActual = 0;
                for (int i = 0; i < listaActual.size(); i++) {
                    if (listaActual.get(i).equals(actual)) {
                        posActual = i;
                        break;
                    }
                }
                actualizarVista(posActual);

                // Avanzar al siguiente en la mesa si queda más de 1
                if (!mesa.quedaUnSolo()) {
                    mesa.darVuelta();
                }

                // Pequeña pausa para no saturar la interfaz
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Fin del juego
            juegoTerminado = true;
            Pastor ganador = null;
            List<Pastor> finalList = mesa.obtenerPastoresEnMesa();
            if (!finalList.isEmpty()) {
                ganador = finalList.get(0);
            }
            vista.mostrarReyFinal(ganador);

        } catch (Exception ex) {
            // Cualquier excepción se comunica a la vista
            vista.mostrarError("Error en el controlador: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean puedeHurtar(Pastor p) {
        if (p == null) return false;
        Pastor masPobre = mesa.encontrarMasPobre();
        return masPobre != null && masPobre.equals(p);
    }

    private void actualizarVista(int posicionActual) {
        List<Pastor> pastores = mesa.obtenerPastoresEnMesa();
        vista.pintarMesa(pastores, posicionActual);
        vista.pintarPila(pilaDesposeidos.obtenerDesposeidos());
    }

    /** Método para forzar la finalización del juego desde la vista (botón "detener"). */
    public void detenerJuego() {
        this.juegoTerminado = true;
    }
    
    /** Método para reiniciar el juego con nuevos parámetros */
    public void reiniciarJuego(int numPastores, int n) {
        // Detener el juego actual
        this.juegoTerminado = true;
        
        // Limpiar la vista
        vista.limpiar();
        
        // Reiniciar variables
        this.turno = 0;
        this.juegoTerminado = false;
        
        // Reiniciar con nuevos parámetros
        iniciar(numPastores, n);
    }
}
