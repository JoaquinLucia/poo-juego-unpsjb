package controlador.utilidades;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GestorDeSonido {

    // Caché para almacenar los clips ya cargados en memoria y evitar el delay inicial
    private static final Map<String, Clip> cacheClips = new HashMap<>();

    /**
     * Precarga un efecto de sonido en la memoria para que el primer clic sea instantáneo.
     */
    public static void precargarEfecto(String rutaRelativa) {
        if (cacheClips.containsKey(rutaRelativa)) return;
        
        try {
            URL url = GestorDeSonido.class.getClassLoader().getResource(rutaRelativa);
            if (url == null) {
                System.err.println("No se encontró el audio para precargar: " + rutaRelativa);
                return;
            }

            InputStream audioSrc = url.openStream();
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            cacheClips.put(rutaRelativa, clip);
        } catch (Exception e) {
            System.err.println("Error al precargar el audio: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    /**
     * Reproduce el efecto de sonido utilizando la caché para una respuesta inmediata.
     */
    public static void reproducirEfecto(String rutaRelativa) {
        try {
            // Si no está precargado, lo cargamos por seguridad
            if (!cacheClips.containsKey(rutaRelativa)) {
                precargarEfecto(rutaRelativa);
            }

            Clip clip = cacheClips.get(rutaRelativa);
            if (clip != null) {
                clip.setFramePosition(0); // Vuelve al inicio del audio
                clip.start();            // Reproduce
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir el audio en la ruta: " + rutaRelativa);
            e.printStackTrace();
        }
    }
}