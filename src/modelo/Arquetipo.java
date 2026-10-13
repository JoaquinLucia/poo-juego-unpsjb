package modelo;

public enum Arquetipo {
    CABALLERO(
        "Caballero",
        "Guerrero de Hierro",
        "Un veterano de las guerras antiguas cubierto de acero oxidado.",
        "Golpe Pesado: Un ataque demoledor que aturde al rival.",
        new int[]{5, 4, 3}, // Vida, Defensa, Ataque
        "personajes/caballero.png"
    ),
    MAGO(
        "Mago",
        "Hechicero Arcano",
        "Estudioso de los fuegos primordiales y las artes olvidadas.",
        "Llamarada: Arrasa al enemigo con fuego puro.",
        new int[]{3, 2, 5},
        "personajes/mago.png"
    ),
    ARQUERO(
        "Arquero",
        "Cazador Silvestre",
        "Vigilante de los senderos oscuros con reflejos felinos.",
        "Disparo Certero: Una flecha rápida que nunca falla.",
        new int[]{4, 3, 4},
        "personajes/arquero.png"
    );

    private final String nombre;
    private final String rol;
    private final String descripcion;
    private final String habilidad;
    private final int[] stats;
    private final String rutaImagen;

    public static final int STAT_MAXIMO = 5;
    public static final String[] NOMBRES_STATS = {"VIDA", "DEF", "ATQ"};

    Arquetipo(String nombre, String rol, String descripcion, String habilidad, int[] stats, String rutaImagen) {
        this.nombre = nombre;
        this.rol = rol;
        this.descripcion = descripcion;
        this.habilidad = habilidad;
        this.stats = stats;
        this.rutaImagen = rutaImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public int[] getStats() {
        return stats;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }
}