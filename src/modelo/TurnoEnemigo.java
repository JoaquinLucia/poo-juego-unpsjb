package modelo;

public class TurnoEnemigo implements FaseTurno {

    @Override
    public String atacar(Batalla batalla) {
        return "No es el turno del héroe.";
    }

    @Override
    public String usarHabilidad(Batalla batalla, int indiceHabilidad) {
        return "No es el turno del héroe.";
    }

    @Override
    public String defender(Batalla batalla) {
        return "No es el turno del héroe.";
    }

    @Override
    public String jugarEnemigo(Batalla batalla) {
        Enemigo enemigo = batalla.getEnemigo();
        Heroe heroe = batalla.getHeroe();

        // El ataque simple (la estrategia avanzada la maneja Batalla.java)
        enemigo.atacar(heroe);
        String resultado = enemigo.getNombre() + " ataca a " + heroe.getNombre() + ".";

        if (!batalla.estaTerminada()) {
            batalla.cambiarFase(new TurnoJugador());
        }

        return resultado;
    }

    @Override
    public boolean estaTerminada() {
        return false;
    }
}