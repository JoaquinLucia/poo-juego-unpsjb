package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Puntaje;
import modelo.PuntajeDAO;

/**
 * Patrón DAO — implementación para SQLite.
 *
 * Traduce entre objetos Puntaje y filas de la tabla "puntaje".
 * Es la única clase que escribe SQL sobre esa tabla.
 */
public class PuntajeDAOSQLite implements PuntajeDAO {

    private final String sqlInsertar =
            "INSERT INTO puntaje (jugador, heroe, puntos) VALUES (?, ?, ?)";

    // Mismo orden en las dos consultas: más puntos primero; con empate, el que llegó antes
    private final String sqlMejores =
            "SELECT jugador, heroe, puntos, fecha FROM puntaje ORDER BY puntos DESC, fecha ASC LIMIT ?";
    private final String sqlTodos =
            "SELECT jugador, heroe, puntos, fecha FROM puntaje ORDER BY puntos DESC, fecha ASC";

   
    @Override
    public void guardar(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }
        try (PreparedStatement ps = conexion().prepareStatement(sqlInsertar)) {
            ps.setString(1, puntaje.getJugador());
            ps.setString(2, puntaje.getHeroe());
            ps.setInt(3, puntaje.getPuntos());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el puntaje", e);
        }
    }

    @Override
    public List<Puntaje> mejores(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        try (PreparedStatement ps = conexion().prepareStatement(sqlMejores)) {
            ps.setInt(1, cantidad);
            return leerPuntajes(ps);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron leer los puntajes", e);
        }
    }

    @Override
    public List<Puntaje> todos() {
        try (PreparedStatement ps = conexion().prepareStatement(sqlTodos)) {
            return leerPuntajes(ps);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron leer los puntajes", e);
        }
    }

    // ---------- Auxiliares ----------

    private Connection conexion() {
        return ConexionBD.getInstancia().getConexion();
    }

    /** Ejecuta la consulta y convierte cada fila en un Puntaje. */
    private List<Puntaje> leerPuntajes(PreparedStatement ps) throws SQLException {
        List<Puntaje> resultado = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Puntaje(
                        rs.getString("jugador"),
                        rs.getString("heroe"),
                        rs.getInt("puntos"),
                        rs.getString("fecha")));
            }
        }
        return resultado;
    }
}