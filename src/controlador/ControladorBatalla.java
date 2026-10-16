package controlador;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

import modelo.Batalla;
import modelo.Entidad;
import modelo.Habilidad;
import vista.VistaBatalla;
import controlador.utilidades.GestorDeSonido;

public class ControladorBatalla {
    private static final int PAUSA_FIN_BATALLA_MS = 2000; // tiempo para leer el resultado

    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro maestro;
    private final Timer timerEnemigo;
    
    // Guardamos la ruta del audio actual para poder detenerlo al finalizar la batalla
    private String rutaMusicaActual;

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

    /** Prepara el mensaje inicial de la pelea y la música específica del enemigo. */
    public void iniciar() {
        String nombreEnemigo = batalla.getEnemigo().getNombre();
        vista.mostrarResultado("¡Un " + nombreEnemigo + " se interpone en tu camino!");
    
        // Seleccionamos la música según el nombre del enemigo
        if (nombreEnemigo != null) {
            String nombreMinuscula = nombreEnemigo.toLowerCase();
            if (nombreMinuscula.contains("bruto")) {
                rutaMusicaActual = "recursos/sonidos/bruto_musica.wav";
            } else if (nombreMinuscula.contains("vibora")) {
                rutaMusicaActual = "recursos/sonidos/vibora_musica.wav";
            } else if (nombreMinuscula.contains("viuda")) {
                rutaMusicaActual = "recursos/sonidos/viuda_musica.wav";
            } else if (nombreMinuscula.contains("guardian")) {
                rutaMusicaActual = "recursos/sonidos/guardian_musica.wav";
            } else {
                rutaMusicaActual = "recursos/sonidos/bruto_musica.wav";
            }
        } else {
            rutaMusicaActual = "recursos/sonidos/bruto_musica.wav";
        }
    
        // Reproducir la música en bucle continuo
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle(rutaMusicaActual);
    }

    /** Detiene los temporizadores y la música de fondo de esta batalla. */
    public void cerrar() {
        timerEnemigo.stop();
        if (rutaMusicaActual != null) {
            GestorDeSonido.getInstancia().detenerAudio(rutaMusicaActual);
        }
    }

    public VistaBatalla getVista() {
        return vista;
    }

    /** Deja ver el resultado unos segundos, detiene la música y le avisa al maestro. */
    private void terminarBatalla(boolean ganoHeroe) {
        vista.setBotonesHabilitados(false);
        
        // Creamos el timer de pausa para leer el resultado y mantener la música viva durante este lapso
        Timer pausa = new Timer(PAUSA_FIN_BATALLA_MS, e -> {
            // Recién cuando termina la pausa y la animación, cortamos la música del enemigo
            if (rutaMusicaActual != null) {
                GestorDeSonido.getInstancia().detenerAudio(rutaMusicaActual);
            }
            // Y le avisamos al maestro que la batalla terminó oficialmente
            maestro.batallaTerminada(ganoHeroe);
        });
        
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
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        batalla.getHeroe().atacar(batalla.getEnemigo());
        vista.mostrarResultado(batalla.getHeroe().getNombre() + " asestó un golpe básico!");
        vista.efectoTemblor(); 
        finalizarTurnoJugador();
    }

    public void accionJugador_Defender() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        Entidad heroe = batalla.getHeroe();
        heroe.setDefendiendo(true);
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(heroe.getNombre() + " adopta una postura defensiva.");
        
        Timer timer = new Timer(1500, ev -> turnoEnemigo());
        timer.setRepeats(false);
        timer.start();
    }

    private void accionJugador_Habilidad() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        if (batalla.getHeroe().getCantidadHabilidades() > 0) {
            Habilidad hab = batalla.getHeroe().getHabilidad(0);
            
            if (hab.getCdListo()) {
                hab.ejecutarHabilidad(batalla.getHeroe(), batalla.getEnemigo());
                vista.mostrarResultado("¡" + batalla.getHeroe().getNombre() + " usó " + hab.getNombreHabilidad() + "!");
                vista.efectoTemblor(); 
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
        vista.setBotonesHabilitados(false); 
        
        reducirCooldowns(batalla.getHeroe());

        if (!batalla.getEnemigo().estaVivo()) {
            vista.mostrarResultado("¡Victoria! Has derrotado al " + batalla.getEnemigo().getNombre());
            terminarBatalla(true);
            return; 
        }

        if (batalla.getEnemigo().getaturdir()) {
            vista.mostrarResultado("El " + batalla.getEnemigo().getNombre() + " está aturdido y pierde su turno.");
            batalla.getEnemigo().sacarAturdimiento(); 
            
            Timer timerRecuperacion = new Timer(1500, e -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            timerRecuperacion.setRepeats(false);
            timerRecuperacion.start();
        } else {
            this.timerEnemigo.start();
        }
    }

    private void turnoEnemigo() {
        if (!batalla.getEnemigo().estaVivo()) return;

        Entidad enemigo = batalla.getEnemigo();
        Entidad heroe = batalla.getHeroe();

        if (enemigo.getaturdir()) {
            vista.mostrarResultado(enemigo.getNombre() + " está aturdido y pierde su turno.");
            enemigo.sacarAturdimiento();
            reducirCooldowns(enemigo);
            actualizarVista();
            
            Timer timerAturdido = new Timer(2000, e -> {
                vista.setBotonesHabilitados(true);
                vista.mostrarResultado("¡Es tu turno!");
            });
            timerAturdido.setRepeats(false);
            timerAturdido.start();
            return;
        }

        boolean usoHabilidad = false;
        for (Habilidad hab : enemigo.getHabilidades()) {
            if (hab.getCdListo()) {
                hab.ejecutarHabilidad(enemigo, heroe);
                vista.mostrarResultado("¡" + enemigo.getNombre() + " usó " + hab.getNombreHabilidad() + "!");
                usoHabilidad = true;
                break;
            }
        }

        if (!usoHabilidad) {
            enemigo.atacar(heroe);
            vista.mostrarResultado("El " + enemigo.getNombre() + " te atacó ferozmente.");
        }

        vista.efectoTemblor();
        reducirCooldowns(enemigo);
        actualizarVista();

        Timer timerEspera = new Timer(2500, e -> {
            if (!heroe.estaVivo()) {
                vista.mostrarResultado("¡Has sido derrotado por el " + enemigo.getNombre() + "!");
                terminarBatalla(false);
            } else {
                if (heroe.getaturdir()) {
                    vista.mostrarResultado("¡Estás aturdido! Pierdes tu turno.");
                    heroe.sacarAturdimiento();
                    reducirCooldowns(heroe);
                    
                    Timer timerDobleAtaque = new Timer(2000, ev -> turnoEnemigo());
                    timerDobleAtaque.setRepeats(false);
                    timerDobleAtaque.start();
                } else {
                    heroe.setDefendiendo(false); 
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