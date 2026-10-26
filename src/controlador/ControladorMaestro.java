package controlador;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
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
 *        → Sala del jefe → Pelea final → Puntaje → Menú
 *
 * Durante la partidaActual, Esc abre el menú de pausa.
 *
 * Crea a los controladores de cada pantalla y se pasa a sí mismo por constructor.
 * Los controladores no se conocen entre sí: cuando pasa algo que cambia de pantalla,
 * llaman al método correspondiente del maestro (comenzar(), heroeElegido(), ...).
 */
public class ControladorMaestro {

    // Nombres de las pantallas dentro de la ventana
    private final String pantallaMenu = "menu";
    private final String pantallaSeleccion = "seleccion";
    private final String pantallaCamino = "camino";
    private final String pantallaBatalla = "batalla";

    private final VentanaPrincipal ventanaPrincipal  = new VentanaPrincipal();
    private final CatalogoHabilidades catalogoHabilidades = new CatalogoHabilidades();
    private final CatalogoEnemigos catalogoEnemigos = new CatalogoEnemigos(catalogoHabilidades);
    private final PuntajeDAO puntajeDAO;   // interfaz: la implementación (SQLite) la elige el main

    // ---- Controladores de cada pantalla ----
    private final VistaMenu vistaMenu = new VistaMenu();
    private final ControladorMenu controladorMenu;
    private final ControladorPausa controladorPausa;

    // Se crean de nuevo en cada partidaActual / batalla
    private PanelSeleccionPersonaje vistaSeleccion;
    private ControladorSeleccion controladorSeleccion;
    private VistaCaminoMazmorra vistaCamino;
    private ControladorCaminoMazmorra controladorCamino;
    private VistaBatalla vistaBatalla;
    private ControladorBatalla controladorBatalla;

    private Partida partidaActual;

    public ControladorMaestro(PuntajeDAO puntajeDAO) {
        this.puntajeDAO = puntajeDAO;

        vistaMenu.aplicarEscala(ventanaPrincipal .getEscala());
        controladorMenu = new ControladorMenu(vistaMenu, puntajeDAO, this);
        ventanaPrincipal .agregarPantalla(pantallaMenu, vistaMenu);

        // Menú de pausa: se crea una sola vez y se abre con Esc mientras hay partidaActual
        controladorPausa = new ControladorPausa(new PanelPausa(ventanaPrincipal ), this);
        ventanaPrincipal .registrarTecla(KeyEvent.VK_ESCAPE, "pausa",
                () -> abrirPausa(),
                () -> partidaActual != null && !ventanaPrincipal .enTransicion());

        // Cerrar con la X también pasa por salirDelJuego(), así la conexión se cierra siempre
        ventanaPrincipal .setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventanaPrincipal .addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salirDelJuego();
            }
        });
    }

    /** Arranca el juego mostrando el menú. */
    public void iniciarJuego() {
        controladorMenu.mostrar();
        ventanaPrincipal .mostrarDirecto(pantallaMenu);
    }

    // ---------- Lo que pueden pedir los controladores de cada pantalla ----------

    /** ControladorMenu: botón "Comenzar". Pantalla de selección nueva cada vez. */
    public void comenzar() {
        quitarPantallaSeleccion();
        vistaSeleccion = new PanelSeleccionPersonaje();
        controladorSeleccion = new ControladorSeleccion(vistaSeleccion, new SeleccionHeroe(crearHeroes()), this);
        ventanaPrincipal .agregarPantalla(pantallaSeleccion, vistaSeleccion);
        ventanaPrincipal .cambiarPantalla(pantallaSeleccion, null, null);
    }

    /** ControladorSeleccion: botón "Volver". */
    public void volverAlMenu() {
        ventanaPrincipal .cambiarPantalla(pantallaMenu, null, () -> quitarPantallaSeleccion());
    }

    /** ControladorSeleccion: botón "Elegir héroe". Empieza la partidaActual en el primer camino. */
    public void heroeElegido() {
        partidaActual = new Partida(controladorSeleccion.getHeroeElegido());

        // Pantalla del camino nueva para cada partidaActual (así no se acumulan listeners)
        if (vistaCamino != null) {
            ventanaPrincipal .quitarPantalla(vistaCamino);
        }
        vistaCamino = new VistaCaminoMazmorra();
        controladorCamino = new ControladorCaminoMazmorra(partidaActual.getCamino(), vistaCamino, this);
        ventanaPrincipal.agregarPantalla(pantallaCamino, vistaCamino);

        // La selección se quita cuando el fundido ya terminó (si se quita antes, se ve el salto)
        ventanaPrincipal.cambiarPantalla(pantallaCamino, null, () -> quitarPantallaSeleccion());
    }

    /** ControladorCaminoMazmorra: se eligió una flecha. */
    public void caminoElegido() {
        iniciarBatalla(controladorCamino.getDificultadElegida());
    }

    /** ControladorCaminoMazmorra: botón de la sala del jefe. */
    public void enfrentarJefe() {
        iniciarBatalla(Dificultad.NORMAL);
    }

    /** ControladorBatalla: alguien quedó sin vida. */
    public void batallaTerminada() {
        if (partidaActual == null) {
            return;   // la partidaActual se abandonó desde la pausa mientras terminaba la pelea
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
            ventanaPrincipal.cambiarPantalla(pantallaCamino, null, null);
        }
    }

    /** ControladorPausa: botón "Volver al juego". */
    public void reanudarPartida() {
        ventanaPrincipal.atenuar(false);
    }

    /** ControladorPausa: botón "Volver al título". Se descarta la partidaActual sin guardar puntaje. */
    public void abandonarPartida() {
        ventanaPrincipal.atenuar(false);
        partidaActual = null;
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, null);
    }

    /** ControladorPausa: botón "Salir del juego". */
    public void salirDelJuego() {
        ConexionBD.cerrar();
        System.exit(0);
    }

    // ---------- Acciones internas del maestro ----------

    /**
     * Batalla contra el enemigo del lugar actual (en la sala del jefe, el jefe).
     * El catálogo crea el enemigo con sus estadísticas ya ajustadas a la dificultad.
     */
    private void iniciarBatalla(Dificultad dificultad) {
        Lugar lugar = partidaActual.getCamino().getLugarActual();

        Enemigo enemigo = catalogoEnemigos.crearPersonaje(lugar.getTipoEnemigo(), dificultad);

        // Pantalla de batalla nueva para cada pelea
        if (vistaBatalla != null) {
            ventanaPrincipal.quitarPantalla(vistaBatalla);
        }
        vistaBatalla = new VistaBatalla();
        controladorBatalla = new ControladorBatalla(vistaBatalla, new Batalla(partidaActual.getHeroe(), enemigo, lugar), this);
        ventanaPrincipal.agregarPantalla(pantallaBatalla, vistaBatalla);

        ventanaPrincipal.cambiarPantalla(pantallaBatalla, null, null);
        controladorBatalla.iniciarBatalla();
    }

    /** Esc durante la partidaActual: oscurece el juego y abre el menú de pausa. */
    private void abrirPausa() {
        ventanaPrincipal.atenuar(true);
        controladorPausa.mostrar();
    }

    // ---------- Auxiliares ----------

    /** Pide el nombreJugador, guarda el puntaje y vuelve al menú. */
    private void terminarPartida(boolean derrotoAlJefe) {
        int puntos = partidaActual.calcularPuntajeFinal();
        String nombreJugador = vistaBatalla.pedirNombreFinal(derrotoAlJefe, puntos);   // el texto lo arma la vista
        if (nombreJugador != null && !nombreJugador.isBlank()) {
            puntajeDAO.guardar(new Puntaje(nombreJugador, partidaActual.getHeroe().getNombre(), puntos));
        }
        partidaActual = null;
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, null);
    }

    /** Saca la pantalla de selección de la ventana (ya no se necesita). */
    private void quitarPantallaSeleccion() {
        if (vistaSeleccion != null) {
            ventanaPrincipal.quitarPantalla(vistaSeleccion);
            vistaSeleccion = null;
            controladorSeleccion = null;
        }
    }

    /** Los héroes disponibles. Cada vez se crean nuevos, con vida llena y habilidades sin cooldown. */
    private List<Heroe> crearHeroes() {
        List<Heroe> heroes = new ArrayList<>();

        List<Habilidad> habilidadesDeArtorias = new ArrayList<>();
        habilidadesDeArtorias.add(catalogoHabilidades.crearHabilidad("golpe_aturdidor"));
        heroes.add(new Heroe("Artorias", 600, 15, 3, habilidadesDeArtorias,
                "/recursos/personajes/caballero.png", "/recursos/artoriasFrontal.jpg","Caballero de gwyn"));

        List<Habilidad> habilidadesDeMerlin = new ArrayList<>();
        habilidadesDeMerlin.add(catalogoHabilidades.crearHabilidad("dardo_venenoso"));
        heroes.add(new Heroe("Merlin", 450, 25, 2, habilidadesDeMerlin,
                "/recursos/personajes/mago.png", "/recursos/magoFrontal.jpg","Mago de Gwyn"));

        return heroes;
    }
}