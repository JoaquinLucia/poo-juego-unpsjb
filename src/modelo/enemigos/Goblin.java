package modelo.enemigos;

import modelo.Enemigo;
import modelo.Habilidad;
import modelo.HabilidadAtaque;
import modelo.Robable;

public class Goblin extends Enemigo implements Robable {

    public Goblin() {
        super("Goblin", 40, 8, 1, new HabilidadAtaque("Ataque Furtivo", "Ataque veloz", 10, 1));
    }

    @Override
    public Habilidad obtenerHabilidad() {
        return new HabilidadAtaque("Ataque Furtivo", "Habilidad robada de un Goblin", 10, 1);
    }
}