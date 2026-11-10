package controlador;

import controlador.utilidades.GestorDeSonido;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import modelo.Puntaje;
import modelo.PuntajeDAO;
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
        vista.cargando(true);
        new SwingWorker<List<Puntaje>, Void>() {
 
            @Override
            protected List<Puntaje> doInBackground() {
                return puntajeDAO.mejores(100);          // en otro hilo
            }
 
            @Override
            protected void done() {                      // en el hilo de eventos
                try {
                    abrirTablaPuntuaciones(get());
                } catch (InterruptedException | ExecutionException ex) {
                    vista.mostrarError("No se pudieron cargar los puntajes");   // cómo se muestra, lo decide la vista
                }
                vista.cargando(false);
            }
        }.execute();
    }

        //método para armar y mostrar la tabla de puntuaciones. Se llama solo desde el hilo de eventos.
        private void abrirTablaPuntuaciones(List<Puntaje> mejores){
        
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        PanelPuntuaciones modal = new PanelPuntuaciones(ventana);

        modal.limpiarPuntajes();
        int posicion = 1;
        for (Puntaje p : mejores) {
            modal.agregarPuntaje(posicion++, p.getJugador(), p.getPuntos());
        }
        modal.onCerrar(e -> modal.dispose());

        vista.setVisible(false);   // esconde el menú de fondo
        modal.setVisible(true);    // se frena acá hasta que se cierra
        vista.setVisible(true);
    }

private void mostrarOpciones() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        vista.paneles.PanelOpciones modalOpciones = new vista.paneles.PanelOpciones(ventana);

        modalOpciones.onCerrar(e -> modalOpciones.dispose());

        vista.setVisible(false);   // esconde el menú
        modalOpciones.setVisible(true); // frena acá hasta que cierra
        vista.setVisible(true);    // muestra el menú
    }

    private void salir() {
    if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
        controladorM.salirDelJuego();
    }
}
}
