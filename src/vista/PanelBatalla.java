package vista;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public PanelBatalla() {
        // Carga segura de la imagen de fondo
        URL urlFondo = getClass().getResource("/recursos/pantalla_de_combate.jpg");
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

    public void actualizarBarras(String nomHeroe, int hpHeroe, String nomEnemigo, int hpEnemigo) {
        nombreHeroe.setText(nomHeroe.toUpperCase());
        nombreEnemigo.setText(nomEnemigo.toUpperCase());

        // Si el héroe tiene más vida que el máximo de la barra (ej: Caballero con 600), ajustamos el tope
        if (vidaHeroe.getMaximum() == 100 && hpHeroe > 100) vidaHeroe.setMaximum(hpHeroe);
        if (vidaEnemigo.getMaximum() == 100 && hpEnemigo > 100) vidaEnemigo.setMaximum(hpEnemigo);

        if (primeraVez) {
            // Setear instantáneamente al iniciar la pelea
            vidaHeroe.setValue(Math.max(0, hpHeroe));
            vidaHeroe.setString(Math.max(0, hpHeroe) + " HP");
            
            vidaEnemigo.setValue(Math.max(0, hpEnemigo));
            vidaEnemigo.setString(Math.max(0, hpEnemigo) + " HP");
            
            primeraVez = false;
        } else {
            // Llamar a la animación suave durante el combate
            animarBarraVida(vidaHeroe, Math.max(0, hpHeroe));
            animarBarraVida(vidaEnemigo, Math.max(0, hpEnemigo));
        }
    }

    private void animarBarraVida(JProgressBar barra, int nuevaVida) {
        // Aumentamos el tiempo a 30 milisegundos para ralentizar la animación general
        Timer timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int valorActual = barra.getValue();
                
                // Dividimos por 30 (antes 10) para que reste fragmentos más pequeños y tarde más en llegar
                int diferencia = Math.abs(valorActual - nuevaVida);
                int paso = Math.max(1, diferencia / 30); 

                if (valorActual > nuevaVida) {
                    barra.setValue(Math.max(nuevaVida, valorActual - paso)); // Resta vida lentamente
                } else if (valorActual < nuevaVida) {
                    barra.setValue(Math.min(nuevaVida, valorActual + paso)); // Suma vida lentamente
                } else {
                    ((Timer) e.getSource()).stop(); // Se detiene exacto en el valor
                }
                
                barra.setString(barra.getValue() + " HP");
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}