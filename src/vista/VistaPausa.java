package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Menú de pausa: un modal sin bordes con el título y tres opciones.
 * Usa los mismos colores que el panel de batalla (fondo casi negro y borde dorado).
 * El estilo final se puede cambiar acá sin tocar el controlador.
 */
public class VistaPausa extends JDialog {
    private static final Color FONDO = new Color(15, 14, 16);
    private static final Color BORDE = new Color(135, 105, 58);
    private static final Color TEXTO = new Color(220, 190, 120);
    private static final Color BOTON = new Color(40, 35, 30);
    private static final Color BOTON_HOVER = new Color(62, 52, 40);

    private final JButton btnReanudar = crearBoton("Volver al juego");
    private final JButton btnTitulo = crearBoton("Volver al título");
    private final JButton btnSalir = crearBoton("Salir del juego");

    public VistaPausa(JFrame ventana) {
        super(ventana, "Menú de Pausa", true); // true = modal: bloquea el juego mientras está abierto
        setUndecorated(true);

        JLabel titulo = new JLabel("Menú de Pausa", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 34));
        titulo.setForeground(TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JPanel botones = new JPanel(new GridLayout(3, 1, 0, 14));
        botones.setOpaque(false);
        botones.add(btnReanudar);
        botones.add(btnTitulo);
        botones.add(btnSalir);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 3),
                BorderFactory.createEmptyBorder(32, 48, 36, 48)));
        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(botones, BorderLayout.CENTER);
        setContentPane(contenido);

        getRootPane().setDefaultButton(btnReanudar); // Enter = volver al juego
        pack();
        setSize(new Dimension(Math.max(getWidth(), 420), getHeight()));
    }

    /** Muestra el modal centrado. Este método se bloquea hasta que el modal se cierra. */
    public void mostrar() {
        setLocationRelativeTo(getOwner());
        btnReanudar.requestFocusInWindow();
        setVisible(true);
    }

    public void cerrar() {
        setVisible(false);
    }

    // ---------- Enganches para el controlador ----------

    public void onReanudar(ActionListener listener) {
        btnReanudar.addActionListener(listener);
        // Esc dentro de la pausa también vuelve al juego
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "reanudar");
        getRootPane().getActionMap().put("reanudar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });
    }

    public void onVolverAlTitulo(ActionListener listener) {
        btnTitulo.addActionListener(listener);
    }

    public void onSalir(ActionListener listener) {
        btnSalir.addActionListener(listener);
    }

    // ---------- Estilo ----------

    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Serif", Font.BOLD, 20));
        boton.setForeground(new Color(230, 210, 165));
        boton.setBackground(BOTON);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 2),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { boton.setBackground(BOTON_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { boton.setBackground(BOTON); }
        });
        return boton;
    }
}
