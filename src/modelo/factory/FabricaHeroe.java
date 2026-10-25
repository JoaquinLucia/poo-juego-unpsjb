package modelo.factory;

import java.util.ArrayList;
import java.util.List;
import modelo.Habilidad;
import modelo.Heroe;

/**
 * Crea héroes a partir de su tipo en texto.
 */
public class FabricaHeroe implements FabricaPersonaje {

    private FabricaHabilidad fabricaHabilidad;

    public FabricaHeroe() {
        this.fabricaHabilidad = new FabricaHabilidad();
    }

    public FabricaHeroe(FabricaHabilidad fabricaHabilidad) {
        this.fabricaHabilidad = fabricaHabilidad;
    }

    @Override
    public Heroe crearPersonaje(String tipo) {
        List<Habilidad> habilidades = new ArrayList<>();
        
        if (tipo.equalsIgnoreCase("caballero")) {
            habilidades.add(fabricaHabilidad.crearHabilidad("golpe_aturdidor"));
            return new Heroe("Artorias", 9999, 1, 4, habilidades, 
                    "/recursos/personajes/caballero.png", 
                    "/recursos/artoriasFrontal.jpg", "Caballero de Gwyn");
        }
        
        return null;
    }
}
 
