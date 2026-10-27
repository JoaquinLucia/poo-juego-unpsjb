import javax.swing.SwingUtilities;
import controlador.ControladorMaestro;
import persistencia.PuntajeDAOSQLite;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inicializar la persistencia de la base de datos (SQLite)
            PuntajeDAOSQLite puntajeDAO = new PuntajeDAOSQLite();

            // Crear el Controlador Maestro (se encarga de la VentanaPrincipal, 
            //    el menú, los catálogos, la música y todos los eventos de los botones)
            ControladorMaestro controladorMaestro = new ControladorMaestro(puntajeDAO);

            //  Arrancar el juego mostrando el menú principal y la música de fondo
            controladorMaestro.iniciar();
        });
    }
}