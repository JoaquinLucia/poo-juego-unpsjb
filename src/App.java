import controlador.ControladorMaestro;
import javax.swing.SwingUtilities;
import modelo.PuntajeDAO;
import persistencia.ConexionBD;
import persistencia.PuntajeDAOSQLite;

public class App {
    public static void main(String[] args) {
        // 1. Arrancamos la conexión a la base de datos (crea el archivo y tabla si no existen)
        try {
            ConexionBD.getInstancia();
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo conectar a la base de datos - " + e.getMessage());
        }

        // 2. Ejecutamos el juego en el hilo seguro de Swing
        SwingUtilities.invokeLater(() -> {
            // Instanciamos la persistencia
            PuntajeDAO dao = new PuntajeDAOSQLite();
            
            // Creamos al orquestador principal inyectándole la base de datos
            ControladorMaestro controlador = new ControladorMaestro(dao);
            
            controlador.iniciar();
        });
    }
}