package vista;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PanelBatalla extends JPanel {
    private Image imagen;
    private final JProgressBar vidaHeroe;
    private final JProgressBar vidaEnemigo;
    private final JLabel nombreHeroe;
    private final JLabel nombreEnemigo;
    
    private boolean primeraVez = true;

    // Guardamos el nombre del héroe actual para saber qué sprite mostrar
    private String nombreHeroeActual = "arquero";

    // Gestor de la animación actual del personaje
    private AnimacionSprite animacionActual;

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

    /** Permite configurar el héroe actual para que el panel sepa qué sprite cargar en reposo */
    public void setNombreHeroeActual(String nombre) {
        if (nombre != null) {
            this.nombreHeroeActual = nombre.toLowerCase();
            repaint();
        }
    }

    /** Permite asignar y reproducir una animación de sprite en el panel */
    public void reproducirAnimacion(AnimacionSprite animacion) {
        this.animacionActual = animacion;
        if (this.animacionActual != null) {
            this.animacionActual.iniciar();
        }
    }

    public void actualizarBarras(String nomHeroe, int hpHeroe, String nomEnemigo, int hpEnemigo) {
        nombreHeroe.setText(nomHeroe.toUpperCase());
        nombreEnemigo.setText(nomEnemigo.toUpperCase());

        // Actualizamos dinámicamente el nombre del héroe para los sprites
        setNombreHeroeActual(nomHeroe);

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

/** Recorta automáticamente los sprites ajustando los márgenes para eliminar el sobrante del caballero */
    public Image[] recortarSpriteSheet(String rutaRelativa, int totalFrames) {
        try {
            java.net.URL urlImagen = getClass().getResource(rutaRelativa);
            if (urlImagen == null) {
                System.err.println("No se encontró la imagen en la ruta: " + rutaRelativa);
                return null;
            }
            BufferedImage spriteSheet = javax.imageio.ImageIO.read(urlImagen);
            
            int anchoFrame = spriteSheet.getWidth() / totalFrames;
            int altoFrame = spriteSheet.getHeight();
            
            int margenX = 15; 
            int anchoRecorte = anchoFrame - 60;
            
            // Ajustamos el recorte para el caballero
            if (nombreHeroeActual.contains("caballero")) {
                margenX = 20;           
                anchoRecorte = anchoFrame - 95; 
            } else if (nombreHeroeActual.contains("mago")) {
                margenX = 15;
                anchoRecorte = anchoFrame - 60;
            }
            
            Image[] frames = new Image[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = spriteSheet.getSubimage(i * anchoFrame + margenX, 0, anchoRecorte, altoFrame);
            }
            return frames;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 1. Dibujamos la imagen de fondo de la mazmorra
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }

        // 2. Determinamos qué imagen mostrar en pantalla según el héroe seleccionado
        Image frameParaDibujar = null;

        if (animacionActual != null && animacionActual.isEnReproduccion()) {
            frameParaDibujar = animacionActual.getFotogramaActual();
            repaint(); 
        } else {
            String rutaSprite = "/recursos/sprites/arquero_ataque.png";
            
            if (nombreHeroeActual.contains("caballero")) {
                rutaSprite = "/recursos/sprites/caballero_ataque.png";
            } else if (nombreHeroeActual.contains("mago")) {
                rutaSprite = "/recursos/sprites/mago_ataque.png";
            }

            Image[] framesEstaticos = recortarSpriteSheet(rutaSprite, 4);
            if (framesEstaticos != null && framesEstaticos.length > 0) {
                frameParaDibujar = framesEstaticos[0]; 
            }
        }

        // 3. Dibujamos al héroe con el ancho ajustado y limpio de artefactos
        if (frameParaDibujar != null) {
            int x = 160;
            int y = 10;
            int ancho = 300; 
            int alto = 920;  
            
            if (nombreHeroeActual.contains("caballero")) {
                ancho = 265; 
                alto = 580;  
                x = 175;     
                y = 220;     
            } else if (nombreHeroeActual.contains("mago")) {
                ancho = 300;
                alto = 920;
                x = 160;
                y = 10;
            }

            g.drawImage(frameParaDibujar, x, y, ancho, alto, this);
        }
    }
}