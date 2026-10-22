package vista;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import modelo.Habilidad;
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

    public void mostrarDefensa(String nombre) {
        panelInferior.mostrarResultado(nombre + " adopta una postura defensiva");
    }

// Este método arma el menú flotante con las habilidades disponibles
    public void mostrarMenuHabilidades(java.util.List<String> nombres, java.util.function.Consumer<Integer> onSeleccion) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        menu.setBackground(new java.awt.Color(20, 18, 17));
        menu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(135, 105, 58), 2));

        for (int i = 0; i < nombres.size(); i++) {
            final int index = i;
            javax.swing.JMenuItem item = new javax.swing.JMenuItem(nombres.get(i));
            item.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 18));
            item.setBackground(new java.awt.Color(40, 35, 30));
            item.setForeground(new java.awt.Color(230, 210, 165));
            
            item.addActionListener(e -> onSeleccion.accept(index));
            menu.add(item);
        }
        
        javax.swing.JButton btn = panelInferior.getBotonHabilidad();
        menu.show(btn, 0, -menu.getPreferredSize().height);
    }

    // Método actualizado para recibir las opciones dinámicas del botín
    public int mostrarBotin(String nombreEnemigo, modelo.Habilidad habilidad, Object[] opciones) {
        vista.DialogoRecompensa dialogo = new vista.DialogoRecompensa(
            (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
            nombreEnemigo, habilidad, opciones
        );
        return dialogo.mostrarDialogo();
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
    public void onDefenderse(ActionListener listener) {
        panelInferior.getBotonDefenderse().addActionListener(listener);
    }

    // --- ANIMACIONES ---

    public void actualizarRetratos(String rutaHeroe, String rutaEnemigo) {
        panelInferior.setRetratos(rutaHeroe, rutaEnemigo);
    }

    public void reproducirAnimacion(AnimacionSprite animacion) {
        panelFondo.reproducirAnimacion(animacion);
    }

    public java.awt.Image[] recortarSpriteSheet(String ruta, int totalFrames) {
        return panelFondo.recortarSpriteSheet(ruta, totalFrames);
    }
    public void setSpriteHeroeBatalla(java.awt.Image img) {
        panelFondo.setSpriteHeroeBatalla(img);
    }
}
