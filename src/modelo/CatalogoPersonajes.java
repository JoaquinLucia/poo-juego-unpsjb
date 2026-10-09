package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Listado de todos los héroes y enemigos del juego, armados con Builder.
 * Reemplaza a las fábricas (CaballeroFactory, EnemigoEliteFactory, etc.).
 *
 * Se guarda una "receta" (Supplier) y no el objeto ya creado: cada vez que
 * se pide un personaje se construye uno NUEVO, con la vida completa y con
 * habilidades propias (si dos enemigos compartieran la misma Habilidad,
 * compartirían también el cooldown).
 */
public class CatalogoPersonajes {
    private final Map<String, Supplier<Heroe>> heroes = new HashMap<>();
    private final Map<String, Supplier<Enemigo>> enemigos = new HashMap<>();

    public CatalogoPersonajes() {
        cargarHeroes();
        cargarEnemigos();
    }

    // Valores de ejemplo: copiá acá los que tenías en tus fábricas
    private void cargarHeroes() {
        heroes.put("CABALLERO", () -> new Heroe.Builder("Caballero")
                .vida(600).ataque(35)
                .maxHabilidades(1) // Ajustamos el máximo a 1
                .habilidad(new HabilidadGolpe("Embestida", "Golpe con el escudo", 70, 3))
                .build());

        // EL MAGO: Solo curación
        heroes.put("MAGO", () -> new Heroe.Builder("Mago")
                .vida(350).ataque(20)
                .maxHabilidades(1) // Ajustamos el máximo a 1
                .habilidad(new HabilidadCuracion("Luz Divina", "Curación profunda", 120, 4))
                .build());

        // EL ARQUERO: Solo daño en ráfaga
        heroes.put("ARQUERO", () -> new Heroe.Builder("Arquero")
                .vida(450).ataque(45)
                .maxHabilidades(1) // Ajustamos el máximo a 1
                .habilidad(new HabilidadGolpe("Lluvia de Flechas", "Daño ráfaga", 80, 2))
                .build());
    }

private void cargarEnemigos() {
        enemigos.put("esclavo", () -> new Enemigo.Builder("Bruto de las Estepas")
                .vida(180).ataque(20)
                .imagen("/recursos/batalla/bruto_icon.png")
                // El Bruto pega fuerte y te deja aturdido perdiendo tu turno
                .habilidad(new HabilidadAturdir("Golpe Sísmico", "Daña y aturde por 1 turno", 30, 4)) 
                .build());

        enemigos.put("guardia_hueco", () -> new Enemigo.Builder("Víbora del Foso")
                .vida(200).ataque(30)
                .imagen("/recursos/batalla/vibora_icon.png")
                // La víbora ataca rápido con mucho daño bruto
                .habilidad(new HabilidadGolpe("Mordedura Letal", "Ataque perforante", 45, 2))
                .build());

        enemigos.put("sacerdotisa", () -> new Enemigo.Builder("Viuda de Sangre")
                .vida(240).ataque(22)
                .imagen("/recursos/batalla/viuda_icon.png")
                // La Viuda se cura a sí misma con el daño que te hace
                .habilidad(new HabilidadRoboVida("Beso Venenoso", "Roba vida al héroe", 40, 3)) 
                .build());

        enemigos.put("guardian_cenizas", () -> new Enemigo.Builder("Guardia Infernal")
                .vida(850).ataque(35)
                .maxHabilidades(2)
                .imagen("/recursos/batalla/guardia_infernal_icon.png")
                // El Jefe tiene ambas cosas: pega durísimo, aturde y se cura.
                .habilidad(new HabilidadAturdir("Empalamiento", "Daña fuertemente y aturde", 60, 4))
                .habilidad(new HabilidadRoboVida("Cosecha de Almas", "Daña y se cura", 40, 3))
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
