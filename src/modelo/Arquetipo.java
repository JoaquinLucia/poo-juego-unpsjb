package modelo;

/**
 * Personajes jugables de "Senderos de las Cenizas".
 *
 * Al agregar una constante acá, la pantalla de selección la levanta automáticamente,
 * le dibuja su ficha, estadísticas y activa las flechas para navegar.
 * Ahora también le pasa el retrato enfocado a la interfaz de batalla.
 */
public enum Arquetipo {

    CABALLERO(
        "Caballero de Gwyn", "Guerrero",
        "Juró proteger una ciudad que ya no existe...",
        "Habilidad Principal...",
        6, 3,
        "/recursos/personajes/caballero.png",
        "/recursos/batalla/Artorias.png"
    ),

    MAGO(
        "Mago de Vinheim", "Soporte Mágico",
        "Un erudito exiliado...",
        "Habilidad Principal...",
        3, 2,
        "/recursos/personajes/mago.png",
        "/recursos/batalla/mago_icono.png" 
    ),

    ARQUERO(
        "Cazador de Sombras", "Tirador DPS",
        "Se mueve sin hacer ruido...",
        "Habilidad Principal...",
        4, 5,
        "/recursos/personajes/arquero.png",
        "/recursos/batalla/arquero_icono.png" 
    );

    /** Nombres de los stats, en el mismo orden que getStats(). */
    public static final String[] NOMBRES_STATS = {"Vida", "Ataque"};
    public static final int STAT_MAXIMO = 10;

    private final String nombre;
    private final String rol;
    private final String descripcion;
    private final String habilidad;
    private final int vida;
    private final int ataque;
    
    // Rutas de las imágenes
    private final String rutaImagen;
    private final String rutaRetrato;

    Arquetipo(String nombre, String rol, String descripcion, String habilidad,
              int vida, int ataque, String rutaImagen, String rutaRetrato) {
        this.nombre = nombre;
        this.rol = rol;
        this.descripcion = descripcion;
        this.habilidad = habilidad;
        this.vida = vida;
        this.ataque = ataque;
        this.rutaImagen = rutaImagen;
        this.rutaRetrato = rutaRetrato;
    }

    public String getNombre()      { return nombre; }
    public String getRol()         { return rol; }
    public String getDescripcion() { return descripcion; }
    public String getHabilidad()   { return habilidad; }
    
    // Getters para las imágenes
    public String getRutaImagen()  { return rutaImagen; }
    public String getRutaRetrato() { return rutaRetrato; }

    public int getVida()    { return vida; }
    public int getAtaque()  { return ataque; }

    /** Los stats en el mismo orden que NOMBRES_STATS (para dibujar las barras). */
    public int[] getStats() {
        return new int[] {vida, ataque};
    }
}