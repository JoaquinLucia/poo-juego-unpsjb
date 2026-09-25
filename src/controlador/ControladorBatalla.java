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
    }

    private void vincularEventos() {
        // Asumiendo que la vista tiene el botón de ataque del héroe
        if (vista.getBotonHeroe() != null) {
            vista.getBotonHeroe().addActionListener(e -> procesarAtaqueHeroe());
        }
    }

    private void procesarAtaqueHeroe() {
        if (batalla.getTurnoActual() != Batalla.Turno.HEROE || batalla.estaTerminada()) {
            return;
        }

        // 1. Ejecutar turno del jugador
        String logHeroe = batalla.ejecutarAtaqueHeroe();
        vista.mostraeResultado(batalla.getHeroe().getNombre(), batalla.getHeroe().getAtaque());
        actualizarInterfaz();

        // 2. Verificar si murió el enemigo
        if (batalla.estaTerminada()) {
            finalizarCombate();
            return;
        }

        // 3. Concurrencia: Turno del enemigo tras 1 segundo sin bloquear la UI
        bloquearControles(true);
        Timer timerEnemigo = new Timer(1000, evento -> {
            String logEnemigo = batalla.ejecutarTurnoEnemigo();
            vista.mostraeResultado(batalla.getEnemigo().getNombre(), batalla.getEnemigo().getAtaque());
            actualizarInterfaz();
            bloquearControles(false);

            if (batalla.estaTerminada()) {
                finalizarCombate();
            }
        });
        timerEnemigo.setRepeats(false);
        timerEnemigo.start();
    }

    private void actualizarInterfaz() {
        // Acá se sincronizan barras de vida o etiquetas según los métodos de VistaBatalla
    }

    private void bloquearControles(boolean bloquear) {
        if (vista.getBotonHeroe() != null) {
            vista.getBotonHeroe().setEnabled(!bloquear);
        }
    }

    private void finalizarCombate() {
        bloquearControles(true);
        if (batalla.ganoHeroe()) {
            System.out.println("¡Victoria!");
        } else {
            System.out.println("Derrota...");
        }
    }
}