package modelo;

/**
 * Las dificultades del juego. Cada una sabe su nombre, en qué porcentaje
 * modifica a los enemigos y cuántos puntos da ganar una batalla con ella.
 * Para agregar una dificultad nueva alcanza con agregar una línea acá.
 */
public enum Dificultad {

    //        nombre              % vida                   % ataque                  puntos
    FACIL   ("Fácil",      75,      75,      50),
    NORMAL  ("Normal",    100,     100,     100),
    DIFICIL ("Difícil",   150,     125,     200);

    private final String nombre;
    private final int porcentajeVida;
    private final int porcentajeAtaque;
    private final int puntosPorVictoria;

    Dificultad(String nombre, int porcentajeVida, int porcentajeAtaque, int puntosPorVictoria) {
        this.nombre = nombre;
        this.porcentajeVida = porcentajeVida;
        this.porcentajeAtaque = porcentajeAtaque;
        this.puntosPorVictoria = puntosPorVictoria;
    }

    /** Vida del enemigo ajustada a esta dificultad. Nunca queda en menos de 1. */
    public int ajustarVida(int vidaBase) {
        int vida = vidaBase * porcentajeVida / 100;
        if (vida < 1) {
            return 1;
        }
        return vida;
    }

    /** Ataque del enemigo ajustado a esta dificultad. */
    public int ajustarAtaque(int ataqueBase) {
        return ataqueBase * porcentajeAtaque / 100;
    }

    public String getNombre()          { return nombre; }
    public int getPuntosPorVictoria()  { return puntosPorVictoria; }
}