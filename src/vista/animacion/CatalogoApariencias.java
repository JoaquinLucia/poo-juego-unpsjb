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
        heroes.put("artorias", new Apariencia(33, 131, 494, 483, 0));
        heroes.put("merlin",   new Apariencia(110, 160, 562, 414, 0));
        
        enemigos.put("jester", new Apariencia(1009, 131, 494, 483, 0));   // antes 145  (+80)
        enemigos.put("esclavo", new Apariencia(892, 0, 526, 614, 0));
        enemigos.put("cazador", new Apariencia(795, 47, 636, 567, 0));
        enemigos.put("akuma", new Apariencia(660, 131, 540, 483, 0));
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
