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

    // Método de instancia para reproducir sonidos
    public void reproducirEfecto(String rutaRelativa) {
        try {
            if (!cacheClips.containsKey(rutaRelativa)) {
                precargarEfecto(rutaRelativa);
            }

            Clip clip = cacheClips.get(rutaRelativa);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();            
            }
        } catch (Exception e) {
            System.err.println("No se pudo reproducir el audio: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    private void precargarEfecto(String rutaRelativa) {
        if (cacheClips.containsKey(rutaRelativa)) return;
        try {
            URL url = null;
            
            // Estrategia de búsqueda robusta para evitar errores de ruta en VS Code:
            // 1. Intenta buscar tal cual se pasó
            url = GestorDeSonido.class.getClassLoader().getResource(rutaRelativa);
            
            // 2. Si falla, intenta anteponiendo "recursos/"
            if (url == null && !rutaRelativa.startsWith("recursos/")) {
                url = GestorDeSonido.class.getClassLoader().getResource("recursos/" + rutaRelativa);
            }
            
            // 3. Si falla y empieza con barra, la remueve y busca
            if (url == null && rutaRelativa.startsWith("/")) {
                String limpia = rutaRelativa.substring(1);
                url = GestorDeSonido.class.getClassLoader().getResource(limpia);
            }

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