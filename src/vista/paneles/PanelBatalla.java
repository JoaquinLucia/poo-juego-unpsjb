package vista.paneles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import recursos.rutas.RutasRecursos;
import vista.animacion.CatalogoApariencias;
import vista.animacion.HojaSprites;
import vista.animacion.PersonajeEnPantalla;

/**
 * Parte de arriba de la pantalla de combate: fondo, barras de vida y personajes.
 * Este panel solo ARMA la pantalla y reparte los pedidos:
 *  - el fondo lo pinta PanelFondo (de quien hereda)
 *  - las barras las maneja BarraVida
 *  - los personajes los maneja PersonajeEnPantalla
 */
public class PanelBatalla extends PanelFondo {

    private final RutasRecursos rutas;
    private final CatalogoApariencias catalogo;
    private final HojaSprites hojas;

    
    private final BarraVida barraHeroe = new BarraVida("HÉROE", new Color(50, 200, 50));       // verde
    private final BarraVida barraEnemigo = new BarraVida("ENEMIGO", new Color(200, 50, 50));   // rojo

    private final PersonajeEnPantalla heroe;
    private final PersonajeEnPantalla enemigo;

    // true hasta la primera actualización de vidas de cada pelea
    private boolean primeraVez = true;

    public PanelBatalla(RutasRecursos rutas, CatalogoApariencias catalogo, HojaSprites hojas) {
        super(new GridBagLayout());
        this.rutas = rutas;
        this.catalogo = catalogo;
        this.hojas = hojas;
        

        this.heroe = new PersonajeEnPantalla(true, catalogo, hojas, rutas);
        this.enemigo = new PersonajeEnPantalla(false, catalogo, hojas, rutas);

        setFondo(rutas.getFondoPorDefecto());
        agregarBarras();
        agregarPersonajes();
    }

    private void agregarBarras() {
        JPanel panelVida = new JPanel(new GridLayout(1, 2, 40, 0));
        panelVida.setOpaque(false);
        panelVida.add(barraHeroe);
        panelVida.add(barraEnemigo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 80, 0, 80);
        add(panelVida, gbc);
    }

    private void agregarPersonajes() {
        // Layout null: cada personaje se ubica con coordenadas exactas (ver CatalogoApariencias)
        JPanel panelSprites = new JPanel(null);
        panelSprites.setOpaque(false);
        panelSprites.setPreferredSize(new Dimension(1200, 400));
        panelSprites.add(heroe.getLabel());
        panelSprites.add(enemigo.getLabel());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelSprites, gbc);
    }

    // ---------- Pedidos que llegan desde VistaBatalla ----------

    /** Deja el panel listo para una pelea nueva. */
    public void reiniciar() {
        barraHeroe.detener();
        barraEnemigo.detener();
        heroe.detener();
        enemigo.detener();
        primeraVez = true;
    }

    public void mostrarFondoCombate(String lugar) {
        setFondo(rutas.fondoCombate(lugar));
    }

    public void actualizarBarras(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        barraHeroe.setNombre(nombreHeroe);
        barraEnemigo.setNombre(nombreEnemigo);

        // la vida nunca se muestra negativa
        vidaHeroe = Math.max(0, vidaHeroe);
        vidaEnemigo = Math.max(0, vidaEnemigo);

        if (primeraVez) {
            barraHeroe.fijarVidaInicial(vidaHeroe);
            barraEnemigo.fijarVidaInicial(vidaEnemigo);
            primeraVez = false;
        } else {
            barraHeroe.animarHasta(vidaHeroe);
            barraEnemigo.animarHasta(vidaEnemigo);
        }
    }

    public void mostrarPersonajes(String nombreHeroe, String nombreEnemigo) {
        heroe.mostrarQuieto(nombreHeroe);
        enemigo.mostrarQuieto(nombreEnemigo);
    }

    public void animarAtaqueHeroe(String nombre, Runnable alTerminar) {
        heroe.animarAtaque(nombre, alTerminar);
    }

    public void animarAtaqueEnemigo(String nombre, Runnable alTerminar) {
        enemigo.animarAtaque(nombre, alTerminar);
    }

    public void animarDerrotaHeroe(String nombre, Runnable alTerminar) {
        heroe.animarDerrota(nombre, alTerminar);
    }

    public void animarDerrotaEnemigo(String nombre, Runnable alTerminar) {
        enemigo.animarDerrota(nombre, alTerminar);
    }
}
