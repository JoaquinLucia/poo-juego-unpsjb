package controlador;

import modelo.Batalla;
import modelo.Camino;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.Nivel;
import modelo.CatalogoPersonajes;
import modelo.Arquetipo;
import modelo.Escenario;
import vista.VentanaPrincipal;
import vista.VistaBatalla;
import vista.VistaMenu;
import vista.VistaSeleccion;
import vista.VistaMazmorra;

public class ControladorMaestro {
    private final VentanaPrincipal ventana;
    private final CatalogoPersonajes catalogo;
    private Nivel nivel;
    private Heroe heroe;
    
    private ControladorMenu ctrlMenu;
    private ControladorSeleccion ctrlSeleccion;
    private ControladorMazmorra ctrlMazmorra;
    private ControladorBatalla ctrlBatalla;

    private static final String MENU = "MENU";
    private static final String SELECCION = "SELECCION";
    private static final String MAZMORRA = "MAZMORRA";
    private static final String BATALLA = "BATALLA";

    public ControladorMaestro() {
        this.ventana = new VentanaPrincipal();
        this.catalogo = new CatalogoPersonajes();
        
        VistaMenu vistaMenu = new VistaMenu();
        VistaSeleccion vistaSeleccion = new VistaSeleccion();
        VistaMazmorra vistaMazmorra = new VistaMazmorra();

        this.ctrlMenu = new ControladorMenu(vistaMenu, this);
        this.ctrlSeleccion = new ControladorSeleccion(vistaSeleccion, this, catalogo);
        this.ctrlMazmorra = new ControladorMazmorra(vistaMazmorra, this);

        ventana.agregarPantalla(MENU, vistaMenu);
        ventana.agregarPantalla(SELECCION, vistaSeleccion);
        ventana.agregarPantalla(MAZMORRA, vistaMazmorra);
    }

    public void iniciar() {
        ventana.mostrarDirecto(MENU);
    }

    public void iniciarJuegoDesdeMenu() {
        ctrlSeleccion.reiniciar();
        ventana.cambiarPantalla(SELECCION, () -> {}, null);
    }

    public void personajeSeleccionado(Arquetipo arquetipoElegido) {
        this.heroe = catalogo.crearHeroe(arquetipoElegido);
        
        // Creamos los escenarios usando los métodos estáticos de Escenario.java
        Escenario escenarioBosque = Escenario.salaNormal(
            "bosque", 
            "/recursos/mazmorra/bosque.png", 
            "Te encuentras en los senderos oscuros del bosque.", 
            "Ir por la senda izquierda.", 
            "Seguir al frente entre los árboles.", 
            "Tomar el sendero derecho."
        );
        
        Escenario escenarioCatacumbas = Escenario.salaNormal(
            "catacumbas", 
            "/recursos/mazmorra/catacumbas.png", 
            "El aire es pesado en las catacumbas.", 
            "Bajar por el pasillo izquierdo.", 
            "Continuar por el corredor principal.", 
            "Explorar la cripta derecha."
        );
        
        Escenario escenarioJefe = Escenario.salaDelJefe(
            "jefe", 
            "/recursos/mazmorra/jefe.png", 
            "Has llegado a la cámara final.", 
            "Enfrentar al Guardián de las Cenizas."
        );

        // Construimos el nivel con el Builder pasando las instancias de los escenarios
        this.nivel = new Nivel.Builder("Senderos de las Cenizas")
                .sala(escenarioBosque, "esclavo")
                .sala(escenarioCatacumbas, "guardia_hueco")
                .jefe(escenarioJefe, "guardian_cenizas")
                .build();
                
        irAExploracion("¡Comienza tu aventura en los Senderos de las Cenizas!");
    }

    private void irAExploracion(String mensajePrevio) {
        String encabezado = nivel.getNombre() + " - Sala " + nivel.getNumeroSala() + 
                " de " + nivel.getCantidadSalas() + 
                " | " + heroe.getNombre() + ": " + heroe.getVida() + " de vida";
        String mensaje = mensajePrevio.isEmpty() ? encabezado : encabezado + "\n" + mensajePrevio;

        ventana.cambiarPantalla(MAZMORRA, () -> {
            cerrarBatalla();
            ctrlMazmorra.mostrar(nivel.getSalaActual().getEscenario(), mensaje);
        }, null);
    }

    public void avanzarSala() {
        if (nivel.haySiguiente()) {
            nivel.avanzar();
            
            if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
                irABatalla();
            } else {
                irAExploracion("Avanzas sin contratiempos a la siguiente sala.");
            }
        } else {
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }

    public void caminoElegido(Camino camino) {
        avanzarSala();
    }

    private void irABatalla() {
        ventana.cambiarPantalla(BATALLA, () -> {
            cerrarBatalla();
            Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
            Batalla batalla = new Batalla(heroe, enemigo);
            
            heroe.reiniciarTodasLasHabilidades();
            
            ctrlBatalla = new ControladorBatalla(new VistaBatalla(), batalla, this);
            ventana.agregarPantalla(BATALLA, ctrlBatalla.getVista());
            ctrlBatalla.iniciar();
        }, null);
    }

    private void cerrarBatalla() {
        if (ctrlBatalla != null) {
            ctrlBatalla.cerrar();
            ventana.quitarPantalla(ctrlBatalla.getVista());
            ctrlBatalla = null;
        }
    }

    public void batallaTerminada(boolean ganoHeroe) {
        if (ganoHeroe) {
            avanzarSala();
        } else {
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }
}