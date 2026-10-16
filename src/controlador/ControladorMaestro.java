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

// Importaciones necesarias para manejar el audio
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class ControladorMaestro {
    private final VentanaPrincipal ventana;
    private final CatalogoPersonajes catalogo;
    private Nivel nivel;
    private Heroe heroe;
    
    private ControladorMenu ctrlMenu;
    private ControladorSeleccion ctrlSeleccion;
    private ControladorMazmorra ctrlMazmorra;
    private ControladorBatalla ctrlBatalla;

    // Reproductor para la música continua de menús / exploración
    private Clip clipMenuMusica;

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
        VistaMazmorra vistaMazmorra = new VistaMazmorra(); // Instancia de la vista

        this.ctrlMenu = new ControladorMenu(vistaMenu, this);
        this.ctrlSeleccion = new ControladorSeleccion(vistaSeleccion, this, catalogo);
        this.ctrlMazmorra = new ControladorMazmorra(vistaMazmorra, this);

        ventana.agregarPantalla(MENU, vistaMenu);
        ventana.agregarPantalla(SELECCION, vistaSeleccion);
        
        // ¡IMPORTANTE! Asegúrate de registrar la vista de la mazmorra aquí:
        ventana.agregarPantalla(MAZMORRA, vistaMazmorra); 
    }

    public void iniciar() {
        reproducirMusicaMenu(); // <--- Inicia la música continua al abrir la aplicación
        ventana.mostrarDirecto(MENU);
    }

    // --- Métodos de gestión de la música continua del menú/exploración ---
    private void reproducirMusicaMenu() {
        try {
            if (clipMenuMusica != null && clipMenuMusica.isRunning()) {
                return; // Si ya está sonando, no la reinicia para evitar cortes
            }
            URL url = getClass().getResource("/recursos/sonidos/juego_musica.wav");
            if (url != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                clipMenuMusica = AudioSystem.getClip();
                clipMenuMusica.open(audioStream);
                clipMenuMusica.start();
                clipMenuMusica.loop(Clip.LOOP_CONTINUOUSLY); // Bucle infinito
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detenerMusicaMenu() {
        if (clipMenuMusica != null && clipMenuMusica.isRunning()) {
            clipMenuMusica.stop();
            clipMenuMusica.close();
        }
    }
    // --------------------------------------------------------------------

    public void iniciarJuegoDesdeMenu() {
        ctrlSeleccion.reiniciar();
        ventana.cambiarPantalla(SELECCION, () -> {}, null);
        // Nota: Como no detenemos la música acá, sigue sonando fluido al pasar a la selección de personaje.
    }

    private void irAExploracion(String mensajePrevio) {
        String encabezado = nivel.getNombre() + " - Sala " + nivel.getNumeroSala() + 
                " de " + nivel.getCantidadSalas() + 
                " | " + heroe.getNombre() + ": " + heroe.getVida() + " de vida";
        String mensaje = mensajePrevio.isEmpty() ? encabezado : encabezado + "\n" + mensajePrevio;

        reproducirMusicaMenu(); // Asegura que siga sonando la música de exploración/caminos

        ventana.cambiarPantalla(MAZMORRA, () -> {
            cerrarBatalla();
            ctrlMazmorra.mostrar(nivel.getSalaActual().getEscenario(), mensaje);
        }, null);
    }

    private void irABatalla() {
        detenerMusicaMenu(); // <--- Apagamos la música del menú justo antes de entrar a pelear

        ventana.cambiarPantalla(BATALLA, () -> {
            cerrarBatalla();
            
            // Creamos el enemigo de la sala actual usando su clave
            Enemigo enemigo = catalogo.crearEnemigo(nivel.getSalaActual().getIdEnemigo());
            Batalla batalla = new Batalla(heroe, enemigo);
            
            heroe.reiniciarTodasLasHabilidades();
            
            VistaBatalla vistaBatalla = new VistaBatalla();
            
            // Determinamos la ruta del retrato del héroe de forma correcta
            String rutaHeroeImg = "/recursos/batalla/caballero_icono.png"; // Valor por defecto para el Caballero
            if (heroe.getNombre().equalsIgnoreCase("Mago")) {
                rutaHeroeImg = "/recursos/batalla/mago_icono.png"; 
            } else if (heroe.getNombre().equalsIgnoreCase("Arquero")) {
                rutaHeroeImg = "/recursos/batalla/arquero_icono.png"; 
            }
            
            // Extraemos la ruta del retrato del enemigo
            String rutaEnemigoImg = enemigo.getRutaImagen();
            if (rutaEnemigoImg == null || rutaEnemigoImg.isEmpty()) {
                rutaEnemigoImg = "/recursos/batalla/bruto_icon.png"; // Valor de respaldo seguro
            }

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
        
        // 1. Escenario del Bosque
        Escenario escenarioBosque = Escenario.salaNormal(
            "bosque", 
            "/recursos/mazmorra/bosque.gif", 
            "Te encuentras en los senderos oscuros del bosque.", 
            "Ir por la senda izquierda.", 
            "Seguir al frente entre los árboles.", 
            "Tomar el sendero derecho."
        );
        
        // 2. Escenario de las Catacumbas/Mazmorra
        Escenario escenarioCatacumbas = Escenario.salaNormal(
            "mazmorraa", 
            "/recursos/mazmorra/mazmorra.gif", 
            "El aire es pesado en las catacumbas.", 
            "Bajar por el pasillo izquierdo.", 
            "Continuar por el corredor principal.", 
            "Explorar la cripta derecha."
        );
        
        // 3. Escenario de la Cueva
        Escenario escenarioCueva = Escenario.salaNormal(
            "cueva", 
            "/recursos/mazmorra/cueva.gif", 
            "Te adentras en una profunda cueva rocosa.", 
            "Tomar la grieta angosta.", 
            "Avanzar por el centro.", 
            "Subir por las rocas."
        );

        // 4. Escenario del Guardián (usando .jefe para cumplir con el requisito del Builder)
        Escenario escenarioGuardian = Escenario.salaDelJefe(
            "jefe", 
            "/recursos/mazmorra/jefe_puerta.gif", 
            "Te aproximas a un gran portal resguardado por una presencia imponente.", 
            "Enfrentar al Guardian."
        );

        // Construimos el nivel cumpliendo la estructura obligatoria del Builder
        this.nivel = new Nivel.Builder("Senderos de las Cenizas")
                .sala(escenarioBosque, "bruto")     // 1. Bosque -> "bruto"
                .sala(escenarioCatacumbas, "vibora") // 2. Catacumbas -> "vibora"
                .sala(escenarioCueva, "viuda")       // 3. Cueva -> "viuda"
                .jefe(escenarioGuardian, "guardian") // 4. Zona final -> "guardian"
                .build();
                
        // Muestra primero los caminos del Bosque
        irAExploracion("");
    }

    public void avanzarSala() {
        if (nivel.haySiguiente()) {
            nivel.avanzar();
            
            if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
                irABatalla();
            } else {
                irAExploracion("Avanzas a una nueva zona de la mazmorra.");
            }
        } else {
            // Si termina la 4.ª sala (el Guardián), regresa al menú y reanuda la música de menú
            reproducirMusicaMenu();
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }

    public void caminoElegido(Camino camino) {
        // Al elegir un camino en la exploración actual, va a la batalla de la sala donde está parado
        if (nivel.getSalaActual().getIdEnemigo() != null && !nivel.getSalaActual().getIdEnemigo().isEmpty()) {
            irABatalla();
        } else {
            avanzarSala();
        }
    }

    public void batallaTerminada(boolean ganoHeroe) {
        if (ganoHeroe) {
            if (nivel.haySiguiente()) {
                nivel.avanzar(); 
                irAExploracion("¡Has derrotado al enemigo! Elige tu siguiente camino.");
            } else {
                // Derrotaste al Guardián (fin del recorrido)
                reproducirMusicaMenu();
                ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
                ventana.cambiarPantalla(MENU, () -> {}, null);
            }
        } else {
            // Si pierdes, vuelves al menú principal con su música
            reproducirMusicaMenu();
            ctrlMenu.getVista().aplicarEscala(ventana.getEscala());
            ventana.cambiarPantalla(MENU, () -> {}, null);
        }
    }
}