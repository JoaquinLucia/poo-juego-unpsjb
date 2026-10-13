package controlador;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import modelo.Arquetipo;
import modelo.CatalogoPersonajes;
import vista.PanelSeleccionPersonaje;
import vista.VistaSeleccion;
import controlador.utilidades.GestorDeSonido;

/**
 * Maneja la lógica de interacción (teclado y mouse) para la selección de arquetipos.
 */
public class ControladorSeleccion {
    private final VistaSeleccion vista;
    private final ControladorMaestro maestro;
    private final CatalogoPersonajes catalogo;
    private Arquetipo[] arquetipos;
    private int indiceActual = 0;

    public ControladorSeleccion(VistaSeleccion vista, ControladorMaestro maestro, CatalogoPersonajes catalogo) {
        this.vista = vista;
        this.maestro = maestro;
        this.catalogo = catalogo;
        
        // Obtenemos los arquetipos disponibles desde el catálogo
        this.arquetipos = Arquetipo.values();

        inicializarEventos();
    }

    public void reiniciar() {
        indiceActual = 0;
        actualizarVista();
    }

    public VistaSeleccion getVista() {
        return vista;
    }

    private void actualizarVista() {
        if (arquetipos.length > 0) {
            vista.mostrarPersonaje(arquetipos[indiceActual], arquetipos.length > 1);
        }
    }

    private void siguiente() {
        if (arquetipos.length > 1) {
            indiceActual = (indiceActual + 1) % arquetipos.length;
            GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
            actualizarVista();
        }
    }

    private void anterior() {
        if (arquetipos.length > 1) {
            indiceActual = (indiceActual - 1 + arquetipos.length) % arquetipos.length;
            GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
            actualizarVista();
        }
    }

    private void confirmar() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        if (arquetipos.length > 0) {
            maestro.personajeSeleccionado(arquetipos[indiceActual]);
        }
    }

    private void volver() {
        GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
        maestro.iniciar(); // Vuelve al menú principal
    }

    private void inicializarEventos() {
        PanelSeleccionPersonaje panel = vista.getPanelSeleccion();

        // Eventos de Teclado
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        anterior();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        siguiente();
                        break;
                    case KeyEvent.VK_ENTER:
                        confirmar();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        volver();
                        break;
                }
            }
        });

        // Eventos de Mouse (Movimiento / Hover)
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                PanelSeleccionPersonaje.Zona zona = panel.zonaEn(e.getPoint());
                panel.setHover(zona);
            }
        });

        // Eventos de Mouse (Clicks)
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PanelSeleccionPersonaje.Zona zona = panel.zonaEn(e.getPoint());
                switch (zona) {
                    case COMENZAR:
                        confirmar();
                        break;
                    case VOLVER:
                        volver();
                        break;
                    case FLECHA_IZQ:
                        anterior();
                        break;
                    case FLECHA_DER:
                        siguiente();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}