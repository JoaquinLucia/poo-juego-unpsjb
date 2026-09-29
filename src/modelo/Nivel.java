package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Secuencia ordenada de salas. Sabe en cuál está el jugador.
 * Se arma con Nivel.Builder, que obliga a que el jefe esté al final.
 */
public class Nivel {
    private final String nombre;
    private final List<Sala> salas;
    private int indiceActual = 0;

    private Nivel(String nombre, List<Sala> salas) {
        this.nombre = nombre;
        this.salas = salas;
    }

    public String getNombre() { return nombre; }
    public Sala getSalaActual() { return salas.get(indiceActual); }
    public int getNumeroSala() { return indiceActual + 1; }
    public int getCantidadSalas() { return salas.size(); }
    public boolean haySiguiente() { return indiceActual < salas.size() - 1; }

    public void avanzar() {
        if (haySiguiente()) {
            indiceActual++;
        }
    }

    // ---------- Builder ----------
    public static class Builder {
        private final String nombre;
        private final List<Sala> salas = new ArrayList<>();
        private boolean tieneJefe = false;

        public Builder(String nombre) {
            this.nombre = nombre;
        }

        public Builder sala(Escenario escenario, String idEnemigo) {
            if (tieneJefe) {
                throw new IllegalStateException("No puede haber salas después del jefe");
            }
            if (escenario.esSalaDelJefe()) {
                throw new IllegalArgumentException("Para la sala del jefe usá jefe(...)");
            }
            salas.add(new Sala(escenario, idEnemigo));
            return this;
        }

        public Builder jefe(Escenario escenario, String idJefe) {
            if (!escenario.esSalaDelJefe()) {
                throw new IllegalArgumentException("El escenario del jefe tiene que ser una sala del jefe");
            }
            salas.add(new Sala(escenario, idJefe));
            tieneJefe = true;
            return this;
        }

        public Nivel build() {
            if (!tieneJefe) {
                throw new IllegalStateException("El nivel necesita una sala del jefe");
            }
            return new Nivel(nombre, new ArrayList<>(salas));
        }
    }
}
