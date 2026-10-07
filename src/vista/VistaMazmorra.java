package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Pantalla de exploración: imagen de fondo, flechas encima y log abajo.
 * Es un panel dentro de VentanaPrincipal (ya no es una ventana propia).
 * Solo muestra lo que le pide el controlador; no tiene lógica del juego.
 */
public class VistaMazmorra extends JPanel {
    // Cartel de texto (se usa cuando el camino no tiene imagen, por ejemplo el jefe)
    private static final int ANCHO_CARTEL = 130;
    private static final int ALTO_CARTEL = 38;

    // Con el panel de 1000 px de alto, las flechas se dibujan al 75% de su tamaño.
    // Si las querés más grandes o más chicas, cambiá este número.
    private static final double TAMANO_FLECHAS = 0.75;

    private Image fondo;
    private final JPanel panelFondo;
    private final JTextArea txtLog = new JTextArea(4, 40);

    private final List<JButton> carteles = new ArrayList<>();
    private final List<double[]> posiciones = new ArrayList<>();

    public VistaMazmorra() {
        super(new BorderLayout());

        // Centro: panel que dibuja la imagen y ubica las flechas encima
        panelFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    // Activa el suavizado de alta calidad para estirar la imagen del camino
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    // "this" como observer hace que el GIF animado se siga redibujando
                    g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                }
            }

            @Override
            public void doLayout() {
                // Se llama cada vez que cambia el tamaño: reubica y reescala los carteles
                double escala = TAMANO_FLECHAS * getHeight() / 1000.0;
                for (int i = 0; i < carteles.size(); i++) {
                    JButton cartel = carteles.get(i);
                    Dimension tam;
                    if (cartel instanceof BotonImagen) {
                        tam = ((BotonImagen) cartel).escalar(escala);
                    } else {
                        tam = new Dimension(ANCHO_CARTEL, ALTO_CARTEL);
                    }
                    double[] p = posiciones.get(i);
                    int x = (int) (p[0] * getWidth()) - tam.width / 2;
                    int y = (int) (p[1] * getHeight()) - tam.height / 2;
                    cartel.setBounds(x, y, tam.width, tam.height);
                }
            }
        };
        panelFondo.setBackground(new Color(15, 14, 16));
        add(panelFondo, BorderLayout.CENTER);

        // Abajo: log de solo lectura (mismos colores que el panel de batalla)
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        txtLog.setFont(new Font("Serif", Font.BOLD, 20));
        txtLog.setBackground(new Color(15, 14, 16));
        txtLog.setForeground(new Color(220, 190, 120));
        txtLog.setMargin(new Insets(10, 15, 10, 15));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(135, 105, 58), 3));
        add(scrollLog, BorderLayout.SOUTH);

    }

    // ---------- Lo que usa el controlador ----------

    /**
     * Crea un cartel centrado en (posX, posY), valores de 0.0 a 1.0.
     * Si rutaIcono tiene una imagen, el cartel es esa imagen (las flechas);
     * si no, es un cartel con el texto.
     */
    public JButton agregarCartel(String texto, String rutaIcono, double posX, double posY) {
        BufferedImage icono = rutaIcono == null ? null : cargarImagenPNG(rutaIcono);
        JButton cartel = icono != null ? new BotonImagen(icono, texto) : crearCartelTexto(texto);

        carteles.add(cartel);
        posiciones.add(new double[]{posX, posY});
        panelFondo.add(cartel);
        panelFondo.revalidate();
        return cartel;
    }

    public void limpiarCarteles() {
        for (JButton cartel : carteles) {
            panelFondo.remove(cartel);
        }
        carteles.clear();
        posiciones.clear();
        panelFondo.revalidate();
        panelFondo.repaint();
    }

    public void cambiarFondo(String ruta) {
        URL url = buscarRecurso(ruta);
        fondo = url == null ? null : new ImageIcon(url).getImage(); // ImageIcon mantiene la animación del GIF
        panelFondo.repaint();
    }

    public void mostrarEnLog(String texto) {
        txtLog.setText(texto);
    }

    // ---------- Botón hecho con una imagen (las flechas) ----------

    /**
     * Botón que se dibuja solo con su imagen. Se aclara con el mouse encima,
     * se oscurece al hacer clic, y solo responde sobre la parte visible de la
     * flecha (no sobre las esquinas transparentes).
     */
    private static class BotonImagen extends JButton {
        private final BufferedImage original;
        private BufferedImage actual;

        BotonImagen(BufferedImage original, String textoAlternativo) {
            this.original = original;
            getAccessibleContext().setAccessibleName(textoAlternativo);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        /** Reescala la imagen (solo si cambió el tamaño) y devuelve el tamaño nuevo. */
        Dimension escalar(double escala) {
            int ancho = Math.max(1, (int) Math.round(original.getWidth() * escala));
            int alto = Math.max(1, (int) Math.round(original.getHeight() * escala));
            if (actual == null || actual.getWidth() != ancho || actual.getHeight() != alto) {
                actual = redimensionar(original, ancho, alto);
                setIcon(new ImageIcon(actual));
                setRolloverIcon(new ImageIcon(ajustarBrillo(actual, 1.3f))); // mouse encima
                setPressedIcon(new ImageIcon(ajustarBrillo(actual, 0.8f)));  // al hacer clic
            }
            return new Dimension(ancho, alto);
        }

        @Override
        public boolean contains(int x, int y) {
            // Solo cuenta como "encima" si el píxel de la flecha no es transparente
            if (actual == null || x < 0 || y < 0 || x >= actual.getWidth() || y >= actual.getHeight()) {
                return false;
            }
            int alfa = (actual.getRGB(x, y) >>> 24);
            return alfa > 120;
        }
    }

    // ---------- Auxiliares ----------

    private JButton crearCartelTexto(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean resaltado = getModel().isRollover();
                g2.setColor(new Color(40, 25, 15, resaltado ? 230 : 170));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(resaltado ? new Color(255, 215, 120) : new Color(160, 120, 60));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Serif", Font.BOLD, 18));
        boton.setForeground(new Color(235, 200, 120));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

private static BufferedImage redimensionar(BufferedImage original, int ancho, int alto) {
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        g.drawImage(original, 0, 0, ancho, alto, null);
        g.dispose();
        return resultado;
    }

    // factor > 1 aclara, factor < 1 oscurece (la transparencia no se toca)
    private static BufferedImage ajustarBrillo(BufferedImage imagen, float factor) {
        RescaleOp op = new RescaleOp(new float[]{factor, factor, factor, 1f},
                                     new float[]{0f, 0f, 0f, 0f}, null);
        return op.filter(imagen, null);
    }

    private BufferedImage cargarImagenPNG(String ruta) {
        URL url = buscarRecurso(ruta);
        if (url == null) {
            return null;
        }
        try {
            BufferedImage leida = ImageIO.read(url);
            // Pasarla a ARGB para poder aclararla y leer la transparencia
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

    /** Busca en el classpath y, si no está, como archivo (para VS Code). */
    private URL buscarRecurso(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url != null) {
            return url;
        }
        String relativa = ruta.startsWith("/") ? ruta.substring(1) : ruta;
        for (String base : new String[]{"", "src/"}) {
            File archivo = new File(base + relativa);
            if (archivo.exists()) {
                try {
                    return archivo.toURI().toURL();
                } catch (IOException e) {
                    break;
                }
            }
        }
        System.err.println("No se encontró la imagen: " + ruta
                + " (buscada en el classpath y en " + new File(relativa).getAbsolutePath() + ")");
        return null;
    }
}
