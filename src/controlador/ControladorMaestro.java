package controlador;

import modelo.Arquetipo;
import modelo.Batalla;
import modelo.Camino;
import modelo.CatalogoPersonajes;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.ModeloPuntuacion;
import modelo.ModeloTablaPuntuacion;
import modelo.Nivel;
import modelo.RepositorioEscenarios;
import vista.VistaBatalla;
import vista.VistaMazmorra;
import vista.VistaMenu;

/**
 * Controlador maestro (patrón Mediator).
 *
 * Es el único que sabe en qué orden van las pantallas:
 *   Menú → Selección → [Exploración → Batalla] x salas → Victoria / Derrota → Menú
 *
 * Los demás controladores no se conocen entre sí: cuando terminan lo suyo,
 * le avisan al maestro y él decide qué pantalla sigue.
 */
public class ControladorMaestro {
    private static final int PUNTOS_POR_ENEMIGO = 100;

    // Modelos que viven toda la aplicación
    private final ModeloTablaPuntuacion tablaPuntuaciones = new ModeloTablaPuntuacion();
    private final RepositorioEscenarios repositorio = new RepositorioEscenarios();
    private final CatalogoPersonajes catalogo = new CatalogoPersonajes();

    // Controladores de cada pantalla
    private final ControladorMenu ctrlMenu;
    private final ControladorMazmorra ctrlMazmorra;
    private ControladorBatalla ctrlBatalla; // se crea uno nuevo en cada pelea

    // Estado de la partida en curso
    private Heroe heroe;
    private Nivel nivel;
    private Arquetipo arquetipoElegido;
    private int enemigosDerrotados;

    public ControladorMaestro() {
        ctrlMenu = new ControladorMenu(new VistaMenu(), tablaPuntuaciones, this);
        ctrlMazmorra = new ControladorMazmorra(new VistaMazmorra(), this);
    }

    public void iniciar() {
        irAMenu();
    }

    // ---------- Navegación ----------

    private void ocultarTodo() {
        ctrlMenu.ocultar();
        ctrlMazmorra.ocultar();
        cerrarBatalla();
    }

    public void irAMenu() {
        ocultarTodo();
        ctrlMenu.mostrar();
    }

    private void irAExploracion(String mensajePrevio) {
        ocultarTodo();
        String encabezado = nivel.getNombre() + " — Sala " + nivel.getNumeroSala()
                + " de " + nivel.getCantidadSalas()
                + "   |   " + heroe.getNombre() + ": " + heroe.getVida() + " de vida";
        String mensaje = mensajePrevio.isEmpty() ? encabezado : encabezado + "\n" + mensajePrevio;
        ctrlMazmorra.mostrar(nivel.getSalaActual().getEscenario(), mensaje);
    }

    private void irABatalla() {
        ocultarTodo();
        Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
        Batalla batalla = new Batalla(heroe, enemigo);
        
        // <-- Le pasamos el arquetipoElegido a VistaBatalla
        ctrlBatalla = new ControladorBatalla(new VistaBatalla(arquetipoElegido), batalla, this); 
        ctrlBatalla.iniciar();
    }

    private void cerrarBatalla() {
        if (ctrlBatalla != null) {
            ctrlBatalla.cerrar();
            ctrlBatalla = null;
        }
    }

    // ---------- Avisos de los otros controladores ----------

    /** Lo llama ControladorMenu (a través de la selección) al tocar "Comenzar". */
    public void comenzarPartida(Arquetipo arquetipo) {
        this.arquetipoElegido = arquetipo; // <-- Guardamos el arquetipo acá
        heroe = catalogo.crearHeroe(arquetipo);
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

    // ---------- Fin de la partida ----------

    private void terminarPartida(boolean victoria) {
        int puntos = enemigosDerrotados * PUNTOS_POR_ENEMIGO;
        tablaPuntuaciones.agregar(new ModeloPuntuacion(heroe.getNombre(), puntos));

        irAMenu();
        String mensaje = victoria
                ? "¡Victoria! Derrotaste al jefe de " + nivel.getNombre() + ".\nPuntos: " + puntos
                : "Caíste en la sala " + nivel.getNumeroSala() + " de " + nivel.getCantidadSalas()
                  + ".\nPuntos: " + puntos;
        ctrlMenu.mostrarMensaje(mensaje);

        heroe = null;
        nivel = null;
    }
}
