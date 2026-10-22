package vista;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class AnimacionSprite {
    private Image[] fotogramas;
    private int frameActual = 0;
    private Timer timerAnimacion;
    private boolean enReproduccion = false;

    /**
     * @param frames Array de imágenes que componen la secuencia de la animación.
     * @param velocidadMs Milisegundos que dura cada fotograma en pantalla (ej. 120 ms).
     * @param alTerminar Acción opcional que se ejecuta al terminar la animación (ej. volver al estado inicial).
     */
    public AnimacionSprite(Image[] frames, int velocidadMs, Runnable alTerminar) {
        this.fotogramas = frames;
        
        this.timerAnimacion = new Timer(velocidadMs, (ActionEvent e) -> {
            frameActual++;
            if (frameActual >= fotogramas.length) {
                // Al llegar al final de la animación:
                frameActual = 0;
                timerAnimacion.stop();
                enReproduccion = false;
                
                if (alTerminar != null) {
                    alTerminar.run(); // Ejecuta lo que queramos al finalizar (ej. regresar al sprite quieto)
                }
            }
        });
        this.timerAnimacion.setRepeats(true);
    }

    /** Inicia la reproducción de la animación desde el principio. */
    public void iniciar() {
        if (fotogramas == null || fotogramas.length == 0) return;
        frameActual = 0;
        enReproduccion = true;
        timerAnimacion.start();
    }

    /** Detiene la animación de golpe. */
    public void detener() {
        if (timerAnimacion.isRunning()) {
            timerAnimacion.stop();
        }
        enReproduccion = false;
        frameActual = 0;
    }

    public boolean isEnReproduccion() {
        return enReproduccion;
    }

    /** Devuelve la imagen correspondiente al fotograma actual para dibujarla en el panel. */
    public Image getFotogramaActual() {
        if (fotogramas == null || fotogramas.length == 0) return null;
        return fotogramas[frameActual];
    }
}