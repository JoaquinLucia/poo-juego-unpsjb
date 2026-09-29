package modelo;

/**
 * Un camino (cartel) dentro de un escenario.
 * La posición está en proporción a la imagen (0.0 a 1.0).
 */
public class Camino {
    private final String nombre;
    private final String descripcion;
    private final String textoCartel;
    private final String rutaIcono; // imagen del cartel; null = cartel de texto
    private final double posX;
    private final double posY;

    public Camino(String nombre, String descripcion, String textoCartel, String rutaIcono,
                  double posX, double posY) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textoCartel = textoCartel;
        this.rutaIcono = rutaIcono;
        this.posX = posX;
        this.posY = posY;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getTextoCartel() { return textoCartel; }
    public String getRutaIcono() { return rutaIcono; }
    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
}
