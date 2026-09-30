package controlador;

import vista.VistaPausa;

/**
 * Menú de pausa. Solo traduce los botones en avisos al maestro:
 * qué pasa con la partida lo decide el ControladorMaestro.
 */
public class ControladorPausa {
    private final VistaPausa vista;

    public ControladorPausa(VistaPausa vista, ControladorMaestro maestro) {
        this.vista = vista;

        vista.onReanudar(e -> {
            vista.cerrar();
            maestro.reanudarJuego();
        });
        vista.onVolverAlTitulo(e -> {
            vista.cerrar();
            maestro.volverAlTitulo();
        });
        vista.onSalir(e -> maestro.salirDelJuego());
    }

    /** Abre el menú de pausa. Se bloquea hasta que el jugador elige una opción. */
    public void mostrar() {
        vista.mostrar();
    }
}
