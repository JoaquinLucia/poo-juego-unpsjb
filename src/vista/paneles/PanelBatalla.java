package vista.paneles;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import vista.AnimacionSprite;

public class PanelBatalla extends JPanel {
    private Image imagen;
    private Image spriteHeroeReposo;
    private final JProgressBar vidaHeroe;
    private final JProgressBar vidaEnemigo;
    private final JLabel nombreHeroe;
    private final JLabel nombreEnemigo;
    
    // 1. Unificamos el nombre de la variable
    private AnimacionSprite animacionAtaque;
    private Timer timerRenderizado;
    
    // Bandera para saber si es el inicio de la pelea
    private boolean primeraVez = true;
    private String nombreHeroeActual = "arquero";

    public PanelBatalla() {
        // Carga segura de la imagen de fondo
        URL urlFondo = getClass().getResource("/recursos/pantalla_de_combate.jpg");
        if (urlFondo != null) {
            imagen = new ImageIcon(urlFondo).getImage();
        }

        // BARRAS DE VIDA
        vidaHeroe = new JProgressBar(0, 100);
        vidaEnemigo = new JProgressBar(0, 100);
        
        vidaHeroe.setStringPainted(true);
        vidaEnemigo.setStringPainted(true);
        vidaHeroe.setForeground(new Color(50, 200, 50)); 
        vidaEnemigo.setForeground(new Color(200, 50, 50)); 

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

        // 2. Inicializamos el Timer para renderizar la animación a ~60 FPS
        timerRenderizado = new Timer(16, e -> {
            if (animacionAtaque != null && animacionAtaque.isEnReproduccion()) {
                repaint();
            }
        });
        timerRenderizado.start();
    }

    public void setSpriteHeroeBatalla(Image img) {
        this.spriteHeroeReposo = img;
        repaint();
    }

    public void actualizarBarras(String nomHeroe, int hpHeroe, String nomEnemigo, int hpEnemigo) {
        nombreHeroe.setText(nomHeroe.toUpperCase());
        nombreEnemigo.setText(nomEnemigo.toUpperCase());

        if (vidaHeroe.getMaximum() == 100 && hpHeroe > 100) vidaHeroe.setMaximum(hpHeroe);
        if (vidaEnemigo.getMaximum() == 100 && hpEnemigo > 100) vidaEnemigo.setMaximum(hpEnemigo);

        if (primeraVez) {
            vidaHeroe.setValue(Math.max(0, hpHeroe));
            vidaHeroe.setString(Math.max(0, hpHeroe) + " HP");
            
            vidaEnemigo.setValue(Math.max(0, hpEnemigo));
            vidaEnemigo.setString(Math.max(0, hpEnemigo) + " HP");
            
            primeraVez = false;
        } else {
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

    public void setNombreHeroeActual(String nombre) {
        if (nombre != null) {
            this.nombreHeroeActual = nombre.toLowerCase();
            repaint();
        }
    }

    // 3. Asignamos correctamente a la variable de clase unificada
    public void reproducirAnimacion(AnimacionSprite animacion) {
        this.animacionAtaque = animacion;
        if (this.animacionAtaque != null) {
            this.animacionAtaque.iniciar();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Coordenadas fijas: Lado izquierdo (1/4 de la pantalla) y apoyado en el piso
        int anchoSprite = 450; 
        int altoSprite = 450;
        int x = (getWidth() / 4) - (anchoSprite / 2) - 50; 
        int y = getHeight() - altoSprite - 100; // Ajustá este '100' si lo querés más arriba o más abajo

        // 1. Si está atacando, dibujamos la animación
        if (animacionAtaque != null && animacionAtaque.isEnReproduccion()) {
            Image fotograma = animacionAtaque.getFotogramaActual();
            if (fotograma != null) {
                g.drawImage(fotograma, x, y, anchoSprite, altoSprite, this);
            }
        } 
        // 2. Si no está atacando, lo dibujamos parado esperando
        else if (spriteHeroeReposo != null) {
            g.drawImage(spriteHeroeReposo, x, y, anchoSprite, altoSprite, this);
        }
    }
    public java.awt.Image[] recortarSpriteSheet(String ruta, int totalFrames) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            System.out.println("No se encontró el sprite: " + ruta);
            return null;
        }
        try {
            java.awt.image.BufferedImage hoja = javax.imageio.ImageIO.read(url);
            int anchoFrame = hoja.getWidth() / totalFrames;
            int altoFrame = hoja.getHeight();
            java.awt.Image[] frames = new java.awt.Image[totalFrames];
            
            // AJUSTE: Le restamos píxeles al ancho que recorta para que no agarre al de al lado
            int compensacion = 85; 
            
            for (int i = 0; i < totalFrames; i++) {
                // Al anchoFrame le restamos la compensación para que el cuadro sea más ajustado
                frames[i] = hoja.getSubimage(i * anchoFrame, 0, anchoFrame - compensacion, altoFrame);
            }
            return frames;
        } catch (Exception e) {
            System.out.println("Error al cortar el sprite: " + e.getMessage());
            return null;
        }
    }
}