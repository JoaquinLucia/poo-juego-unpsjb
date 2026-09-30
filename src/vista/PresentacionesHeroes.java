package vista;

import java.util.Collection;
import java.util.Map;

/**
 * Presentación visual de cada héroe, indexada por el mismo id que usa
 * modelo.CatalogoPersonajes.
 *
 * Las imágenes son PNG con fondo transparente dentro de /recursos/personajes/.
 */
public final class PresentacionesHeroes {

    private static final Map<String, PresentacionHeroe> DATOS = Map.of(
        "caballero", new PresentacionHeroe(
            "Guerrero",
            "Juró proteger una ciudad que ya no existe. Su armadura todavía "
                + "guarda el calor del último incendio, y su espada no ha "
                + "vuelto a la vaina desde entonces.",
            "/recursos/personajes/caballero.png"),

        "mago", new PresentacionHeroe(
            "Mago de combate",
            "Explorador de tierras malditas. Busca cosechar almas perdidas "
                + "entre los vestigios de lo que fue antes un próspero imperio "
                + "para encontrar el secreto de la inmortalidad.",
            "/recursos/personajes/mago.png"),

        "cazadora", new PresentacionHeroe(
            "Cazadora de demonios",
            "Sobreviviente de la ciudad que cayó en una sola noche. Recorre "
                + "las ruinas del imperio con su ballesta de runas, asesinando "
                + "demonios, hasta que no quede ninguno.",
            "/recursos/personajes/cazadora.png"));

    private PresentacionesHeroes() {}

    public static PresentacionHeroe de(String id) {
        PresentacionHeroe p = DATOS.get(id);
        if (p == null) {
            throw new IllegalStateException("Falta la presentación del héroe: " + id);
        }
        return p;
    }

    /**
     * Llamar una vez al arrancar el juego (desde Main) con catalogo.idsHeroes().
     * Si a algún héroe le falta su presentación, el juego falla al abrir
     * y no a mitad de la selección.
     */
    public static void verificar(Collection<String> idsHeroes) {
        for (String id : idsHeroes) {
            de(id);
        }
    }
}
