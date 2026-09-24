import javax.swing.SwingUtilities;
import controlador.Juego;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.enemigos.Goblin;
import vista.VistaJuego;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Creamos el Modelo (El Héroe y el Enemigo)
            Heroe heroe = new Heroe("Lancelot", 100, 20, 1, 3);
            Enemigo enemigo = new Goblin("Goblin Salvaje", 50, 10, 15);

            // 2. Creamos la Vista pasándole el modelo
            VistaJuego vista = new VistaJuego(heroe);

            // 3. ¡Registramos la vista como observadora del modelo!
            heroe.agregarObservador(vista);

            // 4. Creamos el Controlador pasándole la vista, el héroe y el enemigo
            new Juego(vista, heroe, enemigo);

            // 5. Mostramos la ventana
            vista.mostrarVentana();
        });
    }
}