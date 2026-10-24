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
    private final ControladorMaestro controlador;

    private final int pausaEnemigoMs = 2500;    // para que se vea qué hizo cada uno
    private final int pausaFinalMs = 1500;     // para que se vea el resultado antes de cambiar de pantalla

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controlador) {
        this.vista = vista;
        this.batalla = batalla;
        this.controlador = controlador;

        batalla.suscribir(this);
        vista.onAtacar(e -> batalla.ejecutarAtaqueHeroe());
        vista.onHabilidad(e -> usarHabilidad(0));
    }

    /** Muestra el estado inicial y arranca la pelea. */
    public void iniciar() {
        vista.mostrarFondo(batalla.getLugar().getRutaCombate());
        vista.mostrarRetratos(batalla.getHeroe().getSpriteFrontal(),
                              batalla.getEnemigo().getSpriteFrontal());
        refrescarVidas();
        batalla.comenzar();   // llega turnoCambiado con el primer turno
    }

    /** Lo consulta el controlador maestro después de recibir BATALLA_TERMINADA. */
    public boolean gano() {
        return batalla.resultadoBatalla();
    }

    private void usarHabilidad(int indice) {
        if (!batalla.ejecutarHabilidadHeroe(indice)) {
            vista.mostrarEnfriamiento(getNombreHabilidad(indice), getTurnosEnfriamiento(indice));
        }
    }

    // ---------- Avisos de la batalla ----------

    @Override
    public void ataqueRealizado(Entidad atacante, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarAtaque(atacante.getNombre(), objetivo.getNombre(), danio);
        refrescarVidas();
    }

    @Override
    public void habilidadUsada(Entidad usuario, String habilidad, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarHabilidad(usuario.getNombre(), habilidad, objetivo.getNombre(), danio);
        refrescarVidas();
    }

    @Override
    public void turnoPerdido(Entidad aturdida) {
        vista.mostrarAturdido(aturdida.getNombre());
    }

    @Override
    public void turnoCambiado(Entidad aQuienLeToca) {
        refrescarVidas();   // un veneno pudo bajar vida al cerrar el turno anterior
        vista.mostrarTurno(aQuienLeToca.getNombre());
        if (aQuienLeToca == batalla.getEnemigo()) {
            vista.setBotonesHabilitados(false);
            despuesDe(pausaEnemigoMs, () -> batalla.ejecutarTurnoEnemigo());
        } else {
            vista.setBotonesHabilitados(true);   // si está aturdido, al tocar un botón pierde el turno
        }
    }

    @Override
    public void finDeBatalla(boolean victoria) {
        refrescarVidas();
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(victoria);
        despuesDe(pausaFinalMs, () -> {
            batalla.desuscribir(this);   // ya no necesita avisos de esta batalla
            controlador.batallaTerminada();
        });
    }

    // ---------- Auxiliares ----------

    private void refrescarVidas() {
        vista.actualizarEstadisticas(
                batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
                batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida());
    }

    /** Ejecuta una acción una sola vez después de una pausa. */
    private void despuesDe(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }

    public String getNombreHabilidad(int indice) {
        return batalla.getHeroe().getHabilidad(indice).getNombreHabilidad();
    }

    public int getTurnosEnfriamiento(int indice) {
        return batalla.getHeroe().getHabilidad(indice).getCooldownActual();
    }
}