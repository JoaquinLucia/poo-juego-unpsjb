package modelo;

import java.util.List;

public class Enemigo extends Entidad {

    public Enemigo(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                   List<Habilidad> habilidades, String sprite, String spriteFrontal,
                   Dificultad dificultad) {
        super(nombre,
              dificultad.ajustarVida(vida),
              dificultad.ajustarAtaque(ataque),
              cantidadHabilidadMax, habilidades, sprite, spriteFrontal);
    }

    /**
     * Decide qué hacer en su turno: devuelve la posición de la primera habilidad lista,
     * o -1 si no hay ninguna (entonces hace un ataque común). No ejecuta nada.
     */
    public int elegirHabilidad() {
        for (int i = 0; i < getCantidadHabilidades(); i++) {
            if (puedeUsarHabilidad(i)) {
                return i;
            }
        }
        return -1;
    }
}