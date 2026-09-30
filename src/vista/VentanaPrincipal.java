package vista;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * La única ventana del juego. Cada pantalla (menú, mazmorra, batalla) es un
 * panel dentro de un CardLayout, así cambiar de pantalla no abre ni cierra
 * ventanas: solo se cambia la "carta" que se ve, con un fundido a negro.
 */
public class VentanaPrincipal extends JFrame {

    // Tamaño de diseño del menú (el mismo que usaba VistaMenu en modo ventana)
    private static final int ANCHO_DISENO = 800;
    private static final int ALTO_DISENO = 600;

    private static final int MS_POR_PASO = 15;
    private static final float PASO_OSCURECER = 0.08f; // ~200 ms hasta negro
    private static final float PASO_ACLARAR = 0.06f;   // ~250 ms hasta ver la pantalla
    private static final float OPACIDAD_PAUSA = 0.55f;  // cuánto se oscurece el juego detrás de la pausa

    private final CardLayout cartas = new CardLayout();
    private final JPanel pantallas = new JPanel(cartas);
    private final CapaFundido capaFundido = new CapaFundido();
    private Timer animacion;

    public VentanaPrincipal() {
        super("Senderos de las Cenizas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        URL urlIcono = getClass().getResource("/recursos/imagenes_menu/casco_alado.png");
        if (urlIcono != null) {
            try {
                setIconImage(ImageIO.read(urlIcono));
            } catch (IOException e) {
                System.err.println("No se pudo leer el ícono de la ventana");
            }
        }

        pantallas.setBackground(Color.BLACK);
        setContentPane(pantallas);
        setGlassPane(capaFundido); // capa transparente que va por encima de todo

        // Pantalla completa sin bordes, igual que tenía el menú
        Rectangle pantalla = getGraphicsConfiguration().getBounds();
        setUndecorated(true);
        setResizable(false);
        setBounds(pantalla);
    }

    /** Factor para escalar el diseño de 800x600 del menú a esta pantalla. */
    public double getEscala() {
        return Math.min(getWidth() / (double) ANCHO_DISENO, getHeight() / (double) ALTO_DISENO);
    }

    public void agregarPantalla(String nombre, JComponent panel) {
        pantallas.add(panel, nombre);
    }

    public void quitarPantalla(JComponent panel) {
        pantallas.remove(panel);
    }

    /** Cambia de pantalla sin animación (se usa al arrancar). */
    public void mostrarDirecto(String nombre) {
        cartas.show(pantallas, nombre);
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Cambia de pantalla con un fundido a negro:
     *   1. oscurece la pantalla actual,
     *   2. ya en negro, ejecuta "preparar" (cargar la sala, crear la batalla...) y cambia de carta,
     *   3. aclara la pantalla nueva,
     *   4. al terminar ejecuta "alTerminar" (puede ser null).
     */
    public void cambiarPantalla(String nombre, Runnable preparar, Runnable alTerminar) {
        if (enTransicion()) {
            return; // ya hay un cambio en curso (por ejemplo, un doble clic)
        }
        capaFundido.setVisible(true); // bloquea el mouse mientras dura la transición

        animacion = new Timer(MS_POR_PASO, null);
        animacion.addActionListener(e -> {
            capaFundido.opacidad = Math.min(1f, capaFundido.opacidad + PASO_OSCURECER);
            capaFundido.repaint();
            if (capaFundido.opacidad >= 1f) {
                animacion.stop();
                if (preparar != null) {
                    preparar.run();
                }
                cartas.show(pantallas, nombre);
                pantallas.revalidate();
                aclarar(alTerminar);
            }
        });
        animacion.start();
    }

    public boolean enTransicion() {
        return animacion != null && animacion.isRunning();
    }

    /**
     * Oscurece (o aclara) el juego detrás de un modal, como el menú de pausa.
     * Mientras está oscurecido, la capa también bloquea el mouse.
     */
    public void atenuar(boolean atenuado) {
        capaFundido.opacidad = atenuado ? OPACIDAD_PAUSA : 0f;
        capaFundido.setVisible(atenuado);
        capaFundido.repaint();
    }

    /**
     * Asocia una tecla a una acción en toda la ventana.
     * "habilitada" se consulta en cada pulsación: si devuelve false, la tecla
     * queda libre para otras pantallas (por ejemplo, Esc en la selección de personaje).
     */
    public void registrarTecla(int codigoTecla, String nombre, Runnable accion, BooleanSupplier habilitada) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(codigoTecla, 0), nombre);
        getRootPane().getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }

            @Override
            public boolean isEnabled() {
                return habilitada.getAsBoolean();
            }
        });
    }

    private void aclarar(Runnable alTerminar) {
        animacion = new Timer(MS_POR_PASO, null);
        animacion.addActionListener(e -> {
            capaFundido.opacidad = Math.max(0f, capaFundido.opacidad - PASO_ACLARAR);
            capaFundido.repaint();
            if (capaFundido.opacidad <= 0f) {
                animacion.stop();
                capaFundido.setVisible(false);
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
        animacion.start();
    }

    /** Rectángulo negro con transparencia variable que cubre toda la ventana. */
    private static class CapaFundido extends JComponent {
        private float opacidad = 0f;

        CapaFundido() {
            setOpaque(false);
            // Consume los clics para que no se toque nada durante la transición
            addMouseListener(new MouseAdapter() { });
            addMouseMotionListener(new MouseAdapter() { });
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, Math.round(opacidad * 255)));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
