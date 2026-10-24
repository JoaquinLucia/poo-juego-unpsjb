package modelo.catalogos;

import modelo.Habilidad;
import modelo.habilidades.HabilidadAturdir;
import modelo.habilidades.HabilidadCurar;
import modelo.habilidades.HabilidadDesangrar;
import modelo.habilidades.HabilidadVeneno;

public class CatalogoHabilidades  {

    /** Cada llamada crea una instancia nueva: nunca compartir habilidades entre entidades. */
  
    public Habilidad crearHabilidad(String id) {
        return switch (id.toLowerCase()) {

            //                                    nombre, descripción, dañoBase, cooldown, duración
            case "golpe_aturdidor" -> new HabilidadAturdir(
                    "Golpe aturdidor", "Golpea y aturde al enemigo un turno.",
                    10, 3, 1);

            //                                    nombre, descripción, dañoBase, cooldown, dañoPorTurno, duración
            case "dardo_venenoso" -> new HabilidadVeneno(
                    "Dardo venenoso", "Envenena: 4 de daño por turno durante 3 turnos.",
                    5, 3, 4, 3);

            //                                    nombre, descripción, dañoBase, cooldown, dañoInicial, duración
            case "corte_profundo" -> new HabilidadDesangrar(
                    "Corte profundo", "Abre una herida que sangra cada vez más.",
                    8, 3, 2, 3);

            case "grito" -> new HabilidadAturdir(
                    "Grito aterrador", "Aturde al enemigo sin dañarlo.",
                    0, 4, 2);

            case "curacion" -> new HabilidadCurar(
                    "Curación", "Recupera 25 de vida.",
                    25, 4);
 
            default -> throw new IllegalArgumentException("Habilidad desconocida: " + id);
        };
    }
}