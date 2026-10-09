package controlador;

import modelo.ModeloMenu;
import modelo.ModeloTablaPuntuacion;
import vista.VistaMenu;
import vista.VistaPuntuaciones;

public class ControladorMenu {

    private final VistaMenu vista;
    private final ModeloTablaPuntuacion tablaPuntuaciones;
    private final ModeloMenu modeloMenu;
    private final ControladorMaestro maestro;
    private final ControladorSeleccion controladorSeleccion;

    public ControladorMenu(VistaMenu vista, ModeloTablaPuntuacion tablaPuntuaciones,
                           ControladorMaestro maestro) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;
        this.maestro = maestro;
        this.modeloMenu = new ModeloMenu();

        // Sub-controlador de la selección de personaje
        this.controladorSeleccion = new ControladorSeleccion(
            modeloMenu,
            vista.getPanelSeleccion(),
            () -> vista.mostrarMenu(),                                  // "Volver"
            (arquetipoElegido) -> maestro.comenzarPartida(arquetipoElegido) // "Comenzar"
        );

        // Botones del menú principal
        vista.onComenzar(e -> iniciarSeleccionPersonaje());
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

    /** Muestra el menú principal (al iniciar o al volver de una partida). */
    public void mostrar() {
        vista.mostrarMenu();
        vista.setVisible(true);
    }

    public void ocultar() {
        vista.setVisible(false);
    }

    public void mostrarMensaje(String mensaje) {
        vista.mostrarMensaje(mensaje);
    }

    private void iniciarSeleccionPersonaje() {
        controladorSeleccion.entrar();
        vista.mostrarSeleccion();
    }

    private void mostrarPuntuaciones() {
        VistaPuntuaciones modal = new VistaPuntuaciones(vista);
        modal.cargarPuntuaciones(tablaPuntuaciones.getPuntuacionesOrdenadas());
        modal.setVisible(true);
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
