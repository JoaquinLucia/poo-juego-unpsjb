package modelo;

import java.util.ArrayList;
import java.util.List;
import modelo.command.Comando;

public class Batalla {

    public enum Turno { HEROE, ENEMIGO }

    private final Heroe heroe;
    private final Enemigo enemigo;
    private final Lugar lugar;
    private Turno turnoActual = Turno.HEROE;

    private final List<String> historial = new ArrayList<>();


    public Batalla(Heroe heroe, Enemigo enemigo, Lugar lugar) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.lugar = lugar;
        // Que no queden efectos ni cooldowns de una batalla anterior
        heroe.prepararParaBatalla();
        enemigo.prepararParaBatalla();
    }

    // ---------- Acciones ----------COMAND

    /** Ejecuta el comando, gasta el turno y devuelve qué pasó. */
    public String ejecutar(Comando comando) {
        Entidad actor = entidadEnTurno();
        
        if (actor == heroe) {
        heroe.dejarDeDefender();   // primero se apaga la defensa vieja
         }

        String texto;
        if (!actor.puedeActuar()) {
            texto = actor.getNombre() + " está aturdido y pierde el turno";   // el comando ni se ejecuta
        } else {
            texto = comando.ejecutar();
        }

        historial.add(texto);
        if (!batallaTerminada()) {
            pasarTurno();
        }
        return texto;
    }
    
    // ---------- Flujo de turnos ----------

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

    // ---------- Consultas ----------

    /** Efectos que tiene encima el héroe (veneno, desangrado, aturdimiento). */
    public List<EfectoPorTurno> getEfectosSobreHeroe() {
        return heroe.getEfectosActivos();
    }
    public Entidad entidadEnTurno() {
        return (turnoActual == Turno.HEROE) ? heroe : enemigo;
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
    public List<String> getHistorial() { return new ArrayList<>(historial); }


    public Heroe getHeroe()         { return heroe; }
    public Enemigo getEnemigo()     { return enemigo; }
    public Turno getTurnoActual()   { return turnoActual; }
    public Lugar getLugar()         { return lugar; }

}