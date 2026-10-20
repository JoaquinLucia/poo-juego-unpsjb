// import controlador.ControladorBatalla;
// import java.util.ArrayList;
// import java.util.List;
// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;
// import modelo.Batalla;
// import modelo.Enemigo;
// import modelo.Habilidad;
// import modelo.Heroe;
// import modelo.factory.FabricaEnemigo;
// import modelo.factory.FabricaHabilidad;
// import vista.VistaBatalla;

// public class PruebaBatalla {

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             // ---- Fábricas ----
//             FabricaHabilidad fabricaHabilidad = new FabricaHabilidad();
//             FabricaEnemigo fabricaEnemigo = new FabricaEnemigo(fabricaHabilidad);

//             // ---- Héroe de prueba ----
//             List<Habilidad> habilidadesHeroe = new ArrayList<>();
//             habilidadesHeroe.add(fabricaHabilidad.crearHabilidad("golpe_aturdidor"));
//             Heroe heroe = new Heroe("Artorias", 9999, 1, 4, habilidadesHeroe,
//                 "/recursos/personajes/caballero.png", 
//                 "/recursos/artoriasFrontal.jpg","Caballero de Gwyn");

//             // ---- Enemigo de prueba ----
//             Enemigo enemigo = fabricaEnemigo.crearPersonaje("cazador");

//             // ---- MVC ----
//             Batalla batalla = new Batalla(heroe, enemigo);
//             VistaBatalla vista = new VistaBatalla();
//             ControladorBatalla controlador = new ControladorBatalla(vista, batalla);

//             // ---- Ventana ----
//             JFrame ventana = new JFrame("Prueba - Batalla");
//             ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             ventana.setContentPane(vista);
//             ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
//             ventana.setVisible(true);

//             controlador.iniciar();
//         });
//     }
// }