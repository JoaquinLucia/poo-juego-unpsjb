import mvc.controlador.ControladorMenu;
import javax.swing.SwingUtilities;
import mvc.modelo.ModeloTablaPuntuacion;
import mvc.vista.VistaMenu;

public class App {
    public static void main(String[] args) throws Exception {
            SwingUtilities.invokeLater(() -> {
            ModeloTablaPuntuacion tabla = new ModeloTablaPuntuacion(); // vacía por ahora
            VistaMenu vista = new VistaMenu();
            ControladorMenu controlador = new ControladorMenu(vista, tabla);
            controlador.iniciar();
        });
    }
}
