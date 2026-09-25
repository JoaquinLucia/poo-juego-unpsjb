package Vista;
//iamgenes 
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class PanelBatalla extends JPanel {
    private final Image imagen;
    public PanelBatalla() {
        imagen = new ImageIcon("src/recursos/pantalla_de_combate.jpg").getImage();
    
}
    @Override
    protected void paintComponent(Graphics g) { //Graphics es la herramienta que Swing nos da para dibujar
        super.paintComponent(g);

        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);// getWidth(), getHeight() hacen que la imagen ocupe todo el panel.
    }
}
