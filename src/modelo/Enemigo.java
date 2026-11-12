package modelo;
 
import java.util.List;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;
import modelo.command.ComandoPasarTurno;
 
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

        // El enemigo tiene una chance de "pasar turno" o recuperar compostura
        // Si la vida desciende del umbral crítico de 45 HP y la IA evalúa con una probabilidad del 50%(0.5)para decidir entre un respiro estrategico y ceder el turno
        if (this.getVida() < 45 && Math.random() < 0.5) {
            return new ComandoPasarTurno(this);
        }

        return new ComandoAtacar(this, objetivo);
    }
}