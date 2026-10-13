package controlador;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modelo.ModeloTablaPuntuacion;
import vista.VistaMenu;
import vista.VistaPuntuaciones;
import controlador.utilidades.GestorDeSonido;

public class ControladorMenu {

    private final VistaMenu vista;
    private final ModeloTablaPuntuacion tablaPuntuaciones;
    private final ControladorMaestro maestro;

    // Constructor de 3 parámetros
    public ControladorMenu(VistaMenu vista, ModeloTablaPuntuacion tablaPuntuaciones, ControladorMaestro maestro) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;
        this.maestro = maestro;
        configurarEventos();
    }

    // Constructor alternativo de 2 parámetros (para que no falle el ControladorMaestro)
    public ControladorMenu(VistaMenu vista, ControladorMaestro maestro) {
        this.vista = vista;
        this.tablaPuntuaciones = new ModeloTablaPuntuacion();
        this.maestro = maestro;
        configurarEventos();
    }

    private void configurarEventos() {
        vista.onComenzar(e -> iniciarSeleccionPersonaje());
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

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
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        maestro.iniciarJuegoDesdeMenu();
    }

    private void mostrarPuntuaciones() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        VistaPuntuaciones modal = new VistaPuntuaciones(ventana);
        modal.cargarPuntuaciones(tablaPuntuaciones.getPuntuacionesOrdenadas());
        modal.setVisible(true);
    }

    private void mostrarOpciones() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        vista.mostrarMensaje("Opciones (próximamente)");
    }

    private void salir() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
            System.exit(0);
        }
    }
}
