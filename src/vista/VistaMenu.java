package vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla del menú principal y de la selección de personaje.
 * Es un panel dentro de VentanaPrincipal (ya no es una ventana propia).
 */
public class VistaMenu extends JPanel {

    private static final String RUTA_IMAGENES = "/recursos/imagenes_menu/";

    // El diseño es de 800x600 (escala 1); VentanaPrincipal calcula el factor
    // para la pantalla real y lo pasa a aplicarEscala().
    private static final double ESCALA_IMAGENES = 0.7; // tamaño de los PNG en modo ventana
    private static final int SEPARACION_BOTONES = 12;
    private static final int SEPARACION_LOGO = 25;

    // Si falta alguna imagen, se muestra el texto en su lugar
    private final JLabel logo = new JLabel("Senderos de las Cenizas", SwingConstants.CENTER);
    private final BufferedImage imagenLogo = cargarImagen("logo_senderos_de_las_cenizas.png");

    private final JButton comenzar = crearBoton("Comenzar");
    private final JButton ranking = crearBoton("Tabla de puntajes");
    private final JButton opciones = crearBoton("Opciones");
    private final JButton salir = crearBoton("Salir");
    private final JButton[] botones = {comenzar, ranking, opciones, salir};

    // --- NUEVAS VARIABLES PARA LA SELECCIÓN DE PERSONAJE ---
    private static final String PANTALLA_SELECCION = "seleccion";
    private final PanelSeleccionPersonaje panelSeleccion = new PanelSeleccionPersonaje();

    // Mismo orden que el arreglo de botones
    private final BufferedImage[] imagenesBotones = {
            cargarImagen("boton_comenzar.png"),
            cargarImagen("boton_tabla_de_puntuaciones.png"),
            cargarImagen("boton_opciones.png"),
            cargarImagen("boton_salir.png"),
            cargarImagen("boton_volver.png")
    };

    // Espacios entre botones (se guardan para poder escalarlos)
    private final Box.Filler[] separadores = new Box.Filler[botones.length - 1];
    private final BorderLayout layoutContenido = new BorderLayout(0, SEPARACION_LOGO);
    private final JPanel contenido = new JPanel(layoutContenido);

    // El menú y la selección de personaje son dos "cartas" de este panel
    private static final String PANTALLA_MENU = "menu";
    private final CardLayout cartas = new CardLayout();

    public VistaMenu() {
        setLayout(cartas);

        // BoxLayout vertical: cada botón conserva el tamaño de su imagen
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);
        for (int i = 0; i < botones.length; i++) {
            if (i > 0) {
                Dimension espacio = new Dimension(0, SEPARACION_BOTONES);
                separadores[i - 1] = new Box.Filler(espacio, espacio, espacio);
                panelBotones.add(separadores[i - 1]);
            }
            panelBotones.add(botones[i]);
        }

        contenido.setOpaque(false);
        contenido.add(logo, BorderLayout.NORTH);
        contenido.add(panelBotones, BorderLayout.CENTER);

        // GridBagLayout sin "fill" respeta el tamaño preferido del contenido
        // y lo centra: por eso los botones no se estiran con la ventana.
        PanelFondo fondo = new PanelFondo("/recursos/imagenes_menu/fondo.gif", new GridBagLayout());
        fondo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        fondo.add(contenido);

        add(fondo, PANTALLA_MENU);
        add(panelSeleccion, PANTALLA_SELECCION);
    }

    private JButton crearBoton (String texto) { 
        JButton boton = new JButton(texto);
        // Sin el aspecto de Swing: solo se ve la imagen
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder());
        boton.setOpaque(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setForeground(java.awt.Color.WHITE);
        return boton;
    }

    // ---------- Imágenes ----------

    private static BufferedImage cargarImagen(String nombre) {
        String ruta = RUTA_IMAGENES + nombre;
        URL url = VistaMenu.class.getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("No se pudo leer la imagen: " + ruta);
            return null;
        }
    }

    private static BufferedImage escalar(BufferedImage original, double factor) {
        int ancho = Math.max(1, (int) Math.round(original.getWidth() * factor));
        int alto = Math.max(1, (int) Math.round(original.getHeight() * factor));
        BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = escalada.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, ancho, alto, null);
        g.dispose();
        return escalada;
    }

    // factor > 1 aclara, factor < 1 oscurece (la transparencia no se toca)
    private static BufferedImage ajustarBrillo(BufferedImage imagen, float factor) {
        RescaleOp op = new RescaleOp(
                new float[]{factor, factor, factor, 1f},
                new float[]{0f, 0f, 0f, 0f},
                null);
        return op.filter(imagen, null);
    }

    // ---------- Escala ----------

    /** Escala logo, botones y separaciones (lo llama el maestro con la escala de la ventana). */
    public void aplicarEscala(double escala) {
        double factor = escala * ESCALA_IMAGENES;

        if (imagenLogo != null) {
            logo.setText(null);
            logo.setIcon(new ImageIcon(escalar(imagenLogo, factor)));
        }

        for (int i = 0; i < botones.length; i++) {
            if (imagenesBotones[i] == null) {
                continue; // queda el texto
            }
            BufferedImage imagen = escalar(imagenesBotones[i], factor);
            JButton boton = botones[i];
            boton.setText(null);
            boton.setIcon(new ImageIcon(imagen));
            boton.setRolloverIcon(new ImageIcon(ajustarBrillo(imagen, 1.2f))); // mouse encima
            boton.setPressedIcon(new ImageIcon(ajustarBrillo(imagen, 0.8f)));  // al hacer clic
        }

        Dimension espacio = new Dimension(0, (int) Math.round(SEPARACION_BOTONES * escala));
        for (Box.Filler separador : separadores) {
            separador.changeShape(espacio, espacio, espacio);
        }
        layoutContenido.setVgap((int) Math.round(SEPARACION_LOGO * escala));

        contenido.revalidate();
        contenido.repaint();
    }

    // ---------- Enganches para el controlador ----------

    public void onComenzar(ActionListener listener) {
        comenzar.addActionListener(listener);
    }

    public void onVerPuntuaciones(ActionListener listener) {
        ranking.addActionListener(listener);
    }

    public void onOpciones(ActionListener listener) {
        opciones.addActionListener(listener);
    }

    public void onSalir(ActionListener listener) {
        salir.addActionListener(listener);
    }

    // ---------- Accesos para el Controlador de Menú y Selección ----------

    public void mostrarSeleccion() {
        cartas.show(this, PANTALLA_SELECCION);
    }

    public void mostrarMenu() {
        cartas.show(this, PANTALLA_MENU);
    }
    
    // Método clave para que ControladorMenu le pase el panel a ControladorSeleccion
    public PanelSeleccionPersonaje getPanelSeleccion() {
        return panelSeleccion;
    }

    // ---------- Diálogos ----------

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public boolean confirmar(String mensaje, String titulo) {
        int respuesta = JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }
}