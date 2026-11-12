package modelo.command;

import modelo.Entidad;

public class ComandoPasarTurno implements Comando {

    private final Entidad actor;

    public ComandoPasarTurno(Entidad actor) {
        this.actor = actor;
    }

    @Override
    public String ejecutar() {
        return actor.getNombre() + " decide pasar el turno y recuperar fuerzas";
    }
}
