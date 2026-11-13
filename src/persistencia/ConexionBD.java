package persistencia;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión única a la base de datos SQLite del juego (patrón Singleton).
 *
 * - Hay una sola instancia en todo el programa: se obtiene con getInstancia().
 * - La primera vez crea el archivo y la tabla de puntajes.
 * - Los DAO piden la conexión con getConexion(); nadie más abre conexiones.
 *
 * Tabla:
 *   puntaje   un registro por partida terminada: jugador, héroe, puntos y fecha
 */
public final class ConexionBD {

    private static final String ARCHIVO = "datos/juego.db";

    private static ConexionBD instancia;   // la única instancia

    private final Connection conexion;

    /** Privado: nadie de afuera puede hacer new ConexionBD(). */
    private ConexionBD() {
        try {
            new File(ARCHIVO).getParentFile().mkdirs();   // crea la carpeta "datos" si no existe
            conexion = DriverManager.getConnection("jdbc:sqlite:" + ARCHIVO);
            crearTablas();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo abrir la base de datos " + ARCHIVO, e);
        }
    }

    /** Devuelve la única instancia; la crea la primera vez que se pide. */
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    /** Cierra la conexión (al salir del juego). */
    public static synchronized void cerrar() {
        if (instancia != null) {
            try {
                instancia.conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la base de datos: " + e.getMessage());
            }
            instancia = null;
        }
    }

    /** Crea la tabla solo si no existe (se puede ejecutar siempre sin problema). */
    private void crearTablas() throws SQLException {
        try (Statement st = conexion.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS puntaje (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    jugador  TEXT    NOT NULL CHECK (length(trim(jugador)) > 0),
                    heroe    TEXT    NOT NULL,
                    puntos   INTEGER NOT NULL CHECK (puntos >= 0),
                    fecha    TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
                )""");
        }
    }
}