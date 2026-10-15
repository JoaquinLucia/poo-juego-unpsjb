package modelo;

import java.util.ArrayList;
import java.util.List;

public class Heroe extends Entidad {
    private int nivel;
    private java.util.List<Habilidad> habilidadesConocidas = new java.util.ArrayList<>();
    public Heroe(String nombre, int vida, int ataque, int nivel, int cantidadHabilidadMax) {
        super(nombre, vida, ataque,cantidadHabilidadMax);
        if (nivel < 1) {
            throw new IllegalArgumentException("El nivel debe ser mayor o igual a 1");
        }
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public void subirNivel(){
        this.nivel++;
    }
    public void aprenderHabilidad(Habilidad nuevaHabilidad) {
        // Chequeamos que no la tenga repetida (por nombre)
        boolean yaLaTiene = habilidadesConocidas.stream()
            .anyMatch(h -> h.getNombreHabilidad().equals(nuevaHabilidad.getNombreHabilidad()));
        
        if (!yaLaTiene) {
            habilidadesConocidas.add(nuevaHabilidad);
        }
    }

    public java.util.List<Habilidad> getHabilidadesConocidas() {
        return habilidadesConocidas;
    }

    public void equiparHabilidades(java.util.List<Habilidad> elegidas) {
        // Limpiamos las habilidades activas de Entidad
        this.getHabilidades().clear();
        for (Habilidad h : elegidas) {
            this.agregarHabilidad(h); // Método de la clase padre Entidad
        }
    }


    // ---------- Builder ----------
    public static class Builder {
        private final String nombre;
        private int vida = 100;
        private int ataque = 10;
        private int nivel = 1;
        private int maxHabilidades = 2;
        private final List<Habilidad> habilidades = new ArrayList<>();

        public Builder(String nombre) {
            this.nombre = nombre;
        }

        public Builder vida(int vida)                     { this.vida = vida; return this; }
        public Builder ataque(int ataque)                 { this.ataque = ataque; return this; }
        public Builder nivel(int nivel)                   { this.nivel = nivel; return this; }
        public Builder maxHabilidades(int maxHabilidades) { this.maxHabilidades = maxHabilidades; return this; }
        public Builder habilidad(Habilidad habilidad)     { this.habilidades.add(habilidad); return this; }

        public Heroe build() {
            if (habilidades.size() > maxHabilidades) {
                throw new IllegalStateException(nombre + " tiene más habilidades que el máximo (" + maxHabilidades + ")");
            }
            Heroe heroe = new Heroe(nombre, vida, ataque, nivel, maxHabilidades);
            for (Habilidad h : habilidades) {
                heroe.agregarHabilidad(h);
            }
            return heroe;
        }
    }
}
