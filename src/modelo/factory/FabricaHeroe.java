// package modelo.factory;
 
// import java.util.ArrayList;
// import java.util.List;
// import modelo.DatosEntidad;
// import modelo.Habilidad;
// import modelo.Heroe;
 
// /**
//  * Crea héroes a partir de sus datos (DatosEntidad).
//  *
//  * Solo crea: no guarda datos, no lee la base y no conoce el DAO.
//  * Los datos se los pasa quien la usa (por ejemplo, el héroe elegido en la selección).
//  */
// public class FabricaHeroe implements FabricaPersonaje {
 
//     private FabricaHabilidades fabricaHabilidad;
 
//     public FabricaHeroe(FabricaHabilidades fabricaHabilidad) {
//         this.fabricaHabilidad = fabricaHabilidad;
//     }
 
//     @Override
//     public Heroe crearPersonaje(DatosEntidad datos) {
//         List<Habilidad> habilidades = new ArrayList<>();
//         for (String idHabilidad : datos.habilidades()) {
//             habilidades.add(fabricaHabilidad.crearHabilidad(idHabilidad));
//         }
 
//         return new Heroe(datos.nombre(), datos.vida(), datos.ataque(),
//                 datos.maxHabilidades(), habilidades);
//     }

// }
 
