package vista;

import javax.swing.*;
import java.awt.*;

public class VistaJuego extends JFrame {
    private JButton btnAtaqueBasico;
    private JButton btnRobarHabilidad;
    private JButton btnUsarHabilidadRobada;
    private JLabel lblEstado;

    public VistaJuego() {
        setTitle("Juego de Pelea por Turnos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 1. Usamos BorderLayout en la ventana principal
        setLayout(new BorderLayout(10, 10));

        // 2. Inicializamos los componentes
        lblEstado = new JLabel("¡Comienza la batalla!", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnAtaqueBasico = new JButton("Ataque Básico");
        btnRobarHabilidad = new JButton("Robar Habilidad");
        btnUsarHabilidadRobada = new JButton("Usar Habilidad Robada");

        // 3. Panel superior/norte para el estado (usando FlowLayout o BorderLayout)
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panelNorte.add(lblEstado);
        add(panelNorte, BorderLayout.NORTH);

        // 4. Panel inferior/sur para los botones de acción (usando GridLayout de una fila, como el TODO 3)
        JPanel panelBotones = new JPanel(new GridLayout(1, 0, 10, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        panelBotones.add(btnAtaqueBasico);
        panelBotones.add(btnRobarHabilidad);
        panelBotones.add(btnUsarHabilidadRobada);
        
        add(panelBotones, BorderLayout.SOUTH);

        // 5. Reemplazamos el setSize fijo por pack() para que se adapte al contenido
        pack();
        setLocationRelativeTo(null); // Centrar en pantalla
    }

    // Getters intactos para que el controlador los siga enlazando perfecto
    public JButton getBtnAtaqueBasico() { return btnAtaqueBasico; }
    public JButton getBtnRobarHabilidad() { return btnRobarHabilidad; }
    public JButton getBtnUsarHabilidadRobada() { return btnUsarHabilidadRobada; }
    public JLabel getLblEstado() { return lblEstado; }

    public void mostrarVentana() {
        setVisible(true);
    }
}
