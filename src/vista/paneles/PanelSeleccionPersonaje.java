package vista.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * Pantalla de selección de héroe, con el mismo GIF animado de fondo que el menú.
 * Es un panel dentro de VentanaPrincipal, así los cambios de pantalla tienen fundido.
 */
public class PanelSeleccionPersonaje extends JPanel {

    private final Color colorSinFondo = new Color(20, 18, 17);   // si no se encuentra el GIF
    private final Image fondo = cargarFondo("/recursos/imagenes_menu/fondo.gif");

    private final PanelTitulo panelTitulo = new PanelTitulo();
    private final PanelPersonaje panelImagen = new PanelPersonaje();
    private final PanelDatosPersonaje panelDatos = new PanelDatosPersonaje();
    private final PanelControles panelControles = new PanelControles();

    public PanelSeleccionPersonaje() {
        super(new BorderLayout(15, 15));
        setBackground(colorSinFondo);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 30, 0));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        panelCentral.add(panelImagen);
        panelCentral.add(panelDatos);

        add(panelTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            // "this" como observer hace que el GIF siga animándose
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public PanelTitulo getPanelTitulo()         { return panelTitulo; }
    public PanelPersonaje getPanelImagen()      { return panelImagen; }
    public PanelDatosPersonaje getPanelDatos()  { return panelDatos; }
    public PanelControles getPanelControles()   { return panelControles; }

    // ---------- Auxiliares ----------


    private Image cargarFondo(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        return new ImageIcon(url).getImage();   // ImageIcon mantiene la animación del GIF
    }

}