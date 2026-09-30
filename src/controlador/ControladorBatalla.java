package controlador;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.Timer;
import modelo.Batalla;
import vista.VistaBatalla;

/**
 * Controla la pantalla de combate.
 *
 * Las reglas de la pelea (turnos, aturdimiento, cooldowns, quién ganó) viven
 * en modelo.Batalla. Este controlador solo le pide a Batalla que ejecute cada
 * turno y se ocupa de la pantalla: mensajes, botones, temblor y los tiempos
 * entre un turno y otro.
 */
public class ControladorBatalla {
    private static final int PAUSA_FIN_BATALLA_MS = 2000; // tiempo para leer el resultado
    private static final int PAUSA_TURNO_MS = 1500;       // espera antes de que actúe el enemigo

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

        // Turno del enemigo, con una pausa para que se lea lo que pasó antes
        this.timerEnemigo = new Timer(PAUSA_TURNO_MS, (ActionEvent e) -> turnoEnemigo());
        this.timerEnemigo.setRepeats(false);
        timers.add(timerEnemigo);

        // Cada botón le pide a Batalla la acción correspondiente
        this.vista.onAtacar(e -> accionJugador(batalla::ejecutarAtaqueHeroe));
        this.vista.onHabilidad(e -> accionJugador(() -> batalla.ejecutarHabilidadHeroe(0)));

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

    public VistaBatalla getVista() {
        return vista;
    }

    // ---------- Turnos ----------

    /**
     * Ejecuta una acción del héroe a través de Batalla. Si después de la acción
     * sigue siendo el turno del héroe, la acción no se hizo (por ejemplo, la
     * habilidad estaba en cooldown) y solo se muestra el aviso.
     */
    private void accionJugador(Supplier<String> accion) {
        if (batalla.getTurnoActual() != Batalla.Turno.HEROE || batalla.estaTerminada()) {
            return;
        }
        vista.mostrarResultado(accion.get());

        if (batalla.getTurnoActual() == Batalla.Turno.HEROE) {
            return;
        }
        vista.efectoTemblor();
        despuesDelTurno();
    }

    private void turnoEnemigo() {
        if (batalla.estaTerminada()) {
            return;
        }
        vista.mostrarResultado(batalla.ejecutarTurnoEnemigo());
        vista.efectoTemblor();
        despuesDelTurno();
    }

    /** Decide qué sigue leyendo el estado de Batalla. */
    private void despuesDelTurno() {
        actualizarVista();

        if (batalla.estaTerminada()) {
            boolean gano = batalla.ganoHeroe();
            vista.mostrarResultado(gano
                    ? "¡Victoria! Has derrotado al " + batalla.getEnemigo().getNombre()
                    : "¡Has sido derrotado por el " + batalla.getEnemigo().getNombre() + "!");
            terminarBatalla(gano);
            return;
        }

        vista.setBotonesHabilitados(false);
        if (batalla.getTurnoActual() == Batalla.Turno.ENEMIGO) {
            timerEnemigo.start();
        } else if (batalla.getHeroe().getaturdir()) {
            // Batalla consume el turno del héroe aturdido
            programar(PAUSA_TURNO_MS, () -> accionJugador(batalla::ejecutarAtaqueHeroe));
        } else {
            vista.setBotonesHabilitados(true);
            vista.mostrarResultado("¡Es tu turno!");
        }
    }

    /** Deja ver el resultado unos segundos y le avisa al maestro. */
    private void terminarBatalla(boolean ganoHeroe) {
        vista.setBotonesHabilitados(false);
        programar(PAUSA_FIN_BATALLA_MS, () -> maestro.batallaTerminada(ganoHeroe));
    }

    // ---------- Pantalla ----------

    private void actualizarVista() {
        vista.actualizarEstadisticas(
            batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
            batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida()
        );
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
}