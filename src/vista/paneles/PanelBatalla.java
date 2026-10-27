package vista.paneles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

public class PanelBatalla extends JPanel {
    private Image imagen;
    private final JProgressBar vidaHeroe;
    private final JProgressBar vidaEnemigo;
    private final JLabel nombreHeroe;
    private final JLabel nombreEnemigo;
    
    // Componentes visuales para el héroe y el enemigo en el panel
    private final JLabel labelEnemigo;
    private final JLabel labelHeroe;
    
    private Timer timerAnimacionEnemigo;
    private Timer timerAnimacionHeroe;

    // Bandera para saber si es el inicio de la pelea 
    private boolean primeraVez = true;
    private Timer timerHeroe;     // un timer por barra para no apilar animaciones
    private Timer timerEnemigoBarra;

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

        // MANTENEMOS TU GRIDBAGLAYOUT ORIGINAL EXACTO PARA LAS BARRAS ARRIBA
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 80, 0, 80);

        add(panelVida, gbc);

        // Panel contenedor inferior transparente para los sprites
        JPanel panelSprites = new JPanel(null);
        panelSprites.setOpaque(false);
        panelSprites.setPreferredSize(new Dimension(1200, 400)); 
        
        // Coordenadas iniciales por defecto para el Héroe y el Enemigo
        labelHeroe = new JLabel();
        labelHeroe.setBounds(210, 20, 310, 560);
        panelSprites.add(labelHeroe);

        labelEnemigo = new JLabel();
        labelEnemigo.setBounds(880, 110, 380, 380); 
        panelSprites.add(labelEnemigo);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelSprites, gbc);
    }

    /** Método auxiliar para aplicar los bounds según el enemigo */
    private void ajustarBoundsEnemigo(String ruta) {
        String rutaLower = ruta.toLowerCase();
        if (rutaLower.contains("esclavo") || rutaLower.contains("cazador") || rutaLower.contains("akuma")) {
            labelEnemigo.setBounds(850, 110, 400, 400); 
        } else {
            labelEnemigo.setBounds(880, 110, 380, 380); 
        }
    }

    /** Método auxiliar para aplicar los bounds según el héroe */
    private void ajustarBoundsHeroe(String ruta) {
        String rutaLower = ruta.toLowerCase();
        if (rutaLower.contains("artorias")) {
            labelHeroe.setBounds(150, -50, 290, 670); 
        } else if (rutaLower.contains("mago") || rutaLower.contains("merlin")) {
            labelHeroe.setBounds(210, 20, 310, 560); // Merlín corrido más a la derecha
        } else {
            labelHeroe.setBounds(150, 120, 380, 380); 
        }
    }

    /** Cambia el fondo según el lugar del combate. */
    public void setFondo(String ruta) {
        URL url = getClass().getResource(ruta);
        imagen = (url != null) ? new ImageIcon(url).getImage() : null;
        if (url == null) System.err.println("No se encontró el fondo: " + ruta);
        repaint();
    }

    /** Carga el sprite inicial en reposo del enemigo */
    public void mostrarSpriteEnemigo(String rutaSprite) {
        ajustarBoundsEnemigo(rutaSprite);

        URL url = getClass().getResource(rutaSprite);
        if (url != null) {
            ImageIcon iconOriginal = new ImageIcon(url);
            Image imgOriginal = iconOriginal.getImage();
            int anchoTotal = iconOriginal.getIconWidth();
            int altoTotal = iconOriginal.getIconHeight();
            
            int totalFrames = 4;
            double anchoPreciso = (double) anchoTotal / totalFrames;

            int sx1 = (int) Math.round(0 * anchoPreciso);
            int sx2 = (int) Math.round(1 * anchoPreciso);
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            g2d.drawImage(imgOriginal, 0, 0, frameWidth, altoTotal, sx1, 0, sx2, altoTotal, null);
            g2d.dispose();
            
            int w = labelEnemigo.getWidth() > 0 ? labelEnemigo.getWidth() : 380;
            int h = labelEnemigo.getHeight() > 0 ? labelEnemigo.getHeight() : 380;

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();
            
            labelEnemigo.setIcon(new ImageIcon(frameEscalado));
            labelEnemigo.revalidate();
            labelEnemigo.repaint();
        } else {
            System.err.println("No se encontró el archivo de sprite: " + rutaSprite);
        }
    }

    /** Carga el sprite inicial en reposo del héroe con recorte limpio */
    public void mostrarSpriteHeroe(String rutaSprite) {
        ajustarBoundsHeroe(rutaSprite);

        URL url = getClass().getResource(rutaSprite);
        if (url != null) {
            ImageIcon iconOriginal = new ImageIcon(url);
            Image imgOriginal = iconOriginal.getImage();
            int anchoTotal = iconOriginal.getIconWidth();
            int altoTotal = iconOriginal.getIconHeight();
            
            int totalFrames = 4;
            double anchoPreciso = (double) anchoTotal / totalFrames;

            int sx1 = (int) Math.round(0 * anchoPreciso);
            int sx2 = (int) Math.round(1 * anchoPreciso);

            String rutaLower = rutaSprite.toLowerCase();
            if (rutaLower.contains("artorias")) {
                sx2 -= 35; 
            } else if (rutaLower.contains("mago") || rutaLower.contains("merlin")) {
                sx2 -= 25; 
            }
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            g2d.drawImage(imgOriginal, 0, 0, frameWidth, altoTotal, sx1, 0, sx2, altoTotal, null);
            g2d.dispose();
            
            int w = labelHeroe.getWidth() > 0 ? labelHeroe.getWidth() : 310;
            int h = labelHeroe.getHeight() > 0 ? labelHeroe.getHeight() : 560;

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();
            
            labelHeroe.setIcon(new ImageIcon(frameEscalado));
            labelHeroe.revalidate();
            labelHeroe.repaint();
        } else {
            System.err.println("No se encontró el archivo de sprite del héroe: " + rutaSprite);
        }
    }

    /** Deja el panel listo para una pelea nueva. */
    public void reiniciar() {
        if (timerHeroe != null) timerHeroe.stop();
        if (timerEnemigoBarra != null) timerEnemigoBarra.stop();
        if (timerAnimacionEnemigo != null) timerAnimacionEnemigo.stop();
        if (timerAnimacionHeroe != null) timerAnimacionHeroe.stop();
        primeraVez = true;
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
            timerHeroe = animarBarraVida(timerHeroe, vidaHeroe, Math.max(0, hpHeroe));
            timerEnemigoBarra = animarBarraVida(timerEnemigoBarra, vidaEnemigo, Math.max(0, hpEnemigo));
        }
    }

    private Timer animarBarraVida(Timer anterior, JProgressBar barra, int nuevaVida) {
        if (anterior != null) anterior.stop();
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

    /** Reproduce la animación de ataque del enemigo */
    public void reproducirAnimacionAtaque(String rutaSpriteSheet, Runnable alTerminar) {
        if (timerAnimacionEnemigo != null && timerAnimacionEnemigo.isRunning()) return;

        ajustarBoundsEnemigo(rutaSpriteSheet);

        URL url = getClass().getResource(rutaSpriteSheet);
        if (url == null) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        ImageIcon iconOriginal = new ImageIcon(url);
        Image imgOriginal = iconOriginal.getImage();
        int anchoTotal = iconOriginal.getIconWidth();
        int altoTotal = iconOriginal.getIconHeight();
        
        int totalFrames = 4;
        double anchoPreciso = (double) anchoTotal / totalFrames;

        ImageIcon[] frames = new ImageIcon[totalFrames];
        int w = labelEnemigo.getWidth() > 0 ? labelEnemigo.getWidth() : 380;
        int h = labelEnemigo.getHeight() > 0 ? labelEnemigo.getHeight() : 380;

        for (int i = 0; i < totalFrames; i++) {
            int sx1 = (int) Math.round(i * anchoPreciso);
            int sx2 = (int) Math.round((i + 1) * anchoPreciso);
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            
            g2d.drawImage(imgOriginal, 
                          0, 0, frameWidth, altoTotal, 
                          sx1, 0, sx2, altoTotal, 
                          null);
            g2d.dispose();

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();

            frames[i] = new ImageIcon(frameEscalado);
        }

        final int[] frameActual = {0};
        timerAnimacionEnemigo = new Timer(100, e -> {
            if (frameActual[0] < totalFrames) {
                labelEnemigo.setIcon(frames[frameActual[0]]);
                frameActual[0]++;
            } else {
                ((Timer) e.getSource()).stop();
                labelEnemigo.setIcon(frames[0]); 
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
        timerAnimacionEnemigo.start();
    }

    /** Reproduce la animación de ataque del héroe */
    public void reproducirAnimacionAtaqueHeroe(String rutaSpriteSheet, Runnable alTerminar) {
        if (timerAnimacionHeroe != null && timerAnimacionHeroe.isRunning()) return;

        ajustarBoundsHeroe(rutaSpriteSheet);

        URL url = getClass().getResource(rutaSpriteSheet);
        if (url == null) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        ImageIcon iconOriginal = new ImageIcon(url);
        Image imgOriginal = iconOriginal.getImage();
        int anchoTotal = iconOriginal.getIconWidth();
        int altoTotal = iconOriginal.getIconHeight();
        
        int totalFrames = 4;
        double anchoPreciso = (double) anchoTotal / totalFrames;

        ImageIcon[] frames = new ImageIcon[totalFrames];
        int w = labelHeroe.getWidth() > 0 ? labelHeroe.getWidth() : 310;
        int h = labelHeroe.getHeight() > 0 ? labelHeroe.getHeight() : 560;

        for (int i = 0; i < totalFrames; i++) {
            int sx1 = (int) Math.round(i * anchoPreciso);
            int sx2 = (int) Math.round((i + 1) * anchoPreciso);

            String rutaLower = rutaSpriteSheet.toLowerCase();
            if (i == 0) {
                if (rutaLower.contains("artorias")) {
                    sx2 -= 35;
                } else if (rutaLower.contains("mago") || rutaLower.contains("merlin")) {
                    sx2 -= 25; // Recorte limpio en el frame inicial/final de ataque para Merlín
                }
            }
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            
            g2d.drawImage(imgOriginal, 
                          0, 0, frameWidth, altoTotal, 
                          sx1, 0, sx2, altoTotal, 
                          null);
            g2d.dispose();

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();

            frames[i] = new ImageIcon(frameEscalado);
        }

        final int[] frameActual = {0};
        timerAnimacionHeroe = new Timer(100, e -> {
            if (frameActual[0] < totalFrames) {
                labelHeroe.setIcon(frames[frameActual[0]]);
                frameActual[0]++;
            } else {
                ((Timer) e.getSource()).stop();
                labelHeroe.setIcon(frames[0]); 
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
        timerAnimacionHeroe.start();
    }

    /** Reproduce la animación de derrota del enemigo de derecha a izquierda y se queda estática en el frame caído  */
    public void reproducirAnimacionDerrota(String rutaSpriteSheet, Runnable alTerminar) {
        if (timerAnimacionEnemigo != null && timerAnimacionEnemigo.isRunning()) return;

        ajustarBoundsEnemigo(rutaSpriteSheet);

        URL url = getClass().getResource(rutaSpriteSheet);
        if (url == null) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        ImageIcon iconOriginal = new ImageIcon(url);
        Image imgOriginal = iconOriginal.getImage();
        int anchoTotal = iconOriginal.getIconWidth();
        int altoTotal = iconOriginal.getIconHeight();
        
        int totalFrames = 4;
        double anchoPreciso = (double) anchoTotal / totalFrames;

        ImageIcon[] frames = new ImageIcon[totalFrames];
        int w = labelEnemigo.getWidth() > 0 ? labelEnemigo.getWidth() : 380;
        int h = labelEnemigo.getHeight() > 0 ? labelEnemigo.getHeight() : 380;

        for (int i = 0; i < totalFrames; i++) {
            int sx1 = (int) Math.round(i * anchoPreciso);
            int sx2 = (int) Math.round((i + 1) * anchoPreciso);
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            
            g2d.drawImage(imgOriginal, 
                          0, 0, frameWidth, altoTotal, 
                          sx1, 0, sx2, altoTotal, 
                          null);
            g2d.dispose();

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();

            frames[i] = new ImageIcon(frameEscalado);
        }

        final int[] frameActual = {totalFrames - 1};
        timerAnimacionEnemigo = new Timer(120, e -> {
            if (frameActual[0] >= 0) {
                labelEnemigo.setIcon(frames[frameActual[0]]);
                frameActual[0]--;
            } else {
                ((Timer) e.getSource()).stop();
                labelEnemigo.setIcon(frames[0]); 
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
        timerAnimacionEnemigo.start();
    }

    /** Reproduce la animación de derrota del héroe y se queda estática en el frame final caído */
    public void reproducirAnimacionDerrotaHeroe(String rutaSpriteSheet, Runnable alTerminar) {
        if (timerAnimacionHeroe != null && timerAnimacionHeroe.isRunning()) return;

        ajustarBoundsHeroe(rutaSpriteSheet);

        URL url = getClass().getResource(rutaSpriteSheet);
        if (url == null) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        ImageIcon iconOriginal = new ImageIcon(url);
        Image imgOriginal = iconOriginal.getImage();
        int anchoTotal = iconOriginal.getIconWidth();
        int altoTotal = iconOriginal.getIconHeight();
        
        int totalFrames = 4;
        double anchoPreciso = (double) anchoTotal / totalFrames;

        ImageIcon[] frames = new ImageIcon[totalFrames];
        int w = labelHeroe.getWidth() > 0 ? labelHeroe.getWidth() : 310;
        int h = labelHeroe.getHeight() > 0 ? labelHeroe.getHeight() : 560;

        for (int i = 0; i < totalFrames; i++) {
            int sx1 = (int) Math.round(i * anchoPreciso);
            int sx2 = (int) Math.round((i + 1) * anchoPreciso);
            int frameWidth = sx2 - sx1;

            BufferedImage subImg = new BufferedImage(frameWidth, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = subImg.createGraphics();
            
            g2d.drawImage(imgOriginal, 
                          0, 0, frameWidth, altoTotal, 
                          sx1, 0, sx2, altoTotal, 
                          null);
            g2d.dispose();

            BufferedImage frameEscalado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frameEscalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(subImg, 0, 0, w, h, null);
            g2.dispose();

            frames[i] = new ImageIcon(frameEscalado);
        }

        final int[] frameActual = {0};
        timerAnimacionHeroe = new Timer(120, e -> {
            if (frameActual[0] < totalFrames) {
                labelHeroe.setIcon(frames[frameActual[0]]);
                frameActual[0]++;
            } else {
                ((Timer) e.getSource()).stop();
                // Se queda estático en el último fotograma (héroe derrotado en el suelo)
                labelHeroe.setIcon(frames[totalFrames - 1]); 
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
        timerAnimacionHeroe.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}