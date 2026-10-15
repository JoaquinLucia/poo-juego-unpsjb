package vista; 

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class ReproductorSonido {
    
    public static void reproducirClick() {
        try {
            // Busca el archivo dentro de la carpeta de recursos de tu proyecto
            URL url = ReproductorSonido.class.getResource("/recursos/sonidos/sonidobotonesop.wav"); 
            
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start(); // Dispara el sonido en un hilo secundario automático
            } else {
                System.err.println("No se encontró el archivo de audio en la ruta.");
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir sonido: " + e.getMessage());
        }
    }
}