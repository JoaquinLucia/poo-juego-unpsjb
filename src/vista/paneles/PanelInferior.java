package vista.paneles;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class PanelInferior extends JPanel {

    private final JButton atacar;
    private final JButton habilidad;
    private final JButton defenderse;
    private final JLabel etiquetaResultado;
    private final JLabel etiquetaTurno;

    private final JLabel personajeHeroe = new JLabel();
    private final JLabel personajeEnemigo = new JLabel();

    public PanelInferior() {
        this.atacar = new JButton("⚔ Atacar");
        this.habilidad = new JButton("✦ Habilidad");
        this.defenderse = new JButton("🛡 Defenderse");

        this.estilizarBoton(atacar);
        this.estilizarBoton(habilidad);
        this.estilizarBoton(defenderse);

        this.etiquetaResultado = new JLabel("Resultado: ...");
        etiquetaResultado.setForeground(new Color(220, 190, 120));    
        etiquetaResultado.setHorizontalAlignment(JLabel.CENTER);
        etiquetaResultado.setFont(new Font("Serif", Font.BOLD, 22));

        this.etiquetaTurno = new JLabel(" ");
        etiquetaTurno.setForeground(new Color(220, 190, 120));    
        etiquetaTurno.setHorizontalAlignment(JLabel.CENTER);
        etiquetaTurno.setFont(new Font("Serif", Font.BOLD, 25));

        personajeHeroe.setHorizontalAlignment(JLabel.CENTER);
        personajeHeroe.setVerticalAlignment(JLabel.CENTER);
        personajeEnemigo.setHorizontalAlignment(JLabel.CENTER);
        personajeEnemigo.setVerticalAlignment(JLabel.CENTER);

        this.setLayout(new BorderLayout(15, 10)); 
        this.setPreferredSize(new Dimension(0, 230));
        this.setBackground(new Color(15, 14, 16));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(135, 105, 58), 3), BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        
        JPanel panelBatalla = new JPanel(new BorderLayout(15,0));
        panelBatalla.setOpaque(false);

        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(this.atacar);
        panelBotones.add(this.habilidad);
        panelBotones.add(this.defenderse);

        panelBatalla.add(etiquetaTurno, BorderLayout.NORTH);
        panelBatalla.add(panelBotones,BorderLayout.CENTER);
        panelBatalla.add(etiquetaResultado,BorderLayout.SOUTH);

        this.add(personajeHeroe, BorderLayout.WEST);
        this.add(panelBatalla, BorderLayout.CENTER);
        this.add(personajeEnemigo, BorderLayout.EAST);
    }
    
    public void setBotonesHabilitados(boolean estado) {
        atacar.setEnabled(estado);
        habilidad.setEnabled(estado);
        defenderse.setEnabled(estado);
    }

    public void mostrarResultado(String mensaje) {
        etiquetaResultado.setText(mensaje);
    }

    public void mostrarTurno(String mensaje) {
    etiquetaTurno.setText(mensaje);
    }
    public void setRetratos(String rutaHeroe, String rutaEnemigo) {
        cargarRetrato(personajeHeroe, rutaHeroe);
        cargarRetrato(personajeEnemigo, rutaEnemigo);
    }

    private void cargarRetrato(JLabel label, String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró el sprite: " + ruta);
            return;
        }
        Image img = new ImageIcon(url).getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(img));
    }

    public JButton getBotonAtacar() { return atacar; }
    public JButton getBotonHabilidad() { return habilidad; }
    public JButton getBotonDefenderse() { return defenderse; }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(new Color(40, 35, 30));
        boton.setForeground(new Color(230, 210, 165));
        boton.setFont(new Font("Serif", Font.BOLD, 18));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(135, 105, 58), 2),BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    }
}