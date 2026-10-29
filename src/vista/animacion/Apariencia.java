package vista.animacion;

/**
 * Cómo se dibuja un personaje en la pantalla de batalla:
 * dónde va, qué tamaño tiene y cuánto recortar del primer frame.
 * Es una clase de datos: solo guarda valores y los devuelve.
 */
public class Apariencia {

    private final int x;
    private final int y;
    private final int ancho;
    private final int alto;
    // píxeles a quitar del borde derecho del primer frame
    // (algunas hojas tienen un resto del frame siguiente pegado)
    private final int recortePrimerFrame;

    public Apariencia(int x, int y, int ancho, int alto, int recortePrimerFrame) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.recortePrimerFrame = recortePrimerFrame;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getRecortePrimerFrame() { return recortePrimerFrame; }
}
