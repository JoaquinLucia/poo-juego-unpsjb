package controlador.utilidades;

import javax.sound.sampled.*;
import java.net.URL;

public class GestorDeSonido {
    public static void reproducirEfecto(String rutaRelativa) {
        try {
            URL urlArchivo = GestorDeSonido.class.getClassLoader().getResource(rutaRelativa);
            
            if (urlArchivo != null) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(urlArchivo);
                Clip clip = AudioSystem.getClip();
                
                // Agregamos un control para cerrar el clip al terminar de reproducir
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("No se encontró el audio en la ruta: " + rutaRelativa);
            }
        } catch (Exception e) {
            System.out.println("Error al reproducir el sonido:");
            e.printStackTrace();
        }
    }
}