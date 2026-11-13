package controlador;

import modelo.Dificultad;
import modelo.ModeloCaminoMazmorra;
import vista.VistaCaminoMazmorra;

/*
 * Controla el recorrido: camino → batalla → camino → batalla → camino → batalla → jefe.
 *
 * - En los caminos, cada flecha corresponde a una dificultad:
 *   Izquierda = Fácil, Frente = Normal, Derecha = Difícil.
 * - En la sala del jefe hay un solo botón, para la pelea final.
 * - Las batallas las arma el mediador; este controlador solo le avisa qué se eligió.
 */
public class ControladorCaminoMazmorra {

    private final ModeloCaminoMazmorra modelo;
    private final VistaCaminoMazmorra vista;
    private final ControladorMaestro controlador;
    public ControladorCaminoMazmorra(ModeloCaminoMazmorra modelo, VistaCaminoMazmorra vista, ControladorMaestro controlador) {
        this.modelo = modelo;
        this.vista = vista;
        this.controlador = controlador;
        vista.onIzquierda(e -> elegirCamino(Dificultad.FACIL));
        vista.onFrente(e -> elegirCamino(Dificultad.NORMAL));
        vista.onDerecha(e -> elegirCamino(Dificultad.DIFICIL));
        vista.onEnfrentarJefe(e -> controlador.enfrentarJefe());

        actualizarVista();
    }

    /** Lo consulta el maestro después de recibir CAMINO_ELEGIDO. */
    public Dificultad getDificultadElegida() {
        return modelo.getDificultadElegida();
    }

    /** Lo llama el maestro al ganar una batalla de camino: pasa al lugar siguiente. */
    public void volverDeBatalla() {
        modelo.avanzar();
        actualizarVista();
    }

    private void elegirCamino(Dificultad dificultad) {
        modelo.elegir(dificultad);
        controlador.caminoElegido();
    }

    /** Muestra el lugar actual: flechas en los caminos, un solo botón en la sala del jefe. */
    private void actualizarVista() {
        vista.mostrarFondoCamino(modelo.getLugarActual().name());  // 

        if (modelo.llegoAlJefe()) {
            vista.mostrarBotonJefe();
            vista.mostrarEnLog("Llegaste a la sala del jefe. No hay vuelta atrás.");
        } else {
            vista.mostrarFlechas();
            vista.mostrarEnLog("Camino " + modelo.getNumeroCamino() + ". Elegí por dónde seguir.");
        }
    }

}
