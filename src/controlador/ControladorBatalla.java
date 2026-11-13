package controlador;

import java.util.EnumMap;
import java.util.Map;
import javax.swing.Timer;
import modelo.Batalla;
import modelo.Enemigo;
import modelo.Entidad;
import modelo.Habilidad;
import modelo.Heroe;
import modelo.command.Comando;
import modelo.command.ComandoAtacar;
import modelo.command.ComandoDefender;
import modelo.command.ComandoHabilidad;
import vista.VistaBatalla;

/*
 * Controla la pantalla de combate. Es el único que conoce al modelo y a la vista:
 *  - cada botón ejecuta un Comando sobre Batalla;
 *  - después de cada acción, pasarAlSiguienteTurno() muestra el estado y decide qué sigue;
 *  - las animaciones van ANTES de la acción: la acción se ejecuta cuando la animación termina.
 */
public class ControladorBatalla {
    public enum AccionHeroe { ATACAR, HABILIDAD, DEFENDER }

    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro controladorMaestro;

    private final int pausaTurnoEnemigoMs = 2500;   // para que se vea qué hizo cada uno
    private final int pausaFinBatallaMs = 1500;     // para ver el resultado antes de cambiar de pantalla

    private final Map<AccionHeroe, Comando> comandosPorAccion = new EnumMap<>(AccionHeroe.class);

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controladorMaestro) {
        this.vista = vista;
        this.batalla = batalla;
        this.controladorMaestro = controladorMaestro;

        // Se crean los comandos y se asigna uno a cada botón
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();
        comandosPorAccion .put(AccionHeroe.ATACAR, new ComandoAtacar(heroe, enemigo));
        comandosPorAccion .put(AccionHeroe.HABILIDAD, new ComandoHabilidad(heroe, enemigo, 0));
        comandosPorAccion .put(AccionHeroe.DEFENDER, new ComandoDefender(heroe));

        vista.onAtacar(e -> manejarAccionHeroe(AccionHeroe.ATACAR));
        vista.onHabilidad(e -> manejarAccionHeroe(AccionHeroe.HABILIDAD));
        vista.onDefenderse(e -> manejarAccionHeroe(AccionHeroe.DEFENDER));
    }

    /** Muestra el estado inicial y arranca la pelea. */
    public void iniciarBatalla() {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();

        vista.prepararNuevaBatalla();
        // A la vista se le pasa el NOMBRE del lugar (ej. "BOSQUE"), no una ruta:
        // qué imagen le corresponde lo decide la vista.
        vista.mostrarFondoCombate(batalla.getLugar().name());
        vista.mostrarRetratos(heroe.getSpriteFrontal(), enemigo.getSpriteFrontal());
        vista.mostrarPersonajes(heroe.getNombre(), enemigo.getNombre());
        actualizarVidasEnVista();
    }

    /** Lo consulta el controlador maestro cuando termina la batalla. */
    public boolean heroeGano() {
        return batalla.resultadoBatalla();
    }

    // ---------- Turno del héroe ----------

    private void manejarAccionHeroe(AccionHeroe boton) {
        // Habilidad en enfriamiento: se avisa y NO se gasta el turno
        if (boton == AccionHeroe.HABILIDAD && habilidadEnEnfriamiento()) {
            Habilidad habilidad = batalla.getHeroe().getHabilidad(0);
            vista.mostrarEnfriamiento(habilidad.getNombreHabilidad(), habilidad.getCooldownActual());
            return;
        }

        // Se deshabilitan enseguida: un doble clic no puede resolverComando dos veces mientras dura la animación
        vista.setBotonesHabilitados(false);

        Heroe heroe = batalla.getHeroe();
        Comando comando = comandosPorAccion .get(boton);

        // Defenderse no tiene animación; estando aturdido tampoco (pierde el turno)
        if (boton != AccionHeroe.DEFENDER && heroe.puedeActuar()) {
            vista.animarAtaqueHeroe(heroe.getNombre(), () -> resolverComando(comando));
        } else {
            resolverComando(comando);
        }
    }

    private boolean habilidadEnEnfriamiento() {
        Heroe heroe = batalla.getHeroe();
        // si está aturdido no se chequea: pierde el turno igual
        return heroe.puedeActuar() && !heroe.puedeUsarHabilidad(0);
    }

    // ---------- Turno del enemigo ----------

    private void ejecutarTurnoEnemigo() {
        if (batalla.batallaTerminada() || batalla.getTurnoActual() != Batalla.Turno.ENEMIGO) {
            return;
        }

        Enemigo enemigo = batalla.getEnemigo();
        Comando comando = enemigo.elegirComando(batalla.getHeroe());

        vista.animarAtaqueEnemigo(enemigo.getNombre(), () -> resolverComando(comando));
    }

    // ---------- Flujo de la batalla ----------

    /** Ejecuta un comando en el modelo, muestra el resultado y pasa al siguiente turno. */
    private void resolverComando(Comando comando) {
        String resultado = batalla.ejecutar(comando);
        vista.efectoTemblor();
        vista.mostrarResultado(resultado);
        pasarAlSiguienteTurno();
    }

    /** Refleja el estado de la batalla y decide qué sigue. */
    private void pasarAlSiguienteTurno() {
        actualizarVidasEnVista();

        if (batalla.batallaTerminada()) {
            finalizarBatalla();
            return;
        }

        Entidad entidadEnTurno = batalla.entidadEnTurno();
        vista.mostrarTurno(entidadEnTurno.getNombre());

        if (entidadEnTurno == batalla.getEnemigo()) {
            vista.setBotonesHabilitados(false);
            ejecutarConRetraso(pausaTurnoEnemigoMs, () -> ejecutarTurnoEnemigo());
        } else {
            vista.setBotonesHabilitados(true);   // si está aturdido, al tocar un botón pierde el turno
        }
    }

    private void finalizarBatalla() {
        vista.setBotonesHabilitados(false);
        boolean gano = heroeGano();
        vista.mostrarResultado(gano);

        // Primero se ve caer al perdedor; después de una pausa se cambia de pantalla
        if (gano) {
            vista.animarDerrotaEnemigo(batalla.getEnemigo().getNombre(), () -> volverAlControladorMaestro());
        } else {
            vista.animarDerrotaHeroe(batalla.getHeroe().getNombre(), () -> volverAlControladorMaestro());
        }
    }

    private void volverAlControladorMaestro() {
        ejecutarConRetraso(pausaFinBatallaMs, () -> controladorMaestro.batallaTerminada());
    }

    // ---------- Auxiliares ----------

    private void actualizarVidasEnVista() {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();
        vista.actualizarEstadisticas(heroe.getNombre(), heroe.getVida(),
                                     enemigo.getNombre(), enemigo.getVida());
    }

    /** Ejecuta una acción una sola vez después de una pausa. */
    private void ejecutarConRetraso(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }
}
