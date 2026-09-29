package vista;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Pantalla de combate. Es un panel dentro de VentanaPrincipal
 * (ya no abre una ventana propia).
 */
public class VistaBatalla extends JPanel {
    private final PanelBatalla panelFondo;
    private final PanelInferior panelInferior;

    public VistaBatalla() {
        super(new BorderLayout(0, 0));

        this.panelFondo = new PanelBatalla();
        this.panelInferior = new PanelInferior();

        add(panelFondo, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // --- EFECTOS VISUALES ---

    public void efectoTemblor() {
        int xOriginal = panelFondo.getX();
        int yOriginal = panelFondo.getY();

        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            int contador = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                contador++;
                if (contador >= 8) { // Más cortito (8 en vez de 15)
                    panelFondo.setLocation(xOriginal, yOriginal);
                    ((Timer) e.getSource()).stop();
                } else {
                    // Mueve apenas entre -3 y 3 píxeles (mucho más suave)
                    int offsetX = (int) (Math.random() * 6 - 3);
                    int offsetY = (int) (Math.random() * 6 - 3);
                    panelFondo.setLocation(xOriginal + offsetX, yOriginal + offsetY);
                }
            }
        });
        timer.start();
    }

    // --- MÉTODOS PARA QUE EL CONTROLADOR ACTUALICE LA INTERFAZ ---

    public void actualizarEstadisticas(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        panelFondo.actualizarBarras(nombreHeroe, vidaHeroe, nombreEnemigo, vidaEnemigo);
    }

    public void mostrarResultado(String mensaje) {
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
