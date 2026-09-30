package vista;

/**
 * Todo lo que la pantalla de selección necesita para dibujar un personaje,
 * ya resuelto por el controlador.
 *
 * Nombre, habilidad, vida y ataque salen del héroe real (CatalogoPersonajes);
 * rol, descripción e imagen salen de PresentacionesHeroes.
 * Los máximos son el valor más alto entre todos los héroes y sirven para
 * dibujar las barras en proporción.
 */
public record FichaPersonaje(
        String id,
        String nombre,
        String rol,
        String descripcion,
        String rutaImagen,
        String habilidad,
        int vida,
        int vidaMaxima,
        int ataque,
        int ataqueMaximo) {}
