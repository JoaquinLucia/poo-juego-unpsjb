package modelo;

import java.util.List;
/**
 * Patrón DAO — interfaz.
 *
 * Qué se puede hacer con los puntajes guardados, sin decir cómo ni dónde se guardan.
 * Los controladores dependen de esta interfaz; la base concreta (SQLite) queda escondida
 * en la implementación.
 */
public interface PuntajeDAO {

    /** Guarda el puntaje de una partida terminada. */
    public void guardar(Puntaje puntaje);

    /** Los mejores puntajes, de mayor a menor. Con empate, primero el más antiguo. */
    public List<Puntaje> mejores(int cantidad);

    /** Todos los puntajes, de mayor a menor. */
    public List<Puntaje> todos();
}