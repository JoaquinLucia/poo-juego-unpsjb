package modelo.factory;

import modelo.Habilidad;
import modelo.habilidades.HabilidadAturdir;
import modelo.habilidades.HabilidadCurar;
import modelo.habilidades.HabilidadDesangrar;
import modelo.habilidades.HabilidadVeneno;

public class FabricaHabilidad implements FabricaHabilidades {

    @Override
    public Habilidad crearHabilidad(String id) {
        return switch (id.toLowerCase()) {

            case "golpe_aturdidor" -> new HabilidadAturdir(
                    "Golpe aturdidor", "Golpea y aturde al enemigo un turno.",
                    10, 3, 1);

            case "dardo_venenoso" -> new HabilidadVeneno(
                    "Dardo venenoso", "Envenena: 4 de daño por turno durante 3 turnos.",
                    5, 3, 4, 3);

            case "corte_profundo" -> new HabilidadDesangrar(
                    "Corte profundo", "Abre una herida que sangra cada vez más.",
                    8, 3, 2, 3);

            case "grito" -> new HabilidadAturdir(
                    "Grito aterrador", "Aturde al enemigo sin dañarlo.",
                    0, 4, 1);

            case "curacion" -> new HabilidadCurar(
                    "Curación", "Recupera 25 de vida.",
                    25, 4);
            case "niebla_toxica" -> new HabilidadVeneno(
                    "Niebla tóxica", "Nube venenosa que hace 3 de daño por 4 turnos.",
                    5, 4, 3, 4);
            
            case "mazazo" -> new HabilidadAturdir(
                    "Mazazo brutal", "Golpe pesado que aturde por 2 turnos.",
                    15, 5, 2);

            default -> throw new IllegalArgumentException("Habilidad desconocida: " + id);
        };
    }
}