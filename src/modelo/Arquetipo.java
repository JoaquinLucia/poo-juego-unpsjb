package modelo;

/**
 * Personajes jugables de "Senderos de las Cenizas".
 *
 * Por ahora solo está el Caballero. Para sumar otro personaje alcanza con
 * agregar otra constante: la pantalla de selección muestra flechas para
 * recorrerlos automáticamente cuando hay más de uno.
 *
 * Cada personaje se dibuja con su imagen (PNG con fondo transparente
 * dentro de /recursos/personajes/).
 */
public enum Arquetipo {

    CABALLERO(
        "Caballero de Gwyn", "Guerrero",
        "Juró proteger una ciudad que ya no existe. Su armadura todavía "
            + "guarda el calor del último incendio, y su espada no ha "
            + "vuelto a la vaina desde entonces.",
        "Habilidad Principal: "
        + "golpe aturdidor - daña y provoca aturdimiento al enemigo por 3 turnos.",
        9, 7,
        "/recursos/personajes/caballero.png");

    /** Nombres de los stats, en el mismo orden que getStats(). */
    public static final String[] NOMBRES_STATS = {"Vida", "Ataque"};
    public static final int STAT_MAXIMO = 10;

    private final String nombre;
    private final String rol;
    private final String descripcion;
    private final String habilidad;
    private final int vida;
    private final int ataque;
    private final String rutaImagen;

    Arquetipo(String nombre, String rol, String descripcion, String habilidad,
              int vida, int ataque, String rutaImagen) {
        this.nombre = nombre;
        this.rol = rol;
        this.descripcion = descripcion;
        this.habilidad = habilidad;
        this.vida = vida;
        this.ataque = ataque;
        this.rutaImagen = rutaImagen;
    }

    public String getNombre()      { return nombre; }
    public String getRol()         { return rol; }
    public String getDescripcion() { return descripcion; }
    public String getHabilidad()   { return habilidad; }
    public String getRutaImagen()  { return rutaImagen; }

    public int getVida()    { return vida; }
    public int getAtaque()  { return ataque; }

    /** Los stats en el mismo orden que NOMBRES_STATS (para dibujar las barras). */
    public int[] getStats() {
        return new int[] {vida, ataque};
    }
}