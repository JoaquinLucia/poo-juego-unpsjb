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
 *        → Sala del jefe → Pelea final → Puntaje → Menú
 *
 * Durante la partida, Esc abre el menú de pausa.
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

    private final VentanaPrincipal ventana = new VentanaPrincipal();
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

    private Partida partida;

    public ControladorMaestro(PuntajeDAO puntajeDAO) {
        this.puntajeDAO = puntajeDAO;

        vistaMenu.aplicarEscala(ventana.getEscala());
        controladorMenu = new ControladorMenu(vistaMenu, puntajeDAO, this);
        ventana.agregarPantalla(pantallaMenu, vistaMenu);

        // Menú de pausa: se crea una sola vez y se abre con Esc mientras hay partida
        controladorPausa = new ControladorPausa(new PanelPausa(ventana), this);
        ventana.registrarTecla(KeyEvent.VK_ESCAPE, "pausa",
                () -> abrirPausa(),
                () -> partida != null && !ventana.enTransicion());

        // Cerrar con la X también pasa por salirDelJuego(), así la conexión se cierra siempre
        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salirDelJuego();
            }
        });
    }

    /** Arranca el juego mostrando el menú. */
    public void iniciar() {
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
        controladorMenu.mostrar();
        ventana.mostrarDirecto(pantallaMenu);
    }

    // ---------- Lo que pueden pedir los controladores de cada pantalla ----------

    /** ControladorMenu: botón "Comenzar". Pantalla de selección nueva cada vez. */
    public void comenzar() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton comenzar
        quitarSeleccion();
        vistaSeleccion = new PanelSeleccionPersonaje();
        controladorSeleccion = new ControladorSeleccion(vistaSeleccion, new SeleccionHeroe(crearHeroes()), this);
        ventana.agregarPantalla(pantallaSeleccion, vistaSeleccion);
        ventana.cambiarPantalla(pantallaSeleccion, null, null);
    }

    /** ControladorSeleccion: botón "Volver". */
    public void volverAlMenu() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton volver
        ventana.cambiarPantalla(pantallaMenu, null, () -> {
            GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
            quitarSeleccion();
        });
    }

    /** ControladorSeleccion: botón "Elegir héroe". Empieza la partida en el primer camino. */
    public void heroeElegido() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton heroe elegido
        partida = new Partida(controladorSeleccion.getHeroeElegido());

        // Pantalla del camino nueva para cada partida (así no se acumulan listeners)
        if (vistaCamino != null) {
            ventana.quitarPantalla(vistaCamino);
        }
        vistaCamino = new VistaCaminoMazmorra();
        controladorCamino = new ControladorCaminoMazmorra(partida.getCamino(), vistaCamino, this);
        ventana.agregarPantalla(pantallaCamino, vistaCamino);

        // La selección se quita cuando el fundido ya terminó (si se quita antes, se ve el salto)
        ventana.cambiarPantalla(pantallaCamino, null, () -> quitarSeleccion());
    }

    /** ControladorCaminoMazmorra: se eligió una flecha. */
    public void caminoElegido() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton camino elegido
        iniciarBatalla(controladorCamino.getDificultadElegida());
    }

    /** ControladorCaminoMazmorra: botón de la sala del jefe. */
    public void enfrentarJefe() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton enfrentar jefe
        iniciarBatalla(Dificultad.NORMAL);
    }

    /** ControladorBatalla: alguien quedó sin vida. */
    public void batallaTerminada() {
        if (partida == null) {
            return;   // la partida se abandonó desde la pausa mientras terminaba la pelea
        }
        boolean gano = controladorBatalla.gano();

        if (!gano) {
            terminarPartida(false);
        } else if (partida.getCamino().llegoAlJefe()) {
            partida.registrarVictoriaJefe();
            terminarPartida(true);
        } else {
            partida.registrarVictoria();
            controladorCamino.volverDeBatalla();   // pasa al lugar siguiente
            GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
            ventana.cambiarPantalla(pantallaCamino, null, null);
        }
    }

    /** ControladorPausa: botón "Volver al juego". */
    public void reanudar() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton reanudar
        ventana.atenuar(false);
    }

    /** ControladorPausa: botón "Volver al título". Se descarta la partida sin guardar puntaje. */
    public void abandonarPartida() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // sonido boton abandonar partida
        ventana.atenuar(false);
        partida = null;
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
        ventana.cambiarPantalla(pantallaMenu, null, null);
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
        Lugar lugar = partida.getCamino().getLugarActual();

        Enemigo enemigo = catalogoEnemigos.crearPersonaje(lugar.getTipoEnemigo(), dificultad);

        // sonido musica de enemigo
        switch (lugar.getTipoEnemigo().toLowerCase()) {
            case "esclavo":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/esclavo_musica.wav"); // sonido enemigo esclavo
                break;
            case "cazador":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/cazador_musica.wav"); // sonido enemigo cazador
                break;
            case "jester":
            case "jestar":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/jester_musica.wav"); // sonido enemigo jester
                break;
            case "akuma":
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/akuma_musica.wav"); // sonido jefe akuma
                break;
            default:
                GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
                break;
        }

        // Pantalla de batalla nueva para cada pelea
        if (vistaBatalla != null) {
            ventana.quitarPantalla(vistaBatalla);
        }
        vistaBatalla = new VistaBatalla();
        controladorBatalla = new ControladorBatalla(vistaBatalla, new Batalla(partida.getHeroe(), enemigo, lugar), this);
        ventana.agregarPantalla(pantallaBatalla, vistaBatalla);

        ventana.cambiarPantalla(pantallaBatalla, null, null);
        controladorBatalla.iniciar();
    }

    /** Esc durante la partida: oscurece el juego y abre el menú de pausa. */
    private void abrirPausa() {
        ventana.atenuar(true);
        controladorPausa.mostrar();
    }

    // ---------- Auxiliares ----------

    /** Pide el nombre, guarda el puntaje y vuelve al menú. */
    private void terminarPartida(boolean derrotoAlJefe) {
        int puntos = partida.calcularPuntajeFinal();
        String nombre = vistaBatalla.pedirNombreFinal(derrotoAlJefe, puntos);   // el texto lo arma la vista
        if (nombre != null && !nombre.isBlank()) {
            puntajeDAO.guardar(new Puntaje(nombre, partida.getHeroe().getNombre(), puntos));
        }
        partida = null;
        GestorDeSonido.getInstancia().reproducirMusicaEnBucle("recursos/sonidos/juego_musica.wav"); // sonido musica general juego
        ventana.cambiarPantalla(pantallaMenu, null, null);
    }

    /** Saca la pantalla de selección de la ventana (ya no se necesita). */
    private void quitarSeleccion() {
        if (vistaSeleccion != null) {
            ventana.quitarPantalla(vistaSeleccion);
            vistaSeleccion = null;
            controladorSeleccion = null;
        }
    }

    /** Los héroes disponibles. Cada vez se crean nuevos, con vida llena y habilidades sin cooldown. */
    private List<Heroe> crearHeroes() {
        List<Heroe> heroes = new ArrayList<>();

        List<Habilidad> habilidadesArtorias = new ArrayList<>();
        habilidadesArtorias.add(catalogoHabilidades.crearHabilidad("golpe_aturdidor"));
        heroes.add(new Heroe("Artorias", 600, 15, 3, habilidadesArtorias,
           "/recursos/personajes/caballero.png", "/recursos/artoriasFrontal.jpg","Caballero de gwyn"));

        List<Habilidad> habilidadesMerlin = new ArrayList<>();
        habilidadesMerlin.add(catalogoHabilidades.crearHabilidad("dardo_venenoso"));
        heroes.add(new Heroe("Merlin", 450, 25, 2, habilidadesMerlin,
           "/recursos/personajes/mago.png", "/recursos/magoFrontal.jpg","Mago de Gwyn"));

        return heroes;
    }
}