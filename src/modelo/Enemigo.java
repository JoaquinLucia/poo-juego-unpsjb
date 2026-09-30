package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enemigo extends Entidad {

    private Enemigo(Builder b) {
        super(b.nombre, b.vida, b.ataque, b.maxHabilidades);
        if (b.habilidades.isEmpty()) {
            throw new IllegalStateException(b.nombre + " necesita al menos una habilidad");
        }
        for (Habilidad h : b.habilidades) {
            agregarHabilidad(h); // Entidad valida el máximo
        }
    }

    // ---------- Builder ----------
    public static class Builder {
        private final String nombre;
        private int vida;
        private int ataque;
        private int maxHabilidades;
        private final List<Habilidad> habilidades = new ArrayList<>();

        public Builder(String nombre) {
            this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser null");
        }

        public Builder vida(int vida)                     { this.vida = vida; return this; }
        public Builder ataque(int ataque)                 { this.ataque = ataque; return this; }
        public Builder maxHabilidades(int maxHabilidades) { this.maxHabilidades = maxHabilidades; return this; }

        public Builder habilidad(Habilidad habilidad) {
            habilidades.add(Objects.requireNonNull(habilidad, "La habilidad no puede ser null"));
            return this;
        }

        public Enemigo build() {
            if (vida <= 0)   throw new IllegalStateException(nombre + ": falta definir vida (> 0)");
            if (ataque < 0)  throw new IllegalStateException(nombre + ": falta definir ataque (>= 0)");
            return new Enemigo(this);
        }
    }
}