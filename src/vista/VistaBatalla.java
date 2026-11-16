package vista;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import recursos.rutas.RutasRecursos;
import vista.animacion.CatalogoApariencias;
import vista.animacion.HojaSprites;
import vista.paneles.PanelBatalla;
import vista.paneles.PanelInferior;

/**
 * Pantalla de combate. Es un panel dentro de VentanaPrincipal.
 * No conoce el modelo: el controlador le pasa textos y números,
 * y ella decide cómo mostrarlos (qué imagen, qué animación).
 */
public class VistaBatalla extends JPanel {

    private final PanelBatalla panelBatalla;
    private final PanelInferior panelInferior = new PanelInferior();
    private Timer timerTemblor;
    private int pasosTemblor;

    public VistaBatalla(RutasRecursos rutas) {
        super(new BorderLayout(0, 0));
        this.panelBatalla = new PanelBatalla(rutas, new CatalogoApariencias(), new HojaSprites());
        add(panelBatalla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // --- EFECTOS VISUALES ---

    public void efectoTemblor() {
        if (timerTemblor != null && timerTemblor.isRunning()) {
            return;
        }
        int x = panelBatalla.getX();
        int y = panelBatalla.getY();
        pasosTemblor = 0;

        timerTemblor = new Timer(20, e -> {
            pasosTemblor++;
            if (pasosTemblor >= 8) {
                panelBatalla.setLocation(x, y);   // vuelve a su lugar
                timerTemblor.stop();
            } else {
                panelBatalla.setLocation(x + (int) (Math.random() * 6 - 3),
                                         y + (int) (Math.random() * 6 - 3));
            }
        });
        timerTemblor.start();
    }

    // --- MÉTODOS PARA QUE EL CONTROLADOR ACTUALICE LA INTERFAZ ---

    /** Corta animaciones de la pelea anterior y prepara las barras para la nueva. */
    public void prepararNuevaBatalla() {
        panelBatalla.reiniciar();
    }

    /** Recibe el nombre del lugar (ej. "BOSQUE"); la vista sabe qué imagen le corresponde. */
    public void mostrarFondoCombate(String lugar) {
        panelBatalla.mostrarFondoCombate(lugar);
    }

    public void actualizarEstadisticas(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        panelBatalla.actualizarBarras(nombreHeroe, vidaHeroe, nombreEnemigo, vidaEnemigo);
    }

    public void mostrarResultado(boolean gano) {
        if (gano) {
            mostrarResultado("¡Victoria!");
        } else {
            mostrarResultado("Derrota...");
        }
    }

    public void mostrarResultado(String mensaje) {
        panelInferior.mostrarResultado(mensaje);
    }

    public void mostrarEnfriamiento(String habilidad, int turnos) {
        mostrarResultado(habilidad + " está en enfriamiento: " + turnos + " turnos");
    }

    public void mostrarTurno(String nombre) {
        panelInferior.mostrarTurno("Turno de " + nombre);
    }

    public void setBotonesHabilitados(boolean estado) {
        panelInferior.setBotonesHabilitados(estado);
    }

    public void mostrarRetratos(String rutaHeroe, String rutaEnemigo) {
        panelInferior.setRetratos(rutaHeroe, rutaEnemigo);
    }

    /** Muestra el resultado final y pide el nombre del jugador. Devuelve null si cancela. */
    public String pedirNombreFinal(boolean derrotoAlJefe, int puntos) {
        String titulo;
        if (derrotoAlJefe) {
            titulo = "¡Derrotaste al jefe!";
        } else {
            titulo = "Has caído en la mazmorra...";
        }
        String mensaje = titulo + "\nPuntaje: " + puntos + "\n\nIngresá tu nombre:";
        return JOptionPane.showInputDialog(this, mensaje, "Fin de la partida", JOptionPane.PLAIN_MESSAGE);
    }

    public void mostrarAturdido(String nombre) {
        mostrarResultado(nombre + " está aturdido y pierde el turno");
    }

    // --- ANIMACIONES (reciben nombres; las rutas las resuelve la vista) ---

    public void mostrarPersonajes(String nombreHeroe, String nombreEnemigo) {
        panelBatalla.mostrarPersonajes(nombreHeroe, nombreEnemigo);
    }

    public void animarAtaqueHeroe(String nombre, Runnable alTerminar) {
        panelBatalla.animarAtaqueHeroe(nombre, alTerminar);
    }

    public void animarAtaqueEnemigo(String nombre, Runnable alTerminar) {
        panelBatalla.animarAtaqueEnemigo(nombre, alTerminar);
    }

    public void animarDerrotaHeroe(String nombre, Runnable alTerminar) {
        panelBatalla.animarDerrotaHeroe(nombre, alTerminar);
    }

    public void animarDerrotaEnemigo(String nombre, Runnable alTerminar) {
        panelBatalla.animarDerrotaEnemigo(nombre, alTerminar);
    }

    // --- ENGANCHES DE BOTONES PARA EL CONTROLADOR ---

    public void onAtacar(ActionListener listener) {
        reemplazarAccion(panelInferior.getBotonAtacar(), listener);
    }

    public void onHabilidad(ActionListener listener) {
        reemplazarAccion(panelInferior.getBotonHabilidad(), listener);
    }

    public void onDefenderse(ActionListener listener) {
        reemplazarAccion(panelInferior.getBotonDefenderse(), listener);
    }

    /** Quita los listeners de una batalla anterior para que un clic no dispare dos veces. */
    private void reemplazarAccion(JButton boton, ActionListener listener) {
        for (ActionListener anterior : boton.getActionListeners()) {
            boton.removeActionListener(anterior);
        }
        boton.addActionListener(listener);
    }
}
