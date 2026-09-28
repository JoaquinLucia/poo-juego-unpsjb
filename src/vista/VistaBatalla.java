package vista;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class VistaBatalla {
    private final JFrame ventana;
    private final PanelBatalla panelFondo;
    private final PanelInferior panelInferior;

    public VistaBatalla() {
        this.ventana = new JFrame("Combate");
        this.ventana.setExtendedState(JFrame.MAXIMIZED_BOTH); // Se abre en pantalla completa
        this.ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setLayout(new BorderLayout(0, 0));

        this.panelFondo = new PanelBatalla();
        this.panelInferior = new PanelInferior();

        this.ventana.add(panelFondo, BorderLayout.CENTER);
        this.ventana.add(panelInferior, BorderLayout.SOUTH);
    }

    public void mostrar() {
        this.ventana.setVisible(true);
    }

    // --- MÉTODOS PARA QUE EL CONTROLADOR ACTUALICE LA INTERFAZ ---

    public void actualizarEstadisticas(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        // Le pasamos la orden al panel de arriba para que mueva las barras de vida
        panelFondo.actualizarBarras(nombreHeroe, vidaHeroe, nombreEnemigo, vidaEnemigo);
    }

    public void mostrarResultado(String mensaje) {
        // Le pasamos la orden al panel de abajo para que escriba qué pasó
        panelInferior.mostrarResultado(mensaje);
    }

    public void setBotonesHabilitados(boolean estado) {
        panelInferior.setBotonesHabilitados(estado);
    }

    // --- ENGANCHES DE BOTONES PARA EL CONTROLADOR ---

    public void onAtacar(ActionListener listener) {
        panelInferior.getBotonAtacar().addActionListener(listener);
    }

    public void onHabilidad(ActionListener listener) {
        panelInferior.getBotonHabilidad().addActionListener(listener);
    }
}