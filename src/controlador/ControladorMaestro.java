package controlador;

import controlador.utilidades.GestorDeSonido;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;   
import java.util.HashMap;
import java.util.List;       
import java.util.Map;
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
import recursos.rutas.RutasRecursos;
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
 *
 * Dependencias externas (persistencia y sonido) se reciben por constructor:
 * el maestro no sabe qué implementación concreta usa cada una.
 */
public class ControladorMaestro {

    // Rutas de audio centralizadas en constantes.
    // Antes estaban escritas a mano en cada método.
    private static final String MUSICA_PRINCIPAL = "recursos/sonidos/juego_musica.wav";
    private static final String EFECTO_BOTON = "recursos/sonidos/sonidobotonesop.wav";
    private final RutasRecursos rutas = new RutasRecursos();
    private final Map<String, String> musicaPorEnemigo = new HashMap<>();


    // Nombres de las pantallas dentro de la ventana
    private final String pantallaMenu = "menu";
    private final String pantallaSeleccion = "seleccion";
    private final String pantallaCamino = "camino";
    private final String pantallaBatalla = "batalla";

    
    private final VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
    private final CatalogoHabilidades catalogoHabilidades = new CatalogoHabilidades();
    private final CatalogoEnemigos catalogoEnemigos = new CatalogoEnemigos(catalogoHabilidades);

    // ---- Dependencias inyectadas (las elige el main) ----
    private final PuntajeDAO puntajeDAO;
    private final GestorDeSonido sonido;   // [MODIFICADO] nuevo atributo: el gestor de sonido inyectado

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

    // El constructor ahora recibe también el GestorDeSonido (inyección de dependencia)
    public ControladorMaestro(PuntajeDAO puntajeDAO, GestorDeSonido sonido) {
        this.puntajeDAO = puntajeDAO;
        this.sonido = sonido;   // [MODIFICADO] se guarda la referencia inyectada

        musicaPorEnemigo.put("esclavo", "recursos/sonidos/esclavo_musica.wav");
        musicaPorEnemigo.put("cazador", "recursos/sonidos/cazador_musica.wav");
        musicaPorEnemigo.put("jester",  "recursos/sonidos/jester_musica.wav");
        musicaPorEnemigo.put("akuma",   "recursos/sonidos/akuma_musica.wav");

        vistaMenu.aplicarEscala(ventanaPrincipal.getEscala());
        controladorMenu = new ControladorMenu(vistaMenu, puntajeDAO, this);
        ventanaPrincipal.agregarPantalla(pantallaMenu, vistaMenu);

        // Menú de pausa: se crea una sola vez y se abre con Esc mientras hay partida
        controladorPausa = new ControladorPausa(new PanelPausa(ventanaPrincipal), this);
        ventanaPrincipal.registrarTecla(KeyEvent.VK_ESCAPE, "pausa",
                () -> abrirMenuPausa(),
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
        reproducirMusicaPrincipal();   //
        controladorMenu.mostrar();
        ventanaPrincipal.mostrarDirecto(pantallaMenu);
    }

    // ---------- Lo que pueden pedir los controladores de cada pantalla ----------

    /** ControladorMenu: botón "Comenzar". Pantalla de selección nueva cada vez. */
    public void comenzar() {
        reproducirSonidoBoton();   
        quitarPantallaSeleccion();
        vistaSeleccion = new PanelSeleccionPersonaje();
        controladorSeleccion = new ControladorSeleccion(vistaSeleccion, new SeleccionHeroe(crearHeroesDisponibles()), this);
        ventanaPrincipal.agregarPantalla(pantallaSeleccion, vistaSeleccion);
        ventanaPrincipal.cambiarPantalla(pantallaSeleccion, null, null);
    }

    /** ControladorSeleccion / ControladorPausa: botón "Volver" o "Volver al título". */
    public void volverAlMenu() {
        reproducirSonidoBoton();   
        ventanaPrincipal.atenuar(false);
        partidaActual = null;
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, () -> {
            reproducirMusicaPrincipal();   
            quitarPantallaSeleccion();
        });
    }

    /** ControladorSeleccion: botón "Elegir héroe". Empieza la partida en el primer camino. */
    public void heroeElegido() {
        reproducirSonidoBoton();   
        partidaActual = new Partida(controladorSeleccion.getHeroeElegido());

        // Pantalla del camino nueva para cada partida (así no se acumulan listeners)
        if (vistaCamino != null) {
            ventanaPrincipal.quitarPantalla(vistaCamino);
        }

        vistaCamino = new VistaCaminoMazmorra(rutas);
        controladorCamino = new ControladorCaminoMazmorra(partidaActual.getCamino(), vistaCamino, this);
        ventanaPrincipal.agregarPantalla(pantallaCamino, vistaCamino);

        // La selección se quita cuando el fundido ya terminó
        ventanaPrincipal.cambiarPantalla(pantallaCamino, null, () -> quitarPantallaSeleccion());
    }

    /** ControladorCaminoMazmorra: se eligió una flecha. */
    public void caminoElegido() {
        reproducirSonidoBoton();  
        iniciarBatalla(controladorCamino.getDificultadElegida());
    }

    /** ControladorCaminoMazmorra: botón de la sala del jefe. */
    public void enfrentarJefe() {
        reproducirSonidoBoton();  
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
            reproducirMusicaPrincipal();  
            ventanaPrincipal.cambiarPantalla(pantallaCamino, null, null);
        }
    }

    /** ControladorPausa: botón "Volver al juego" o reanudar partida. */
    public void reanudarPartida() {
        reproducirSonidoBoton();   
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

        
        // La elección de la música ahora la hace reproducirMusicaDeBatalla() usando el Map.
        reproducirMusicaDeBatalla(lugar.getTipoEnemigo());

        if (vistaBatalla != null) {
            ventanaPrincipal.quitarPantalla(vistaBatalla);
        }
        vistaBatalla = new VistaBatalla(rutas);
        controladorBatalla = new ControladorBatalla(vistaBatalla, new Batalla(partidaActual.getHeroe(), enemigo, lugar), this);
        ventanaPrincipal.agregarPantalla(pantallaBatalla, vistaBatalla);

        ventanaPrincipal.cambiarPantalla(pantallaBatalla, null, null);
        controladorBatalla.iniciarBatalla();
    }

    private void abrirMenuPausa() {
        ventanaPrincipal.atenuar(true);
        controladorPausa.mostrar();
    }

    // (solo cambio de orden, la lógica es la misma).
    private void terminarPartida(boolean derrotoAlJefe) {
        int puntos = partidaActual.calcularPuntajeFinal();
        String nombreJugador = vistaBatalla.pedirNombreFinal(derrotoAlJefe, puntos);
        if (nombreJugador != null && !nombreJugador.isBlank()) {
            puntajeDAO.guardar(new Puntaje(nombreJugador, partidaActual.getHeroe().getNombre(), puntos));
        }
        partidaActual = null;
        reproducirMusicaPrincipal();  
        ventanaPrincipal.cambiarPantalla(pantallaMenu, null, null);
    }

    // ---------- Sonido ----------
    // Sección nueva: métodos auxiliares que usan el gestor inyectado.
    // Todo el sonido del maestro pasa por acá.

    /*sonido de clic de los botones. */
    private void reproducirSonidoBoton() {
        sonido.reproducirEfecto(EFECTO_BOTON);
    }

    /* música del menú y del camino. */
    private void reproducirMusicaPrincipal() {
        sonido.reproducirMusicaEnBucle(MUSICA_PRINCIPAL);
    }

    /* elige la música según el enemigo; si no tiene una propia, suena la principal. */
    private void reproducirMusicaDeBatalla(String tipoEnemigo) {
        String ruta = musicaPorEnemigo.get(tipoEnemigo.toLowerCase());
        if (ruta == null) {
            ruta = MUSICA_PRINCIPAL;   // ese enemigo no tiene música propia
        }
        sonido.reproducirMusicaEnBucle(ruta);
    }
    // ---------- Auxiliares ----------

    private void quitarPantallaSeleccion() {
        if (vistaSeleccion != null) {
            ventanaPrincipal.quitarPantalla(vistaSeleccion);
            vistaSeleccion = null;
            controladorSeleccion = null;
        }
    }

    private List<Heroe> crearHeroesDisponibles() {
        List<Heroe> heroes = new ArrayList<>();

        List<Habilidad> habilidadesArtorias = new ArrayList<>();
        habilidadesArtorias.add(catalogoHabilidades.crearHabilidad("golpe_aturdidor"));
        heroes.add(new Heroe("Artorias", 600, 15, 3, habilidadesArtorias,
                "/recursos/personajes/caballero.png", "/recursos/artoriasFrontal.jpg", "Caballero de Gwyn", 0.5));

        List<Habilidad> habilidadesMerlin = new ArrayList<>();
        habilidadesMerlin.add(catalogoHabilidades.crearHabilidad("dardo_venenoso"));
        heroes.add(new Heroe("Merlin", 450, 25, 2, habilidadesMerlin,
                "/recursos/personajes/mago.png", "/recursos/magoFrontal.jpg", "Mago de Gwyn", 0.7));

        return heroes;
    }
}