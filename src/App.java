import javax.swing.SwingUtilities;

import controlador.ControladorMaestro;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControladorMaestro().iniciar());
    }
}