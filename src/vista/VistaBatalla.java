package vista;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import vista.paneles.PanelBatalla;
import vista.paneles.PanelInferior; 

 // Pantalla de combate. Es un panel dentro de VentanaPrincipal

public class VistaBatalla extends JPanel{
    private final PanelBatalla panelFondo= new PanelBatalla();
    private final PanelInferior panelInferior = new PanelInferior();
    private Timer timerTemblor;
    private int pasosTemblor;


    public VistaBatalla() {        
        super(new BorderLayout(0, 0));
        add(panelFondo, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // --- EFECTOS VISUALES ---
    public void efectoTemblor() {
        if (timerTemblor != null && timerTemblor.isRunning()) {
            return;
        }
        int x = panelFondo.getX();
        int y = panelFondo.getY();
        pasosTemblor = 0;

        timerTemblor = new Timer(20, e -> {
            if (++pasosTemblor >= 8) {
                panelFondo.setLocation(x, y);   // vuelve a su lugar
                timerTemblor.stop();
            } else {
                panelFondo.setLocation(x + (int) (Math.random() * 6 - 3),
                                       y + (int) (Math.random() * 6 - 3));
            }
        });
        timerTemblor.start();
    }
    // --- MÉTODOS PARA QUE EL CONTROLADOR ACTUALICE LA INTERFAZ ---
    public void actualizarEstadisticas(String nombreHeroe, int vidaHeroe, String nombreEnemigo, int vidaEnemigo) {
        panelFondo.actualizarBarras(nombreHeroe, vidaHeroe, nombreEnemigo, vidaEnemigo);
    }
    public void mostrarResultado(boolean gano) {
        mostrarResultado(gano ? "¡Victoria!" : "Derrota...");
    }

    public void mostrarResultado(String mensaje) {
        panelInferior.mostrarResultado(mensaje);
    }
    
    public void mostrarAtaque(String atacante, String objetivo, int danio) {
        mostrarResultado(atacante + " atacó a " + objetivo + " e hizo " + danio + " de daño");
    }

    public void mostrarHabilidad(String atacante, String habilidad, String objetivo, int danio) {
        mostrarResultado(atacante + " usó " + habilidad + " sobre " + objetivo + " (" + danio + " de daño)");
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
        String titulo = derrotoAlJefe ? "¡Derrotaste al jefe!" : "Has caído en la mazmorra...";
        String mensaje = titulo + "\nPuntaje: " + puntos + "\n\nIngresá tu nombre:";
        return javax.swing.JOptionPane.showInputDialog(this, mensaje, "Fin de la partida",
                javax.swing.JOptionPane.PLAIN_MESSAGE);
    }
    //nuevo revisa 
    // /** Muestra el resultado final y pide el nombre del jugador. Devuelve null si cancela. */
    // public String pedirNombreFinal(boolean derrotoAlJefe, int puntos) {
    //     String titulo = derrotoAlJefe ? "¡Derrotaste al jefe!" : "Has caído en la mazmorra...";
    //     return JOptionPane.showInputDialog(this,
    //             titulo + "\nPuntaje: " + puntos + "\n\nIngresá tu nombre:",
    //             "Fin de la partida", JOptionPane.PLAIN_MESSAGE);
    // }

    // --- ENGANCHES DE BOTONES PARA EL CONTROLADOR ---

    public void onAtacar(ActionListener listener) {
        panelInferior.getBotonAtacar().addActionListener(listener);
    }

    public void onHabilidad(ActionListener listener) {
        panelInferior.getBotonHabilidad().addActionListener(listener);
    }
}
