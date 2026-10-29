package vista.animacion;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import recursos.rutas.RutasRecursos;

/**
 * Un personaje dibujado en la pantalla de batalla (el héroe o el enemigo).
 * Junta las piezas: busca su apariencia, pide los frames y se los da al animador.
 *
 * Antes este código estaba copiado para el héroe y para el enemigo dentro de
 * PanelBatalla. Ahora hay una sola clase y se crean dos objetos.
 */
public class PersonajeEnPantalla {

    private final int msPorFrameAtaque = 100;
    private final int msPorFrameDerrota = 120;

    private final JLabel label = new JLabel();
    private final AnimadorSprite animador = new AnimadorSprite(label);

    private final boolean esHeroe;
    private final CatalogoApariencias catalogo;
    private final HojaSprites hojas;
    private final RutasRecursos rutas;

    public PersonajeEnPantalla(boolean esHeroe, CatalogoApariencias catalogo,
                               HojaSprites hojas, RutasRecursos rutas) {
        this.esHeroe = esHeroe;
        this.catalogo = catalogo;
        this.hojas = hojas;
        this.rutas = rutas;
    }

    /** El panel lo necesita para agregarlo a la pantalla. */
    public JLabel getLabel() {
        return label;
    }

    /** Muestra al personaje quieto: el primer frame de su hoja de ataque. */
    public void mostrarQuieto(String nombre) {
        Apariencia apariencia = ubicar(nombre);
        ImageIcon frame = hojas.obtenerPrimerFrame(rutas.spriteAtaque(nombre),
                apariencia.getRecortePrimerFrame(), apariencia.getAncho(), apariencia.getAlto());
        animador.mostrar(frame);
    }

    public void animarAtaque(String nombre, Runnable alTerminar) {
        Apariencia apariencia = ubicar(nombre);
        ImageIcon[] frames = hojas.obtenerFrames(rutas.spriteAtaque(nombre),
                apariencia.getRecortePrimerFrame(), apariencia.getAncho(), apariencia.getAlto());

        // de izquierda a derecha y al final vuelve a la pose de reposo
        animador.reproducir(frames, msPorFrameAtaque, false, false, alTerminar);
    }

    public void animarDerrota(String nombre, Runnable alTerminar) {
        Apariencia apariencia = ubicar(nombre);
        // la hoja de derrota no lleva recorte (igual que en el código original)
        ImageIcon[] frames = hojas.obtenerFrames(rutas.spriteDerrota(nombre),
                0, apariencia.getAncho(), apariencia.getAlto());

        // Las hojas de derrota de los enemigos están dibujadas de derecha a izquierda:
        // se recorren al revés y terminan en el frame 0, que es el personaje caído.
        boolean alReves = !esHeroe;
        animador.reproducir(frames, msPorFrameDerrota, alReves, true, alTerminar);
    }

    public void detener() {
        animador.detener();
    }

    /** Busca la apariencia del personaje y ubica el label en su lugar. */
    private Apariencia ubicar(String nombre) {
        Apariencia apariencia;
        if (esHeroe) {
            apariencia = catalogo.buscarHeroe(nombre);
        } else {
            apariencia = catalogo.buscarEnemigo(nombre);
        }
        label.setBounds(apariencia.getX(), apariencia.getY(), apariencia.getAncho(), apariencia.getAlto());
        return apariencia;
    }
}
