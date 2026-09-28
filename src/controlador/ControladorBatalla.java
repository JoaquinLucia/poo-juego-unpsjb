package controlador;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

import modelo.Batalla;
import modelo.Entidad;
import modelo.Habilidad;
import vista.VistaBatalla;

public class ControladorBatalla {
    private final VistaBatalla vista;
    private final Batalla batalla;
    private final Timer timerEnemigo;

    public ControladorBatalla(VistaBatalla vista, Batalla batalla) {
        this.vista = vista;
        this.batalla = batalla;

        // Configuramos el temporizador para el turno del enemigo (1.5 segundos)
        this.timerEnemigo = new Timer(1500, (ActionEvent e) -> turnoEnemigo());
        this.timerEnemigo.setRepeats(false);

        // Enganchamos los botones de la vista a los métodos del controlador
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

        // Actualización inicial
        actualizarVista();
    }

    private void actualizarVista() {
        vista.actualizarEstadisticas(
            batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
            batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida()
        );
    }

    private void accionJugador_Atacar() {
        // El jugador ataca
        batalla.getHeroe().atacar(batalla.getEnemigo());
        vista.mostrarResultado(batalla.getHeroe().getNombre() + " asestó un golpe básico!");
        
        finalizarTurnoJugador();
    }

    private void accionJugador_Habilidad() {
        if (batalla.getHeroe().getCantidadHabilidades() > 0) {
            Habilidad hab = batalla.getHeroe().getHabilidad(0);
            
            if (hab.getCdListo()) {
                hab.ejecutarHabilidad(batalla.getHeroe(), batalla.getEnemigo());
                vista.mostrarResultado("¡" + batalla.getHeroe().getNombre() + " usó " + hab.getNombreHabilidad() + "!");
                finalizarTurnoJugador();
            } else {
                vista.mostrarResultado("Habilidad en enfriamiento (" + hab.getCooldownActual() + " turnos)");
                // No llamamos a finalizarTurnoJugador(), le dejamos elegir otra acción.
            }
        } else {
            vista.mostrarResultado("El héroe no tiene habilidades.");
        }
    }

    private void finalizarTurnoJugador() {
        actualizarVista();
        vista.setBotonesHabilitados(false); // Bloqueamos para que no spammee
        
        // Reducimos el cooldown de las habilidades del jugador
        reducirCooldowns(batalla.getHeroe());

        if (!batalla.getEnemigo().estaVivo()) {
            vista.mostrarResultado("¡Victoria! Has derrotado al " + batalla.getEnemigo().getNombre());
            // Acá podrías llamar al Menú para volver o guardar el puntaje
            return;
        }

        // Si el enemigo está vivo, chequeamos si está aturdido
        if (batalla.getEnemigo().getaturdir()) {
            vista.mostrarResultado("El " + batalla.getEnemigo().getNombre() + " está aturdido y pierde su turno.");
            batalla.getEnemigo().sacarAturdimiento(); // Se le pasa el efecto para el próximo turno
            
            // Como perdió el turno, le devolvemos los controles al jugador tras una pausa breve
            Timer timerRecuperacion = new Timer(1500, e -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            timerRecuperacion.setRepeats(false);
            timerRecuperacion.start();
            
        } else {
            // Si no está aturdido, arranca el timer normal para que ataque
            this.timerEnemigo.start();
        }
    }

    private void turnoEnemigo() {
        if (!batalla.getEnemigo().estaVivo()) return;

        // El enemigo ataca
        batalla.getEnemigo().atacar(batalla.getHeroe());
        vista.mostrarResultado("El " + batalla.getEnemigo().getNombre() + " te atacó ferozmente.");
        
        // Reducimos cooldowns del enemigo (si llegara a tener habilidades)
        reducirCooldowns(batalla.getEnemigo());
        
        actualizarVista();

        if (!batalla.getHeroe().estaVivo()) {
            vista.mostrarResultado("¡Has sido derrotado por el " + batalla.getEnemigo().getNombre() + "!");
        } else {
            vista.setBotonesHabilitados(true); // Le devolvemos el turno al jugador
        }
    }
    
    // Método auxiliar para bajarle los turnos a todas las habilidades de una entidad
    private void reducirCooldowns(Entidad entidad) {
        for (Habilidad h : entidad.getHabilidades()) {
            if (h != null) {
                h.cronoCooldown();
            }
        }
    }
}