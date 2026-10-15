package controlador;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import modelo.Batalla;
import modelo.Habilidad;
import vista.VistaBatalla;
import vista.ReproductorSonido;

public class ControladorBatalla {
    private static final int PAUSA_FIN_BATALLA_MS = 2000;
    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro maestro;
    private final Timer timerEnemigo;

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro maestro) {
        this.vista = vista;
        this.batalla = batalla;
        this.maestro = maestro;

        this.timerEnemigo = new Timer(1500, (ActionEvent e) -> turnoEnemigo());
        this.timerEnemigo.setRepeats(false);
// Listeners    
    this.vista.onAtacar(e -> {
        ReproductorSonido.reproducirClick(); 
        accionJugador_Atacar();
});

    this.vista.onHabilidad(e -> {
        ReproductorSonido.reproducirClick();
        mostrarMenuHabilidades(); 
});

    this.vista.onDefenderse(e -> {
        ReproductorSonido.reproducirClick();
        accionJugador_Defender();
});

        actualizarVista();
    }

    public void iniciar() {
        vista.mostrarResultado("¡Un " + batalla.getEnemigo().getNombre() + " se interpone en tu camino!");
        vista.mostrar();
    }

    public void cerrar() {
        timerEnemigo.stop();
        vista.cerrar();
    }

private void terminarBatalla(boolean ganoHeroe) {
        vista.setBotonesHabilitados(false);
        
        if (ganoHeroe) {
            modelo.Heroe heroe = batalla.getHeroe();
            modelo.Enemigo enemigo = batalla.getEnemigo();
            
            // 1. Robar habilidad del enemigo (tomamos la primera que tenga)
            if (enemigo.getCantidadHabilidades() > 0) {
                modelo.Habilidad robada = enemigo.getHabilidad(0);
                while (!robada.getCdListo()) {
    robada.cronoCooldown();
}
                heroe.aprenderHabilidad(robada);
                vista.mostrarResultado("¡Has robado la habilidad: " + robada.getNombreHabilidad() + "!");
            }

            // 2. Esperamos un toque para que leas el mensaje y vemos qué pasa
            javax.swing.Timer pausa = new javax.swing.Timer(2500, e -> {
                if (heroe.getHabilidadesConocidas().size() > 2) {
                    mostrarDialogoSeleccionHabilidades(heroe);
                } else {
                    // Si tiene 2 o menos, las equipa automáticamente
                    heroe.equiparHabilidades(heroe.getHabilidadesConocidas());
                    maestro.batallaTerminada(true);
                }
            });
            pausa.setRepeats(false);
            pausa.start();
        } else {
            // Si pierdes, directo al Game Over
            javax.swing.Timer pausa = new javax.swing.Timer(2000, e -> maestro.batallaTerminada(false));
            pausa.setRepeats(false);
            pausa.start();
        }
    }

    private void actualizarVista() {
        vista.actualizarEstadisticas(
            batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
            batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida()
        );
    }

    private void accionJugador_Atacar() {
        String resultado = batalla.ejecutarAtaqueHeroe(); // Delega al modelo
        
        vista.mostrarResultado(resultado);
        vista.efectoTemblor();
        actualizarVista();
        vista.setBotonesHabilitados(false);

        if (batalla.estaTerminada()) {
            terminarBatalla(batalla.ganoHeroe());
        } else {
            this.timerEnemigo.start();
        }
    }

private void accionJugador_Habilidad(int indiceFija) {
        String resultado = batalla.ejecutarHabilidadHeroe(indiceFija);
        vista.mostrarResultado(resultado);
        vista.efectoTemblor();
        actualizarVista();
        vista.setBotonesHabilitados(false);

        if (batalla.estaTerminada()) {
            terminarBatalla(batalla.ganoHeroe());
        } else {
            this.timerEnemigo.start();
        }
    }

    private void accionJugador_Defender() {
        String resultado = batalla.ejecutarDefensaHeroe(); // Delega al modelo
        
        vista.mostrarResultado(resultado);
        actualizarVista();
        vista.setBotonesHabilitados(false);

        if (batalla.estaTerminada()) {
            terminarBatalla(batalla.ganoHeroe());
        } else {
            this.timerEnemigo.start();
        }
    }

    private void turnoEnemigo() {
        // Toda la IA (estrategia, stuns, daño) ocurre adentro del modelo
        String mensajeAccion = batalla.ejecutarTurnoEnemigo(); 
        
        vista.mostrarResultado(mensajeAccion);
        vista.efectoTemblor();
        actualizarVista();

        if (batalla.estaTerminada()) {
            terminarBatalla(batalla.ganoHeroe());
        } else {
            // Evaluamos si el enemigo te aturdió para manejar el timer visual
            if (batalla.getHeroe().getaturdir()) {
                vista.mostrarResultado("¡Estás aturdido! Pierdes tu turno.");
                
                // Si el héroe está aturdido, el enemigo vuelve a jugar después de una pausa
                Timer timerDobleAtaque = new Timer(2000, ev -> {
                    batalla.getHeroe().sacarAturdimiento(); // Limpiamos el stun
                    turnoEnemigo(); // Vuelve a jugar el enemigo
                });
                timerDobleAtaque.setRepeats(false);
                timerDobleAtaque.start();
            } else {
                // Flujo normal: te devuelve los controles
                batalla.getHeroe().setDefendiendo(false); // Sacamos la defensa al volver tu turno
                Timer timerEspera = new Timer(1500, e -> {
                    vista.setBotonesHabilitados(true);
                    vista.mostrarResultado("¡Es tu turno!");
                });
                timerEspera.setRepeats(false);
                timerEspera.start();
            }
        }
        }
    private void mostrarDialogoSeleccionHabilidades(modelo.Heroe heroe) {
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame)null, true);
        dialogo.setUndecorated(true); // ¡ESTO LE QUITA LA BARRA DE WINDOWS! Da aspecto de juego 100%
        
        // Creamos un panel principal que sirva de "fondo" para todo el cartel
        javax.swing.JPanel panelPrincipal = new javax.swing.JPanel(new java.awt.BorderLayout());
        panelPrincipal.setBackground(new java.awt.Color(15, 15, 15)); // Negro/Gris muy oscuro
        panelPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 80, 50), 3)); // Borde dorado opaco

        // Título con estética medieval
        javax.swing.JLabel lblTitulo = new javax.swing.JLabel(" HAS APRENDIDO DEMASIADO. ELIGE 2 HABILIDADES ", javax.swing.SwingConstants.CENTER);
        lblTitulo.setForeground(new java.awt.Color(220, 200, 150)); // Color pergamino/dorado
        lblTitulo.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 18));
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 15, 10));
        panelPrincipal.add(lblTitulo, java.awt.BorderLayout.NORTH);
        
        // Contenedor de las casillas de elección
        javax.swing.JPanel panelChecks = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panelChecks.setBackground(new java.awt.Color(15, 15, 15));
        panelChecks.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 50, 20, 50));
        
        java.util.List<javax.swing.JCheckBox> checks = new java.util.ArrayList<>();
        
        for (modelo.Habilidad h : heroe.getHabilidadesConocidas()) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(h.getNombreHabilidad());
            
            // --- ESTÉTICA DE LOS CHECKBOXES ---
            cb.setBackground(new java.awt.Color(25, 25, 25));
            cb.setForeground(java.awt.Color.WHITE);
            cb.setFont(new java.awt.Font("Serif", java.awt.Font.PLAIN, 16));
            cb.setFocusPainted(false); // Le saca el cuadradito punteado feo de selección
            
            if (heroe.getHabilidades().contains(h)) {
                cb.setSelected(true);
            }
            checks.add(cb);
            panelChecks.add(cb);
        }
        
        // Botón de confirmar estilo "Sangre/Fuego"
        javax.swing.JButton btnConfirmar = new javax.swing.JButton("SELLAR VÍNCULO");
        btnConfirmar.setBackground(new java.awt.Color(60, 15, 15)); // Rojo muy oscuro
        btnConfirmar.setForeground(java.awt.Color.WHITE);
        btnConfirmar.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 16));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 10, 12, 10));
        
        btnConfirmar.addActionListener(e -> {
            java.util.List<modelo.Habilidad> seleccionadas = new java.util.ArrayList<>();
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    seleccionadas.add(heroe.getHabilidadesConocidas().get(i));
                }
            }
            
            if (seleccionadas.size() > 2) {
                javax.swing.JOptionPane.showMessageDialog(dialogo, "El límite de tu cuerpo soporta solo 2 habilidades.");
            } else if (seleccionadas.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(dialogo, "Debes aferrarte a por lo menos 1 habilidad.");
            } else {
                heroe.equiparHabilidades(seleccionadas);
                dialogo.dispose(); 
                maestro.batallaTerminada(true); 
            }
        });
        
        panelPrincipal.add(panelChecks, java.awt.BorderLayout.CENTER);
        panelPrincipal.add(btnConfirmar, java.awt.BorderLayout.SOUTH);
        
        dialogo.add(panelPrincipal);
        dialogo.pack(); 
        dialogo.setLocationRelativeTo(null); 
        dialogo.setVisible(true); 
    }
private void mostrarMenuHabilidades() {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        
        // Colores extraídos de la estética de tu panel inferior
        java.awt.Color fondoMarron = new java.awt.Color(38, 33, 28);
        java.awt.Color textoDorado = new java.awt.Color(220, 185, 115);
        java.awt.Color bordeDorado = new java.awt.Color(166, 131, 71);

        menu.setBackground(fondoMarron);
        menu.setBorder(javax.swing.BorderFactory.createLineBorder(bordeDorado, 1));

        if (batalla.getHeroe().getCantidadHabilidades() == 0) {
            vista.mostrarResultado("El héroe no tiene habilidades equipadas.");
            return;
        }

        for (int i = 0; i < batalla.getHeroe().getCantidadHabilidades(); i++) {
            modelo.Habilidad hab = batalla.getHeroe().getHabilidad(i);
            
            // Creamos un ÚNICO ítem por habilidad
            javax.swing.JMenuItem item = new javax.swing.JMenuItem("✦ " + hab.getNombreHabilidad());
            
            item.setBackground(fondoMarron);
            item.setForeground(textoDorado);
            item.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 15));
            item.setOpaque(true);

            if (!hab.getCdListo()) {
                item.setEnabled(false);
                // Si el LookAndFeel de Java te pone feo el texto deshabilitado, forzamos el color con HTML
                item.setText("<html><font color='#808080'>✦ " + hab.getNombreHabilidad() + " (CD: " + hab.getCooldownActual() + ")</font></html>");
            }
            
            final int indice = i;
            item.addActionListener(ev -> accionJugador_Habilidad(indice));
            
            menu.add(item);
        }
        
        vista.mostrarMenuHabilidades(menu); 
    }
}