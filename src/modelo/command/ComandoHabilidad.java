package modelo.command;
import modelo.Entidad;
import modelo.Habilidad;

public class ComandoHabilidad implements Comando {
 
    private final Entidad actor;
    private final Entidad objetivo;
    private final int indice;
 
    public ComandoHabilidad(Entidad actor, Entidad objetivo, int indice) {
        this.actor = actor;
        this.objetivo = objetivo;
        this.indice = indice;
    }
 

@Override
    public String ejecutar() {
        Habilidad habilidad = actor.getHabilidad(indice);
        actor.usarHabilidad(indice, objetivo);
        return habilidad.getResumenDeUsoHab(actor.getNombre(), objetivo.getNombre());

    }
}
 