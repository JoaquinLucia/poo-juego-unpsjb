package vista.paneles;
import java.awt.*;
import javax.swing.*;

public class PanelControles extends JPanel {

    private final Color FONDO = new Color(20, 18, 17);
    private final Color BOTON = new Color(40, 35, 30);
    private final Color DORADO = new Color(230, 210, 165);
    private final Color BORDE = new Color(135, 105, 58);

    private final JButton anterior;
    private final JButton siguiente;
    private final JButton elegir;
    private final JButton volver;

    public PanelControles() {

        setLayout(new BorderLayout(20, 10));
        setBackground(FONDO);

        setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));

        // Navegación entre personajes
        JPanel panelNavegacion =new JPanel(new GridLayout(1, 2, 20, 0));

        panelNavegacion.setBackground(FONDO);

        anterior = new JButton("◀ ANTERIOR");
        siguiente = new JButton("SIGUIENTE ▶");

        estilizarBoton(anterior);
        estilizarBoton(siguiente);

        panelNavegacion.add(anterior);
        panelNavegacion.add(siguiente);

        add(panelNavegacion, BorderLayout.CENTER);

        // Acciones principales
        JPanel panelAcciones =new JPanel(new GridLayout(1, 2, 20, 0));

        panelAcciones.setBackground(FONDO);

        elegir = new JButton("ELEGIR HÉROE");
        volver = new JButton("VOLVER");

        estilizarBoton(elegir);
        estilizarBoton(volver);

        panelAcciones.add(elegir);
        panelAcciones.add(volver);

        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton boton) {

        boton.setBackground(BOTON);
        boton.setForeground(DORADO);
        boton.setFont(new Font("Serif", Font.BOLD, 18));

        boton.setFocusPainted(false);
        boton.setOpaque(true);

        boton.setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        );
    }

    public JButton getAnterior() {
        return anterior;
    }

    public JButton getSiguiente() {
        return siguiente;
    }

    public JButton getElegir() {
        return elegir;
    }

    public JButton getVolver() {
        return volver;
    }
}

