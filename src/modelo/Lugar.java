package modelo;

/**
 * Los lugares del recorrido, en orden.
 * Los tres primeros son caminos (se elige por dónde seguir y hay batalla);
 * el último es la sala del jefe (pelea final).
 * Cada lugar sabe su fondo y qué enemigo aparece ahí.
 */
public enum Lugar {

    BOSQUE("/recursos/mazmorra/bosque.gif", "jester"),
    CUEVA("/recursos/mazmorra/cueva.gif", "esclavo"),
    MAZMORRA("/recursos/mazmorra/mazmorra.gif", "cazador"),
    SALA_JEFE("/recursos/mazmorra/jefe_puerta.gif", "akuma");

    private final String rutaFondo;
    private final String tipoEnemigo;   // el nombre que entiende FabricaEnemigo

    Lugar(String rutaFondo, String tipoEnemigo) {
        this.rutaFondo = rutaFondo;
        this.tipoEnemigo = tipoEnemigo;
    }

    public String getRutaFondo()   { return rutaFondo; }
    public String getTipoEnemigo() { return tipoEnemigo; }
}
