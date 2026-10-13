package vista;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import modelo.Arquetipo;

/**
 * Contenedor visual para la pantalla de selección de personaje.
 */
public class VistaSeleccion extends JPanel {
    private final PanelSeleccionPersonaje panelSeleccion;

    public VistaSeleccion() {
        setLayout(new BorderLayout());
        panelSeleccion = new PanelSeleccionPersonaje();
        add(panelSeleccion, BorderLayout.CENTER);
    }

    public PanelSeleccionPersonaje getPanelSeleccion() {
        return panelSeleccion;
    }

    public void mostrarPersonaje(Arquetipo arquetipo, boolean hayVarios) {
        panelSeleccion.mostrarPersonaje(arquetipo, hayVarios);
    }
}
