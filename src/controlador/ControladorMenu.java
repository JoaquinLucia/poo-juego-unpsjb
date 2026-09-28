package controlador;

import modelo.ModeloPuntuacion;
import modelo.Batalla;
import modelo.Enemigo;
import modelo.EnemigoFactory;
import modelo.EnemigoEliteFactory; 
import modelo.Heroe;
import modelo.HeroeFactory;          // <-- NUEVO IMPORT
import modelo.CaballeroFactory;      // <-- NUEVO IMPORT
import modelo.MagoFactory;           // <-- NUEVO IMPORT
import modelo.ArqueroFactory;        // <-- NUEVO IMPORT
import modelo.ModeloTablaPuntuacion;
import vista.VistaBatalla;
import vista.VistaMenu;
import vista.VistaPuntuaciones;
import controlador.ControladorBatalla;

public class ControladorMenu {

    private final VistaMenu vista;
    private final ModeloTablaPuntuacion tablaPuntuaciones;

    public ControladorMenu(VistaMenu vista, ModeloTablaPuntuacion tablaPuntuaciones) {
        this.vista = vista;
        this.tablaPuntuaciones = tablaPuntuaciones;

        // Botones del menú principal
        vista.onComenzar(e -> vista.mostrarSeleccion()); // <-- AHORA VA A LA SELECCIÓN
        vista.onVerPuntuaciones(e -> mostrarPuntuaciones());
        vista.onOpciones(e -> mostrarOpciones());
        vista.onSalir(e -> salir());

        // Botones de la selección de personaje
        vista.onSeleccionarCaballero(e -> iniciarBatalla(new CaballeroFactory()));
        vista.onSeleccionarMago(e -> iniciarBatalla(new MagoFactory()));
        vista.onSeleccionarArquero(e -> iniciarBatalla(new ArqueroFactory()));
        vista.onVolverMenu(e -> vista.mostrarMenu());
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    // Este método reemplaza a tu viejo comenzarJuego()
    // Recibe la fábrica del héroe que el jugador acaba de elegir
    private void iniciarBatalla(HeroeFactory fabricaHeroe) {
        // 1. Ocultamos el menú completo
        vista.dispose(); 

        // 2. Instanciamos al héroe usando la fábrica que nos pasaron
        Heroe heroe = fabricaHeroe.crearHeroe();
        
        // 3. Instanciamos al enemigo
        EnemigoFactory fabricaEnemigo = new EnemigoEliteFactory(); 
        Enemigo enemigo = fabricaEnemigo.crearEnemigo();
        
        // 4. Creamos la batalla
        Batalla batalla = new Batalla(heroe, enemigo);
        
        // 5. Instanciamos la Vista y el Controlador de batalla
        VistaBatalla vistaBatalla = new VistaBatalla();
        new ControladorBatalla(vistaBatalla, batalla);
        
        // 6. Mostramos la pantalla de combate
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