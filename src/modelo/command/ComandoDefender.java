package modelo.command;

import modelo.Heroe;

public class ComandoDefender implements Comando {

    private final Heroe actor;

    public ComandoDefender(Heroe actor) {
        this.actor = actor;
    }

    @Override
    public String ejecutar() {
        actor.defender();   // el método de Entidad que activa estaDefendiendo()
        return actor.getNombre() + " se prepara para bloquear el daño";
    }
}