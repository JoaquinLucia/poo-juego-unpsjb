package mvc.controlador;

import mvc.modelo.ModeloTablaPuntuacion;
import mvc.vista.VistaMenu;
import mvc.vista.VistaPuntuaciones;

// Conecta la vista con el modelo y decide qué hace cada botón.
public class ControladorMenu {

    private final VistaMenu vista;
    private final ModeloTablaPuntuacion tablaPuntuaciones;

    public ControladorMenu(VistaMenu vista, ModeloTablaPuntuacion tablaPuntuaciones) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;

        vista.onComenzar(e -> comenzarJuego());
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    private void comenzarJuego() {
        // Acá después creás la vista y el controlador del juego
        vista.mostrarMensaje("¡Comienza el juego!");
    }

    private void mostrarPuntuaciones() {
        VistaPuntuaciones modal = new VistaPuntuaciones(vista);
        modal.cargarPuntuaciones(tablaPuntuaciones.getPuntuacionesOrdenadas());
        modal.setVisible(true); // bloquea hasta que se cierre
    }

    private void mostrarOpciones() {
        vista.mostrarMensaje("Opciones (próximamente)");
    }

    private void salir() {
        if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
            vista.dispose();
            System.exit(0);
        }
    }
}