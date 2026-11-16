package recursos.rutas;
import java.util.HashMap;
import java.util.Map;

/**
 * Guarda en un solo lugar todas las rutas de imágenes del juego.
 * Si se mueve una carpeta o cambia una extensión, solo se modifica esta clase.
 *
 * Es parte de la VISTA: el modelo no sabe qué imagen tiene cada cosa.
 * Los lugares se identifican por su nombre (ej. "BOSQUE"), así esta clase
 * no necesita importar nada del modelo.
 */
public class RutasRecursos {

    private final String carpetaEscenarios = "/recursos/mazmorra/";
    private final String carpetaSprites = "/recursos/sprites/";
    private final String fondoPorDefecto = "/recursos/mazmorra/mazmorra_animada.gif";

    // nombre del lugar -> archivo de imagen
    private final Map<String, String> fondosCamino = new HashMap<>();
    private final Map<String, String> fondosCombate = new HashMap<>();

    public RutasRecursos() {
        agregarEscenario("BOSQUE",    "bosque.gif",      "combate_bosque.gif");
        agregarEscenario("CUEVA",     "cueva.gif",       "combate_cueva.gif");
        agregarEscenario("MAZMORRA",  "mazmorra.gif",    "combate_mazmorra.gif");
        agregarEscenario("SALA_JEFE", "jefe_puerta.gif", "combate_jefe.gif");
    }

    private void agregarEscenario(String lugar, String archivoCamino, String archivoCombate) {
        fondosCamino.put(lugar, archivoCamino);
        fondosCombate.put(lugar, archivoCombate);
    }

    // ---------- Fondos ----------

    public String getFondoPorDefecto() {
        return fondoPorDefecto;
    }

    /** Fondo de la pantalla del camino. Si el lugar no está cargado, devuelve el fondo por defecto. */
    public String fondoCamino(String lugar) {
        return buscarFondo(fondosCamino, lugar);
    }

    /** Fondo de la pantalla de batalla. Si el lugar no está cargado, devuelve el fondo por defecto. */
    public String fondoCombate(String lugar) {
        return buscarFondo(fondosCombate, lugar);
    }

    private String buscarFondo(Map<String, String> fondos, String lugar) {
        String archivo = fondos.get(lugar);
        if (archivo == null) {
            System.err.println("No hay fondo cargado para el lugar: " + lugar);
            return fondoPorDefecto;
        }
        return carpetaEscenarios + archivo;
    }

    // ---------- Sprites de personajes ----------
    // Todas las hojas siguen la misma regla: nombre_accion.png

    public String spriteAtaque(String nombrePersonaje) {
        return carpetaSprites + nombrePersonaje.toLowerCase() + "_ataque.png";
    }

    public String spriteDerrota(String nombrePersonaje) {
        return carpetaSprites + nombrePersonaje.toLowerCase() + "_derrota.png";
    }
}
