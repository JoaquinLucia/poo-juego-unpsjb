package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import modelo.Arquetipo;
import modelo.ModeloMenu;
import vista.PanelSeleccionPersonaje;
import vista.PanelSeleccionPersonaje.Zona;

/**
 * Controles de la pantalla de selección de personaje.
 *
 * Escucha el teclado y el mouse sobre el panel, actualiza el modelo y le dice
 * a la vista qué mostrar. La vista no toma decisiones: solo dibuja e informa
 * qué hay bajo el puntero.
 *
 * Teclas: Enter / Espacio comenzar, Esc volver, ← → (o A / D) cambiar de personaje.
 */
public class ControladorSeleccion {

    private final ModeloMenu modelo;
    private final PanelSeleccionPersonaje panel;
    private final Runnable alVolver;
    private final Consumer<Arquetipo> alConfirmar;

    public ControladorSeleccion(ModeloMenu modelo, PanelSeleccionPersonaje panel,
                                Runnable alVolver, Consumer<Arquetipo> alConfirmar) {
        this.modelo = modelo;
        this.panel = panel;
        this.alVolver = alVolver;
        this.alConfirmar = alConfirmar;

        configurarTeclado();
        configurarMouse();
        actualizarVista();
    }

    /** Se llama cada vez que se entra a la pantalla de selección. */
    public void entrar() {
        modelo.reiniciarSeleccion();
        panel.setHover(Zona.NINGUNA);
        actualizarVista();
    }

    // ---------- Acciones ----------

    private void mover(int direccion) {
        if (modelo.getCantidadPersonajes() > 1) {
            modelo.moverSeleccion(direccion);
            actualizarVista();
        }
    }

    private void confirmar() {
        Arquetipo elegido = modelo.getPersonajeActual();
        modelo.setPersonajeElegido(elegido);
        alConfirmar.accept(elegido);
    }

    private void volver() {
        alVolver.run();
    }

    private void actualizarVista() {
        panel.mostrarPersonaje(modelo.getPersonajeActual(), modelo.getCantidadPersonajes() > 1);
    }

    // ---------- Teclado ----------

    private void configurarTeclado() {
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();
        vincular(im, am, "seleccionComenzar", this::confirmar, KeyEvent.VK_ENTER, KeyEvent.VK_SPACE);
        vincular(im, am, "seleccionVolver", this::volver, KeyEvent.VK_ESCAPE);
        vincular(im, am, "seleccionIzquierda", () -> mover(-1), KeyEvent.VK_LEFT, KeyEvent.VK_A);
        vincular(im, am, "seleccionDerecha", () -> mover(+1), KeyEvent.VK_RIGHT, KeyEvent.VK_D);
    }

    private void vincular(InputMap im, ActionMap am, String nombre, Runnable accion, int... teclas) {
        for (int tecla : teclas) {
            im.put(KeyStroke.getKeyStroke(tecla, 0), nombre);
        }
        am.put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Con CardLayout el panel sigue en la ventana aunque esté oculto
                if (panel.isShowing()) {
                    accion.run();
                }
            }

            // Deshabilitada cuando la selección no se ve: así la tecla (por ejemplo Esc)
            // queda libre para otras pantallas, como el menú de pausa
            @Override
            public boolean isEnabled() {
                return panel.isShowing();
            }
        });
    }

    // ---------- Mouse ----------

    private void configurarMouse() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                panel.setHover(panel.zonaEn(e.getPoint()));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (panel.zonaEn(e.getPoint())) {
                    case COMENZAR:   confirmar(); break;
                    case VOLVER:     volver();    break;
                    case FLECHA_IZQ: mover(-1);   break;
                    case FLECHA_DER: mover(+1);   break;
                    default:         break;
                }
            }
        };
        panel.addMouseListener(mouse);
        panel.addMouseMotionListener(mouse);
    }
}
