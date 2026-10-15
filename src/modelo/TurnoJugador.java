package modelo;

public class TurnoJugador implements FaseTurno {

    @Override
    public String atacar(Batalla batalla) {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();

        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            batalla.cambiarFase(new TurnoEnemigo());
            return heroe.getNombre() + " estaba aturdido y no pudo atacar.";
        }

        int danio = heroe.getAtaque();
        enemigo.recibirDanio(danio);
        
        String resultado = heroe.getNombre() + " ataca a " + enemigo.getNombre() + " infligiendo " + danio + " de daño.";
        batalla.cambiarFase(new TurnoEnemigo());
        
        return resultado;
    }

    @Override
    public String usarHabilidad(Batalla batalla, int indiceHabilidad) {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();

        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            batalla.cambiarFase(new TurnoEnemigo());
            return heroe.getNombre() + " estaba aturdido y no pudo usar habilidades.";
        }

        Habilidad habilidad = heroe.getHabilidad(indiceHabilidad);

        if (!habilidad.getCdListo()) {
            return "La habilidad " + habilidad.getNombreHabilidad() + " está en cooldown.";
        }

        habilidad.ejecutarHabilidad(heroe, enemigo);
        habilidad.activarcooldown();

        String resultado = heroe.getNombre() + " usó " + habilidad.getNombreHabilidad() + ".";
        batalla.cambiarFase(new TurnoEnemigo());
        
        return resultado;
    }

    @Override
    public String defender(Batalla batalla) {
        Heroe heroe = batalla.getHeroe();

        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            batalla.cambiarFase(new TurnoEnemigo());
            return heroe.getNombre() + " estaba aturdido y no pudo defenderse.";
        }

        heroe.setDefendiendo(true); // Usamos tu método setter
        String resultado = heroe.getNombre() + " adopta una postura defensiva. ¡Reducirá el daño!";
        
        batalla.cambiarFase(new TurnoEnemigo());
        return resultado;
    }

    @Override
    public String jugarEnemigo(Batalla batalla) {
        return "No es el turno del enemigo.";
    }

    @Override
    public boolean estaTerminada() {
        return false;
    }
}