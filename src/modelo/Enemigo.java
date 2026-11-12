package modelo;
 
import java.util.List;
import java.util.Random;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;
 
public class Enemigo extends Entidad {

    private final Random random = new Random();
    private final double probabilidadEsquivar = 0.20; // 20%
    private final double probabilidadCritico = 0.10; // 10%

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
 
    /** Su "IA": arma el comando que va a jugar. Lo ejecuta quien lo reciba. */
    public Comando elegirComando(Entidad objetivo) {
        int indice = elegirHabilidad();
        if (indice >= 0) {
            return new ComandoHabilidad(this, objetivo, indice);
        }
        return new ComandoAtacar(this, objetivo);
    }

    // Ataque común con probabilidad de golpe crítico (daño doble pero que no se imprime en pantalla)
    @Override
    public int atacar(Entidad objetivo) {
        if (random.nextDouble() < probabilidadCritico) {
            System.out.println(getNombre() + " dio un golpe crítico!");
            return objetivo.recibirDanio(getAtaque() * 2);
        }
        return super.atacar(objetivo);
    }

    //Enemigo ahora tiene una probabilidad de esquivar el daño de los ataques del 20% pero no los efectos de las habilidades 
    //tampoco se imprime en pantalla
    @Override
    public int recibirDanio(int danio) {
        if (random.nextDouble() < probabilidadEsquivar) {
            System.out.println(getNombre() + " esquivó el ataque!");
            return 0;
        }
        return super.recibirDanio(danio);
    }

    
}