package modelo;

public class Batalla {
    private Heroe heroe;
    private Enemigo enemigo;
    private Turno turnoActual;

    public enum Turno {
        HEROE,
        ENEMIGO
    }

    public Batalla(Heroe heroe, Enemigo enemigo) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.turnoActual = Turno.HEROE;
    }

    public String ejecutarAtaqueHeroe() {
        if (turnoActual != Turno.HEROE || !heroe.estaVivo()) {
            return "No es el turno del héroe.";
        }

        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            pasarTurno();
            return heroe.getNombre() + " está aturdido y pierde su turno.";
        }

        heroe.atacar(enemigo);
        String mensaje = heroe.getNombre() + " atacó a " + enemigo.getNombre() + " causando " + heroe.getAtaque() + " de daño.";
        pasarTurno();
        return mensaje;
    }

    public String ejecutarHabilidadHeroe(int indiceHabilidad) {
        if (turnoActual != Turno.HEROE || !heroe.estaVivo()) {
            return "No es el turno del héroe.";
        }

        if (heroe.getaturdir()) {
            heroe.sacarAturdimiento();
            pasarTurno();
            return heroe.getNombre() + " está aturdido y pierde su turno.";
        }

        Habilidad habilidad = heroe.getHabilidad(indiceHabilidad);
        if (!habilidad.getCdListo()) {
            return "Habilidad en cooldown (" + habilidad.getCooldownActual() + " turnos restantes).";
        }

        habilidad.ejecutarHabilidad(heroe, enemigo);
        String mensaje = heroe.getNombre() + " usó " + habilidad.getNombreHabilidad() + ".";
        pasarTurno();
        return mensaje;
    }

    public String ejecutarTurnoEnemigo() {
        if (turnoActual != Turno.ENEMIGO || !enemigo.estaVivo()) {
            return "";
        }

        if (enemigo.getaturdir()) {
            enemigo.sacarAturdimiento();
            pasarTurno();
            return enemigo.getNombre() + " está aturdido y pierde su turno.";
        }

        String mensaje;
        Habilidad habilidad = enemigo.getCantidadHabilidades() > 0 ? enemigo.getHabilidad(0) : null;

        if (habilidad != null && habilidad.getCdListo()) {
            habilidad.ejecutarHabilidad(enemigo, heroe);
            mensaje = enemigo.getNombre() + " usó su habilidad " + habilidad.getNombreHabilidad() + ".";
        } else {
            enemigo.atacar(heroe);
            mensaje = enemigo.getNombre() + " atacó a " + heroe.getNombre() + " causando " + enemigo.getAtaque() + " de daño.";
        }

        pasarTurno();
        return mensaje;
    }

    private void pasarTurno() {
        if (turnoActual == Turno.HEROE) {
            turnoActual = Turno.ENEMIGO;
            // Reducir cooldowns del héroe al terminar su turno
            for (Habilidad h : heroe.getHabilidades()) {
                h.cronoCooldown();
            }
        } else {
            turnoActual = Turno.HEROE;
            // Reducir cooldowns del enemigo al terminar su turno
            for (Habilidad h : enemigo.getHabilidades()) {
                h.cronoCooldown();
            }
        }
    }

    public boolean estaTerminada() {
        return !heroe.estaVivo() || !enemigo.estaVivo();
    }

    public boolean ganoHeroe() {
        return heroe.estaVivo() && !enemigo.estaVivo();
    }

    public Heroe getHeroe() { return heroe; }
    public Enemigo getEnemigo() { return enemigo; }
    public Turno getTurnoActual() { return turnoActual; }
}