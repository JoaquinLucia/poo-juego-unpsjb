package vista;

import modelo.Heroe;
import modelo.ObservadorJuego;
import javax.swing.*;
import java.awt.*;

public class VistaJuego extends JFrame implements ObservadorJuego {
    private JButton btnAtaqueBasico;
    private JButton btnRobarHabilidad;
    private JButton btnUsarHabilidadRobada;
    private JLabel lblEstado;
    
    // Referencia al modelo para poder leer los datos cuando avise
    private final Heroe heroe;

    public VistaJuego(Heroe heroe) {
        this.heroe = heroe;
        
        setTitle("Juego de Pelea por Turnos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 1. Usamos BorderLayout en la ventana principal
        setLayout(new BorderLayout(10, 10));

        // 2. Inicializamos los componentes leyendo el mensaje inicial del modelo
        lblEstado = new JLabel(heroe.getUltimoMensaje(), SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnAtaqueBasico = new JButton("Ataque Básico");
        btnRobarHabilidad = new JButton("Robar Habilidad");
        btnUsarHabilidadRobada = new JButton("Usar Habilidad Robada");

        // 3. Panel superior/norte para el estado
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panelNorte.add(lblEstado);
        add(panelNorte, BorderLayout.NORTH);

        // 4. Panel inferior/sur para los botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(1, 0, 10, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        panelBotones.add(btnAtaqueBasico);
        panelBotones.add(btnRobarHabilidad);
        panelBotones.add(btnUsarHabilidadRobada);
        
        add(panelBotones, BorderLayout.SOUTH);

        // 5. pack() y centrado
        pack();
        setLocationRelativeTo(null);
    }

    // Método exigido por la interfaz ObservadorJuego
    @Override
    public void actualizarEstadoJuego() {
        // La vista lee del modelo cuando el modelo avisa
        lblEstado.setText(heroe.getUltimoMensaje());
    }

    // Getters
    public JButton getBtnAtaqueBasico() { return btnAtaqueBasico; }
    public JButton getBtnRobarHabilidad() { return btnRobarHabilidad; }
    public JButton getBtnUsarHabilidadRobada() { return btnUsarHabilidadRobada; }
    public JLabel getLblEstado() { return lblEstado; }

    public void mostrarVentana() {
        setVisible(true);
    }
}