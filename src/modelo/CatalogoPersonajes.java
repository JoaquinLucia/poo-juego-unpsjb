package modelo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Listado de todos los héroes y enemigos del juego, armados con Builder.
 *
 * Es la ÚNICA fuente de los datos de juego (nombre, vida, ataque, habilidades).
 * La pantalla de selección construye un héroe de muestra y lee sus valores de acá,
 * así que nunca puede mostrar algo distinto de lo que después se juega.
 *
 * Se guarda una "receta" (Supplier) y no el objeto ya creado: cada vez que
 * se pide un personaje se construye uno NUEVO, con la vida completa y con
 * habilidades propias (si dos personajes compartieran la misma Habilidad,
 * compartirían también el cooldown).
 *
 * Para agregar un héroe: sumar su receta en cargarHeroes(), su presentación
 * en vista.PresentacionesHeroes (con el mismo id) y su imagen en
 * /recursos/personajes/.
 */
public class CatalogoPersonajes {

    // LinkedHashMap: conserva el orden de carga, que es el orden de las flechas en la selección.
    private final Map<String, Supplier<Heroe>> heroes = new LinkedHashMap<>();
    private final Map<String, Supplier<Enemigo>> enemigos = new HashMap<>();

    public CatalogoPersonajes() {
        cargarHeroes();
        cargarEnemigos();
    }

    // ---------------------------------------------------------------- héroes

    private void cargarHeroes() {
        heroes.put("caballero", () -> new Heroe.Builder("Caballero de Gwyn")
                .vida(600).ataque(35)
                .nivel(1)
                .maxHabilidades(4)
                .habilidad(new HabilidadGolpe("Golpe aturdidor", "Golpe con el escudo", 70, 3))
                .build());

        heroes.put("mago", () -> new Heroe.Builder("Espectro nómada")
                .vida(350).ataque(55)
                .nivel(1)
                .maxHabilidades(4)
                .habilidad(new HabilidadGolpe("Proyectil ígneo", "Daño mágico", 110, 3))
                .build());

        heroes.put("cazadora", () -> new Heroe.Builder("Viuda de Van Helsing")
                .vida(420).ataque(45)
                .nivel(1)
                .maxHabilidades(4)
                .habilidad(new HabilidadGolpe("Virote de plata", "Daño perforante", 90, 2))
                .build());
    }

    /** Ids de todos los héroes, en el orden en que se muestran en la selección. */
    public List<String> idsHeroes() {
        return List.copyOf(heroes.keySet());
    }

    public Heroe crearHeroe(String id) {
        Supplier<Heroe> receta = heroes.get(id);
        if (receta == null) {
            throw new IllegalArgumentException("No existe el héroe: " + id);
        }
        return receta.get();
    }

    public boolean existeHeroe(String id) {
        return heroes.containsKey(id);
    }

    // -------------------------------------------------------------- enemigos

    private void cargarEnemigos() {
        enemigos.put("esclavo", () -> new Enemigo.Builder("Esclavo")
                .vida(150).ataque(20)
                .maxHabilidades(1)
                .habilidad(new HabilidadGolpe("Cadenazo", "Golpe con cadenas", 35, 3))
                .build());

        enemigos.put("guardia_hueco", () -> new Enemigo.Builder("Guardia hueco")
                .vida(220).ataque(25)
                .maxHabilidades(1)
                .habilidad(new HabilidadGolpe("Tajo oxidado", "Espadazo lento", 45, 3))
                .build());

        enemigos.put("sacerdotisa", () -> new Enemigo.Builder("Sacerdotisa de ceniza")
                .vida(260).ataque(30)
                .maxHabilidades(1)
                .habilidad(new HabilidadGolpe("Llama azul", "Fuego frío", 55, 3))
                .build());

        enemigos.put("guardian_cenizas", () -> new Enemigo.Builder("Guardián de las Cenizas")
                .vida(900).ataque(40)
                .maxHabilidades(2)
                .habilidad(new HabilidadGolpe("Martillo ígneo", "Golpe devastador", 90, 3))
                .habilidad(new HabilidadGolpe("Lluvia de brasas", "Daño en área", 60, 2))
                .build());
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
