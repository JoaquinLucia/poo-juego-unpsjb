package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import modelo.Batalla;
import modelo.Entidad;
import modelo.Habilidad;
import vista.AnimacionSprite;
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
        actualizarVista();
        
        // Creamos el timer de pausa para leer el resultado y mantener la música viva durante este lapso
        Timer pausa = new Timer(PAUSA_FIN_BATALLA_MS, e -> {
            if (rutaMusicaActual != null) {
                GestorDeSonido.getInstancia().detenerAudio(rutaMusicaActual);
            }
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
        
        // Seleccionamos dinámicamente el sprite de ataque según el héroe actual
        String nombreHeroe = batalla.getHeroe().getNombre().toLowerCase();
        String rutaAtaque = "/recursos/sprites/arquero_ataque.png"; // por defecto
        
        if (nombreHeroe.contains("caballero")) {
            rutaAtaque = "/recursos/sprites/caballero_ataque.png";
        } else if (nombreHeroe.contains("mago")) {
            rutaAtaque = "/recursos/sprites/mago_ataque.png";
        }

        Image[] framesAtaque = vista.recortarSpriteSheet(rutaAtaque, 4);
        AnimacionSprite animAtaque = new AnimacionSprite(framesAtaque, 120, null);
        vista.reproducirAnimacion(animAtaque);

        boolean eraInmune = batalla.getEnemigo().isInmune();
        batalla.getHeroe().atacar(batalla.getEnemigo());
        
        if (eraInmune) {
            batalla.getEnemigo().setInmune(false);
            vista.mostrarResultado("¡El ataque chocó contra la defensa impenetrable de " + batalla.getEnemigo().getNombre() + "!");
        } else {
            vista.mostrarResultado(batalla.getHeroe().getNombre() + " asestó un golpe básico!");
        }
        
        vista.efectoTemblor(); 
        finalizarTurnoJugador();
    }

    public void accionJugador_Defender() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        Entidad heroe = batalla.getHeroe();
        heroe.setDefendiendo(true);
        vista.setBotonesHabilitados(false);
        actualizarVista();
        vista.mostrarResultado(heroe.getNombre() + " adopta una postura defensiva.");
        
        Timer timer = new Timer(1500, ev -> turnoEnemigo());
        timer.setRepeats(false);
        timer.start();
    }

    private void accionJugador_Habilidad() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        Entidad heroe = batalla.getHeroe();
        
        if (heroe.getCantidadHabilidades() <= 0) {
            vista.mostrarResultado("El héroe no tiene habilidades.");
            return;
        }
        
        if (heroe.getCantidadHabilidades() == 1) {
            ejecutarHabilidad(0);
        } else {
            JPopupMenu menuHabilidades = new JPopupMenu();
            menuHabilidades.setBackground(new Color(40, 30, 20));
            menuHabilidades.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 90), 2));
            
            // Obtenemos el botón de habilidad para medir su ancho exacto
            JButton botonHab = vista.getBotonHabilidad();
            int anchoBoton = botonHab != null ? botonHab.getWidth() : 200;
            
            for (int i = 0; i < heroe.getCantidadHabilidades(); i++) {
                final int index = i;
                Habilidad hab = heroe.getHabilidad(i);
                
                String estadoCd = hab.getCdListo() ? " [Lista]" : " (CD: " + hab.getCooldownActual() + ")";
                JMenuItem item = new JMenuItem("✦ " + hab.getNombreHabilidad() + estadoCd);
                
                item.setBackground(new Color(50, 35, 20));
                item.setForeground(new Color(245, 222, 179));
                item.setFont(new Font("SansSerif", Font.BOLD, 13));
                item.setOpaque(true);
                item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Forzamos a que cada opción tenga el ancho del botón inferior
                item.setPreferredSize(new java.awt.Dimension(anchoBoton, item.getPreferredSize().height));
                
                item.addActionListener(e -> ejecutarHabilidad(index));
                menuHabilidades.add(item);
            }
            
            menuHabilidades.show(botonHab, 0, -menuHabilidades.getPreferredSize().height);
        }
    }

    private void ejecutarHabilidad(int indice) {
        Habilidad hab = batalla.getHeroe().getHabilidad(indice);
        
        if (hab.getCdListo()) {
            boolean eraInmune = batalla.getEnemigo().isInmune();
            
            hab.ejecutarHabilidad(batalla.getHeroe(), batalla.getEnemigo());
            
            if (eraInmune) {
                batalla.getEnemigo().setInmune(false);
                vista.mostrarResultado("¡La habilidad chocó contra la defensa impenetrable de " + batalla.getEnemigo().getNombre() + "!");
            } else {
                vista.mostrarResultado("¡" + batalla.getHeroe().getNombre() + " usó " + hab.getNombreHabilidad() + "!");
            }
            
            vista.efectoTemblor(); 
            finalizarTurnoJugador();
        } else {
            vista.mostrarResultado("Habilidad en enfriamiento (" + hab.getCooldownActual() + " turnos)");
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
                actualizarVista();
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
        if (!batalla.getEnemigo().estaVivo()) {
            terminarBatalla(true);
            return;
        }

        Entidad enemigo = batalla.getEnemigo();
        Entidad heroe = batalla.getHeroe();

        if (enemigo.getaturdir()) {
            vista.mostrarResultado(enemigo.getNombre() + " está aturdido y pierde su turno.");
            enemigo.sacarAturdimiento();
            reducirCooldowns(enemigo);
            actualizarVista();
            
            Timer timerAturdido = new Timer(2000, e -> {
                actualizarVista();
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
            actualizarVista();
            if (!heroe.estaVivo()) {
                vista.mostrarResultado("¡Has sido derrotado por " + enemigo.getNombre() + "!");
                terminarBatalla(false);
            } else {
                if (heroe.getaturdir()) {
                    vista.mostrarResultado("¡Estás aturdido! Pierdes tu turno.");
                    heroe.sacarAturdimiento();
                    reducirCooldowns(heroe);
                    actualizarVista();
                    
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