package Vista;

// para las imagenes 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
       // this.ventana.setSize(360, 200); //tamaño de la ventana
        this.ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);//pantalla completa

        this.ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ventana.setLocationRelativeTo(null);//Centra la ventana respecto a la pantalla.
        this.ventana.setLayout(new BorderLayout(0, 0)); 

        this.heroe=new JButton("Heroe atacar");
        this.enemigo=new JButton("enemigo atacar");
        this.etiquetaResultado = new JLabel("Resultado: ...");
        this.etiquetaResultado.setForeground(Color.green);

        // JPanel panelResultado = new JPanel();
        // panelResultado.add(etiquetaResultado);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setPreferredSize(new Dimension(0, 200));
        panelInferior.setBackground(Color.BLACK);
      //  panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));   // margen
        panelInferior.setBorder(BorderFactory.createLineBorder(new Color(130, 120, 100), 3));


        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        panelBotones.add(this.heroe);
        panelBotones.add(this.enemigo);
        panelBotones.setOpaque(false); //para que se vea transparente
        

        panelInferior.add(panelBotones, BorderLayout.NORTH);
        panelInferior.add(etiquetaResultado, BorderLayout.CENTER);


        PanelBatalla panelfondo = new PanelBatalla(); //FONDO DE LA BATALLA 
        
        

        //agrego a ventana 
        this.ventana.add(panelfondo,BorderLayout.CENTER);

        this.ventana.add(panelInferior, BorderLayout.SOUTH);
        //this.ventana.pack();                         // el tamano sale del tamano preferido de los componentes //Calculá automáticamente el tamaño de la ventana basándote en el tamaño preferido de los componentes
        // this.ventana.setLocationRelativeTo(null);    // despues de pack(), para centrar con el tamano real

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

    public JLabel getetiquetaResultado(){
        return this.etiquetaResultado;
    }
    public void mostraeResultado(String heroe, int danio) {
        this.etiquetaResultado.setText(heroe + " ataco con :" + danio);
    }
}
