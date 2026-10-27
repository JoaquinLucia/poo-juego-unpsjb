package vista.paneles;

import controlador.utilidades.GestorDeSonido;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class PanelOpciones extends JDialog {

    private final Color FONDO = new Color(15, 14, 16);
    private final Color BORDE = new Color(135, 105, 58);
    private final Color DORADO = new Color(230, 210, 165);
    private final Color BOTON = new Color(40, 35, 30);

    private final JSlider sliderMusica;
    private final JSlider sliderEfectos;
    private final JButton btnCerrar;

    public PanelOpciones(JFrame padre) {
        super(padre, "Opciones", true);
        setUndecorated(true);

        // --- Título ---
        JLabel titulo = new JLabel("Opciones de Sonido", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 32));
        titulo.setForeground(DORADO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // --- Sliders ---
        sliderMusica = crearSlider(GestorDeSonido.getInstancia().getVolumenMusica());
        sliderEfectos = crearSlider(GestorDeSonido.getInstancia().getVolumenEfectos());

        // Escuchadores en vivo para ajustar mientras el usuario mueve la barrita
        sliderMusica.addChangeListener(e -> 
            GestorDeSonido.getInstancia().setVolumenMusica(sliderMusica.getValue())
        );
        sliderEfectos.addChangeListener(e -> 
            GestorDeSonido.getInstancia().setVolumenEfectos(sliderEfectos.getValue())
        );

        // --- Panel de Controles ---
        JPanel panelSliders = new JPanel(new GridLayout(4, 1, 0, 10));
        panelSliders.setOpaque(false);
        panelSliders.add(crearEtiqueta("Volumen Música:"));
        panelSliders.add(sliderMusica);
        panelSliders.add(crearEtiqueta("Volumen Efectos:"));
        panelSliders.add(sliderEfectos);

        // --- Botón Cerrar ---
        btnCerrar = new JButton("Volver");
        btnCerrar.setFont(new Font("Serif", Font.BOLD, 20));
        btnCerrar.setForeground(DORADO);
        btnCerrar.setBackground(BOTON);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 2),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)));
        
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panelBoton.add(btnCerrar);

        // --- Armado ---
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 3),
                BorderFactory.createEmptyBorder(30, 40, 20, 40)));
        
        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(panelSliders, BorderLayout.CENTER);
        contenido.add(panelBoton, BorderLayout.SOUTH);

        setContentPane(contenido);
        setPreferredSize(new Dimension(450, 400));
        pack();
        setLocationRelativeTo(padre);
    }

    public void onCerrar(ActionListener listener) {
        btnCerrar.addActionListener(listener);
    }

    // --- Estilos ---
    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 20));
        lbl.setForeground(DORADO);
        return lbl;
    }

    private JSlider crearSlider(int valorInicial) {
        JSlider slider = new JSlider(0, 100, valorInicial);
        slider.setOpaque(false);
        slider.setForeground(DORADO);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }
}