package modelo.enemigos;


import java.util.List;
import modelo.Dificultad;
import modelo.Enemigo;
import modelo.Entidad;
import modelo.Habilidad;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoHabilidad;

// aca la idea si el enemigo tiene menos del 30% de vida trata de rematar con basicos 
public class EnemigoAgil extends Enemigo {

    public EnemigoAgil(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                       List<Habilidad> habilidades, String sprite, String spriteFrontal,
                       String rutaMusica, Dificultad dificultad) {
        super(nombre, vida, ataque, cantidadHabilidadMax, habilidades,
              sprite, spriteFrontal, rutaMusica, dificultad);
    }


    @Override
    public Comando elegirComando(Entidad objetivo) {
      //  if (objetivo.getPorcentajeVida() > 30) {
            int habilidad = elegirHabilidad();
            if (habilidad >= 0) {
                return new ComandoHabilidad(this, objetivo, habilidad);
      //      }
        }
        return new ComandoAtacar(this, objetivo);
    }

    
    @Override
    public void finalizarTurno() {
        super.finalizarTurno();
        if (estaPotenciado()) {
            reducirCooldowns();
        }
    }

    //potenciar seria que reduce mas rapido los cd de las habilidades 
    private boolean estaPotenciado() {
        return getPorcentajeVida() <= 50;
    }
}