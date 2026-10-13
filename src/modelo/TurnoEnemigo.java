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
    public String jugarEnemigo(Batalla batalla) {
        Enemigo enemigo = batalla.getEnemigo();
        Heroe heroe = batalla.getHeroe();

        // Ejecutamos el ataque del enemigo hacia el héroe 
        // (usando el método atacar heredado de Entidad)
        enemigo.atacar(heroe);
        
        String resultado = enemigo.getNombre() + " ataca a " + heroe.getNombre() + ".";

        // Comprobamos si la batalla terminó (si el héroe murió)
        if (batalla.estaTerminada()) {
            // Si termina, podemos dejar la fase actual o manejar el fin de juego
            // (Si no tienes clase BatallaTerminada, puedes dejar esta misma fase o limpiar)
        } else {
            // Devolvemos el turno al jugador
            batalla.cambiarFase(new TurnoJugador());
        }

        return resultado;
    }

    @Override
    public boolean estaTerminada() {
        return false;
    }
}
