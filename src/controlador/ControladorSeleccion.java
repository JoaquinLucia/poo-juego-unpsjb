package controlador;

import modelo.Heroe;
import modelo.SeleccionHeroe;
import vista.paneles.PanelControles;
import vista.paneles.PanelSeleccionPersonaje;

/*
 * Controla la selección de héroe. Cuando el jugador confirma o vuelve,
 * se lo avisa al mediador; el mediador le pide el héroe con getHeroeElegido().
 */
public class ControladorSeleccion {

    private final PanelSeleccionPersonaje vista;
    private final SeleccionHeroe modelo;
    private final ControladorMaestro controlador;

    public ControladorSeleccion(PanelSeleccionPersonaje vista, SeleccionHeroe modelo, ControladorMaestro controlador) {
        this.vista = vista;
        this.modelo = modelo;
        this.controlador = controlador;
        configurarBotones();
        actualizarVista();
    }

    /** Lo consulta el mediador después de recibir HEROE_ELEGIDO. */
    public Heroe getHeroeElegido() {
        return modelo.getHeroeElegido();
    }

    // ---------- Botones ----------

    private void configurarBotones() {
        PanelControles controles = vista.getPanelControles();
        controles.getAnterior().addActionListener(e -> anterior());
        controles.getSiguiente().addActionListener(e -> siguiente());
        controles.getElegir().addActionListener(e -> confirmar());
        controles.getVolver().addActionListener(e -> controlador.volverAlMenu());
    }

    // ---------- Acciones ----------

    private void anterior() {
        modelo.anterior();
        actualizarVista();
    }

    private void siguiente() {
        modelo.siguiente();
        actualizarVista();
    }

    private void confirmar() {
        modelo.confirmar();
        controlador.heroeElegido();
    }

    /** Toma los datos del héroe actual del modelo y se los pasa a la vista. */
    private void actualizarVista() {
        Heroe h = modelo.getActual();

        String habilidad = h.getCantidadHabilidades() > 0
                ? h.getHabilidad(0).getNombreHabilidad()
                : "-";

        vista.getPanelDatos().mostrarDatos(
                h.getNombre(),
                h.getDescripcion(),   // se muestra en "Clase:"
                h.getNivel(),
                h.getVida(),
                h.getAtaque(),
                habilidad);

        vista.getPanelImagen().mostrarImagen(h.getSprite());

        boolean varios = modelo.hayVarios();
        vista.getPanelControles().getAnterior().setEnabled(varios);
        vista.getPanelControles().getSiguiente().setEnabled(varios);
    }

}