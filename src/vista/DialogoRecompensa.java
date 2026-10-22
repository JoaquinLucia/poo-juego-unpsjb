package vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import modelo.Habilidad;

public class DialogoRecompensa extends JDialog {
    private int seleccion = -1; 
    private Image imagenFondo;

    public DialogoRecompensa(JFrame ventanaPadre, String nombreEnemigo, Habilidad habilidadRobada, Object[] opciones) {
        super(ventanaPadre, "Botín de Habilidad", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(600, 450);
        setLocationRelativeTo(ventanaPadre);

        // Cargamos la imagen original de fondo
        try {
            java.net.URL urlImg = getClass().getResource("/recursos/mazmorra/recompensa.png");
            if (urlImg != null) {
                imagenFondo = ImageIO.read(urlImg);
            }
        } catch (Exception e) {
            System.out.println("No se encontró la imagen de recompensa.");
        }

        // Panel principal con dibujo de fondo y capa translúcida para resaltar el texto
        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                    
                    // Capa de atenuación semitransparente oscura
                    g.setColor(new Color(10, 10, 15, 160)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(new Color(20, 15, 10));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                g.setColor(new Color(180, 140, 90));
                g.drawRect(3, 3, getWidth() - 6, getHeight() - 6);
            }
        };
        panelFondo.setLayout(new BorderLayout(15, 15));
        panelFondo.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Panel de información (Textos centrales)
        JPanel panelInfo = new JPanel();
        panelInfo.setOpaque(false);
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("¡Has derrotado al " + nombreEnemigo + "!");
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado brillante
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 22));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHab = new JLabel("Habilidad encontrada: " + habilidadRobada.getNombreHabilidad());
        lblHab.setForeground(Color.WHITE);
        lblHab.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblHab.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Extraemos los datos reales utilizando los métodos de la clase Habilidad
        String infoExtra = "\n• Valor Base (Daño/Efecto): " + habilidadRobada.getValorBase() + 
                           "\n• Enfriamiento (CD): " + habilidadRobada.getCooldown() + " turnos";

        JTextArea txtDesc = new JTextArea("Descripción: " + habilidadRobada.getDescripcion() + "\n" + infoExtra);
        txtDesc.setForeground(new Color(230, 230, 230));
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDesc.setOpaque(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtDesc.setMaximumSize(new Dimension(500, 100));

        panelInfo.add(lblTitulo);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblHab);
        panelInfo.add(Box.createVerticalStrut(8));
        panelInfo.add(txtDesc);

        panelFondo.add(panelInfo, BorderLayout.CENTER);

        // Panel de Botones inferiores (Diseño en flujo horizontal ordenado)
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        for (int i = 0; i < opciones.length; i++) {
            final int index = i;
            JButton btn = new JButton(opciones[i].toString());
            
            btn.setBackground(new Color(50, 35, 20));
            btn.setForeground(new Color(255, 222, 173));
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 133, 63), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            btn.addActionListener(e -> {
                seleccion = index;
                dispose();
            });

            panelBotones.add(btn);
        }

        panelFondo.add(panelBotones, BorderLayout.SOUTH);
        add(panelFondo);
    }

    public int mostrarDialogo() {
        setVisible(true);
        return seleccion;
    }
}