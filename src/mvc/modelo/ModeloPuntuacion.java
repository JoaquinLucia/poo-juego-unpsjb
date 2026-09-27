package mvc.modelo;

public class ModeloPuntuacion {

    private final String jugador;
    private final int puntos;
 
    public ModeloPuntuacion(String jugador, int puntos) {
        this.jugador = jugador;
        this.puntos = puntos;
    }
 
    public String getJugador() {
        return jugador;
    }
 
    public int getPuntos() {
        return puntos;
    }
}
