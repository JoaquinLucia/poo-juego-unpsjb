//VERSION 3 .1 nevo
package vista;
 
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Pantalla del recorrido de la mazmorra:
 *   - arriba, la imagen del lugar,
 *   - en el medio, las tres flechas (caminos) o el botón para enfrentar al jefe,
 *   - abajo, el log.
 * No conoce el modelo: recibe textos y rutas del controlador.
 */
public class VistaCaminoMazmorra extends JPanel {

    private final Color fondoOscuro = new Color(15, 14, 16);
    private final Color madera = new Color(40, 35, 30);
    private final Color dorado = new Color(230, 210, 165);
    private final Color borde = new Color(135, 105, 58);

    // Alto con el que se dibujan las flechas (el ancho se ajusta solo)
    private final int altoFlechas = 110;

    // Si una imagen no se encuentra, el botón muestra el texto en su lugar
    private final JButton izquierda = crearFlecha("/recursos/mazmorra/flecha_izquierda.png", "Izquierda");
    private final JButton frente = crearFlecha("/recursos/mazmorra/flecha_frente.png", "Frente");
    private final JButton derecha = crearFlecha("/recursos/mazmorra/flecha_derecha.png", "Derecha");
    private final JButton enfrentarJefe = crearBotonJefe("Enfrentar al jefe");

    // El medio tiene dos "cartas": las flechas o el botón del jefe
    private final CardLayout cartasMedio = new CardLayout();
    private final JPanel panelMedio = new JPanel(cartasMedio);

    private final JTextArea txtLog = new JTextArea(4, 40);
    private Image fondo;

    public VistaCaminoMazmorra() {
        super(new BorderLayout());
        setBackground(fondoOscuro);

        // ---- Panel superior: la imagen del lugar ----
        JPanel panelSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);   // "this" anima el GIF
                }
            }
        };
        panelSuperior.setBackground(fondoOscuro);
        add(panelSuperior, BorderLayout.CENTER);

        // ---- Medio: flechas o botón del jefe ----
        JPanel panelFlechas = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 10));
        panelFlechas.setBackground(fondoOscuro);
        panelFlechas.add(izquierda);
        panelFlechas.add(frente);
        panelFlechas.add(derecha);

        JPanel panelJefe = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 35));
        panelJefe.setBackground(fondoOscuro);
        panelJefe.add(enfrentarJefe);

        panelMedio.add(panelFlechas, "flechas");
        panelMedio.add(panelJefe, "jefe");

        // ---- Panel inferior: el log ----
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        txtLog.setFont(new Font("Serif", Font.BOLD, 20));
        txtLog.setBackground(fondoOscuro);
        txtLog.setForeground(new Color(220, 190, 120));
        txtLog.setMargin(new Insets(10, 15, 10, 15));
        JScrollPane panelInferior = new JScrollPane(txtLog);
        panelInferior.setBorder(BorderFactory.createLineBorder(borde, 3));

        // Medio y log juntos debajo de la imagen
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(panelMedio, BorderLayout.NORTH);
        abajo.add(panelInferior, BorderLayout.SOUTH);
        add(abajo, BorderLayout.SOUTH);
    }

    // ---------- Lo que usa el controlador ----------

    public void mostrarFondo(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            fondo = null;
        } else {
            fondo = new ImageIcon(url).getImage();   // ImageIcon mantiene la animación del GIF
        }
        repaint();
    }

    /** Muestra las tres flechas para elegir camino. */
    public void mostrarFlechas() {
        cartasMedio.show(panelMedio, "flechas");
    }

    /** Muestra el botón único para la pelea final. */
    public void mostrarBotonJefe() {
        cartasMedio.show(panelMedio, "jefe");
    }

    public void mostrarEnLog(String texto) {
        txtLog.setText(texto);
    }

    // ---------- Enganches de botones para el controlador ----------

    public void onIzquierda(ActionListener listener) {
        izquierda.addActionListener(listener);
    }

    public void onFrente(ActionListener listener) {
        frente.addActionListener(listener);
    }

    public void onDerecha(ActionListener listener) {
        derecha.addActionListener(listener);
    }

    public void onEnfrentarJefe(ActionListener listener) {
        enfrentarJefe.addActionListener(listener);
    }

    // ---------- Auxiliares ----------

    /** Botón que se ve solo como la imagen de la flecha: se aclara con el mouse y se oscurece al hacer clic. */
    private JButton crearFlecha(String ruta, String textoAlternativo) {
        JButton boton = new JButton();
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setRolloverEnabled(true); // Asegura que detecte el hover y los clics correctamente en componentes transparentes
        boton.setBorder(BorderFactory.createEmptyBorder());
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        BufferedImage flecha = cargarImagen(ruta);
        if (flecha != null) {
            BufferedImage escalada = escalarAlto(flecha, altoFlechas);
            boton.setIcon(new ImageIcon(escalada));
            boton.setRolloverIcon(new ImageIcon(ajustarBrillo(escalada, 1.3f)));  // mouse encima
            boton.setPressedIcon(new ImageIcon(ajustarBrillo(escalada, 0.8f)));   // al hacer clic
        } else {
            boton.setText(textoAlternativo);
            boton.setForeground(dorado);
            boton.setFont(new Font("Serif", Font.BOLD, 22));
        }
        return boton;
    }

    /** Botón de madera con letras doradas, como los del resto del juego. */
    private JButton crearBotonJefe(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(madera);
        boton.setForeground(dorado);
        boton.setFont(new Font("Serif", Font.BOLD, 26));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 3),
                BorderFactory.createEmptyBorder(14, 40, 14, 40)));
        return boton;
    }

    /** Lee una imagen en formato ARGB, para poder aclararla y mantener la transparencia. */
    private BufferedImage cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        try {
            BufferedImage leida = ImageIO.read(url);
            BufferedImage argb = new BufferedImage(leida.getWidth(), leida.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = argb.createGraphics();
            g.drawImage(leida, 0, 0, null);
            g.dispose();
            return argb;
        } catch (IOException e) {
            System.err.println("No se pudo leer la imagen: " + ruta);
            return null;
        }
    }

    /** Escala la imagen a un alto dado, manteniendo la proporción. */
    private BufferedImage escalarAlto(BufferedImage original, int alto) {
        int ancho = Math.max(1, original.getWidth() * alto / original.getHeight());
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, ancho, alto, null);
        g.dispose();
        return resultado;
    }

    /** factor > 1 aclara, factor < 1 oscurece (la transparencia no se toca). */
    private BufferedImage ajustarBrillo(BufferedImage imagen, float factor) {
        RescaleOp op = new RescaleOp(new float[]{factor, factor, factor, 1f},
                                     new float[]{0f, 0f, 0f, 0f}, null);
        return op.filter(imagen, null);
    }
}