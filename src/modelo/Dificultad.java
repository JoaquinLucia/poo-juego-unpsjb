package modelo;

/**
 * Las dificultades del juego. Cada una sabe su nombre, cuánto modifica
 * a los enemigos y cuántos puntos da ganar una batalla con ella.
 * Para agregar una dificultad nueva alcanza con agregar una línea acá.
 */
public enum Dificultad {

    FACIL("Fácil", 0.75, 0.75, 50),
    NORMAL("Normal", 1.0, 1.0, 100),
    DIFICIL("Difícil", 1.5, 1.25, 200);

    private final String nombre;
    private final double multiplicadorVida;
    private final double multiplicadorAtaque;
    private final int puntosPorVictoria;

    Dificultad(String nombre, double multiplicadorVida, double multiplicadorAtaque, int puntosPorVictoria) {
        this.nombre = nombre;
        this.multiplicadorVida = multiplicadorVida;
        this.multiplicadorAtaque = multiplicadorAtaque;
        this.puntosPorVictoria = puntosPorVictoria;
    }

    /** Vida del enemigo ajustada a esta dificultad (nunca menos de 1). */
    public int ajustarVida(int vidaBase) {
        return Math.max(1, (int) Math.round(vidaBase * multiplicadorVida));
    }

    /** Ataque del enemigo ajustado a esta dificultad. */
    public int ajustarAtaque(int ataqueBase) {
        return (int) Math.round(ataqueBase * multiplicadorAtaque);
    }

    public String getNombre()          { return nombre; }
    public int getPuntosPorVictoria()  { return puntosPorVictoria; }
}
