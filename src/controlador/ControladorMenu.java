package controlador;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import modelo.Puntaje;
import modelo.PuntajeDAO;
import persistencia.ConexionBD;
import vista.VistaMenu;
import vista.paneles.PanelPuntuaciones;

/*
 * Controla el menú principal. Lo que lleva a otra pantalla se lo avisa al mediador;
 * lo que se resuelve dentro del menú (puntajes, opciones, salir) lo hace solo.
 * Recibe el DAO como interfaz: no sabe en qué base se guardan los puntajes.
 */
public class ControladorMenu {

    private final VistaMenu vista;
    private final PuntajeDAO puntajeDAO;
    private final ControladorMaestro controladorM;
    public ControladorMenu(VistaMenu vista, PuntajeDAO puntajeDAO, ControladorMaestro controlador) {
        
        this.vista = vista;
        this.puntajeDAO = puntajeDAO;
        this.controladorM = controlador;
        vista.onComenzar(e -> controlador.comenzar());
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

    /** Deja el menú principal en primer plano. */
    public void mostrar() {
        vista.mostrarMenu();
    }

    private void mostrarPuntuaciones() {
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        PanelPuntuaciones modal = new PanelPuntuaciones(ventana);

        modal.limpiarPuntajes();
        int posicion = 1;
        for (Puntaje p : puntajeDAO.mejores(100)) {
            modal.agregarPuntaje(posicion++, p.getJugador(), p.getPuntos());
        }
        modal.onCerrar(e -> modal.dispose());

        vista.setVisible(false);   // esconde el menú de fondo
        modal.setVisible(true);    // se frena acá hasta que se cierra
        vista.setVisible(true);
    }

    private void mostrarOpciones() {
        vista.mostrarMensaje("Opciones (próximamente)");
    }

    private void salir() {
        if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
            ConexionBD.cerrar();
            System.exit(0);
        }
    }

}
