package modelo.enemigos;


import java.util.List;
import modelo.Dificultad;
import modelo.Enemigo;
import modelo.Entidad;
import modelo.Habilidad;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;

// inmune al aturdimiento 
public class EnemigoTanque extends Enemigo {

    public EnemigoTanque(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                        List<Habilidad> habilidades, String sprite, String spriteFrontal,
                        String rutaMusica, Dificultad dificultad) {
        super(nombre, vida, ataque, cantidadHabilidadMax, habilidades,
            sprite, spriteFrontal, rutaMusica, dificultad);
    }

    @Override
    public Comando elegirComando(Entidad objetivo) {
        int habilidad = elegirHabilidad();
        if (habilidad >= 0) {
            return new ComandoHabilidad(this, objetivo, habilidad);
        }
        return new ComandoAtacar(this, objetivo);
    }
    @Override
    public void setAturdida(boolean aturdida) { }
}