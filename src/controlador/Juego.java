package controlador;

import modelo.*;
import vista.VistaJuego;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Juego {
    private final VistaJuego vista;
    private final Heroe heroe;
    private Enemigo enemigoActual;

    public Juego(VistaJuego vista, Heroe heroe, Enemigo enemigoInicial) {
        this.vista = vista;
        this.heroe = heroe;
        this.enemigoActual = enemigoInicial;

        // Receptores con Lambdas (un receptor por botón en una sola línea)
        this.vista.getBtnAtaqueBasico().addActionListener(e -> ejecutarAtaque());
        this.vista.getBtnRobarHabilidad().addActionListener(e -> ejecutarRobo());
        this.vista.getBtnUsarHabilidadRobada().addActionListener(e -> ejecutarHabilidadRobada());

        // Receptor de teclado con KeyAdapter (Clase anónima sobre adaptador)
        this.vista.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    ejecutarAtaque();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    ejecutarRobo();
                }
            }
        });

        this.vista.setFocusable(true);
    }

    // Métodos privados con la lógica del negocio
    private void ejecutarAtaque() {
        this.heroe.atacar(this.enemigoActual);
        this.vista.getLblEstado().setText("Atacaste al enemigo. Vida enemigo: " + this.enemigoActual.getVida());
    }

    private void ejecutarRobo() {
        // robarHabilidad en tu Heroe.java recibe (Entidad objetivo)
        this.heroe.robarHabilidad(this.enemigoActual);
        this.vista.getLblEstado().setText("Intentaste robar la habilidad de " + this.enemigoActual.getNombre());
    }

    private void ejecutarHabilidadRobada() {
        // Llama a usarHabilidad heredado de Entidad (usando el índice 0 de la lista)
        this.heroe.usarHabilidad(0, this.enemigoActual);
        this.vista.getLblEstado().setText("Usaste la habilidad robada contra " + this.enemigoActual.getNombre());
    }
}