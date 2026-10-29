package vista.animacion;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Corta una spritesheet horizontal (4 cuadros uno al lado del otro)
 * en frames separados y los agranda al tamaño pedido.
 *
 * Guarda lo que ya cortó: si el personaje ataca diez veces,
 * la imagen se procesa una sola vez.
 */
public class HojaSprites {

    private final int framesPorHoja = 4;

    // clave (ruta + tamaño) -> frames ya cortados
    private final Map<String, ImageIcon[]> yaCortadas = new HashMap<>();

    /**
     * Devuelve los frames de la hoja. Si el archivo no existe, devuelve un arreglo vacío.
     */
    public ImageIcon[] obtenerFrames(String ruta, int recortePrimerFrame, int ancho, int alto) {
        String clave = ruta + "-" + recortePrimerFrame + "-" + ancho + "x" + alto;

        ImageIcon[] frames = yaCortadas.get(clave);
        if (frames == null) {
            frames = cortar(ruta, recortePrimerFrame, ancho, alto);
            yaCortadas.put(clave, frames);
        }
        return frames;
    }

    /** Primer frame de la hoja, para mostrar al personaje quieto. Devuelve null si no existe. */
    public ImageIcon obtenerPrimerFrame(String ruta, int recortePrimerFrame, int ancho, int alto) {
        ImageIcon[] frames = obtenerFrames(ruta, recortePrimerFrame, ancho, alto);
        if (frames.length == 0) {
            return null;
        }
        return frames[0];
    }

    private ImageIcon[] cortar(String ruta, int recortePrimerFrame, int ancho, int alto) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la spritesheet: " + ruta);
            return new ImageIcon[0];
        }

        ImageIcon iconoHoja = new ImageIcon(url);
        Image hoja = iconoHoja.getImage();
        int altoHoja = iconoHoja.getIconHeight();
        double anchoFrame = (double) iconoHoja.getIconWidth() / framesPorHoja;

        ImageIcon[] frames = new ImageIcon[framesPorHoja];
        for (int i = 0; i < framesPorHoja; i++) {
            // columna de la hoja donde empieza y termina este frame
            int desde = (int) Math.round(i * anchoFrame);
            int hasta = (int) Math.round((i + 1) * anchoFrame);
            if (i == 0) {
                hasta = hasta - recortePrimerFrame;
            }
            frames[i] = recortarYEscalar(hoja, desde, hasta, altoHoja, ancho, alto);
        }
        return frames;
    }

    /** Copia la parte [desde, hasta] de la hoja en una imagen nueva del tamaño pedido. */
    private ImageIcon recortarYEscalar(Image hoja, int desde, int hasta, int altoHoja, int ancho, int alto) {
        BufferedImage frame = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // "Vecino más cercano": al agrandar pixel art no se ve borroso
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // destino: todo el frame nuevo (0,0 a ancho,alto)
        // origen:  la porción de la hoja (desde,0 a hasta,altoHoja)
        g.drawImage(hoja, 0, 0, ancho, alto, desde, 0, hasta, altoHoja, null);
        g.dispose();

        return new ImageIcon(frame);
    }
}
