package modelo.enemigos;

import modelo.Enemigo;
import modelo.Habilidad;
import modelo.HabilidadAtaque;
import modelo.Robable;

public class Goblin extends Enemigo implements Robable {

    // Constructor flexible con parámetros
    public Goblin(String nombre, int vida, int ataque, int nivel) {
        super(nombre, vida, ataque, nivel, new HabilidadAtaque("Cuchillazo", "Zarpazo", 10, 1));
    }

    // Constructor por defecto (opcional, usa valores base)
    public Goblin() {
        this("Goblin", 40, 8, 1);
    }

    @Override
    public Habilidad obtenerHabilidad() {
        return new HabilidadAtaque("Cuchillazo", "Habilidad robada de un Goblin", 10, 1);
    }
}