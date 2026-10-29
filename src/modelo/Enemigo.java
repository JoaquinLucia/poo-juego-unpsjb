package modelo;
 
import java.util.List;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;
 
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
 
    /** Su "IA": arma el comando que va a jugar. Lo ejecuta quien lo reciba. */
    public Comando elegirComando(Entidad objetivo) {
        int indice = elegirHabilidad();
        if (indice >= 0) {
            return new ComandoHabilidad(this, objetivo, indice);
        }
        return new ComandoAtacar(this, objetivo);
    }
}