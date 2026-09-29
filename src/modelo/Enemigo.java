package modelo;

import java.util.ArrayList;
import java.util.List;

public class Enemigo extends Entidad {

    public Enemigo(String nombre, int vida, int ataque, int cantidadHabilidadMax, Habilidad habilidadInicial) {
        super(nombre, vida, ataque, cantidadHabilidadMax);
        if (habilidadInicial == null) {
            throw new IllegalArgumentException("El enemigo debe tener una habilidad inicial");
        }
        agregarHabilidad(habilidadInicial);
    }

    // ---------- Builder ----------
    public static class Builder {
        private final String nombre;
        private int vida = 50;
        private int ataque = 5;
        private int maxHabilidades = 1;
        private final List<Habilidad> habilidades = new ArrayList<>();

        public Builder(String nombre) {
            this.nombre = nombre;
        }

        public Builder vida(int vida)                     { this.vida = vida; return this; }
        public Builder ataque(int ataque)                 { this.ataque = ataque; return this; }
        public Builder maxHabilidades(int maxHabilidades) { this.maxHabilidades = maxHabilidades; return this; }
        public Builder habilidad(Habilidad habilidad)     { this.habilidades.add(habilidad); return this; }

        public Enemigo build() {
            if (habilidades.isEmpty()) {
                throw new IllegalStateException(nombre + " necesita al menos una habilidad");
            }
            if (habilidades.size() > maxHabilidades) {
                throw new IllegalStateException(nombre + " tiene más habilidades que el máximo (" + maxHabilidades + ")");
            }
            Enemigo enemigo = new Enemigo(nombre, vida, ataque, maxHabilidades, habilidades.get(0));
            for (int i = 1; i < habilidades.size(); i++) {
                enemigo.agregarHabilidad(habilidades.get(i));
            }
            return enemigo;
        }
    }
}
