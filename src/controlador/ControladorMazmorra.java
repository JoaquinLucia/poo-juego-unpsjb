package controlador;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

import modelo.Camino;
import modelo.Escenario;
import vista.VistaMazmorra;

/**
 * Pantalla de exploración. Muestra el escenario que le indica el maestro
 * y, cuando el jugador elige un camino, le avisa al maestro.
 * No sabe qué pasa después (eso lo decide el ControladorMaestro).
 */
public class ControladorMazmorra {
    private final VistaMazmorra vista;
    private final ControladorMaestro maestro;

    public ControladorMazmorra(VistaMazmorra vista, ControladorMaestro maestro) {
        this.vista = vista;
        this.maestro = maestro;
    }

    /** Muestra un escenario. mensajePrevio se antepone en el log (puede ser vacío). */
    public void mostrar(Escenario escenario, String mensajePrevio) {
        vista.limpiarCarteles();
        vista.cambiarFondo(escenario.getRutaImagen());

        for (Camino camino : escenario.getCaminos()) {
            JButton cartel = vista.agregarCartel(camino.getTextoCartel(), camino.getRutaIcono(),
                    camino.getPosX(), camino.getPosY());

            // Hover: muestra la descripción del camino en el log
            cartel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    vista.mostrarEnLog(camino.getDescripcion());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    vista.mostrarEnLog(escenario.getMensajeInicial());
                }
            });

            // Click: se elige el camino y el maestro pasa al combate
            cartel.addActionListener(e -> maestro.caminoElegido(camino));
        }

        String texto = mensajePrevio.isEmpty()
                ? escenario.getMensajeInicial()
                : mensajePrevio + "\n" + escenario.getMensajeInicial();
        vista.mostrarEnLog(texto);
        vista.mostrar();
    }

    public void ocultar() {
        vista.ocultar();
    }
}
