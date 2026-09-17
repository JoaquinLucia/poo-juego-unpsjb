import javax.swing.SwingUtilities;
import controlador.Juego;
import modelo.*;
import modelo.enemigos.Goblin;
import vista.VistaJuego;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Heroe heroe = new Heroe("Guerrero", 100, 15, 1, 5);
            Enemigo goblin = new Goblin("Goblin Ladrón", 50, 8, 1);

            VistaJuego vista = new VistaJuego();
            
            // Se inicializa el controlador sin guardar la referencia no utilizada
            new Juego(vista, heroe, goblin);

            vista.mostrarVentana();
        });
    }
}