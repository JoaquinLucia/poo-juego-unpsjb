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
        vistaMenu.aplicarEscala(ventana.getEscala());

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

    private void irABatalla() {
        ventana.cambiarPantalla(BATALLA, () -> {
            cerrarBatalla();
            
            // Creamos el enemigo de la sala actual
            Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
            Batalla batalla = new Batalla(heroe, enemigo);
            
            heroe.reiniciarTodasLasHabilidades();
            
            VistaBatalla vistaBatalla = new VistaBatalla();
            
            // Determinamos la ruta del retrato del héroe según su nombre o arquetipo
            String rutaHeroeImg = "/recursos/Artorias.jpg"; 
            if (heroe.getNombre().equalsIgnoreCase("Mago")) {
                rutaHeroeImg = "/recursos/mago.png"; 
            } else if (heroe.getNombre().equalsIgnoreCase("Arquero")) {
                rutaHeroeImg = "/recursos/arquero_icono.png"; 
            }
            
            // Extraemos la ruta del enemigo directamente desde el modelo del enemigo creado
            String rutaEnemigoImg = enemigo.getRutaImagen();
            if (rutaEnemigoImg == null || rutaEnemigoImg.isEmpty()) {
                rutaEnemigoImg = "/recursos/Esclavo.jpg"; // Valor por defecto de seguridad
            }

            // Actualizamos los retratos inferiores en la vista de batalla
            vistaBatalla.actualizarRetratos(rutaHeroeImg, rutaEnemigoImg);
            
            ctrlBatalla = new ControladorBatalla(vistaBatalla, batalla, this);
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

    public void personajeSeleccionado(Arquetipo arquetipoElegido) {
        this.heroe = catalogo.crearHeroe(arquetipoElegido);
        
        // Creamos los escenarios para cada zona de la mazmorra
        Escenario escenarioBosque = Escenario.salaNormal(
            "bosque", 
            "/recursos/mazmorra/bosque.gif", 
            "Te encuentras en los senderos oscuros del bosque.", 
            "Ir por la senda izquierda.", 
            "Seguir al frente entre los árboles.", 
            "Tomar el sendero derecho."
        );
        
        Escenario escenarioCatacumbas = Escenario.salaNormal(
            "mazmorraa", 
            "/recursos/mazmorra/mazmorra.gif", 
            "El aire es pesado en las catacumbas.", 
            "Bajar por el pasillo izquierdo.", 
            "Continuar por el corredor principal.", 
            "Explorar la cripta derecha."
        );
        
        Escenario escenarioCueva = Escenario.salaNormal(
            "cueva", 
            "/recursos/mazmorra/cueva.gif", 
            "Te adentras en una profunda cueva rocosa.", 
            "Tomar la grieta angosta.", 
            "Avanzar por el centro.", 
            "Subir por las rocas."
        );
        
        Escenario escenarioJefe = Escenario.salaDelJefe(
            "jefe", 
            "/recursos/mazmorra/jefe_puerta.gif", 
            "Has llegado a la cámara final.", 
            "Enfrentar al Guardián de las Cenizas."
        );

        // Construimos el nivel con los 3 enemigos y el jefe final en orden
        this.nivel = new Nivel.Builder("Senderos de las Cenizas")
                .sala(escenarioBosque, "esclavo")         // 1. Bruto de las Estepas (¡Primer combate!)
                .sala(escenarioCatacumbas, "guardia_hueco") // 2. Víbora del Foso
                .sala(escenarioCueva, "sacerdotisa")      // 3. Viuda de Sangre
                .jefe(escenarioJefe, "guardian_cenizas")    // 4. Guardia Infernal (Jefe)
                .build();
                
        // Al comenzar, entramos directamente a la batalla contra el Bruto de las Estepas en la Sala 1
        irABatalla();
    }

    public void avanzarSala() {
        if (nivel.haySiguiente()) {
            nivel.avanzar(); // Avanza a la siguiente sala en la lista
            
            // Verificamos si la nueva sala tiene un enemigo o es de exploración
            if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
                irABatalla();
            } else {
                irAExploracion("Avanzas a una nueva zona de la mazmorra.");
            }
        } else {
            // Si ya no hay más salas (fin del juego / victoria total tras el jefe)
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }

    public void caminoElegido(Camino camino) {
        // Cuando el jugador elige un camino en la pantalla de exploración, avanzamos a la batalla de esa sala
        avanzarSala();
    }

    public void batallaTerminada(boolean ganoHeroe) {
        if (ganoHeroe) {
            // Si ganamos, verificamos si hay más salas por delante o si era el jefe final
            if (nivel.haySiguiente()) {
                irAExploracion("¡Has derrotado al enemigo! Elige tu siguiente camino.");
            } else {
                // ¡Derrotaste al jefe final! Volvemos al menú principal
                ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
                ventana.cambiarPantalla(MENU, () -> {}, null);
            }
        } else {
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }
}