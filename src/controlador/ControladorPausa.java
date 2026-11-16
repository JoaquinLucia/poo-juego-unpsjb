package controlador;

import vista.paneles.PanelPausa;

/*
 *
 * Menú de pausa. Solo traduce los botones en avisos al mediador:
 * qué pasa con la partida lo decide el ControladorMaestro.
 */
public class ControladorPausa{

    private final PanelPausa vista;
    private final ControladorMaestro controlador;

    public ControladorPausa(PanelPausa vista, ControladorMaestro controlador) {
        
        this.vista = vista;
        this.controlador = controlador;
        vista.onReanudar(e -> {
            vista.cerrar();
            controlador.reanudarPartida();
        });
        vista.onVolverAlTitulo(e -> {
            vista.cerrar();
            controlador.volverAlMenu();
        });
        vista.onSalir(e -> controlador.salirDelJuego());
    }

    /** Abre el menú de pausa. Se bloquea hasta que el jugador elige una opción. */
    public void mostrar() {
        vista.mostrar();
    }

}
