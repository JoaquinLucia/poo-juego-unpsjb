package modelo.command;

import modelo.Entidad;

public class ComandoAtacar implements Comando {

    private final Entidad actor;
    private final Entidad objetivo;

    public ComandoAtacar(Entidad actor, Entidad objetivo) {
        this.actor = actor;
        this.objetivo = objetivo;
    }

@Override
public String ejecutar() {
    int danio = actor.atacar(objetivo);
    return actor.getNombre() + " atacó a " + objetivo.getNombre() + " y le hizo " + danio + " de daño";
}
}
