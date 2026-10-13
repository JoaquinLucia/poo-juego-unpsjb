package modelo;

public class TurnoJugador implements FaseTurno {

    @Override
    public String atacar(Batalla batalla) {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();

        // 1. Verificamos si el héroe está aturdido
        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            batalla.cambiarFase(new TurnoEnemigo());
            return heroe.getNombre() + " estaba aturdido y no pudo atacar.";
        }

        // 2. Realizamos el ataque
        int danio = heroe.getAtaque();
        enemigo.recibirDanio(danio);
        
        String resultado = heroe.getNombre() + " ataca a " + enemigo.getNombre() + " infligiendo " + danio + " de daño.";

        // 3. Cambiamos de fase al turno del enemigo
        batalla.cambiarFase(new TurnoEnemigo());
        
        return resultado;
    }

    @Override
    public String usarHabilidad(Batalla batalla, int indiceHabilidad) {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();

        // 1. Verificamos si el héroe está aturdido
        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            batalla.cambiarFase(new TurnoEnemigo());
            return heroe.getNombre() + " estaba aturdido y no pudo usar habilidades.";
        }

        // 2. Obtenemos la habilidad del héroe
        Habilidad habilidad = heroe.getHabilidad(indiceHabilidad);

        // 3. Verificamos si la habilidad está lista para usarse (cooldown)
        if (!habilidad.getCdListo()) {
            return "La habilidad " + habilidad.getNombreHabilidad() + " está en cooldown.";
        }

        // 4. Ejecutamos la habilidad y activamos su cooldown
        habilidad.ejecutarHabilidad(heroe, enemigo);
        habilidad.activarcooldown();

        String resultado = heroe.getNombre() + " usó " + habilidad.getNombreHabilidad() + ".";

        // 5. Cambiamos de fase al turno del enemigo
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