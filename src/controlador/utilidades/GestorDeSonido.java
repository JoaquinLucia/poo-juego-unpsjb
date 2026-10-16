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

    // Constructor privado (Patrón Singleton)
    private GestorDeSonido() {}

    // Método global para obtener la única instancia
    public static GestorDeSonido getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorDeSonido();
        }
        return instanciaUnica;
    }

    // Método para reproducir efectos cortos (ej. clics de botones, golpes) una sola vez
    public void reproducirEfecto(String rutaRelativa) {
        try {
            if (!cacheClips.containsKey(rutaRelativa)) {
                precargarEfecto(rutaRelativa);
            }

            Clip clip = cacheClips.get(rutaRelativa);
            if (clip != null) {
                // Si el efecto ya estaba sonando, lo reiniciamos desde el principio
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

    // Método para reproducir música de fondo en bucle infinito (unificado)
    public void reproducirMusicaEnBucle(String rutaRelativa) {
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
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Bucle infinito para la pista solicitada
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir la música en bucle: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    // NUEVO: Método para detener un audio específico (útil al cambiar de pantalla)
    public void detenerAudio(String rutaRelativa) {
        Clip clip = cacheClips.get(rutaRelativa);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
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