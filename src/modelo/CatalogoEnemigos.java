package modelo;

import java.util.List;
import modelo.factory.FabricaHabilidad;

// Se puede prescindir del factory de enemigos y solo hacer un switch; el resto es sobreingeniería
public class CatalogoEnemigos {

    private final FabricaHabilidad fabricaHabilidad;

    public CatalogoEnemigos(FabricaHabilidad fabricaHabilidad) {
        this.fabricaHabilidad = fabricaHabilidad;
    }

    /** Crea el enemigo con sus estadísticas ya ajustadas a la dificultad. */
    public Enemigo crearPersonaje(String tipo, Dificultad dificultad) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de enemigo no puede ser null");
        }
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser null");
        }
        return switch (tipo.toLowerCase()) {
            case "esclavo" -> new Enemigo("Esclavo", 80, 8, 1,
                    List.of(fabricaHabilidad.crearHabilidad("grito")),
                    "/recursos/personajes/mago.png", "/recursos/esclavoFrontal.jpg",
                    dificultad);
            case "cazador" -> new Enemigo("Cazador", 60, 10, 1,
                    List.of(fabricaHabilidad.crearHabilidad("corte_profundo")),
                    "/recursos/personajes/mago.png", "/recursos/cazadorFrontal.jpg",
                    dificultad);
            case "jester" -> new Enemigo("Jestar", 60, 10, 1,
                    List.of(fabricaHabilidad.crearHabilidad("dardo_venenoso")),
                    "/recursos/personajes/mago.png", "/recursos/jesterFrontal.jpg",
                    dificultad);
            case "akuma" -> new Enemigo("Akuma", 300, 20, 2,
                    List.of(fabricaHabilidad.crearHabilidad("golpe_aturdidor"),
                            fabricaHabilidad.crearHabilidad("corte_profundo")),
                    "/recursos/personajes/mago.png", "/recursos/akumaFrontal.jpg",
                    dificultad);
            default -> throw new IllegalArgumentException("Enemigo desconocido: " + tipo);
        };
    }
}