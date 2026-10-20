import controlador.ControladorMaestro;
import modelo.PuntajeDAO;
import persistencia.PuntajeDAOSQLite;

public class PruebaControladorMaestro {
    public static void main(String[] args) {
        PuntajeDAO dao = new PuntajeDAOSQLite();   // la misma que usa tu main
        ControladorMaestro controlador = new ControladorMaestro(dao);
        controlador.iniciar();
    }
}