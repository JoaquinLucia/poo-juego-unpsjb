package controlador;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import controlador.utilidades.GestorDeSonido;
import modelo.Batalla;
import modelo.Dificultad;
import modelo.Enemigo;
import modelo.Habilidad;
import modelo.Heroe;
import modelo.Lugar;
import modelo.Partida;
import modelo.Puntaje;
import modelo.PuntajeDAO;
import modelo.SeleccionHeroe;
import modelo.catalogos.CatalogoEnemigos;
import modelo.catalogos.CatalogoHabilidades;
import persistencia.ConexionBD;
import vista.VentanaPrincipal;
import vista.VistaBatalla;
import vista.VistaCaminoMazmorra;
import vista.VistaMenu;
import vista.paneles.PanelPausa;
import vista.paneles.PanelSeleccionPersonaje;

/**
 * Controlador principal: coordina el juego completo.
 *   Menú → Selección → Camino → Batalla → Camino → Batalla → Camino → Batalla
 *         → Sala del jefe → Pelea final → Puntaje → Menú
 *
 * Durante la partida, Esc abre el menú de pausa.
 *
 * Crea a los controladores de cada pantalla y se pasa a sí mismo por constructor.
 * Los controladores no se conocen entre sí: cuando pasa algo que cambia de pantalla,
 * llaman al método correspondiente del maestro.
 */
public class ControladorMaestro {

    // Nombres de las pantallas dentro de la ventana
    private final String pantallaMenu = "menu";
    private final String pantallaSeleccion = "seleccion";
    private final String pantallaCamino = "camino";
    private final String pantallaBatalla = "batalla";

    private final VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
    private final CatalogoHabilidades catalogoHabilidades = new CatalogoHabilidades();
    private final CatalogoEnemigos catalogoEnemigos = new CatalogoEnemigos(catalogoHabilidades);
    private final PuntajeDAO puntajeDAO;   // interfaz: la implementación (SQLite) la elige el main

    // ---- Controladores de cada pantalla ----
    private final VistaMenu vistaMenu = new VistaMenu();
    private final ControladorMenu controladorMenu;
    private final ControladorPausa controladorPausa;

    // Se crean de nuevo en cada partida / batalla
    private PanelSeleccionPersonaje vistaSeleccion;
    private ControladorSeleccion controladorSeleccion;
    private VistaCaminoMazmorra vistaCamino;
    private ControladorCaminoMazmorra controladorCamino;
    private VistaBatalla vistaBatalla;
    private ControladorBatalla controladorBatalla;

    private Partida partidaActual;

    public ControladorMaestro(PuntajeDAO puntajeDAO) {
        this.puntajeDAO = puntajeDAO;

        vistaMenu.aplicarEscala(ventanaPrincipal.getEscala());
        controladorMenu = new ControladorMenu(vistaMenu, puntajeDAO, this);
        ventanaPrincipal.agregarPantalla(pantallaMenu, vistaMenu);

        // Menú de pausa: se crea una sola vez y se abre con Esc mientras hay partida
        controladorPausa = new ControladorPausa(new PanelPausa(ventanaPrincipal), this);
        ventanaPrincipal.registrarTecla(KeyEvent.VK_ESCAPE, "pausa",
                () -> abrirPausa(),
                () -> partidaActual != null && !ventanaPrincipal.enTransicion());

        // Cerrar con la X también pasa por salirDelJuego(), así la conexión se cierra siempre
        ventanaPrincipal.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventanaPrincipal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salirDelJuego();
            }
        });
    }

    /** Arranca el juego mostrando el menú. */
    public void iniciar() {
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav");
        controladorMenu.mostrar();
        ventanaPrincipal.mostrarDirecto(pantallaMenu);
    }

    // ---------- Lo que pueden pedir los controladores de cada pantalla ----------

    /** ControladorMenu: botón "Comenzar". Pantalla de selección nueva cada vez. */
    public void comenzar() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        quitarPantallaSeleccion();
        vistaSeleccion = new PanelSeleccionPersonaje();
        controladorSeleccion = new ControladorSeleccion(vistaSeleccion, new SeleccionHeroe(crearHeroes()), this);
        ventanaPrincipal.agregarPantalla(pantallaSeleccion, vistaSeleccion);
        ventanaPrincipal.cambiarPantalla(pantallaSeleccion, null, null);
    }

    /** ControladorSeleccion / ControladorPausa: botón "Volver" o "Volver al título". */
    public void volverAlMenu() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        ventanaPrincipal.atenuar(false);
        partidaActual = null;
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, () -> {
            GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav");
            quitarPantallaSeleccion();
        });
    }

    /** ControladorSeleccion: botón "Elegir héroe". Empieza la partida en el primer camino. */
    public void heroeElegido() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        partidaActual = new Partida(controladorSeleccion.getHeroeElegido());

        // Pantalla del camino nueva para cada partida (así no se acumulan listeners)
        if (vistaCamino != null) {
            ventanaPrincipal.quitarPantalla(vistaCamino);
        }
        vistaCamino = new VistaCaminoMazmorra();
        controladorCamino = new ControladorCaminoMazmorra(partidaActual.getCamino(), vistaCamino, this);
        ventanaPrincipal.agregarPantalla(pantallaCamino, vistaCamino);

        // La selección se quita cuando el fundido ya terminó
        ventanaPrincipal.cambiarPantalla(pantallaCamino, null, () -> quitarPantallaSeleccion());
    }

    /** ControladorCaminoMazmorra: se eligió una flecha. */
    public void caminoElegido() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        iniciarBatalla(controladorCamino.getDificultadElegida());
    }

    /** ControladorCaminoMazmorra: botón de la sala del jefe. */
    public void enfrentarJefe() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        iniciarBatalla(Dificultad.NORMAL);
    }

    /** ControladorBatalla: alguien quedó sin vida. */
    public void batallaTerminada() {
        if (partidaActual == null) {
            return;   // la partida se abandonó desde la pausa mientras terminaba la pelea
        }
        boolean gano = controladorBatalla.heroeGano();

        if (!gano) {
            terminarPartida(false);
        } else if (partidaActual.getCamino().llegoAlJefe()) {
            partidaActual.registrarVictoriaJefe();
            terminarPartida(true);
        } else {
            partidaActual.registrarVictoria();
            controladorCamino.volverDeBatalla();   // pasa al lugar siguiente
            GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav");
            ventanaPrincipal.cambiarPantalla(pantallaCamino, null, null);
        }
    }

    /** ControladorPausa: botón "Volver al juego" o reanudar partida. */
    public void reanudarPartida() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        ventanaPrincipal.atenuar(false);
    }

    /** Alias por compatibilidad. */
    public void reanudar() {
        reanudarPartida();
    }

    /** ControladorPausa: botón "Volver al título". Se descarta la partida sin guardar puntaje. */
    public void abandonarPartida() {
        volverAlMenu();
    }

    /** ControladorPausa: botón "Salir del juego". */
    public void salirDelJuego() {
        ConexionBD.cerrar();
        System.exit(0);
    }

    // ---------- Acciones internas del maestro ----------

    private void iniciarBatalla(Dificultad dificultad) {
        Lugar lugar = partidaActual.getCamino().getLugarActual();

        Enemigo enemigo = catalogoEnemigos.crearPersonaje(lugar.getTipoEnemigo(), dificultad);

        switch (lugar.getTipoEnemigo().toLowerCase()) {
            case "esclavo":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/esclavo_musica.wav");
                break;
            case "cazador":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/cazador_musica.wav");
                break;
            case "jester":
            case "jestar":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/jester_musica.wav");
                break;
            case "akuma":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/akuma_musica.wav");
                break;
            default:
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav");
                break;
        }

        if (vistaBatalla != null) {
            ventanaPrincipal.quitarPantalla(vistaBatalla);
        }
        vistaBatalla = new VistaBatalla();
        controladorBatalla = new ControladorBatalla(vistaBatalla, new Batalla(partidaActual.getHeroe(), enemigo, lugar), this);
        ventanaPrincipal.agregarPantalla(pantallaBatalla, vistaBatalla);

        ventanaPrincipal.cambiarPantalla(pantallaBatalla, null, null);
        controladorBatalla.iniciarBatalla();
    }

    private void abrirPausa() {
        ventanaPrincipal.atenuar(true);
        controladorPausa.mostrar();
    }

    // ---------- Auxiliares ----------

    private void terminarPartida(boolean derrotoAlJefe) {
        int puntos = partidaActual.calcularPuntajeFinal();
        String nombreJugador = vistaBatalla.pedirNombreFinal(derrotoAlJefe, puntos);
        if (nombreJugador != null && !nombreJugador.isBlank()) {
            puntajeDAO.guardar(new Puntaje(nombreJugador, partidaActual.getHeroe().getNombre(), puntos));
        }
        partidaActual = null;
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav");
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, null);
    }

    private void quitarPantallaSeleccion() {
        if (vistaSeleccion != null) {
            ventanaPrincipal.quitarPantalla(vistaSeleccion);
            vistaSeleccion = null;
            controladorSeleccion = null;
        }
    }

    private List<Heroe> crearHeroes() {
        List<Heroe> heroes = new ArrayList<>();

        List<Habilidad> habilidadesArtorias = new ArrayList<>();
        habilidadesArtorias.add(catalogoHabilidades.crearHabilidad("golpe_aturdidor"));
        heroes.add(new Heroe("Artorias", 600, 15, 3, habilidadesArtorias,
        "/recursos/personajes/caballero.png", "/recursos/artoriasFrontal.jpg", "Caballero de Gwyn", 0.5));

        List<Habilidad> habilidadesMerlin = new ArrayList<>();
        habilidadesMerlin.add(catalogoHabilidades.crearHabilidad("dardo_venenoso"));
        heroes.add(new Heroe("Merlin", 450, 25, 2, habilidadesMerlin,
        "/recursos/personajes/mago.png", "/recursos/magoFrontal.jpg", "Mago de Gwyn", 0.3));

        return heroes;
    }
}