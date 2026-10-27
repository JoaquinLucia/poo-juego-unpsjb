package modelo;
 
/*
 * Observador de la batalla. Batalla (el Sujeto) conoce esta interfaz y nada más:
 * no sabe si quien la implementa es un controlador, un sonido o un log.
 */
public interface ObservadorBatalla {
 
    /** Alguien hizo un ataque común. */
    void ataqueRealizado(Entidad atacante, Entidad objetivo, int danio);
 
    /** Alguien usó una habilidad. */
    void habilidadUsada(Entidad usuario, String habilidad, Entidad objetivo, int danio);
 
    /** Al que le tocaba estaba aturdido y perdió el turno. */
    void turnoPerdido(Entidad aturdida);
 
    /** Empieza el turno de alguien. */
    void turnoCambiado(Entidad aQuienLeToca);
 
    /** Terminó la batalla. */
    void finDeBatalla(boolean victoria);
    /** Alguien adoptó postura defensiva. */
    void defensaRealizada(Entidad entidad);
}