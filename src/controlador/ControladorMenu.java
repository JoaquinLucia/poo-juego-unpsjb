package controlador;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modelo.CatalogoPersonajes;
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
                           CatalogoPersonajes catalogo, ControladorMaestro maestro) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;
        this.maestro = maestro;
        this.modeloMenu = new ModeloMenu(catalogo.idsHeroes());

        // Sub-controlador de la selección de personaje
        this.controladorSeleccion = new ControladorSeleccion(
            modeloMenu,
            catalogo,
            vista.getPanelSeleccion(),
            () -> vista.mostrarMenu(),                                   // "Volver"
            idHeroe -> this.maestro.comenzarPartida(idHeroe)             // "Comenzar"
        );

        // Botones del menú principal
        vista.onComenzar(e -> iniciarSeleccionPersonaje());
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

    /** Deja el menú principal en primer plano (al iniciar o al volver de una partida). */
    public void mostrar() {
        vista.mostrarMenu();
    }

    public VistaMenu getVista() {
        return vista;
    }

    public void mostrarMensaje(String mensaje) {
        vista.mostrarMensaje(mensaje);
    }

    private void iniciarSeleccionPersonaje() {
        controladorSeleccion.entrar();
        vista.mostrarSeleccion();
    }

    private void mostrarPuntuaciones() {
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        VistaPuntuaciones modal = new VistaPuntuaciones(ventana);
        modal.cargarPuntuaciones(tablaPuntuaciones.getPuntuacionesOrdenadas());
        modal.setVisible(true);
    }

    private void mostrarOpciones() {
        vista.mostrarMensaje("Opciones (próximamente)");
    }

    private void salir() {
        if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
            System.exit(0);
        }
    }
}
