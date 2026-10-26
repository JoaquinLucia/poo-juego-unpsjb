package controlador;

import javax.swing.Timer;
import modelo.Batalla;
import modelo.Entidad;
import modelo.ObservadorBatalla;
import vista.VistaBatalla;

/*
 * Controla la pantalla de combate. Es un Observador de Batalla: no pregunta
 * "¿terminó?" ni "¿está aturdido?", Batalla le avisa y él lo refleja en la vista.
 * Los botones se traducen en pedidos a Batalla.
 */
public class ControladorBatalla implements ObservadorBatalla {

    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro controladorMaestro;

    private final int pausaTurnoEnemigoMs  = 2500;    // para que se vea qué hizo cada uno
    private final int pausaFinBatallaMs  = 1500;     // para que se vea el resultado antes de cambiar de pantalla

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controladorMaestro) {
        this.vista = vista;
        this.batalla = batalla;
        this.controladorMaestro = controladorMaestro;

        batalla.suscribir(this);
        vista.onAtacar(e -> batalla.ejecutarAtaqueHeroe());
        vista.onHabilidad(e -> usarHabilidadDelHeroe(0));
    }

    /** Muestra el estado inicial y arranca la pelea. */
    public void iniciarBatalla() {
        vista.mostrarFondo(batalla.getLugar().getRutaCombate());
        vista.mostrarRetratos(batalla.getHeroe().getSpriteFrontal(),batalla.getEnemigo().getSpriteFrontal());
        actualizarVidasEnVista();
        batalla.comenzar();   // llega turnoCambiado con el primer turno
    }

    /** Lo consulta el controlador maestro después de recibir BATALLA_TERMINADA. */
    public boolean heroeGano() {
        return batalla.resultadoBatalla();
    }

    private void usarHabilidadDelHeroe(int indiceHabilidad) {
        if (!batalla.ejecutarHabilidadHeroe(indiceHabilidad)) {
            vista.mostrarEnfriamiento(getNombreHabilidad(indiceHabilidad), getTurnosEnfriamiento(indiceHabilidad));
        }
    }

    // ---------- Avisos de la batalla ----------

    @Override
    public void ataqueRealizado(Entidad atacante, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarAtaque(atacante.getNombre(), objetivo.getNombre(), danio);
        actualizarVidasEnVista();
    }

    @Override
    public void habilidadUsada(Entidad usuario, String habilidad, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarHabilidad(usuario.getNombre(), habilidad, objetivo.getNombre(), danio);
        actualizarVidasEnVista();
    }

    @Override
    public void turnoPerdido(Entidad entidadAturdida) {
        vista.mostrarAturdido(entidadAturdida.getNombre());
    }

    @Override
    public void turnoCambiado(Entidad entidadEnTurno) {
        actualizarVidasEnVista();   // un veneno pudo bajar vida al cerrar el turno anterior
        vista.mostrarTurno(entidadEnTurno.getNombre());
        if (entidadEnTurno == batalla.getEnemigo()) {
            vista.setBotonesHabilitados(false);
            ejecutarConRetraso(pausaTurnoEnemigoMs , () -> batalla.ejecutarTurnoEnemigo());
        } else {
            vista.setBotonesHabilitados(true);   // si está aturdido, al tocar un botón pierde el turno
        }
    }

    @Override
    public void finDeBatalla(boolean heroeGano) {
        actualizarVidasEnVista();
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(heroeGano);
        ejecutarConRetraso(pausaFinBatallaMs , () -> {
            batalla.desuscribir(this);   // ya no necesita avisos de esta batalla
            controladorMaestro.batallaTerminada();
        });
    }

    // ---------- Auxiliares ----------

    private void actualizarVidasEnVista() {
        vista.actualizarEstadisticas(
                batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
                batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida());
    }

    /** Ejecuta una acción una sola vez después de una pausa. */
    private void ejecutarConRetraso(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }

    private String getNombreHabilidad(int indiceHabilidad) {
        return batalla.getHeroe().getHabilidad(indiceHabilidad).getNombreHabilidad();
    }

    private int getTurnosEnfriamiento(int indiceHabilidad) {
        return batalla.getHeroe().getHabilidad(indiceHabilidad).getCooldownActual();
    }
}