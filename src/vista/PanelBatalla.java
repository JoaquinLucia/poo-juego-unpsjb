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
    
    private boolean primeraVez = true;

    public PanelBatalla() {
        // Carga segura de la imagen de fondo
        URL urlFondo = getClass().getResource("/recursos/batalla/pantalla_de_combate.jpg");
        if (urlFondo != null) {
            imagen = new ImageIcon(urlFondo).getImage();
        }

        // BARRAS DE VIDA
        vidaHeroe = new JProgressBar(0, 100);
        vidaEnemigo = new JProgressBar(0, 100);
        
        vidaHeroe.setStringPainted(true);
        vidaEnemigo.setStringPainted(true);
        vidaHeroe.setForeground(new Color(50, 200, 50)); // Verde
        vidaEnemigo.setForeground(new Color(200, 50, 50)); // Rojo

        // Panel principal que divide la parte superior en 2 mitades (Héroe y Enemigo)
        JPanel panelVida = new JPanel(new GridLayout(1, 2, 60, 0));
        panelVida.setOpaque(false);

        // PANEL DEL HEROE (Izquierda)
        JPanel panelHeroe = new JPanel(new BorderLayout(0, 5));
        panelHeroe.setOpaque(false);
        nombreHeroe = new JLabel("HÉROE");
        nombreHeroe.setForeground(Color.WHITE);
        nombreHeroe.setFont(new Font("Serif", Font.BOLD, 18));
        panelHeroe.add(nombreHeroe, BorderLayout.NORTH);
        panelHeroe.add(vidaHeroe, BorderLayout.CENTER);

        // PANEL DEL ENEMIGO (Derecha)
        JPanel panelEnemigo = new JPanel(new BorderLayout(0, 5));
        panelEnemigo.setOpaque(false);
        
        nombreEnemigo = new JLabel("ENEMIGO");
        nombreEnemigo.setForeground(Color.WHITE);
        nombreEnemigo.setFont(new Font("Serif", Font.BOLD, 18));

        panelEnemigo.add(nombreEnemigo, BorderLayout.NORTH);
        panelEnemigo.add(vidaEnemigo, BorderLayout.CENTER);

        panelVida.add(panelHeroe);
        panelVida.add(panelEnemigo);

        // Layout general del panel de fondo para fijar las barras arriba con sus márgenes
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

        if (primeraVez) {
            vidaHeroe.setMaximum(Math.max(1, hpHeroe));
            vidaEnemigo.setMaximum(Math.max(1, hpEnemigo));

            vidaHeroe.setValue(Math.max(0, hpHeroe));
            vidaHeroe.setString(Math.max(0, hpHeroe) + " HP");
            
            vidaEnemigo.setValue(Math.max(0, hpEnemigo));
            vidaEnemigo.setString(Math.max(0, hpEnemigo) + " HP");
            
            primeraVez = false;
        } else {
            if (hpHeroe > vidaHeroe.getMaximum()) vidaHeroe.setMaximum(hpHeroe);
            if (hpEnemigo > vidaEnemigo.getMaximum()) vidaEnemigo.setMaximum(hpEnemigo);

            animarBarraVida(vidaHeroe, Math.max(0, hpHeroe));
            animarBarraVida(vidaEnemigo, Math.max(0, hpEnemigo));
        }
    }

    private void animarBarraVida(JProgressBar barra, int nuevaVida) {
        Timer timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int valorActual = barra.getValue();
                int diferencia = Math.abs(valorActual - nuevaVida);
                int paso = Math.max(1, diferencia / 30); 

                if (valorActual > nuevaVida) {
                    barra.setValue(Math.max(nuevaVida, valorActual - paso)); 
                } else if (valorActual < nuevaVida) {
                    barra.setValue(Math.min(nuevaVida, valorActual + paso)); 
                } else {
                    ((Timer) e.getSource()).stop(); 
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