import controlador.ControladorMaestro;
import controlador.utilidades.GestorDeSonido;
import modelo.PuntajeDAO;
import persistencia.PuntajeDAOSQLite;

public class App {
    public static void main(String[] args) {
        PuntajeDAO dao = new PuntajeDAOSQLite();   // la misma que usa tu main
        GestorDeSonido sonido = GestorDeSonido.getInstancia();
        ControladorMaestro controlador = new ControladorMaestro(dao,sonido);
        controlador.iniciar();
    }
}