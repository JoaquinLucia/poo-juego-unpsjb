package modelo;


import java.util.List;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;

public abstract class Enemigo extends Entidad {
    private final String rutaMusica;


    protected Enemigo(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                    List<Habilidad> habilidades, String sprite, String spriteFrontal,
                    String rutaMusica, Dificultad dificultad) {
        super(nombre,dificultad.ajustarVida(vida),dificultad.ajustarAtaque(ataque),
            cantidadHabilidadMax, habilidades, sprite, spriteFrontal);
        this.rutaMusica = rutaMusica;

    }

    /** Su "IA": cada tipo de enemigo decide a su manera qué jugar. No ejecuta nada. */
    public Comando elegirComando(Entidad objetivo) {
        int habilidad = elegirHabilidad();
        if (habilidad >= 0) {
            return new ComandoHabilidad(this, objetivo, habilidad);
        }
        return new ComandoAtacar(this, objetivo);
    }
    
    public int elegirHabilidad() {
        for (int i = 0; i < getCantidadHabilidades(); i++) {
            if (puedeUsarHabilidad(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public String getRutaMusica() { return rutaMusica; }
}
