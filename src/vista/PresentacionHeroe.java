package vista;

/**
 * Lo que la pantalla de selección muestra de un héroe y que NO es dato de juego:
 * su rol, su historia y su imagen.
 *
 * Vida, ataque, nombre y habilidad NO van acá: se leen del héroe real
 * que arma CatalogoPersonajes.
 */
public record PresentacionHeroe(String rol, String descripcion, String rutaImagen) {}
