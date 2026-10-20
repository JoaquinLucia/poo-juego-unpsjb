/* 
// public class App {
//     public static void main(String[] args) {
       
      
//     }
// }
import controlador.ControladorBatalla;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import modelo.Batalla;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.factory.FabricaHeroe;
import vista.VistaBatalla;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- Modelo ---
            FabricaHeroe fabrica = new FabricaHeroe();
            Heroe heroe = fabrica.crearPersonaje("caballero");
            Enemigo enemigo = new Enemigo("Lobo", 60, 8, 1, List.of());
            Batalla batalla = new Batalla(heroe, enemigo);

            // --- Vista ---
            VistaBatalla vista = new VistaBatalla();

            // --- Controlador (se registra como observador de la batalla) ---
            ControladorBatalla controlador = new ControladorBatalla(vista, batalla);

            // --- Ventana ---
            JFrame ventana = new JFrame("Senderos de las Cenizas - Batalla");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(vista);
            ventana.setSize(800, 600);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

            // --- Arranque: primer dibujo y botones habilitados ---
            controlador.iniciar();
        });
    }
}*/