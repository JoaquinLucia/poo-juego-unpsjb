package controlador;

import java.awt.event.ActionEvent;
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

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro maestro) {
        this.vista = vista;
        this.batalla = batalla;
        this.maestro = maestro;

        // Configuramos el temporizador para el turno del enemigo (1.5 segundos de pausa)
        this.timerEnemigo = new Timer(1500, (ActionEvent e) -> turnoEnemigo());
        this.timerEnemigo.setRepeats(false);

        // Enganchamos los botones de la vista
        this.vista.onAtacar(e -> {
            if (batalla.getHeroe().estaVivo() && !batalla.getHeroe().getaturdir()) {
                accionJugador_Atacar();
            }
        });

        this.vista.onDefenderse(e -> {
            // Verificamos que el héroe esté en condiciones de actuar
            if (batalla.getHeroe().estaVivo() && !batalla.getHeroe().getaturdir()) {
                accionJugador_Defender(); 
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

    /** Muestra la pantalla de batalla. */
    public void iniciar() {
        vista.mostrarResultado("¡Un " + batalla.getEnemigo().getNombre() + " se interpone en tu camino!");
        vista.mostrar();
    }

    /** Cierra la ventana de esta batalla (cada batalla tiene la suya). */
    public void cerrar() {
        timerEnemigo.stop();
        vista.cerrar();
    }

    /** Deja ver el resultado unos segundos y le avisa al maestro. */
    private void terminarBatalla(boolean ganoHeroe) {
        vista.setBotonesHabilitados(false);
        Timer pausa = new Timer(PAUSA_FIN_BATALLA_MS, e -> maestro.batallaTerminada(ganoHeroe));
        pausa.setRepeats(false);
        pausa.start();
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

    public void accionJugador_Defender() {
        Entidad heroe = batalla.getHeroe();
        
        // 1. El héroe levanta el escudo
        heroe.setDefendiendo(true);
        
        // 2. Bloqueamos botones y mostramos el mensaje
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(heroe.getNombre() + " adopta una postura defensiva.");
        
        // 3. Pasamos el turno al enemigo después de una breve pausa
        javax.swing.Timer timer = new javax.swing.Timer(1500, ev -> turnoEnemigo());
        timer.setRepeats(false);
        timer.start();
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
            Timer timerRecuperacion = new Timer(1500, e -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            timerRecuperacion.setRepeats(false);
            timerRecuperacion.start();
            
        } else {
            // Si no está aturdido, arranca el timer del enemigo para que te devuelva el golpe
            this.timerEnemigo.start();
        }
    }

private void turnoEnemigo() {
        if (!batalla.getEnemigo().estaVivo()) return;

        Entidad enemigo = batalla.getEnemigo();
        Entidad heroe = batalla.getHeroe();

        // 1. Verificar si el enemigo está aturdido
        if (enemigo.getaturdir()) {
            vista.mostrarResultado(enemigo.getNombre() + " está aturdido y pierde su turno.");
            enemigo.sacarAturdimiento();
            reducirCooldowns(enemigo);
            actualizarVista();
            
            // Hacemos una pausa de 2 segundos para que leas que está aturdido
            javax.swing.Timer timerAturdido = new javax.swing.Timer(2000, e -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            timerAturdido.setRepeats(false);
            timerAturdido.start();
            
            return; // Cortamos la ejecución acá
        }

        // 2. Pensamiento del enemigo: ¿Tiene habilidades especiales listas?
        boolean usoHabilidad = false;
        for (modelo.Habilidad hab : enemigo.getHabilidades()) {
            if (hab.getCdListo()) {
                hab.ejecutarHabilidad(enemigo, heroe);
                vista.mostrarResultado("¡" + enemigo.getNombre() + " usó " + hab.getNombreHabilidad() + "!");
                usoHabilidad = true;
                break;
            }
        }

        // Si ninguna habilidad estaba lista, ataca normal
        if (!usoHabilidad) {
            enemigo.atacar(heroe);
            vista.mostrarResultado("El " + enemigo.getNombre() + " te atacó ferozmente.");
        }

        // 3. Efectos visuales instantáneos tras el ataque
        vista.efectoTemblor();
        reducirCooldowns(enemigo);
        actualizarVista();

        // 4. EL SECRETO DEL RITMO: Pausa de 2.5 segundos (2500 ms) antes de chequear el estado y devolverte el turno
        javax.swing.Timer timerEspera = new javax.swing.Timer(2500, e -> {
            if (!heroe.estaVivo()) {
                vista.mostrarResultado("¡Has sido derrotado por el " + enemigo.getNombre() + "!");
                terminarBatalla(false);
            } else {
                // Verificamos si el golpe del enemigo te aturdió
                if (heroe.getaturdir()) {
                    vista.mostrarResultado("¡Estás aturdido! Pierdes tu turno.");
                    heroe.sacarAturdimiento();
                    reducirCooldowns(heroe);
                    
                    // Pausa adicional para leer que te aturdieron, y el enemigo repite turno
                    javax.swing.Timer timerDobleAtaque = new javax.swing.Timer(2000, ev -> turnoEnemigo());
                    timerDobleAtaque.setRepeats(false);
                    timerDobleAtaque.start();
                } else {
                    // Flujo normal: estás vivo y no estás aturdido. Te devuelve los controles.
                    heroe.setDefendiendo(false); 
                    vista.setBotonesHabilitados(true); 
                    vista.mostrarResultado("¡Es tu turno!");
                    vista.setBotonesHabilitados(true); 
                    vista.mostrarResultado("¡Es tu turno!");
                }
            }
        });
        timerEspera.setRepeats(false);
        timerEspera.start();
    }
    
    private void reducirCooldowns(Entidad entidad) {
        for (Habilidad h : entidad.getHabilidades()) {
            if (h != null) {
                h.cronoCooldown();
            }
        }
    }
}