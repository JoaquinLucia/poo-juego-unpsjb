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
        enemigos.put("esclavo", () -> new Enemigo.Builder("Esclavo")
                .vida(150).ataque(20)
                .habilidad(new HabilidadGolpe("Cadenazo", "Golpe con cadenas", 35, 3))
                .build());

        enemigos.put("guardia_hueco", () -> new Enemigo.Builder("Guardia hueco")
                .vida(220).ataque(25)
                .habilidad(new HabilidadGolpe("Tajo oxidado", "Espadazo lento", 45, 3))
                .build());

        enemigos.put("sacerdotisa", () -> new Enemigo.Builder("Sacerdotisa de ceniza")
                .vida(260).ataque(30)
                .habilidad(new HabilidadGolpe("Llama azul", "Fuego frío", 55, 3))
                .build());

        enemigos.put("guardian_cenizas", () -> new Enemigo.Builder("Guardián de las Cenizas")
                .vida(900).ataque(40)
                .maxHabilidades(2)
                .habilidad(new HabilidadGolpe("Martillo ígneo", "Golpe devastador", 90, 3))
                .habilidad(new HabilidadGolpe("Lluvia de brasas", "Daño en área", 60, 2))
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
