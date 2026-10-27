package modelo;

/**
 * Los lugares del recorrido, en orden.
 * Los tres primeros son caminos (se elige por dónde seguir y hay batalla);
 * el último es la sala del jefe (pelea final).
 * Cada lugar sabe su fondo de camino, su fondo de combate y qué enemigo aparece ahí.
 */
public enum Lugar {

    BOSQUE("/recursos/mazmorra/bosque.gif",      "/recursos/mazmorra/combate_bosque.gif",   "jester"),
    CUEVA("/recursos/mazmorra/cueva.gif",        "/recursos/mazmorra/combate_cueva.gif",    "esclavo"),
    MAZMORRA("/recursos/mazmorra/mazmorra.gif",  "/recursos/mazmorra/combate_mazmorra.gif", "cazador"),
    SALA_JEFE("/recursos/mazmorra/jefe_puerta.gif", "/recursos/mazmorra/combate_jefe.gif",  "akuma");

    private final String rutaFondo;     // pantalla del camino
    private final String rutaCombate;   // pantalla de la batalla
    private final String tipoEnemigo;   // el nombre que entiende FabricaEnemigo

    Lugar(String rutaFondo, String rutaCombate, String tipoEnemigo) {
        this.rutaFondo = rutaFondo;
        this.rutaCombate = rutaCombate;
        this.tipoEnemigo = tipoEnemigo;
    }

    public String getRutaFondo()   { return rutaFondo; }
    public String getRutaCombate() { return rutaCombate; }
    public String getTipoEnemigo() { return tipoEnemigo; }
}