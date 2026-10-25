import javax.swing.SwingUtilities;
import vista.VentanaPrincipal;
import vista.VistaMenu;
// import controlador.ControladorMenu; // Descomenta e importa cuando tengas tu controlador del menú

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Crear la ventana principal única del juego (en pantalla completa sin bordes)
            VentanaPrincipal ventana = new VentanaPrincipal();

            // 2. Crear la vista del menú principal
            VistaMenu vistaMenu = new VistaMenu();

            // 3. Agregar la vista al sistema de tarjetas (CardLayout) de la ventana
            ventana.agregarPantalla("menu", vistaMenu);

            // 4. Mostrar el menú directamente al arrancar (sin animación de fundido inicial)
            ventana.mostrarDirecto("menu");

            // 5. Ajustar la escala gráfica del menú según la resolución de la pantalla
            vistaMenu.aplicarEscala(ventana.getEscala());

            // 6. Inicializar el controlador del menú para darle vida a los botones
            // ControladorMenu controlador = new ControladorMenu(ventana, vistaMenu);
            // controlador.iniciar();
        });
    }
}