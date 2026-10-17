package controlador;

import modelo.Batalla;
import modelo.Camino;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.Nivel;
import modelo.CatalogoPersonajes;
import modelo.Arquetipo;
import modelo.Escenario;
import modelo.Habilidad;
import vista.VentanaPrincipal;
import vista.VistaBatalla;
import vista.VistaMenu;
import vista.VistaSeleccion;
import vista.VistaMazmorra;
import vista.DialogoRecompensa; // <--- Importamos nuestro nuevo diálogo personalizado

// Importaciones necesarias para manejar el audio
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class ControladorMaestro {
    private final VentanaPrincipal ventana;
    private final CatalogoPersonajes catalogo;
    private Nivel nivel;
    private Heroe heroe;
    
    private ControladorMenu ctrlMenu;
    private ControladorSeleccion ctrlSeleccion;
    private ControladorMazmorra ctrlMazmorra;
    private ControladorBatalla ctrlBatalla;

    // Reproductor para la música continua de menús / exploración
    private Clip clipMenuMusica;

    private static final String MENU = "MENU";
    private static final String SELECCION = "SELECCION";
    private static final String MAZMORRA = "MAZMORRA";
    private static final String BATALLA = "BATALLA";

    public ControladorMaestro() {
        this.ventana = new VentanaPrincipal();
        this.catalogo = new CatalogoPersonajes();
        
        VistaMenu vistaMenu = new VistaMenu();
        vistaMenu.aplicarEscala(ventana.getEscala());

        VistaSeleccion vistaSeleccion = new VistaSeleccion();
        VistaMazmorra vistaMazmorra = new VistaMazmorra(); // Instancia de la vista

        this.ctrlMenu = new ControladorMenu(vistaMenu, this);
        this.ctrlSeleccion = new ControladorSeleccion(vistaSeleccion, this, catalogo);
        this.ctrlMazmorra = new ControladorMazmorra(vistaMazmorra, this);

        ventana.agregarPantalla(MENU, vistaMenu);
        ventana.agregarPantalla(SELECCION, vistaSeleccion);
        ventana.agregarPantalla(MAZMORRA, vistaMazmorra); 
    }

    public void iniciar() {
        reproducirMusicaMenu(); 
        ventana.mostrarDirecto(MENU);
    }

    // --- Métodos de gestión de la música continua del menú/exploración ---
    private void reproducirMusicaMenu() {
        try {
            if (clipMenuMusica != null && clipMenuMusica.isRunning()) {
                return; 
            }
            URL url = getClass().getResource("/recursos/sonidos/juego_musica.wav");
            if (url != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                clipMenuMusica = AudioSystem.getClip();
                clipMenuMusica.open(audioStream);
                clipMenuMusica.start();
                clipMenuMusica.loop(Clip.LOOP_CONTINUOUSLY); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detenerMusicaMenu() {
        if (clipMenuMusica != null && clipMenuMusica.isRunning()) {
            clipMenuMusica.stop();
            clipMenuMusica.close();
        }
    }

    public void iniciarJuegoDesdeMenu() {
        ctrlSeleccion.reiniciar();
        ventana.cambiarPantalla(SELECCION, () -> {}, null);
    }

    private void irAExploracion(String mensajePrevio) {
        String encabezado = nivel.getNombre() + " - Sala " + nivel.getNumeroSala() + 
                " de " + nivel.getCantidadSalas() + 
                " | " + heroe.getNombre() + ": " + heroe.getVida() + " de vida";
        String mensaje = mensajePrevio.isEmpty() ? encabezado : encabezado + "\n" + mensajePrevio;

        reproducirMusicaMenu(); 

        ventana.cambiarPantalla(MAZMORRA, () -> {
            cerrarBatalla();
            ctrlMazmorra.mostrar(nivel.getSalaActual().getEscenario(), mensaje);
        }, null);
    }

    private void irABatalla() {
        detenerMusicaMenu(); 

        ventana.cambiarPantalla(BATALLA, () -> {
            cerrarBatalla();
            
            Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
            Batalla batalla = new Batalla(heroe, enemigo);
            
            heroe.reiniciarTodasLasHabilidades();
            
            VistaBatalla vistaBatalla = new VistaBatalla();
            
            String rutaHeroeImg = "/recursos/batalla/caballero_icono.png"; 
            if (heroe.getNombre().equalsIgnoreCase("Mago")) {
                rutaHeroeImg = "/recursos/batalla/mago_icono.png"; 
            } else if (heroe.getNombre().equalsIgnoreCase("Arquero")) {
                rutaHeroeImg = "/recursos/batalla/arquero_icono.png"; 
            }
            
            String rutaEnemigoImg = enemigo.getRutaImagen();
            if (rutaEnemigoImg == null || rutaEnemigoImg.isEmpty()) {
                rutaEnemigoImg = "/recursos/batalla/bruto_icon.png"; 
            }

            vistaBatalla.actualizarRetratos(rutaHeroeImg, rutaEnemigoImg);
            
            ctrlBatalla = new ControladorBatalla(vistaBatalla, batalla, this);
            ventana.agregarPantalla(BATALLA, ctrlBatalla.getVista());
            ctrlBatalla.iniciar();
        }, null);
    }

    private void cerrarBatalla() {
        if (ctrlBatalla != null) {
            ctrlBatalla.cerrar();
            ventana.quitarPantalla(ctrlBatalla.getVista());
            ctrlBatalla = null;
        }
    }

    public void personajeSeleccionado(Arquetipo arquetipoElegido) {
        this.heroe = catalogo.crearHeroe(arquetipoElegido);
        
        Escenario escenarioBosque = Escenario.salaNormal(
            "bosque", 
            "/recursos/mazmorra/bosque.gif", 
            "Te encuentras en los senderos oscuros del bosque.", 
            "Ir por la senda izquierda.", 
            "Seguir al frente entre los árboles.", 
            "Tomar el sendero derecho."
        );
        
        Escenario escenarioCatacumbas = Escenario.salaNormal(
            "mazmorraa", 
            "/recursos/mazmorra/mazmorra.gif", 
            "El aire es pesado en las catacumbas.", 
            "Bajar por el pasillo izquierdo.", 
            "Continuar por el corredor principal.", 
            "Explorar la cripta derecha."
        );
        
        Escenario escenarioCueva = Escenario.salaNormal(
            "cueva", 
            "/recursos/mazmorra/cueva.gif", 
            "Te adentras en una profunda cueva rocosa.", 
            "Tomar la grieta angosta.", 
            "Avanzar por el centro.", 
            "Subir por las rocas."
        );

        Escenario escenarioGuardian = Escenario.salaDelJefe(
            "jefe", 
            "/recursos/mazmorra/jefe_puerta.gif", 
            "Te aproximas a un gran portal resguardado por una presencia imponente.", 
            "Enfrentar al Guardian."
        );

        this.nivel = new Nivel.Builder("Senderos de las Cenizas")
                .sala(escenarioBosque, "bruto")    
                .sala(escenarioCatacumbas, "vibora") 
                .sala(escenarioCueva, "viuda")      
                .jefe(escenarioGuardian, "guardian") 
                .build();
                
        irAExploracion("");
    }

    public void avanzarSala() {
        if (nivel.haySiguiente()) {
            nivel.avanzar();
            
            if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
                irABatalla();
            } else {
                irAExploracion("Avanzas a una nueva zona de la mazmorra.");
            }
        } else {
            reproducirMusicaMenu();
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }

    public void caminoElegido(Camino camino) {
        if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
            irABatalla();
        } else {
            avanzarSala();
        }
    }

    public void batallaTerminada(boolean ganoHeroe) {
        if (ganoHeroe) {
            String idEnemigo = nivel.getSalaActual().getIdEnemigo();
            
            if (idEnemigo != null && !idEnemigo.isEmpty()) {
                Enemigo enemigoDerrotado = catalogo.crearEnemigo(idEnemigo);
                
                if (enemigoDerrotado != null && !enemigoDerrotado.getHabilidades().isEmpty()) {
                    Habilidad habilidadRobada = enemigoDerrotado.getHabilidades().get(0);
                    
                    int cantidadActual = heroe.getCantidadHabilidades();
                    final Object[] opciones;
                    
                    if (cantidadActual == 1) {
                        opciones = new Object[] {
                            "Reemplazar 1 (" + heroe.getHabilidad(0).getNombreHabilidad() + ")",
                            "Agregar Habilidad",
                            "Descartar"
                        };
                    } else if (cantidadActual >= 2) {
                        opciones = new Object[] {
                            "Reemplazar 1 (" + heroe.getHabilidad(0).getNombreHabilidad() + ")",
                            "Reemplazar 2 (" + heroe.getHabilidad(1).getNombreHabilidad() + ")",
                            "Descartar"
                        };
                    } else {
                        opciones = new Object[] {
                            "Agregar Habilidad",
                            "Descartar"
                        };
                    }
                    
                    final Enemigo enemigoFin = enemigoDerrotado;
                    final Habilidad habRobada = habilidadRobada;
                    final int cantAct = cantidadActual;

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Usamos nuestro diálogo personalizado en lugar de JOptionPane
                        DialogoRecompensa dialogo = new DialogoRecompensa(
                            ventana, 
                            enemigoFin.getNombre(), 
                            habRobada, 
                            opciones
                        );
                        
                        int seleccion = dialogo.mostrarDialogo();
                        
                        String mensajeRes = "¡Has derrotado al enemigo!";

                        if (cantAct == 1) {
                            if (seleccion == 0) {
                                heroe.reemplazarHabilidad(0, habRobada);
                                mensajeRes = "¡Reemplazaste la habilidad 1 por " + habRobada.getNombreHabilidad() + "!";
                            } else if (seleccion == 1) {
                                heroe.agregarHabilidad(habRobada);
                                mensajeRes = "¡Has agregado la habilidad " + habRobada.getNombreHabilidad() + "!";
                            } else {
                                mensajeRes = "¡Has descartado la habilidad nueva!";
                            }
                        } else if (cantAct >= 2) {
                            if (seleccion == 0) {
                                heroe.reemplazarHabilidad(0, habRobada);
                                mensajeRes = "¡Reemplazaste la habilidad 1 por " + habRobada.getNombreHabilidad() + "!";
                            } else if (seleccion == 1) {
                                heroe.reemplazarHabilidad(1, habRobada);
                                mensajeRes = "¡Reemplazaste la habilidad 2 por " + habRobada.getNombreHabilidad() + "!";
                            } else {
                                mensajeRes = "¡Has descartado la habilidad nueva!";
                            }
                        } else {
                            if (seleccion == 0) {
                                heroe.agregarHabilidad(habRobada);
                                mensajeRes = "¡Has agregado la habilidad " + habRobada.getNombreHabilidad() + "!";
                            } else {
                                mensajeRes = "¡Has descartado la habilidad nueva!";
                            }
                        }

                        if (nivel.haySiguiente()) {
                            nivel.avanzar(); 
                            irAExploracion(mensajeRes + " Elige tu siguiente camino.");
                        } else {
                            reproducirMusicaMenu();
                            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
                            ventana.cambiarPantalla(MENU, () -> {}, null);
                        }
                    });
                    return;
                }
            }
            
            if (nivel.haySiguiente()) {
                nivel.avanzar(); 
                irAExploracion("¡Has derrotado al enemigo! Elige tu siguiente camino.");
            } else {
                reproducirMusicaMenu();
                ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
                ventana.cambiarPantalla(MENU, () -> {}, null);
            }
        } else {
            reproducirMusicaMenu();
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }
}