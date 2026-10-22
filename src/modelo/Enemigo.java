package modelo;

import java.util.List;

public class Enemigo extends Entidad {

    private String nombreUltimaHabilidad;   // null = hizo un ataque común

    public Enemigo(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                   List<Habilidad> habilidades, String sprite, String spriteFrontal,
                   Dificultad dificultad) {
        super(nombre,
              dificultad.ajustarVida(vida),
              dificultad.ajustarAtaque(ataque),
              cantidadHabilidadMax, habilidades, sprite, spriteFrontal);
    }

    /** Usa la primera habilidad lista; si no hay ninguna, ataca. Devuelve el daño que hizo. */
    public int actuarEnemigo(Entidad objetivo) {
        nombreUltimaHabilidad = null;
        for (int i = 0; i < getCantidadHabilidades(); i++) {
            if (puedeUsarHabilidad(i)) {
                nombreUltimaHabilidad = getHabilidad(i).getNombreHabilidad();
                return usarHabilidad(i, objetivo);
            }
        }
        return this.atacar(objetivo);
    }

    /** True si en su último turno usó una habilidad (false = ataque común). */
    public boolean usoHabilidad() {
        return nombreUltimaHabilidad != null;
    }

    public String getNombreUltimaHabilidad() {
        return nombreUltimaHabilidad;
    }

}