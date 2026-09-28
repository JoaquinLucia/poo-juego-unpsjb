package controlador;

import javax.swing.Timer;
import modelo.Batalla;
import modelo.Heroe;
import modelo.Enemigo;
import vista.VistaBatalla;

public class ControladorBatalla {
    private VistaBatalla vista;
    private Batalla batalla;

    public ControladorBatalla(VistaBatalla vista, Batalla batalla) {
        this.vista = vista;
        this.batalla = batalla;

        vincularEventos();
        actualizarInterfaz();
        vista.mostrarResultado("¡Comienza la batalla entre " + batalla.getHeroe().getNombre() + " y " + batalla.getEnemigo().getNombre() + "!");
    }

    private void vincularEventos() {
        // Conectamos los botones de la nueva vista a nuestros métodos
        vista.onAtacar(e -> procesarAtaqueHeroe());
        vista.onHabilidad(e -> procesarHabilidadHeroe());
    }

    private void procesarAtaqueHeroe() {
        if (batalla.getTurnoActual() != Batalla.Turno.HEROE || batalla.estaTerminada()) {
            return;
        }

        // 1. Ejecutar turno del jugador
        String logHeroe = batalla.ejecutarAtaqueHeroe();
        vista.mostrarResultado(logHeroe != null ? logHeroe : batalla.getHeroe().getNombre() + " ataca con " + batalla.getHeroe().getAtaque() + " de daño.");
        actualizarInterfaz();

        // 2. Verificar si terminó el combate
        if (batalla.estaTerminada()) {
            finalizarCombate();
            return;
        }

        // 3. Concurrencia: Turno del enemigo
        iniciarTurnoEnemigo();
    }

    private void procesarHabilidadHeroe() {
        if (batalla.getTurnoActual() != Batalla.Turno.HEROE || batalla.estaTerminada()) {
            return;
        }

        // Suponemos que el héroe usa su habilidad principal (índice 0)
        String logHabilidad = batalla.ejecutarHabilidadHeroe(0);
        
        // Mostramos en la vista qué pasó (si atacó o si tiró el error de cooldown)
        vista.mostrarResultado(logHabilidad);
        actualizarInterfaz();

        // Si la habilidad estaba en cooldown, tu modelo NO llama a pasarTurno().
        // Por lo tanto, si sigue siendo el turno del héroe, cortamos acá y lo dejamos volver a elegir.
        if (batalla.getTurnoActual() == Batalla.Turno.HEROE) {
            return; 
        }

        // Si la batalla terminó con esa habilidad (ej: lo mató)
        if (batalla.estaTerminada()) {
            finalizarCombate();
            return;
        }

        // Si la habilidad se usó con éxito, le toca al enemigo
        iniciarTurnoEnemigo();
    }

    private void iniciarTurnoEnemigo() {
        vista.setBotonesHabilitados(false); // Apagamos los botones para que el jugador no haga spam

        Timer timerEnemigo = new Timer(1000, evento -> {
            String logEnemigo = batalla.ejecutarTurnoEnemigo();
            vista.mostrarResultado(logEnemigo != null ? logEnemigo : batalla.getEnemigo().getNombre() + " contraataca con " + batalla.getEnemigo().getAtaque() + " de daño.");
            actualizarInterfaz();
            
            if (batalla.estaTerminada()) {
                finalizarCombate();
            } else {
                vista.setBotonesHabilitados(true); // Desbloqueamos para el siguiente turno del jugador
            }
        });
        timerEnemigo.setRepeats(false);
        timerEnemigo.start();
    }

    private void actualizarInterfaz() {
        Heroe heroe = batalla.getHeroe();
        Enemigo enemigo = batalla.getEnemigo();
        
        // Enviamos los datos reales del modelo a las etiquetas de la vista
        vista.actualizarEstadisticas(
            heroe.getNombre(), 
            heroe.getVida(), 
            enemigo.getNombre(), 
            enemigo.getVida()
        );
    }

    private void finalizarCombate() {
        vista.setBotonesHabilitados(false);
        if (batalla.ganoHeroe()) {
            vista.mostrarResultado("¡Victoria! " + batalla.getEnemigo().getNombre() + " ha sido derrotado.");
        } else {
            vista.mostrarResultado("Derrota... " + batalla.getHeroe().getNombre() + " ha caído en batalla.");
        }
    }
}