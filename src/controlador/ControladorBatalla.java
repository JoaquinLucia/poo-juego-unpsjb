package controlador;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import modelo.Batalla;
import modelo.Entidad;
import modelo.Habilidad;
import vista.VistaBatalla;

public class ControladorBatalla {
    private static final int PAUSA_FIN_BATALLA_MS = 2000; // tiempo para leer el resultado

    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro maestro;
    private final Timer timerEnemigo;

    // Todos los temporizadores de la pelea, para poder congelarlos durante la pausa
    private final List<Timer> timers = new ArrayList<>();
    private final List<Timer> congelados = new ArrayList<>();

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro maestro) {
        this.vista = vista;
        this.batalla = batalla;
        this.maestro = maestro;

        // Configuramos el temporizador para el turno del enemigo (1.5 segundos de pausa)
        this.timerEnemigo = new Timer(1500, (ActionEvent e) -> turnoEnemigo());
        this.timerEnemigo.setRepeats(false);
        timers.add(timerEnemigo);

        // Enganchamos los botones de la vista
        this.vista.onAtacar(e -> {
            if (batalla.getHeroe().estaVivo() && !batalla.getHeroe().getaturdir()) {
                accionJugador_Atacar();
            }
        });

        this.vista.onHabilidad(e -> {
            if (batalla.getHeroe().estaVivo() && !batalla.getHeroe().getaturdir()) {
                accionJugador_Habilidad();
            }
        });

        // Actualización inicial al entrar a la pantalla
        actualizarVista();
    }

    /** Prepara el mensaje inicial de la pelea (el maestro se encarga de mostrar la pantalla). */
    public void iniciar() {
        vista.mostrarResultado("¡Un " + batalla.getEnemigo().getNombre() + " se interpone en tu camino!");
    }

    /** Detiene los temporizadores de esta batalla (al salir de la pelea). */
    public void cerrar() {
        for (Timer t : timers) {
            t.stop();
        }
        congelados.clear();
    }

    /** Congela la pelea: los temporizadores que estaban corriendo se detienen. */
    public void pausar() {
        congelados.clear();
        for (Timer t : timers) {
            if (t.isRunning()) {
                t.stop();
                congelados.add(t);
            }
        }
    }

    /** Retoma los temporizadores que se congelaron en pausar(). */
    public void reanudar() {
        for (Timer t : congelados) {
            t.restart();
        }
        congelados.clear();
    }

    /** Crea un temporizador de un solo disparo, registrado para poder pausarlo. */
    private void programar(int milisegundos, Runnable accion) {
        Timer t = new Timer(milisegundos, e -> {
            timers.remove((Timer) e.getSource());
            accion.run();
        });
        t.setRepeats(false);
        timers.add(t);
        t.start();
    }

    public VistaBatalla getVista() {
        return vista;
    }

    /** Deja ver el resultado unos segundos y le avisa al maestro. */
    private void terminarBatalla(boolean ganoHeroe) {
        vista.setBotonesHabilitados(false);
        programar(PAUSA_FIN_BATALLA_MS, () -> maestro.batallaTerminada(ganoHeroe));
    }

    private void actualizarVista() {
        vista.actualizarEstadisticas(
            batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
            batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida()
        );
    }

    private void accionJugador_Atacar() {
        batalla.getHeroe().atacar(batalla.getEnemigo());
        vista.mostrarResultado(batalla.getHeroe().getNombre() + " asestó un golpe básico!");
        
        vista.efectoTemblor(); // Hace temblar la pantalla un poquito
        
        finalizarTurnoJugador();
    }

    private void accionJugador_Habilidad() {
        if (batalla.getHeroe().getCantidadHabilidades() > 0) {
            Habilidad hab = batalla.getHeroe().getHabilidad(0);
            
            if (hab.getCdListo()) {
                hab.ejecutarHabilidad(batalla.getHeroe(), batalla.getEnemigo());
                vista.mostrarResultado("¡" + batalla.getHeroe().getNombre() + " usó " + hab.getNombreHabilidad() + "!");
                
                vista.efectoTemblor(); // Tiembla cuando usás la habilidad
                
                finalizarTurnoJugador();
            } else {
                vista.mostrarResultado("Habilidad en enfriamiento (" + hab.getCooldownActual() + " turnos)");
            }
        } else {
            vista.mostrarResultado("El héroe no tiene habilidades.");
        }
    }

    private void finalizarTurnoJugador() {
        actualizarVista();
        vista.setBotonesHabilitados(false); // Bloqueamos los botones para no hacer trampa
        
        reducirCooldowns(batalla.getHeroe());

        if (!batalla.getEnemigo().estaVivo()) {
            vista.mostrarResultado("¡Victoria! Has derrotado al " + batalla.getEnemigo().getNombre());
            terminarBatalla(true);
            return; // Acá termina la pelea
        }

        // Si el enemigo sigue vivo, chequeamos si lo aturdiste con la habilidad
        if (batalla.getEnemigo().getaturdir()) {
            vista.mostrarResultado("El " + batalla.getEnemigo().getNombre() + " está aturdido y pierde su turno.");
            batalla.getEnemigo().sacarAturdimiento(); 
            
            // Pausa cortita y te devuelve el turno a vos
            programar(1500, () -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            
        } else {
            // Si no está aturdido, arranca el timer del enemigo para que te devuelva el golpe
            this.timerEnemigo.start();
        }
    }

    private void turnoEnemigo() {
        if (!batalla.getEnemigo().estaVivo()) return;

        batalla.getEnemigo().atacar(batalla.getHeroe());
        vista.mostrarResultado("El " + batalla.getEnemigo().getNombre() + " te atacó ferozmente.");
        
        vista.efectoTemblor();
        
        reducirCooldowns(batalla.getEnemigo());
        
        actualizarVista();

        if (!batalla.getHeroe().estaVivo()) {
            vista.mostrarResultado("¡Has sido derrotado por el " + batalla.getEnemigo().getNombre() + "!");
            terminarBatalla(false);
        } else {
            vista.setBotonesHabilitados(true); // Te devuelve los botones
            vista.mostrarResultado("¡Es tu turno!");
        }
    }
    
    private void reducirCooldowns(Entidad entidad) {
        for (Habilidad h : entidad.getHabilidades()) {
            if (h != null) {
                h.cronoCooldown();
            }
        }
    }
}