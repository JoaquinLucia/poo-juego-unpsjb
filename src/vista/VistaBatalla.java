package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VistaBatalla {
    private final JFrame ventana;
    private final JButton btnAtacar;
    private final JButton btnHabilidad;
    private final JLabel etiquetaResultado;
    private final JLabel lblVidaHeroe;
    private final JLabel lblVidaEnemigo;

    public VistaBatalla() {
        this.ventana = new JFrame("Combate");
        this.ventana.setSize(500, 250);
        this.ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setLayout(new BorderLayout(10, 10));

        // 1. Panel Superior: Estadísticas (HP y Nombres)
        JPanel panelEstadisticas = new JPanel(new GridLayout(1, 2, 20, 0));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        this.lblVidaHeroe = new JLabel("Héroe HP: ---");
        this.lblVidaEnemigo = new JLabel("Enemigo HP: ---", SwingConstants.RIGHT);
        
        panelEstadisticas.add(this.lblVidaHeroe);
        panelEstadisticas.add(this.lblVidaEnemigo);

        // 2. Panel Central: Botones de Acción del Jugador
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        this.btnAtacar = new JButton("Atacar");
        this.btnHabilidad = new JButton("Usar Habilidad");
        
        panelBotones.add(this.btnAtacar);
        panelBotones.add(this.btnHabilidad);

        // 3. Panel Inferior: Registro de Combate (Consola)
        JPanel panelResultado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelResultado.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        this.etiquetaResultado = new JLabel("¡Comienza la batalla!");
        
        panelResultado.add(this.etiquetaResultado);

        // Ensamblar la ventana
        this.ventana.add(panelEstadisticas, BorderLayout.NORTH);
        this.ventana.add(panelBotones, BorderLayout.CENTER);
        this.ventana.add(panelResultado, BorderLayout.SOUTH);
    }

    public void mostrar() {
        this.ventana.setVisible(true);
    }

    // --- Métodos para que el Controlador actualice la interfaz ---

    public void actualizarEstadisticas(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        this.lblVidaHeroe.setText(nombreHeroe + " HP: " + vidaHeroe);
        this.lblVidaEnemigo.setText(nombreEnemigo + " HP: " + vidaEnemigo);
    }

    public void mostrarResultado(String mensaje) {
        this.etiquetaResultado.setText(mensaje);
    }

    public void setBotonesHabilitados(boolean estado) {
        this.btnAtacar.setEnabled(estado);
        this.btnHabilidad.setEnabled(estado);
    }

    // --- Enganches para el Controlador ---

    public void onAtacar(ActionListener listener) {
        this.btnAtacar.addActionListener(listener);
    }

    public void onHabilidad(ActionListener listener) {
        this.btnHabilidad.addActionListener(listener);
    }
}