package vista;



import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class VistaBatalla {
    private final JFrame ventana;
    private final JButton heroe;
    private final JButton enemigo;
    private final JLabel etiquetaResultado;
    
public VistaBatalla(){
        this.ventana = new JFrame("Batalla");
        this.ventana.setSize(360, 200);
        this.ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setLayout(new BorderLayout(10, 10)); 

        this.heroe=new JButton("Heroe atacar");
        this.enemigo=new JButton("enemigo atacar");
        this.etiquetaResultado = new JLabel("Resultado: ...");


        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(etiquetaResultado);

        JPanel panelBotones = new JPanel(new GridLayout(1, 0, 8, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));   // margen
        panelBotones.add(this.heroe);
        panelBotones.add(this.enemigo);
        
        //agrego a ventana 
        this.ventana.add(panel,BorderLayout.SOUTH);
        this.ventana.add(panelBotones, BorderLayout.CENTER);
        this.ventana.pack();                         // el tamano sale del tamano preferido de los componentes
        this.ventana.setLocationRelativeTo(null);    // despues de pack(), para centrar con el tamano real

}

    public void mostrar(){
        this.ventana.setVisible(true);
    }

    public JButton getBotonHeroe(){
        return this.heroe;
    }

    public JButton getBotonEnemigo(){
        return this.enemigo;
    }

    public JLabel etiquetaResultado(){
        return this.etiquetaResultado;
    }
    public void mostraeResultado(String heroe, int danio) {
        this.etiquetaResultado.setText(String.format(heroe + " ataco con :" + danio));
    }
}
