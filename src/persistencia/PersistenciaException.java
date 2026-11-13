package persistencia;

/**
 * Error al leer o escribir datos guardados.
 *
 * Envuelve la SQLException para que quien use el DAO no dependa de JDBC:
 * si mañana los puntajes se guardan en un archivo, esta excepción sigue sirviendo.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}