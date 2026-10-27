package modelo;

import java.util.ArrayList;
import java.util.List;

/*
 * Sujeto del patrón Observer. Guarda la lista de observadores, ofrece
 * suscribir/desuscribir, y después de cada cambio la recorre y avisa a cada uno.
 */
public class Batalla {

    public enum Turno { HEROE, ENEMIGO }

    private final Heroe heroe;
    private final Enemigo enemigo;
    private final Lugar lugar;
    private Turno turnoActual = Turno.HEROE;

    private final List<ObservadorBatalla> observadores = new ArrayList<>();

    public Batalla(Heroe heroe, Enemigo enemigo, Lugar lugar) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.lugar = lugar;
        // Que no queden efectos ni cooldowns de una batalla anterior
        heroe.prepararParaBatalla();
        enemigo.prepararParaBatalla();
    }

    // ---------- Observer ----------

    public boolean suscribir(ObservadorBatalla observador) {
        if (observador == null) {
            return false;
        }
        this.observadores.add(observador);
        return true;
    }

    public boolean desuscribir(ObservadorBatalla observador) {
        return this.observadores.remove(observador);
    }

    private void avisarAtaque(Entidad atacante, Entidad objetivo, int danio) {
        for (ObservadorBatalla observador : this.observadores) {
            observador.ataqueRealizado(atacante, objetivo, danio);
        }
    }

    private void avisarHabilidad(Entidad usuario, String habilidad, Entidad objetivo, int danio) {
        for (ObservadorBatalla observador : this.observadores) {
            observador.habilidadUsada(usuario, habilidad, objetivo, danio);
        }
    }
        private void avisarDefensa(Entidad entidad) {
        for (ObservadorBatalla observador : this.observadores) {
            observador.defensaRealizada(entidad);
        }
    }

    private void avisarTurnoPerdido(Entidad aturdida) {
        for (ObservadorBatalla observador : this.observadores) {
            observador.turnoPerdido(aturdida);
        }
    }

    private void avisarCambioTurno() {
        Entidad actual = (turnoActual == Turno.HEROE) ? heroe : enemigo;
        for (ObservadorBatalla observador : this.observadores) {
            observador.turnoCambiado(actual);
        }
    }

    private void avisarFin() {
        boolean victoria = resultadoBatalla();
        for (ObservadorBatalla observador : this.observadores) {
            observador.finDeBatalla(victoria);
        }
    }

    /** Avisa a quién le toca el primer turno. Se llama después de suscribir a los observadores. */
    public void comenzar() {
        avisarCambioTurno();
    }

    // ---------- Acciones ----------

    public void ejecutarAtaqueHeroe() {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return;

        if (heroe.puedeActuar()) {
            int danio = heroe.atacar(enemigo);
            avisarAtaque(heroe, enemigo, danio);
        } else {
            avisarTurnoPerdido(heroe);
        }
        terminarAccion();
    }

    /** Devuelve false si la habilidad está en cooldown (el turno NO se gasta). */
    public boolean ejecutarHabilidadHeroe(int indice) {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return false;

        if (heroe.puedeActuar()) {
            if (!heroe.puedeUsarHabilidad(indice)) {
                return false;   // el controlador avisa "en enfriamiento"
            }
            String nombre = heroe.getHabilidad(indice).getNombreHabilidad();
            int danio = heroe.usarHabilidad(indice, enemigo);
            avisarHabilidad(heroe, nombre, enemigo, danio);
        } else {
            avisarTurnoPerdido(heroe);
        }
        terminarAccion();
        return true;
    }
    public void ejecutarDefensaHeroe() {
        if (turnoActual != Turno.HEROE || batallaTerminada()) return;

        if (heroe.puedeActuar()) {
            heroe.activarDefensa(heroe.getMultiplicadorDefensaPropio());
            avisarDefensa(heroe);
        } else {
            avisarTurnoPerdido(heroe);
        }
        terminarAccion();
    }

    public void ejecutarTurnoEnemigo() {
        if (turnoActual != Turno.ENEMIGO || batallaTerminada()) return;

        if (enemigo.puedeActuar()) {
            int indice = enemigo.elegirHabilidad();
            if (indice >= 0) {
                String nombre = enemigo.getHabilidad(indice).getNombreHabilidad();//hacxer getet
                int danio = enemigo.usarHabilidad(indice, heroe);//hacer get
                avisarHabilidad(enemigo, nombre, heroe, danio);
            } else {
                int danio = enemigo.atacar(heroe);
                avisarAtaque(enemigo, heroe, danio);
            }
        } else {
            avisarTurnoPerdido(enemigo);
        }
        terminarAccion();
    }

    // ---------- Flujo de turnos ----------

    private void terminarAccion() {
        if (batallaTerminada()) {
            avisarFin();
            return;
        }
        pasarTurno();
        if (batallaTerminada()) {   // un veneno puede matar al cerrar el turno
            avisarFin();
            return;
        }
        avisarCambioTurno();
    }

    /** Al final del turno de cada uno, se procesan los efectos que tiene encima. */
private void pasarTurno() {
        if (turnoActual == Turno.HEROE) {
            heroe.finalizarTurno();
            turnoActual = Turno.ENEMIGO;
        } else {
            enemigo.finalizarTurno();
            turnoActual = Turno.HEROE;
            heroe.activarDefensa(1.0); // <-- Acá se termina la postura defensiva
        }
    }

    // ---------- Consultas ----------

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