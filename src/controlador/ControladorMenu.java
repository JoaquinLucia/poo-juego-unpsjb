package controlador;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modelo.ModeloMenu;
import modelo.ModeloTablaPuntuacion;
import vista.VistaMenu;
import vista.VistaPuntuaciones;

import controlador.utilidades.GestorDeSonido;

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
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // <--- sonido de click al iniciar la selección
        controladorSeleccion.entrar();
        vista.mostrarSeleccion();
    }

    private void mostrarPuntuaciones() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // <--- sonido de click al mostrar puntuaciones
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(vista);
        VistaPuntuaciones modal = new VistaPuntuaciones(ventana);
        modal.cargarPuntuaciones(tablaPuntuaciones.getPuntuacionesOrdenadas());
        modal.setVisible(true);
    }

    private void mostrarOpciones() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // <--- sonido de click al mostrar opciones
        vista.mostrarMensaje("Opciones (próximamente)");
    }

    private void salir() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav"); // <--- sonido de click al salir
        if (vista.confirmar("¿Seguro que querés salir?", "Salir")) {
            System.exit(0);
        }
    }
}
