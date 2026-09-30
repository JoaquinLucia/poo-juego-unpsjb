package controlador;

import java.awt.event.KeyEvent;
import modelo.Batalla;
import modelo.Camino;
import modelo.CatalogoPersonajes;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.ModeloPuntuacion;
import modelo.ModeloTablaPuntuacion;
import modelo.Nivel;
import modelo.RepositorioEscenarios;
import vista.VentanaPrincipal;
import vista.VistaBatalla;
import vista.VistaMazmorra;
import vista.VistaMenu;
import vista.VistaPausa;

/**
 * Controlador maestro (patrón Mediator).
 *
 * Es el único que sabe en qué orden van las pantallas:
 *   Menú → Selección → [Exploración → Batalla] x salas → Victoria / Derrota → Menú
 *
 * Los demás controladores no se conocen entre sí: cuando terminan lo suyo,
 * le avisan al maestro y él decide qué pantalla sigue.
 *
 * Todas las pantallas viven en una sola VentanaPrincipal; el maestro cambia
 * de una a otra con un fundido a negro.
 */
public class ControladorMaestro {
    private static final int PUNTOS_POR_ENEMIGO = 100;

    // Nombres de las pantallas dentro de la ventana
    private static final String MENU = "menu";
    private static final String MAZMORRA = "mazmorra";
    private static final String BATALLA = "batalla";

    // Modelos que viven toda la aplicación
    private final ModeloTablaPuntuacion tablaPuntuaciones = new ModeloTablaPuntuacion();
    private final RepositorioEscenarios repositorio = new RepositorioEscenarios();
    private final CatalogoPersonajes catalogo = new CatalogoPersonajes();

    // La única ventana del juego
    private final VentanaPrincipal ventana = new VentanaPrincipal();

    // Controladores de cada pantalla
    private final ControladorMenu ctrlMenu;
    private final ControladorMazmorra ctrlMazmorra;
    private ControladorBatalla ctrlBatalla; // se crea uno nuevo en cada pelea
    private final ControladorPausa ctrlPausa;

    // Qué pantalla se está viendo y si el juego está en pausa
    private String pantallaActual = MENU;
    private boolean enPausa = false;

    // Estado de la partida en curso
    private Heroe heroe;
    private Nivel nivel;
    private int enemigosDerrotados;

    public ControladorMaestro() {
        ctrlMenu = new ControladorMenu(new VistaMenu(), tablaPuntuaciones, catalogo, this);
        ctrlMazmorra = new ControladorMazmorra(new VistaMazmorra(), this);

        ctrlPausa = new ControladorPausa(new VistaPausa(ventana), this);

        ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
        ventana.agregarPantalla(MENU, ctrlMenu.getVista());
        ventana.agregarPantalla(MAZMORRA, ctrlMazmorra.getVista());

        // Esc abre la pausa, pero solo cuando se puede pausar (ver sePuedePausar)
        ventana.registrarTecla(KeyEvent.VK_ESCAPE, "pausa", this::pausar, this::sePuedePausar);
    }

    public void iniciar() {
        ctrlMenu.mostrar();
        ventana.mostrarDirecto(MENU);
    }

    // ---------- Navegación ----------
    // Todo lo que va dentro de "preparar" se ejecuta con la pantalla en negro,
    // así el jugador no ve cómo se arma la pantalla nueva.

    private void irAMenu(Runnable alTerminar) {
        ventana.cambiarPantalla(MENU, () -> {
            cerrarBatalla();
            ctrlMenu.mostrar();
            pantallaActual = MENU;
        }, alTerminar);
    }

    private void irAExploracion(String mensajePrevio) {
        String encabezado = nivel.getNombre() + " — Sala " + nivel.getNumeroSala()
                + " de " + nivel.getCantidadSalas()
                + "   |   " + heroe.getNombre() + ": " + heroe.getVida() + " de vida";
        String mensaje = mensajePrevio.isEmpty() ? encabezado : encabezado + "\n" + mensajePrevio;

        ventana.cambiarPantalla(MAZMORRA, () -> {
            cerrarBatalla();
            ctrlMazmorra.mostrar(nivel.getSalaActual().getEscenario(), mensaje);
            pantallaActual = MAZMORRA;
        }, null);
    }

    private void irABatalla() {
        ventana.cambiarPantalla(BATALLA, () -> {
            cerrarBatalla();
            Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
            Batalla batalla = new Batalla(heroe, enemigo);
            ctrlBatalla = new ControladorBatalla(new VistaBatalla(), batalla, this);
            ventana.agregarPantalla(BATALLA, ctrlBatalla.getVista());
            ctrlBatalla.iniciar();
            pantallaActual = BATALLA;
        }, null);
    }

    /** Cada pelea tiene su propia vista: al salir se detiene y se saca de la ventana. */
    private void cerrarBatalla() {
        if (ctrlBatalla != null) {
            ctrlBatalla.cerrar();
            ventana.quitarPantalla(ctrlBatalla.getVista());
            ctrlBatalla = null;
        }
    }

    // ---------- Avisos de los otros controladores ----------

    /** Lo llama ControladorMenu (a través de la selección) al tocar "Comenzar". */
    public void comenzarPartida(String idHeroe) {
        heroe = catalogo.crearHeroe(idHeroe);
        nivel = repositorio.crearNivelUno();
        enemigosDerrotados = 0;
        irAExploracion("");
    }

    /** Lo llama ControladorMazmorra cuando el jugador hace clic en un cartel. */
    public void caminoElegido(Camino camino) {
        irABatalla();
    }

    /** Lo llama ControladorBatalla cuando alguien muere. */
    public void batallaTerminada(boolean ganoHeroe) {
        if (!ganoHeroe) {
            terminarPartida(false);
            return;
        }

        enemigosDerrotados++;
        if (nivel.haySiguiente()) {
            nivel.avanzar();
            irAExploracion("Venciste al enemigo y seguís avanzando.");
        } else {
            terminarPartida(true);
        }
    }

    // ---------- Pausa ----------

    /** Solo se pausa durante la partida (exploración o batalla) y fuera de un fundido. */
    private boolean sePuedePausar() {
        boolean enPartida = pantallaActual.equals(MAZMORRA) || pantallaActual.equals(BATALLA);
        return enPartida && !enPausa && !ventana.enTransicion();
    }

    /** Lo dispara la tecla Esc. */
    private void pausar() {
        enPausa = true;
        if (ctrlBatalla != null) {
            ctrlBatalla.pausar(); // congela el turno del enemigo y demás temporizadores
        }
        ventana.atenuar(true);
        ctrlPausa.mostrar(); // modal: se queda acá hasta que se elige una opción
    }

    /** Lo llama ControladorPausa con "Volver al juego" (o Esc dentro de la pausa). */
    public void reanudarJuego() {
        enPausa = false;
        ventana.atenuar(false);
        if (ctrlBatalla != null) {
            ctrlBatalla.reanudar();
        }
    }

    /** Lo llama ControladorPausa con "Volver al título": se abandona la partida sin puntaje. */
    public void volverAlTitulo() {
        enPausa = false;
        heroe = null;
        nivel = null;
        irAMenu(null); // el fundido sale del oscurecido de la pausa y termina en el menú
    }

    /** Lo llama ControladorPausa con "Salir del juego". */
    public void salirDelJuego() {
        System.exit(0);
    }

    // ---------- Fin de la partida ----------

    private void terminarPartida(boolean victoria) {
        int puntos = enemigosDerrotados * PUNTOS_POR_ENEMIGO;
        tablaPuntuaciones.agregar(new ModeloPuntuacion(heroe.getNombre(), puntos));

        String mensaje = victoria
                ? "¡Victoria! Derrotaste al jefe de " + nivel.getNombre() + ".\nPuntos: " + puntos
                : "Caíste en la sala " + nivel.getNumeroSala() + " de " + nivel.getCantidadSalas()
                  + ".\nPuntos: " + puntos;

        heroe = null;
        nivel = null;

        // El mensaje aparece cuando el menú ya terminó de aclararse
        irAMenu(() -> ctrlMenu.mostrarMensaje(mensaje));
    }
}
