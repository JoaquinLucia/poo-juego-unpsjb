package vista.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Nombre del personaje y su barra de vida.
 * Cuando la vida cambia, la barra baja (o sube) de a poco en lugar de saltar.
 */
public class BarraVida extends JPanel {

    private final JLabel nombre = new JLabel();
    private final JProgressBar barra = new JProgressBar(0, 100);
    private Timer timer;      // uno solo por barra, para no apilar animaciones
    private int vidaDestino;  // valor al que tiene que llegar la animación

    public BarraVida(String textoInicial, Color color) {
        super(new BorderLayout(0, 5));
        setOpaque(false);

        nombre.setText(textoInicial);
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("Serif", Font.BOLD, 18));

        barra.setStringPainted(true);   // muestra el texto "xx HP" adentro
        barra.setForeground(color);

        add(nombre, BorderLayout.NORTH);
        add(barra, BorderLayout.CENTER);
    }

    public void setNombre(String texto) {
        nombre.setText(texto.toUpperCase());
    }

    /** Al empezar la pelea: la vida actual pasa a ser el máximo, sin animación. */
    public void fijarVidaInicial(int vida) {
        detener();
        barra.setMaximum(Math.max(1, vida));
        mostrarValor(vida);
    }

    /** Durante la pelea: la barra se mueve de a poco hasta el valor nuevo. */
    public void animarHasta(int vida) {
        detener();
        vidaDestino = vida;
        timer = new Timer(30, e -> darUnPaso());
        timer.start();
    }

    public void detener() {
        if (timer != null) {
            timer.stop();
        }
    }

    /** Lo llama el Timer cada 30 ms. */
    private void darUnPaso() {
        int actual = barra.getValue();
        int paso = Math.max(1, Math.abs(actual - vidaDestino) / 30);

        if (actual > vidaDestino) {
            mostrarValor(Math.max(vidaDestino, actual - paso));
        } else if (actual < vidaDestino) {
            mostrarValor(Math.min(vidaDestino, actual + paso));
        } else {
            timer.stop();   // llegó al destino
        }
    }

    private void mostrarValor(int vida) {
        barra.setValue(vida);
        barra.setString(vida + " HP");
    }
}
