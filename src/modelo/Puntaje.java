package modelo;

/**
 * El resultado de una partida: quién jugó, con qué héroe, cuántos puntos hizo y cuándo.
 * Es un dato inmutable: una vez creado no cambia.
 */
public class Puntaje {

    private final String jugador;
    private final String heroe;
    private final int puntos;
    private final String fecha;   // la completa la base al guardar; null si todavía no se guardó

    /** Para un puntaje nuevo, recién terminada la partida. */
    public Puntaje(String jugador, String heroe, int puntos) {
        this(jugador, heroe, puntos, null);
    }

    /** Para un puntaje leído de la base, que ya tiene fecha. */
    public Puntaje(String jugador, String heroe, int puntos, String fecha) {
        if (jugador == null || jugador.isBlank()) {
            throw new IllegalArgumentException("El puntaje necesita el nombre del jugador");
        }
        if (heroe == null || heroe.isBlank()) {
            throw new IllegalArgumentException("El puntaje necesita el nombre del héroe");
        }
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos");
        }
        this.jugador = jugador.trim();
        this.heroe = heroe;
        this.puntos = puntos;
        this.fecha = fecha;
    }

    public String getJugador() { return jugador; }
    public String getHeroe()   { return heroe; }
    public int getPuntos()     { return puntos; }
    public String getFecha()   { return fecha; }

}