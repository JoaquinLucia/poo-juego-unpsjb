package modelo.enemigos;

import modelo.Enemigo;
import modelo.Habilidad;
import modelo.HabilidadAtaque;
import modelo.Robable;

public class Esqueleto extends Enemigo implements Robable {

    public Esqueleto() {
        super("Esqueleto", 60, 12, 1, new HabilidadAtaque("Osteoporosis", "Rompe huesos", 15, 2));
    }

    @Override
    public Habilidad obtenerHabilidad() {
        return new HabilidadAtaque("Osteoporosis", "Habilidad robada de un Esqueleto", 15, 2);
    }
}
