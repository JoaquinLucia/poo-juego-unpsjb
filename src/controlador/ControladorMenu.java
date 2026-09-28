package controlador;

import modelo.Arquetipo;
import modelo.ModeloMenu;
import modelo.Batalla;
import modelo.Enemigo;
import modelo.EnemigoEliteFactory; 
import modelo.EnemigoFactory;
import modelo.Heroe;
import modelo.HeroeFactory;
import modelo.CaballeroFactory;
import modelo.MagoFactory;
import modelo.ArqueroFactory;
import modelo.ModeloTablaPuntuacion;
import vista.VistaBatalla;
import vista.VistaMenu;
import vista.VistaPuntuaciones;

public class ControladorMenu {

    private final VistaMenu vista;
    private final ModeloTablaPuntuacion tablaPuntuaciones;
    private final ModeloMenu modeloMenu;
    private ControladorSeleccion controladorSeleccion;

    public ControladorMenu(VistaMenu vista, ModeloTablaPuntuacion tablaPuntuaciones) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;
        this.modeloMenu = new ModeloMenu();

        // Inicializamos el sub-controlador de la interfaz nueva
        this.controladorSeleccion = new ControladorSeleccion(
            modeloMenu, 
            vista.getPanelSeleccion(), 
            () -> vista.mostrarMenu(), // Qué hacer al tocar "Volver"
            (arquetipoElegido) -> iniciarBatalla(arquetipoElegido) // Qué hacer al tocar "Comenzar"
        );

        // Botones del menú principal
        vista.onComenzar(e -> iniciarSeleccionPersonaje()); 
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    private void iniciarSeleccionPersonaje() {
        controladorSeleccion.entrar();
        vista.mostrarSeleccion();
    }

    private void iniciarBatalla(Arquetipo arquetipoElegido) {
        vista.dispose(); 
        
        // Conectamos el Arquetipo visual con tus Fábricas lógicas
        HeroeFactory fabricaHeroe = switch (arquetipoElegido.name()) {
            case "CABALLERO" -> new CaballeroFactory();
            case "MAGO" -> new MagoFactory(); 
            case "ARQUERO" -> new ArqueroFactory();
            default -> new CaballeroFactory();
        };

        Heroe heroe = fabricaHeroe.crearHeroe();
        EnemigoFactory fabricaEnemigo = new EnemigoEliteFactory(); 
        Enemigo enemigo = fabricaEnemigo.crearEnemigo();
        
        Batalla batalla = new Batalla(heroe, enemigo);
        VistaBatalla vistaBatalla = new VistaBatalla();
        new ControladorBatalla(vistaBatalla, batalla);
        
        vistaBatalla.mostrar();
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