package vista;

import modelo.ModeloPuntuacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Modal que muestra la tabla. Recibe los datos ya listos; no los busca por su cuenta.
public class VistaPuntuaciones extends JDialog {

    private final DefaultTableModel modeloTabla;

    public VistaPuntuaciones(JFrame padre) {
        super(padre, "Tabla de puntuaciones", true); // true = modal
        setSize(350, 300);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        String[] columnas = {"Posición", "Jugador", "Puntos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        add(new JScrollPane(new JTable(modeloTabla)), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel panelSur = new JPanel();
        panelSur.add(btnCerrar);
        add(panelSur, BorderLayout.SOUTH);
    }

    public void cargarPuntuaciones(List<ModeloPuntuacion> puntuaciones) {
        modeloTabla.setRowCount(0);
        int posicion = 1;
        for (ModeloPuntuacion p : puntuaciones) {
            modeloTabla.addRow(new Object[]{posicion++, p.getJugador(), p.getPuntos()});
        }
    }
}