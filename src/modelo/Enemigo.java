package modelo;

import java.util.List;

public class Enemigo extends Entidad {

    public Enemigo(String nombre, int vida, int ataque, int cantidadHabilidadMax, List<Habilidad> habilidades, String sprite,String sprite_Frontal) {
        super(nombre, vida, ataque, cantidadHabilidadMax, habilidades,sprite,sprite_Frontal);
        
    }
    public void actuarEnemigo(Entidad objetivo){
        for (int i = 0; i < getCantidadHabilidades(); i++) {
            if (puedeUsarHabilidad(i)) {
                usarHabilidad(i, objetivo);
                return;
            }
        }
        this.atacar(objetivo);
    }

    public void aplicarDificultad(Dificultad dificultad) {
        vida = dificultad.ajustarVida(vida);
        vidaActual = vida;
        ataque = dificultad.ajustarAtaque(ataque);
    }

}