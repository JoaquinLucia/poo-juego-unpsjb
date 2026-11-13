package vista.paneles;



import java.awt.*;
import javax.swing.*;

public class PanelDatosPersonaje extends JPanel {

    private  final Color PANEL = new Color(30, 27, 24);
    private  final Color DORADO = new Color(230, 210, 165);
    private  final Color BORDE = new Color(135, 105, 58);
    private  final Color TEXTO = new Color(190, 175, 145);

    private final JLabel nombre;
    private final JLabel clase;
    private final JLabel nivel;
    private final JLabel vida;
    private final JLabel ataque;
    private final JLabel habilidad;
    private final JLabel defensa;

    public PanelDatosPersonaje() {

        setLayout(new BorderLayout());
        setBackground(PANEL);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE, 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
            )
        );

        JPanel informacion =new JPanel(new GridLayout(0, 1, 5, 5));

        informacion.setBackground(PANEL);

        nombre = crearEtiqueta(" ", 28, DORADO);
        clase = crearEtiqueta("Clase: ", 19, TEXTO);
        nivel = crearEtiqueta("Nivel: ", 18, TEXTO);
        vida = crearEtiqueta("Vida: ", 18, TEXTO);
        ataque = crearEtiqueta("Ataque: ", 18, TEXTO);
        habilidad = crearEtiqueta("Habilidad: ", 18, TEXTO);
        defensa = crearEtiqueta("Defensa: ", 18, TEXTO);

        informacion.add(nombre);
        informacion.add(clase);
        informacion.add(nivel);
        informacion.add(vida);
        informacion.add(ataque);
        informacion.add(habilidad);
        informacion.add(defensa);

        add(informacion, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto,int tamaño,Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(color);
        etiqueta.setFont(new Font("Serif", Font.BOLD, tamaño));
        return etiqueta;
    }



    // ----- Setters: el controlador solo pasa el valor, el prefijo lo pone el panel -----

    public void setNombre(String valor) {
        nombre.setText(valor);
    }

    public void setClase(String valor) {
        clase.setText("Clase: " + valor);
    }

    public void setNivel(int valor) {
        nivel.setText("Nivel: " + valor);
    }

    public void setVida(int valor) {
        vida.setText("Vida: " + valor);
    }

    public void setAtaque(int valor) {
        ataque.setText("Ataque: " + valor);
    }

    public void setHabilidad(String valor) {
        habilidad.setText("Habilidad: " + valor);
    }

    public void setDefensa(int porcentaje) {
    defensa.setText("Defensa: Bloquea " + porcentaje + "%");
}

    /** Actualiza todos los datos de una sola vez. */
    public void mostrarDatos(String nombre, String clase, int nivel, int vida,int ataque, String habilidad, int porcentajeDefensa) {
        setNombre(nombre);
        setClase(clase);
        setNivel(nivel);
        setVida(vida);
        setAtaque(ataque);
        setHabilidad(habilidad);
        setDefensa(porcentajeDefensa);
    }
 
    /** Vuelve al estado inicial. */
    public void limpiar() {
        nombre.setText(" ");
        clase.setText("Clase: ");
        nivel.setText("Nivel: ");
        vida.setText("Vida: ");
        ataque.setText("Ataque: ");
        habilidad.setText("Habilidad: ");
        defensa.setText("Defensa: ");
}
 
    public JLabel getNombre() {
        return nombre;
    }

    public JLabel getClase() {
        return clase;
    }

    public JLabel getNivel() {
        return nivel;
    }

    public JLabel getVida() {
        return vida;
    }

    public JLabel getAtaque() {
        return ataque;
    }

    public JLabel getHabilidad() {
        return habilidad;
    }
}

