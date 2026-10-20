package controlador;

import javax.swing.Timer;
import modelo.Batalla;
import vista.VistaBatalla;

/*
 * Controla la pantalla de combate. Las reglas (turnos, estados, cooldowns)
 * viven en modelo.Batalla; este controlador traduce los botones en pedidos
 * a Batalla y refleja en la vista lo que pasó.
 * Cuando la pelea termina, se lo avisa al mediador; el mediador pregunta gano().
 */
public class ControladorBatalla {

    private final VistaBatalla vista;
    private final Batalla batalla;
    
    private final ControladorMaestro controlador;

    private final int pausaEnemigoMs = 800;    // para que se vea qué hizo cada uno
    private final int pausaFinalMs = 1500;     // para que se vea el resultado antes de cambiar de pantalla
    private boolean terminada = false;

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controlador) {
        this.vista = vista;
        this.batalla = batalla;
        this.controlador = controlador;
        vista.onAtacar(e -> atacar());
        vista.onHabilidad(e -> usarHabilidad(0));
    }

    /** Muestra el estado inicial de la pelea. */
    public void iniciar() {
            vista.mostrarRetratos(batalla.getHeroe().getSpriteFrontal(),batalla.getEnemigo().getSpriteFrontal());
        actualizar();
    }

    /** Lo consulta el mediador después de recibir BATALLA_TERMINADA. */
    public boolean gano() {
        return batalla.resultadoBatalla();
    }

    // ---------- Acciones del héroe ----------
    private boolean puedeActuar() { // <-- nuevo
        return batalla.getTurnoActual() == Batalla.Turno.HEROE && !batalla.batallaTerminada();
    }

    private void atacar() {
        if (!puedeActuar()) return;

        boolean aturdido = batalla.heroeAturdido();   // se pregunta antes: después ya es turno del enemigo
        batalla.ejecutarAtaqueHeroe();
        if (!aturdido) {
            vista.efectoTemblor();
            vista.mostrarAtaque(getNombreHeroe(),getNombreEnemigo(),  // <-- nuevo
            batalla.getUltimoDanio());
        }
        actualizar();
    }
    
    private void usarHabilidad(int indice) {
        if (!puedeActuar()) return;

        boolean aturdido = batalla.heroeAturdido();
        if (!aturdido && !batalla.getHeroe().puedeUsarHabilidad(indice)) {
            return;   // habilidad en enfriamiento: no se gasta el turno
        }
        batalla.ejecutarHabilidadHeroe(indice);
        if (!aturdido) {
            vista.efectoTemblor();
            vista.mostrarHabilidad(getNombreHeroe(), getNombreHabilidad(indice),getNombreEnemigo(), batalla.getUltimoDanio());// <-- nuevo
        }
        actualizar();
    }

    // ---------- Pantalla  ----------
     /** Refleja el estado de la batalla y decide qué sigue. */
    private void actualizar() {
        vista.actualizarEstadisticas(
                getNombreHeroe(), batalla.getHeroe().getVida(),// <-- nuevo
                getNombreEnemigo(), batalla.getEnemigo().getVida());// <-- nuevo
 
        if (batalla.batallaTerminada()) {
            terminar();
        } else if (batalla.getTurnoActual() == Batalla.Turno.ENEMIGO) {
            vista.mostrarTurno(getNombreEnemigo());   // <-- nuevo
            vista.setBotonesHabilitados(false);
            programarTurnoEnemigo();
        } else {
            vista.mostrarTurno(getNombreHeroe());   // <-- nuevo
            vista.setBotonesHabilitados(true);   // si está aturdido, al tocar un botón pierde el turno
        }
    }

    /** El enemigo juega después de una pausa (una sola vez por turno). */
    /** El enemigo juega después de una pausa. */
    private void programarTurnoEnemigo() {
        Timer espera = new Timer(pausaEnemigoMs, e -> {
            vista.efectoTemblor();
            batalla.ejecutarTurnoEnemigo();
            actualizar();
        });

        espera.setRepeats(false);
        espera.start();
    }

    /** Muestra el resultado y, tras una pausa, le avisa al mediador (una sola vez). */
    private void terminar() {
        if (terminada) {return;}
        terminada = true;
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(gano());   // el texto ("¡Victoria!" / "Derrota...") lo arma la vista
 
        despuesDe(pausaFinalMs, () -> controlador.batallaTerminada());
    }
    /** Ejecuta una acción una sola vez después de una pausa. */
    private void despuesDe(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }
    ///////// GET  
    /// get para ller mas facil 
    public String getNombreHeroe(){
        return batalla.getHeroe().getNombre();
    }
    public String getNombreEnemigo(){
        return batalla.getEnemigo().getNombre();
    }
    public String getNombreHabilidad(int indice){
        return batalla.getHeroe().getHabilidad(indice).getNombreHabilidad();
    }
}
