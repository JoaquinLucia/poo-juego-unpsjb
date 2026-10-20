import java.util.List;
import modelo.Puntaje;
import modelo.PuntajeDAO;
import persistencia.ConexionBD;
import persistencia.PersistenciaException;
import persistencia.PuntajeDAOSQLite;

public class PruebaConexion {

    public static void main(String[] args) {
        try {
            // 1. Conexión: crea datos/juego.db y la tabla si no existen
            ConexionBD.getInstancia();
            System.out.println("Conexión abierta correctamente.");

            PuntajeDAO dao = new PuntajeDAOSQLite();

            // 2. Guardar puntajes de prueba (dos con el mismo puntaje, para ver el desempate)
            dao.guardar(new Puntaje("Juan", "Artorias", 1500));
            dao.guardar(new Puntaje("Ana", "Merlin", 980));
            dao.guardar(new Puntaje("Luis", "Artorias", 1500));
            System.out.println("Se guardaron 3 puntajes.\n");

            // 3. Leer los mejores
            System.out.println("--- Mejores 5 ---");
            mostrar(dao.mejores(5));

            // 4. Leer todos
            System.out.println("\n--- Todos ---");
            mostrar(dao.todos());

        } catch (PersistenciaException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            System.err.println("Causa: " + e.getCause());
        } finally {
            // 5. Cerrar la conexión siempre, aunque haya fallado algo
            ConexionBD.cerrar();
            System.out.println("\nConexión cerrada.");
        }
    }

    private static void mostrar(List<Puntaje> puntajes) {
        if (puntajes.isEmpty()) {
            System.out.println("(no hay puntajes)");
            return;
        }
        int posicion = 1;
        for (Puntaje p : puntajes) {
            System.out.println(posicion++ + ". " + p.getJugador() + " (" + p.getHeroe() + ") - "
                    + p.getPuntos() + " pts - " + p.getFecha());
        }
    }
}