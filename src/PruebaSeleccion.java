
// // import controlador.ControladorSeleccion;
// // import javax.swing.SwingUtilities;
// // import vista.VistaSeleccionPersonaje;

// // public class pruebaControladorseleccion {

// //     public static void main(String[] args) {
// //         SwingUtilities.invokeLater(() -> {
// //             VistaSeleccionPersonaje vista = new VistaSeleccionPersonaje();
// //             ControladorSeleccion controlador = new ControladorSeleccion(vista);
// //             vista.setVisible(true);
// //             controlador.entrar();
// //         });
// //     }
// // }
// import java.util.ArrayList;
// import java.util.List;
// import javax.swing.SwingUtilities;
// import controlador.ControladorSeleccion;
// import modelo.Habilidad;
// import modelo.Heroe;
// import modelo.SeleccionHeroe;
// import modelo.factory.FabricaHabilidad;
// import vista.VistaSeleccionPersonaje;

// public class PruebaSeleccion {

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             FabricaHabilidad fabrica = new FabricaHabilidad();
//             List<Heroe> heroes = new ArrayList<>();

//             List<Habilidad> habilidadesArtorias = new ArrayList<>();
//             habilidadesArtorias.add(fabrica.crearHabilidad("golpe_aturdidor"));
//             heroes.add(new Heroe("Artorias", 9999, 3579, 4, habilidadesArtorias,
//                     "/recursos/personajes/caballero.png", "Caballero de Gwyn"));

//             List<Habilidad> habilidadesMago = new ArrayList<>();
//             habilidadesMago.add(fabrica.crearHabilidad("dardo_venenoso"));
//             heroes.add(new Heroe("Merlin", 1234, 9876, 4, habilidadesMago,
//                     "/recursos/personajes/mago.png", "Escolta de Gwyn"));

//             SeleccionHeroe modelo = new SeleccionHeroe(heroes);
//             VistaSeleccionPersonaje vista = new VistaSeleccionPersonaje();
//             ControladorSeleccion controlador = new ControladorSeleccion(vista, modelo);

//             vista.setVisible(true);
//             controlador.entrar();
//         });
//     }
// }