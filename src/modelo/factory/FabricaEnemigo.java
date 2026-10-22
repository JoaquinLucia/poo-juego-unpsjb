package modelo.factory;

import java.util.List;
import modelo.Enemigo;

public class FabricaEnemigo implements FabricaPersonaje {

    private final FabricaHabilidad fabricaHabilidad;

    public FabricaEnemigo(FabricaHabilidad fabricaHabilidad) {
        if (fabricaHabilidad == null) {
            throw new IllegalArgumentException("La fábrica de habilidades no puede ser null");
        }
        this.fabricaHabilidad = fabricaHabilidad;
    }

    @Override
    public Enemigo crearPersonaje(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de enemigo no puede ser null");
        }
        return switch (tipo.toLowerCase()) {
            case "esclavo" -> new Enemigo("Esclavo", 80, 8, 2, // Le subimos el límite a 2
                List.of(fabricaHabilidad.crearHabilidad("grito"),
                        fabricaHabilidad.crearHabilidad("curacion")), // ¡Ahora podés robar curación!
                "/recursos/personajes/mago.png","/recursos/esclavoFrontal.jpg");
                
            case "cazador" -> new Enemigo("Cazador", 60, 10, 1,
                List.of(fabricaHabilidad.crearHabilidad("corte_profundo")),
                    "/recursos/personajes/mago.png","/recursos/cazadorFrontal.jpg");
                    
            case "jester" -> new Enemigo("Jestar", 60, 10, 1,
                List.of(fabricaHabilidad.crearHabilidad("niebla_toxica")),
                    "/recursos/personajes/mago.png","/recursos/jesterFrontal.jpg");

            case "akuma" -> new Enemigo("Akuma", 300, 20, 2,
                List.of(fabricaHabilidad.crearHabilidad("mazazo"),
                    fabricaHabilidad.crearHabilidad("grito")),
                    "/recursos/personajes/mago.png","/recursos/akumaFrontal.jpg");

            default -> throw new IllegalArgumentException("Enemigo desconocido: " + tipo);
        };
    }
}