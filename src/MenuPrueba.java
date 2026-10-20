// import controlador.ControladorMenu;
// import javax.swing.SwingUtilities;
// import vista.VentanaPrincipal;
// import vista.VistaMenu;

// public class MenuPrueba {

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             VentanaPrincipal ventana = new VentanaPrincipal();

//             VistaMenu vistaMenu = new VistaMenu();
//             vistaMenu.aplicarEscala(ventana.getEscala());   // adapta imágenes al tamaño de la pantalla

//             ControladorMenu controlador = new ControladorMenu(vistaMenu);

//             ventana.agregarPantalla("menu", vistaMenu);
//             ventana.mostrarDirecto("menu");                 // la hace visible
//             controlador.mostrar();
//         });
//     }
// }