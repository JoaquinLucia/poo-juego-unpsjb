package modelo.catalogos;

import java.util.List;
import modelo.Dificultad;
import modelo.Enemigo;

public class CatalogoEnemigos {

    private final CatalogoHabilidades habilidades;

    public CatalogoEnemigos(CatalogoHabilidades habilidades) {
        this.habilidades = habilidades;
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
                    List.of(habilidades.crearHabilidad("grito")),
                    "/recursos/personajes/mago.png", "/recursos/esclavoFrontal.jpg",
                    dificultad);
            case "cazador" -> new Enemigo("Cazador", 60, 10, 1,
                    List.of(habilidades.crearHabilidad("corte_profundo")),
                    "/recursos/personajes/mago.png", "/recursos/cazadorFrontal.jpg",
                    dificultad);
            case "jester" -> new Enemigo("Jester", 60, 10, 1,
                    List.of(habilidades.crearHabilidad("dardo_venenoso")),
                    "/recursos/personajes/mago.png", "/recursos/jesterFrontal.jpg",
                    dificultad);
            case "akuma" -> new Enemigo("Akuma", 300, 20, 2,
                    List.of(habilidades.crearHabilidad("golpe_aturdidor"),
                            habilidades.crearHabilidad("corte_profundo"),
                            habilidades.crearHabilidad("curacion")),

                    "/recursos/personajes/mago.png", "/recursos/akumaFrontal.jpg",
                    dificultad);
            default -> throw new IllegalArgumentException("Enemigo desconocido: " + tipo);
        };
    }
}

/* ¿Por qué usar este método frente a otras alternativas? (Ventajas de diseño)
Comparado con alternativas menos prolijas (como hacer new directamente en la lógica del combate o en las vistas), este enfoque resuelve problemas clave de arquitectura:

A. Encapsulamiento del new y Desacoplamiento
La alternativa ingenua: Si no usaras un catálogo, cada vez que quisieras instanciar un enemigo en tu juego tendrías que escribir un bloque larguísimo de código con rutas de imágenes, listas de habilidades y constructores complejos esparcidos por todo el proyecto.

Por qué este método es mejor: El resto del sistema (por ejemplo, el controlador de combate o la generación de niveles) solo le pide al catálogo: catalogoEnemigos.crearPersonaje("akuma", dificultad). El único lugar que conoce las clases concretas y los constructores es el catálogo.
Si mañana cambias el constructor de Enemigo, solo modificas una línea en este archivo.

B. Principio de Responsabilidad Única (Single Responsibility) y Composición
CatalogoEnemigos no sabe cómo se construye un HabilidadVeneno ni cuáles son sus fórmulas internas; simplemente le delega esa tarea a CatalogoHabilidades. Esto mantiene las clases modulares y fáciles de mantener.

C. Seguridad contra Estados Compartidos (Instancias Únicas vs. Nuevas)
El comentario en CatalogoHabilidades ("Cada llamada crea una instancia nueva: nunca compartir habilidades entre entidades") acierta en un detalle crítico de diseño de juegos.

Las habilidades suelen tener estado mutable durante un combate (por ejemplo, turnos actuales de cooldown o acumulaciones de veneno).
Si cometieras el error de crear una sola instancia estática de "Corte profundo" y la compartieras entre todos los enemigos del juego, el cooldown de uno afectaría al otro. Usar un factory garantiza que cada entidad nazca con su propia copia limpia e independiente.

D. Alineación con Open/Closed Principle (SOLID)
Cuando quieras agregar un nuevo enemigo (por ejemplo, un Orco o un Jefe de Zona), tu código existente no se rompe ni se modifica. Simplemente agregas un nuevo case en el switch del catálogo. */