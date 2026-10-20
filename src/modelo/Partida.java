package modelo;

/**
 * Una partida en curso: el héroe elegido, el recorrido por la mazmorra
 * y los puntos que se van ganando.
 * No sabe nada de pantallas.
 */
public class Partida {

    private final int bonusJefe = 500;

    private final Heroe heroe;
    private final ModeloCaminoMazmorra camino = new ModeloCaminoMazmorra();
    private int puntos = 0;

    public Partida(Heroe heroe) {
        if (heroe == null) {
            throw new IllegalArgumentException("La partida necesita un héroe");
        }
        this.heroe = heroe;
    }

    /** Se ganó una batalla de camino: suma los puntos según la dificultad elegida. */
    public void registrarVictoria() {
        puntos += camino.getDificultadElegida().getPuntosPorVictoria();
    }

    /** Se derrotó al jefe: suma el bonus. */
    public void registrarVictoriaJefe() {
        puntos += bonusJefe;
    }

    /** Puntaje final: lo acumulado más la vida que le quedó al héroe. */
    public int calcularPuntajeFinal() {
        return puntos + heroe.getVida();
    }

    public Heroe getHeroe()                  { return heroe; }
    public ModeloCaminoMazmorra getCamino()  { return camino; }
}
