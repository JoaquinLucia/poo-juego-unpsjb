package vista;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.net.URL;

public class PanelFondo extends JPanel {

    private final Image imagen;

    public PanelFondo(String rutaRecurso, LayoutManager layout) {
        super(layout);
        URL url = getClass().getResource(rutaRecurso);
        if (url == null) {
            System.err.println("No se encontró el fondo: " + rutaRecurso);
            imagen = null;
        } else {
            imagen = new ImageIcon(url).getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            // Estira la imagen a la ventana usando la aceleración nativa (CERO LAG)
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}