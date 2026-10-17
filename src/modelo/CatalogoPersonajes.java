package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Listado de todos los héroes y enemigos del juego, armados con Builder.
 * Se guarda una "receta" (Supplier) para que cada vez que se pida un personaje se construya uno NUEVO.
 */
public class CatalogoPersonajes {
    private final Map<String, Supplier<Heroe>> heroes = new HashMap<>();
    private final Map<String, Supplier<Enemigo>> enemigos = new HashMap<>();

    public CatalogoPersonajes() {
        cargarHeroes();
        cargarEnemigos();
    }

    private void cargarHeroes() {
        heroes.put("CABALLERO", () -> new Heroe.Builder("Caballero")
                .vida(800).ataque(30)
                .maxHabilidades(2) 
                .habilidad(new HabilidadGolpe("Embestida", "Golpe con el escudo", 160, 2))
                .build());

        // EL MAGO: Solo curación
        heroes.put("MAGO", () -> new Heroe.Builder("Mago")
                .vida(600).ataque(60)
                .maxHabilidades(2)
                .habilidad(new HabilidadGolpe("Bola de Almas", "Ataque mágico", 140, 3))
                .build());

        // EL ARQUERO: Solo daño en ráfaga
        heroes.put("ARQUERO", () -> new Heroe.Builder("Arquero")
                .vida(700).ataque(45)
                .maxHabilidades(2)
                .habilidad(new HabilidadGolpe("Lluvia de Flechas", "Daño ráfaga", 80, 2))
                .build());
    }

    private void cargarEnemigos() {
        enemigos.put("bruto", () -> new Enemigo.Builder("Bruto")
                .vida(320).ataque(25) // Ajustado a los 180 de HP ideales para el primer combate
                .imagen("/recursos/batalla/bruto_icon.png")
                .habilidad(new HabilidadAturdir("Golpe Sísmico", "Daña y aturde por 1 turno", 35, 2)) 
                .build());

        enemigos.put("vibora", () -> new Enemigo.Builder("Vibora")
                .vida(380).ataque(35)
                .imagen("/recursos/batalla/vibora_icon.png")
                .habilidad(new HabilidadInmunidad("Piel Escamosa", "Se vuelve inmune por 1 turno", 3))
                .build());

        enemigos.put("viuda", () -> new Enemigo.Builder("Viuda")
                .vida(450).ataque(40)
                .imagen("/recursos/batalla/viuda_icon.png")
                .habilidad(new HabilidadRoboVida("Beso Venenoso", "Roba vida al héroe", 55, 3)) 
                .build());

        enemigos.put("guardian", () -> new Enemigo.Builder("Guardian")
                .vida(650).ataque(45)
                .maxHabilidades(2)
                .imagen("/recursos/batalla/guardia_infernal_icon.png")
                .habilidad(new HabilidadAturdir("Empalamiento", "Daña fuertemente y aturde", 70, 4))
                .habilidad(new HabilidadRoboVida("Cosecha de Almas", "Daña y se cura", 55, 3))
                .build());
    }

    public Heroe crearHeroe(Arquetipo arquetipo) {
        Supplier<Heroe> receta = heroes.get(arquetipo.name());
        if (receta == null) {
            throw new IllegalArgumentException("No hay héroe cargado para: " + arquetipo.name());
        }
        return receta.get();
    }

    public Enemigo crearEnemigo(String id) {
        Supplier<Enemigo> receta = enemigos.get(id);
        if (receta == null) {
            throw new IllegalArgumentException("No existe el enemigo: " + id);
        }
        return receta.get();
    }

    public boolean existeEnemigo(String id) {
        return enemigos.containsKey(id);
    }
}
