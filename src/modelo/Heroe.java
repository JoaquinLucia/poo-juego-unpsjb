package modelo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class Heroe extends Entidad {
    private int nivel;

    private Heroe(Builder b) {
        super(b.nombre, b.vida, b.ataque, b.maxHabilidades);
        if (b.nivel < 1) {
            throw new IllegalArgumentException("El nivel debe ser mayor o igual a 1");
        }
        this.nivel = b.nivel;
        for (Habilidad h : b.habilidades) {
            agregarHabilidad(h); // Entidad valida el máximo
        }
    }

    public int getNivel() { return nivel; }

    public void subirNivel() { nivel++; }

    public static class Builder {
        private final String nombre;
        private int vida;
        private int ataque ;
        private int nivel;
        private int maxHabilidades;
        private final List<Habilidad> habilidades = new ArrayList<>();

        public Builder(String nombre) {
            this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser null");
        }

        public Builder vida(int vida)                     { this.vida = vida; return this; }
        public Builder ataque(int ataque)                 { this.ataque = ataque; return this; }
        public Builder nivel(int nivel)                   { this.nivel = nivel; return this; }
        public Builder maxHabilidades(int maxHabilidades) { this.maxHabilidades = maxHabilidades; return this; }
        public Builder habilidad(Habilidad habilidad)     { this.habilidades.add(habilidad); return this; }

        public Heroe build() {
            return new Heroe(this);
        }
    }
}