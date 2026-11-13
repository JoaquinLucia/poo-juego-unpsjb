package modelo;

/**
 * Los lugares del recorrido, en orden.
 * Los tres primeros son caminos (se elige por dónde seguir y hay batalla);
 * el último es la sala del jefe (pelea final).
 *
 * Solo guarda lo que importa para el juego (qué enemigo aparece).
 * Las imágenes de cada lugar las resuelve la vista
 */
public enum Lugar {

    BOSQUE("jester"),
    CUEVA("esclavo"),
    MAZMORRA("cazador"),
    SALA_JEFE("akuma");

    private final String tipoEnemigo;   // el nombre que entiende CatalogoEnemigos

    Lugar(String tipoEnemigo) {
        this.tipoEnemigo = tipoEnemigo;
    }

    public String getTipoEnemigo() { return tipoEnemigo; }
}
