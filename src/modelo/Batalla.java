package modelo;

import java.util.List;

public class Batalla {

    public enum Turno { HEROE, ENEMIGO }

    private final Heroe heroe;
    private final Enemigo enemigo;
    private int ultimoDanio;
    private Turno turnoActual = Turno.HEROE;

    public Batalla(Heroe heroe, Enemigo enemigo) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        // Que no queden efectos ni cooldowns de una batalla anterior
        heroe.prepararParaBatalla();
        enemigo.prepararParaBatalla();
    }

    public void ejecutarAtaqueHeroe() {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return;

        if (heroe.puedeActuar()) {
            heroe.atacar(enemigo);
        } else {
            perderTurno(heroe);
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
            heroe.usarHabilidad(indiceHabilidad, enemigo);
        } else {
            perderTurno(heroe);
        }
        terminarAccion();
        return true;
    }

    public void ejecutarDefensaHeroe() {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return;

        if (heroe.puedeActuar()) {
            heroe.defenderse();
            ultimoDanio = 0; 
        } else {
            perderTurno(heroe);
        }
        terminarAccion();
    }

    public void ejecutarTurnoEnemigo() {
        if (turnoActual != Turno.ENEMIGO || batallaTerminada()) return;

        if (enemigo.puedeActuar()) {
            enemigo.actuarEnemigo(heroe);
        } else {
            perderTurno(enemigo);
        }
        terminarAccion();
    }

    private void perderTurno(Entidad entidad) {
        ultimoDanio = 0;
        System.out.println(entidad.getNombre() + " está aturdido y pierde 1 turno");
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

    // el int devuelve el último daño que se realizó, lo usamos para imprimir en consola por cada turno
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
}