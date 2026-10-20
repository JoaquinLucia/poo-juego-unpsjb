// import controlador.ControladorCaminoMazmorra;
// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;
// import modelo.ModeloCaminoMazmorra;
// import vista.VistaCaminoMazmorra;

// /**
//  * Prueba la elección de dificultad sola, sobre la vista del camino.
//  */
// public class PruebaDificultad {
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             // Modelo
//             ModeloCaminoMazmorra modelo = new ModeloCaminoMazmorra();

//             // Vista
//             VistaCaminoMazmorra vista = new VistaCaminoMazmorra();

//             // Controlador: recibe solo el modelo y la vista
//             new ControladorCaminoMazmorra(modelo, vista);

//             JFrame ventana = new JFrame("Prueba - Dificultad");
//             ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             ventana.setContentPane(vista);
//             ventana.setSize(1000, 700);
//             ventana.setLocationRelativeTo(null);
//             ventana.setVisible(true);
//         });
//     }
// }