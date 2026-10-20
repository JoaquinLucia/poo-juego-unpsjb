package vista.paneles;


import java.awt.*;
import javax.swing.*;

public class PanelTitulo extends JPanel {

    private final Color FONDO = new Color(20, 18, 17);
    private final Color DORADO = new Color(230, 210, 165);

    private final JLabel titulo;

    public PanelTitulo() {

        setBackground(FONDO);
        setLayout(new java.awt.BorderLayout());

        titulo = new JLabel("SELECCIÓN DE HÉROE",SwingConstants.CENTER);

        titulo.setForeground(DORADO);
        titulo.setFont(new Font("Serif", Font.BOLD, 32));

        titulo.setBorder(BorderFactory.createEmptyBorder(25, 10, 15, 10));

        add(titulo, java.awt.BorderLayout.CENTER);
    }

    public JLabel getTitulo() {
        return titulo;
    }
    public void setTitulo(String texto) {
        titulo.setText(texto);
    }
}