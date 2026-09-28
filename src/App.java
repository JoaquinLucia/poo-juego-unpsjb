import controlador.ControladorMenu;
import javax.swing.SwingUtilities;
import modelo.ModeloTablaPuntuacion;
import vista.VistaMenu;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            ModeloTablaPuntuacion tabla = new ModeloTablaPuntuacion(); 
            VistaMenu vistaMenu = new VistaMenu();
            ControladorMenu controladorMenu = new ControladorMenu(vistaMenu, tabla);
            
            
            controladorMenu.iniciar();
        });
    }
}