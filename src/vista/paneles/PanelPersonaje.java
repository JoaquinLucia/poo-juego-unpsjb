package vista.paneles;

import java.awt.*;
import javax.swing.*;

public class PanelPersonaje extends JPanel {

    private static final Color PANEL = new Color(30, 27, 24);
    private static final Color BORDE = new Color(135, 105, 58);
    private static final Color TEXTO_SECUNDARIO = new Color(190, 175, 145);

    private final JLabel imagenPersonaje;

    public PanelPersonaje() {

        setLayout(new BorderLayout());
        setBackground(PANEL);

        setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE, 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
            )
        );

        imagenPersonaje = new JLabel("IMAGEN DEL PERSONAJE",SwingConstants.CENTER);

        imagenPersonaje.setForeground(TEXTO_SECUNDARIO);
        imagenPersonaje.setFont(new Font("Serif", Font.BOLD, 20));

        add(imagenPersonaje, BorderLayout.CENTER);
    }
    public void mostrarImagen(String ruta) {
    java.net.URL url = (ruta == null) ? null : getClass().getResource(ruta);

    if (url == null) {                       // no encontró el archivo
        imagenPersonaje.setIcon(null);
        imagenPersonaje.setText("SIN IMAGEN");
        return;
    }

    Image img = new ImageIcon(url).getImage()
            .getScaledInstance(-1, 450, Image.SCALE_SMOOTH); // alto 450, ancho proporcional
    imagenPersonaje.setText("");
    imagenPersonaje.setIcon(new ImageIcon(img));
}

    public JLabel getImagenPersonaje() {
        return imagenPersonaje;
    }
}