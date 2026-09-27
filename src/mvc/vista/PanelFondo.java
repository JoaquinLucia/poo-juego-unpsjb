package mvc.vista;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.net.URL;

// Panel que dibuja una imagen de fondo (GIF animado incluido) cubriendo todo su tamaño.
public class PanelFondo extends JPanel {

    private final Image imagen;

    public PanelFondo(String rutaRecurso, LayoutManager layout) {
        super(layout);
        URL url = getClass().getResource(rutaRecurso);
        if (url == null) {
            System.err.println("No se encontró el fondo: " + rutaRecurso);
            imagen = null;
        } else {
            // Se carga con ImageIcon (no con ImageIO) para que el GIF conserve la animación
            imagen = new ImageIcon(url).getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen == null) {
            return;
        }

        int anchoImagen = imagen.getWidth(this);
        int altoImagen = imagen.getHeight(this);
        if (anchoImagen <= 0 || altoImagen <= 0) {
            return; // todavía se está cargando
        }

        // Modo "cover": escala manteniendo la proporción y recorta lo que sobra,
        // así el fondo no se deforma al pasar a pantalla completa
        double escala = Math.max(
                getWidth() / (double) anchoImagen,
                getHeight() / (double) altoImagen);
        int ancho = (int) Math.ceil(anchoImagen * escala);
        int alto = (int) Math.ceil(altoImagen * escala);
        int x = (getWidth() - ancho) / 2;
        int y = (getHeight() - alto) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // Pasar "this" como observador hace que el panel se repinte en cada cuadro del GIF
        g2.drawImage(imagen, x, y, ancho, alto, this);
        g2.dispose();
    }
}