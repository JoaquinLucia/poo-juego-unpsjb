package modelo;

/**
 * Estado del recorrido por la mazmorra:
 *   camino → batalla → camino → batalla → camino → batalla → sala del jefe → pelea final.
 *
 * Sabe en qué lugar está el jugador y con qué dificultad eligió el último camino.
 * No sabe nada de imágenes, botones ni pantallas.
 */
public class ModeloCaminoMazmorra {

    private final Lugar[] recorrido = Lugar.values();   // en el orden del enum
    private int posicion = 0;                           // 0 = el primer camino
    private Dificultad dificultadElegida = Dificultad.NORMAL;

    /** El jugador eligió por dónde seguir. */
    public void elegir(Dificultad dificultad) {
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser null");
        }
        if (llegoAlJefe()) {
            throw new IllegalStateException("En la sala del jefe no se elige camino");
        }
        this.dificultadElegida = dificultad;
    }

    /** Se llama al ganar una batalla de camino: pasa al lugar siguiente. */
    public void avanzar() {
        if (!llegoAlJefe()) {
            posicion++;
        }
    }

    /** Vuelve al principio (por ejemplo, al empezar una partida nueva). */
    public void reiniciar() {
        posicion = 0;
        dificultadElegida = Dificultad.NORMAL;
    }

    public Lugar getLugarActual() {
        return recorrido[posicion];
    }

    /** True cuando ya se pasaron los tres caminos y toca la pelea final. */
    public boolean llegoAlJefe() {
        return getLugarActual() == Lugar.SALA_JEFE;
    }

    /** Número del camino actual, empezando en 1 (para mostrar). */
    public int getNumeroCamino() {
        return posicion + 1;
    }

    public Dificultad getDificultadElegida() {
        return dificultadElegida;
    }
}