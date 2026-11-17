package modelo.enemigos;


import java.util.List;
import modelo.Dificultad;
import modelo.EfectoPorTurno;
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

    /** Si el héroe tiene menos del 30% de vida lo remata con básicos; si no, usa su primera habilidad lista. */
    @Override
    public Comando elegirComando(Entidad objetivo) {
        if (objetivo.getPorcentajeVida() >= 30) {
            int habilidad = elegirHabilidad();
            if (habilidad >= 0) {
                return new ComandoHabilidad(this, objetivo, habilidad);
            }
        }
        return new ComandoAtacar(this, objetivo);
    }

    
    @Override
    public int recibirDanio(int danio) {
        return super.recibirDanio(danio * 80 / 100); //reduce un 80 % de sanio
    }
    @Override
    public void aturdir(EfectoPorTurno efecto) { } //redefinido para que sea unmune a aturdimiento 
}