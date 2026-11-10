package persistencia;

import java.util.concurrent.BlockingQueue;
import modelo.Puntaje;
import modelo.PuntajeDAO;

//
 // Consumidor del patrón productor-consumidor.
 //Corre en su propio hilo ("guardador") y es el ÚNICO que guarda con el DAO:
 //el juego deja los puntajes en la cola con put() y sigue, sin esperar a la base.
 //
public class GuardadorDePuntajes implements Runnable {

    // Elemento especial: avisa que no vienen más puntajes. Se compara con == a propósito.
    // Ajustar los argumentos al constructor real de Puntaje.
    public static final Puntaje FIN = new Puntaje("jugador", "heroe", 0);

    private final BlockingQueue<Puntaje> pendientes;
    private final PuntajeDAO puntajes;

    public GuardadorDePuntajes(BlockingQueue<Puntaje> pendientes, PuntajeDAO puntajes) {
        this.pendientes = pendientes;
        this.puntajes = puntajes;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Puntaje puntaje = this.pendientes.take();   // espera si la cola está vacía
                if (puntaje == FIN) {
                    return;                                 // no viene nada más: el hilo termina
                }
                try {
                    this.puntajes.guardar(puntaje);         // corre en el hilo guardador
                } catch (PersistenciaException e) {
                    System.err.println("No se pudo guardar el puntaje de "
                            + puntaje.getJugador() + ": " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();             // no tragarse el pedido de interrupción
        }
    }
}