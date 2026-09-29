package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lo que se ve en una pantalla de exploración: imagen de fondo, texto y caminos.
 */
public class Escenario {
    // Todas las salas normales usan las mismas flechas y posiciones
    private static final String RUTA_FLECHAS = "/recursos/mazmorra/";
    private static final double[] POS_IZQUIERDA = {0.32, 0.46};
    private static final double[] POS_FRENTE    = {0.50, 0.41};
    private static final double[] POS_DERECHA   = {0.69, 0.46};

    private final String id;
    private final String rutaImagen;
    private final String mensajeInicial;
    private final boolean esSalaDelJefe;
    private final List<Camino> caminos = new ArrayList<>();

    private Escenario(String id, String rutaImagen, String mensajeInicial, boolean esSalaDelJefe) {
        this.id = id;
        this.rutaImagen = rutaImagen;
        this.mensajeInicial = mensajeInicial;
        this.esSalaDelJefe = esSalaDelJefe;
    }

    /** Sala normal: siempre tiene los 3 caminos (izquierda, frente y derecha). */
    public static Escenario salaNormal(String id, String rutaImagen, String mensajeInicial,
                                       String descIzquierda, String descFrente, String descDerecha) {
        Escenario sala = new Escenario(id, rutaImagen, mensajeInicial, false);
        sala.caminos.add(new Camino("la izquierda", descIzquierda, "Izquierda",
                RUTA_FLECHAS + "flecha_izquierda.png", POS_IZQUIERDA[0], POS_IZQUIERDA[1]));
        sala.caminos.add(new Camino("el frente", descFrente, "Frente",
                RUTA_FLECHAS + "flecha_frente.png", POS_FRENTE[0], POS_FRENTE[1]));
        sala.caminos.add(new Camino("la derecha", descDerecha, "Derecha",
                RUTA_FLECHAS + "flecha_derecha.png", POS_DERECHA[0], POS_DERECHA[1]));
        return sala;
    }

    /** Sala del jefe: no tiene los 3 caminos, solo un cartel para enfrentarlo. */
    public static Escenario salaDelJefe(String id, String rutaImagen, String mensajeInicial,
                                        String descJefe) {
        Escenario sala = new Escenario(id, rutaImagen, mensajeInicial, true);
        sala.caminos.add(new Camino("el jefe", descJefe, "Enfrentar", null, 0.50, 0.55));
        return sala;
    }

    public String getId() { return id; }
    public String getRutaImagen() { return rutaImagen; }
    public String getMensajeInicial() { return mensajeInicial; }
    public boolean esSalaDelJefe() { return esSalaDelJefe; }
    public List<Camino> getCaminos() { return Collections.unmodifiableList(caminos); }
}
