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
    
    // NUEVO: Variable para recordar qué música en bucle está sonando actualmente
    private String musicaActualEnBucle = null;

    // Constructor privado (Patrón Singleton)
    private GestorDeSonido() {}

    // Patrón Singleton seguro para hilos
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
                clip.start();            
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir el audio: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    // Método para reproducir música de fondo en bucle infinito (ahora corta la anterior automáticamente)
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
}