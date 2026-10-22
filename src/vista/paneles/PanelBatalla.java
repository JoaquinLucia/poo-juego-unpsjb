package vista.paneles;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class PanelBatalla extends JPanel {
    private Image imagen;
    private final JProgressBar vidaHeroe;
    private final JProgressBar vidaEnemigo;
    private final JLabel nombreHeroe;
    private final JLabel nombreEnemigo;
    
    // Bandera para saber si es el inicio de la pelea (y no hacer la animación de llenado)
    private boolean primeraVez = true;
    private Timer timerHeroe;     // un timer por barra, para no apilar animaciones
    private Timer timerEnemigo;

    public PanelBatalla() {
        // Carga segura de la imagen de fondo por defecto
        URL urlFondo = getClass().getResource("/recursos/mazmorra_animada.gif");
        if (urlFondo != null) {
            imagen = new ImageIcon(urlFondo).getImage();
        }

        // BARRAS DE VIDA (Arrancan en 100 por defecto, se ajustan solas en el primer turno)
        vidaHeroe = new JProgressBar(0, 100);
        vidaEnemigo = new JProgressBar(0, 100);
        
        // Le agregamos texto a las barras para ver los números de HP
        vidaHeroe.setStringPainted(true);
        vidaEnemigo.setStringPainted(true);
        vidaHeroe.setForeground(new Color(50, 200, 50)); // Verde
        vidaEnemigo.setForeground(new Color(200, 50, 50)); // Rojo

        JPanel panelVida = new JPanel(new GridLayout(1, 2, 40, 0));
        panelVida.setOpaque(false);

        // PANEL DEL HEROE
        JPanel panelHeroe = new JPanel(new BorderLayout(0, 5));
        panelHeroe.setOpaque(false);
        nombreHeroe = new JLabel("HÉROE");
        nombreHeroe.setForeground(Color.WHITE);
        nombreHeroe.setFont(new Font("Serif", Font.BOLD, 18));
        panelHeroe.add(nombreHeroe, BorderLayout.NORTH);
        panelHeroe.add(vidaHeroe, BorderLayout.CENTER);

        // PANEL DEL ENEMIGO
        JPanel panelEnemigo = new JPanel(new BorderLayout(0, 5));
        panelEnemigo.setOpaque(false);
        nombreEnemigo = new JLabel("ENEMIGO");
        nombreEnemigo.setForeground(Color.WHITE);
        nombreEnemigo.setFont(new Font("Serif", Font.BOLD, 18));
        panelEnemigo.add(nombreEnemigo, BorderLayout.NORTH);
        panelEnemigo.add(vidaEnemigo, BorderLayout.CENTER);

        panelVida.add(panelHeroe);
        panelVida.add(panelEnemigo);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 80, 0, 80);

        add(panelVida, gbc);
    }

        /** Cambia el fondo según el lugar del combate. */
     public void setFondo(String ruta) {
        URL url = getClass().getResource(ruta);
        imagen = (url != null) ? new ImageIcon(url).getImage() : null;
        if (url == null) System.err.println("No se encontró el fondo: " + ruta);
        repaint();
    }

    /** Deja el panel listo para una pelea nueva. */
    public void reiniciar() {
        if (timerHeroe != null) timerHeroe.stop();
        if (timerEnemigo != null) timerEnemigo.stop();
        primeraVez = true;
    }

     public void actualizarBarras(String nomHeroe, int hpHeroe, String nomEnemigo, int hpEnemigo) {
        nombreHeroe.setText(nomHeroe.toUpperCase());
        nombreEnemigo.setText(nomEnemigo.toUpperCase());

        if (primeraVez) {
            // El tope de cada barra es la vida con la que arranca ESTA pelea
            vidaHeroe.setMaximum(Math.max(1, hpHeroe));
            vidaEnemigo.setMaximum(Math.max(1, hpEnemigo));

            vidaHeroe.setValue(Math.max(0, hpHeroe));
            vidaHeroe.setString(Math.max(0, hpHeroe) + " HP");
            vidaEnemigo.setValue(Math.max(0, hpEnemigo));
            vidaEnemigo.setString(Math.max(0, hpEnemigo) + " HP");
            primeraVez = false;
        } else {
            timerHeroe = animarBarraVida(timerHeroe, vidaHeroe, Math.max(0, hpHeroe));
            timerEnemigo = animarBarraVida(timerEnemigo, vidaEnemigo, Math.max(0, hpEnemigo));
        }
    }

    private Timer animarBarraVida(Timer anterior, JProgressBar barra, int nuevaVida) {
        if (anterior != null) anterior.stop();   // si quedaba una animación a medias, se corta
        Timer timer = new Timer(30, e -> {
            int valorActual = barra.getValue();
            int paso = Math.max(1, Math.abs(valorActual - nuevaVida) / 30);

            if (valorActual > nuevaVida) {
                barra.setValue(Math.max(nuevaVida, valorActual - paso));
            } else if (valorActual < nuevaVida) {
                barra.setValue(Math.min(nuevaVida, valorActual + paso));
            } else {
                ((Timer) e.getSource()).stop();
            }
            barra.setString(barra.getValue() + " HP");
        });
        timer.start();
        return timer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}