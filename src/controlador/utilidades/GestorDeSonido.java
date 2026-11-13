package controlador.utilidades;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GestorDeSonido {

    private static GestorDeSonido instanciaUnica;
    private final Map<String, Clip> cacheClips = new HashMap<>();
    
    // Variable para recordar qué música en bucle está sonando actualmente
    private String musicaActualEnBucle = null;
    // Variables para las opciones de gestion de volumen
    private int volumenMusica = 100;
    private int volumenEfectos = 100; 
    // Constructor privado 
    private GestorDeSonido() {}

    // Patrón Singleton sincronizado
    public static synchronized GestorDeSonido getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorDeSonido();
        }
        return instanciaUnica;
    }

    // Método para reproducir efectos cortos (clics, golpes) una sola vez
    public void reproducirEfecto(String rutaRelativa) {
        try {
            if (!cacheClips.containsKey(rutaRelativa)) {
                precargarEfecto(rutaRelativa);
            }

            Clip clip = cacheClips.get(rutaRelativa);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                aplicarVolumen(clip, volumenEfectos);
                clip.start();            
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir el audio: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    // Método para reproducir música de fondo en bucle infinito 
    public void reproducirMusicaEnBucle(String rutaRelativa) {
        try {
            // Si hay otra música sonando en bucle y es diferente, la detenemos primero
            if (musicaActualEnBucle != null && !musicaActualEnBucle.equals(rutaRelativa)) {
                detenerAudio(musicaActualEnBucle);
            }

            if (!cacheClips.containsKey(rutaRelativa)) {
                precargarEfecto(rutaRelativa);
            }

            Clip clip = cacheClips.get(rutaRelativa);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                aplicarVolumen(clip, volumenMusica);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Reproducción continua en bucle
                musicaActualEnBucle = rutaRelativa; // Actualizamos la pista actual activa
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir la música en bucle: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    // Método para detener un audio específico
    public void detenerAudio(String rutaRelativa) {
        Clip clip = cacheClips.get(rutaRelativa);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
        }
        if (rutaRelativa.equals(musicaActualEnBucle)) {
            musicaActualEnBucle = null;
        }
    }

    // Método privado para cargar el archivo desde los recursos del proyecto
    private void precargarEfecto(String rutaRelativa) {
        if (cacheClips.containsKey(rutaRelativa)) return;
        try {
            URL url = GestorDeSonido.class.getClassLoader().getResource(rutaRelativa);
            
            if (url == null) {
                System.err.println("¡ERROR! No se encontró el archivo de audio en ninguna variante para: " + rutaRelativa);
                return;
            }

            InputStream audioSrc = url.openStream();
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            cacheClips.put(rutaRelativa, clip);
            
        } catch (Exception e) {
            System.err.println("Excepción al cargar el audio: " + rutaRelativa);
            e.printStackTrace();
        }
    }
    // getters para sonido
    public int getVolumenMusica() { return volumenMusica; }
    public int getVolumenEfectos() { return volumenEfectos; } 
    public void setVolumenMusica(int volumen) {
        this.volumenMusica = volumen;
        // Si hay música sonando, le actualizamos el volumen en vivo
        if (musicaActualEnBucle != null) {
            Clip clip = cacheClips.get(musicaActualEnBucle);
            aplicarVolumen(clip, volumen);
        }
    }
    public void setVolumenEfectos(int volumen) {
        this.volumenEfectos = volumen;
    }
    // Lógica matemática para convertir de 0-100% a Decibeles (FloatControl)
    private void aplicarVolumen(Clip clip, int volumenPorcentaje) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volumenPorcentaje <= 0) {
                control.setValue(control.getMinimum()); // Muteado
            } else {
                // Escala logarítmica para que el deslizador se sienta natural al oído humano
                float dB = (float) (Math.log10(volumenPorcentaje / 100.0) * 20.0);
                control.setValue(dB);
            }
        }
    }
}
