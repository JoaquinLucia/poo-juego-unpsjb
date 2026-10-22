package modelo;

import java.util.List;

public class Batalla {

    public enum Turno { HEROE, ENEMIGO }

    private final Heroe heroe;
    private final Enemigo enemigo;
    private Turno turnoActual = Turno.HEROE;
    private int ultimoDanio;   // daño de la última acción (0 si perdió el turno)
    private final Lugar lugar;

    public Batalla(Heroe heroe, Enemigo enemigo, Lugar lugar) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.lugar = lugar;
        // Que no queden efectos ni cooldowns de una batalla anterior
        heroe.prepararParaBatalla();
        enemigo.prepararParaBatalla();
    }

    public void ejecutarAtaqueHeroe() {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return;

        if (heroe.puedeActuar()) {
            ultimoDanio = heroe.atacar(enemigo);
        } else {
            ultimoDanio = 0;   // aturdido: pierde el turno
        }
        terminarAccion();
        }

    /** Devuelve false si la habilidad está en cooldown (el turno NO se gasta). */
    public boolean ejecutarHabilidadHeroe(int indiceHabilidad) {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return false;

        if (heroe.puedeActuar()) {
            if (!heroe.puedeUsarHabilidad(indiceHabilidad)) {
                return false; // la interfaz avisa "en enfriamiento"
            }
            ultimoDanio = heroe.usarHabilidad(indiceHabilidad, enemigo);
        } else {
            ultimoDanio = 0;   // aturdido: pierde el turno
        }
        terminarAccion();
        return true;
}

    public void ejecutarTurnoEnemigo() {
        if (turnoActual != Turno.ENEMIGO || batallaTerminada()) return;

        if (enemigo.puedeActuar()) {
            ultimoDanio = enemigo.actuarEnemigo(heroe);
        } else {
            ultimoDanio = 0;   // aturdido: pierde el turno
        }
        terminarAccion();
    }


    /** Al final del turno de cada uno, se procesan los efectos que tiene encima. */
    private void pasarTurno() {
        if (turnoActual == Turno.HEROE) {
            heroe.finalizarTurno();
            turnoActual = Turno.ENEMIGO;
        } else {
            enemigo.finalizarTurno();
            turnoActual = Turno.HEROE;
        }
    }

    private void terminarAccion() {
        if (!batallaTerminada()) {
            pasarTurno();
        }
    }

    /** Daño que hizo la última acción (ataque o habilidad). */
    public int getUltimoDanio() { return ultimoDanio; }

    /** Al que le toca ahora está aturdido (sirve para avisarle al jugador). */
    public boolean heroeAturdido() {
        return turnoActual == Turno.HEROE && !heroe.puedeActuar();
    }

    /** Efectos que tiene encima el héroe (veneno, desangrado, aturdimiento). */
    public List<EfectoPorTurno> getEfectosSobreHeroe() {
        return heroe.getEfectosActivos();
    }

    /** Efectos que tiene encima el enemigo. */
    public List<EfectoPorTurno> getEfectosSobreEnemigo() {
        return enemigo.getEfectosActivos();
    }

    public boolean batallaTerminada() {
        return !heroe.estaVivo() || !enemigo.estaVivo();
    }

    public boolean resultadoBatalla() {
        return heroe.estaVivo() && !enemigo.estaVivo();
    }

    public Heroe getHeroe()         { return heroe; }
    public Enemigo getEnemigo()     { return enemigo; }
    public Turno getTurnoActual()   { return turnoActual; }
    public Lugar getLugar()         { return lugar; }
}