package vista.animacion;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Pasa los frames de una animación, uno por uno, sobre un JLabel.
 *
 * Regla importante: cada vez que se llama a reproducir(), al final SIEMPRE
 * se ejecuta alTerminar, aunque no haya imagen. El controlador pone la acción
 * del juego ahí adentro; si se perdiera, la batalla quedaría trabada.
 */
public class AnimadorSprite {

    private final JLabel label;
    private Timer timer;

    // Datos de la animación que se está reproduciendo
    private ImageIcon[] frames;
    private int paso;                  // cuántos frames ya se mostraron
    private boolean alReves;           // recorrer del último al primero
    private boolean quedarseEnUltimo;  // al terminar, dejar el último frame mostrado
    private Runnable alTerminar;

    public AnimadorSprite(JLabel label) {
        this.label = label;
    }

    /** Muestra una imagen fija (personaje quieto). */
    public void mostrar(ImageIcon frame) {
        detener();
        label.setIcon(frame);
    }

    public void reproducir(ImageIcon[] frames, int msPorFrame, boolean alReves,
                           boolean quedarseEnUltimo, Runnable alTerminar) {
        detener();   // si había otra animación, se corta

        this.frames = frames;
        this.paso = 0;
        this.alReves = alReves;
        this.quedarseEnUltimo = quedarseEnUltimo;
        this.alTerminar = alTerminar;

        if (frames.length == 0) {   // no hay imagen: se avisa igual que terminó
            terminar();
            return;
        }

        timer = new Timer(msPorFrame, e -> siguienteFrame());
        timer.start();
    }

    public void detener() {
        if (timer != null) {
            timer.stop();
        }
    }

    /** Lo llama el Timer cada msPorFrame milisegundos. */
    private void siguienteFrame() {
        if (paso < frames.length) {
            label.setIcon(frames[indiceDelPaso(paso)]);
            paso++;
        } else {
            timer.stop();
            if (quedarseEnUltimo) {
                label.setIcon(frames[indiceDelPaso(frames.length - 1)]);
            } else {
                label.setIcon(frames[0]);   // vuelve a la pose de reposo
            }
            terminar();
        }
    }

    /** Traduce el número de paso al frame que corresponde según el sentido. */
    private int indiceDelPaso(int numeroDePaso) {
        if (alReves) {
            return frames.length - 1 - numeroDePaso;
        }
        return numeroDePaso;
    }

    private void terminar() {
        // Se guarda en una variable local y se limpia el atributo antes de ejecutarlo,
        // porque la acción puede arrancar otra animación con este mismo animador.
        Runnable accion = alTerminar;
        alTerminar = null;
        if (accion != null) {
            accion.run();
        }
    }
}
