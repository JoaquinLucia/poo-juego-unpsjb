package vista.animacion;

import java.util.HashMap;
import java.util.Map;

/**
 * Sabe dónde y de qué tamaño se dibuja cada personaje.
 * Reemplaza los if (ruta.contains("artorias")) que estaban repartidos en PanelBatalla.
 * Para sumar un personaje con medidas propias se agrega una línea en el constructor
 * (abierto a extensión sin tocar el resto del código).
 */
public class CatalogoApariencias {

    private final Map<String, Apariencia> heroes = new HashMap<>();
    private final Map<String, Apariencia> enemigos = new HashMap<>();

    // Se usan cuando el personaje no está en la lista
    private final Apariencia heroePorDefecto = new Apariencia(150, 120, 380, 380, 0);
    private final Apariencia enemigoPorDefecto = new Apariencia(880, 110, 380, 380, 0);   // ej.: jester

    public CatalogoApariencias() {
        //                         x    y   ancho alto recorte
        heroes.put("artorias", new Apariencia(150,  30, 290, 670, 35));   // antes -50  (+80)
        heroes.put("merlin",   new Apariencia(210, 100, 310, 560, 25));   // antes 20   (+80, a probar)

        enemigos.put("jester",  new Apariencia(880, 225, 380, 380, 0));   // antes 145  (+80)
        enemigos.put("esclavo", new Apariencia(850, 195, 400, 400, 0));   // antes 110  (a probar)
        enemigos.put("cazador", new Apariencia(850, 195, 400, 400, 0));   // antes 110  (a probar)
        enemigos.put("akuma",   new Apariencia(850, 195, 400, 400, 0));
    }

    public Apariencia buscarHeroe(String nombre) {
        return buscar(heroes, nombre, heroePorDefecto);
    }

    public Apariencia buscarEnemigo(String nombre) {
        return buscar(enemigos, nombre, enemigoPorDefecto);
    }

    /** Busca una clave que esté contenida en el nombre (igual que hacía el código original con la ruta). */
    private Apariencia buscar(Map<String, Apariencia> lista, String nombre, Apariencia porDefecto) {
        String nombreEnMinuscula = nombre.toLowerCase();
        for (String clave : lista.keySet()) {
            if (nombreEnMinuscula.contains(clave)) {
                return lista.get(clave);
            }
        }
        return porDefecto;
    }
}
