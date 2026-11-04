package modelo.catalogos;

import modelo.Habilidad;
import modelo.habilidades.HabilidadAturdir;
import modelo.habilidades.HabilidadCurar;
import modelo.habilidades.HabilidadDesangrar;
import modelo.habilidades.HabilidadVeneno;

public class CatalogoHabilidades  {

    /** Cada llamada crea una instancia nueva: nunca compartir habilidades entre entidades. */
  
    public Habilidad crearHabilidad(String id) {
        return switch (id.toLowerCase()) {

            //                                    nombre, descripción, dañoBase, cooldown, duración
            case "golpe_aturdidor" -> new HabilidadAturdir(
                    "Golpe aturdidor", "Golpea y aturde al enemigo un turno.",
                    80, 3, 1);

            //                                    nombre, descripción, dañoBase, cooldown, dañoPorTurno, duración
            case "dardo_venenoso" -> new HabilidadVeneno(
                    "Dardo venenoso", "Envenena: 4 de daño por turno durante 3 turnos.",
                    5, 3, 4, 3);

            //                                    nombre, descripción, dañoBase, cooldown, dañoInicial, duración
            case "corte_profundo" -> new HabilidadDesangrar(
                    "Corte profundo", "Abre una herida que sangra cada vez más.",
                    8, 3, 2, 3);

            case "grito" -> new HabilidadAturdir(
                    "Grito aterrador", "Aturde al enemigo sin dañarlo.",
                    0, 4, 2);

            case "curacion" -> new HabilidadCurar(
                    "Curación", "Recupera 25 de vida.",
                    25, 4);
 
            default -> throw new IllegalArgumentException("Habilidad desconocida: " + id);
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