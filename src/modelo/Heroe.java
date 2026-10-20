package modelo;

import java.util.List;

public class Heroe extends Entidad {
    private int nivel;
    private String descripcion;

    public Heroe(String nombre, int vida, int ataque, int cantidadHabilidadMax, List<Habilidad> habilidades, String sprite,String sprite_Frontal,String descripcion) {
        super(nombre, vida, ataque, cantidadHabilidadMax, habilidades,sprite,sprite_Frontal);
        this.nivel = 1;
        this.descripcion = descripcion;    
    }

    // HABILIDADES
    public void reemplazarHabilidad(int posicion, Habilidad nuevaHabilidad) {
        if (nuevaHabilidad == null) {
            throw new IllegalArgumentException("La habilidad no puede ser null");
        }
        if (posicion < 0 || posicion >= getCantidadHabilidades()) {
            throw new IllegalArgumentException("La posición de la habilidad no es válida");
        }
        getHabilidades().set(posicion, nuevaHabilidad);
    }

    public int getNivel()          { return nivel; }
    public String getDescripcion() { return this.descripcion; }

    public void subirNivel() { nivel++; }
}