package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de la pantalla de selección: qué héroes hay disponibles,
 * cuál está seleccionado y cuál eligió el jugador.
 * No sabe nada de la vista.
 */
public class SeleccionHeroe {

    private final List<Heroe> heroes;
    private int indice = 0;
    private Heroe heroeElegido;   // null hasta que el jugador confirma

    public SeleccionHeroe(List<Heroe> heroes) {
        if (heroes == null || heroes.isEmpty()) {
            throw new IllegalArgumentException("Tiene que haber al menos un héroe para elegir");
        }
        this.heroes = new ArrayList<>(heroes);
    }

    // ---------- Navegación ----------

    public void siguiente() {
        indice = (indice + 1) % heroes.size();
    }

    public void anterior() {
        indice = (indice - 1 + heroes.size()) % heroes.size();
    }

    public void reiniciar() {
        indice = 0;
        heroeElegido = null;
    }

    // ---------- Elección ----------

    public void confirmar() {
        heroeElegido = getActual();
    }

    public boolean hayHeroeElegido() {
        return heroeElegido != null;
    }

    // ---------- Consultas ----------

    public Heroe getActual()        { return heroes.get(indice); }
    public Heroe getHeroeElegido()  { return heroeElegido; }
    public int getCantidad()        { return heroes.size(); }
    public boolean hayVarios()      { return heroes.size() > 1; }
}