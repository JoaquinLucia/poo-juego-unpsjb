package vista.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Modal con la tabla de puntuaciones.
 * Recibe los datos ya listos del controlador; no conoce el modelo ni la base.
 * Usa los mismos colores que el menú de pausa (fondo casi negro y borde dorado).
 */
public class PanelPuntuaciones extends JDialog {

    private final Color fondo = new Color(15, 14, 16);
    private final Color fondoFila = new Color(28, 25, 22);
    private final Color fondoFilaAlterna = new Color(36, 32, 27);
    private final Color borde = new Color(135, 105, 58);
    private final Color dorado = new Color(220, 190, 120);
    private final Color texto = new Color(230, 210, 165);
    private final Color boton = new Color(40, 35, 30);

    private final DefaultTableModel filasTabla;
    private final JButton cerrar = new JButton("Volver");

    public PanelPuntuaciones(JFrame padre) {
        super(padre, "Tabla de puntuaciones", true);   // modal
        setUndecorated(true);

        // ---- Título ----
        JLabel titulo = new JLabel("Tabla de puntuaciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 32));
        titulo.setForeground(dorado);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // ---- Tabla ----
        filasTabla = new DefaultTableModel(new String[]{"Pos.", "Jugador", "Puntos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = crearTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(borde, 2));
        scroll.getViewport().setBackground(fondoFila);
        scroll.setPreferredSize(new Dimension(520, 360));

        // ---- Botón ----
        cerrar.setFont(new Font("Serif", Font.BOLD, 20));
        cerrar.setForeground(texto);
        cerrar.setBackground(boton);
        cerrar.setFocusPainted(false);
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 2),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)));
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        panelBoton.add(cerrar);

        // ---- Armado ----
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(fondo);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 3),
                BorderFactory.createEmptyBorder(28, 36, 24, 36)));
        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);
        contenido.add(panelBoton, BorderLayout.SOUTH);

        setContentPane(contenido);
        getRootPane().setDefaultButton(cerrar);   // Enter cierra
        pack();
        setLocationRelativeTo(padre);
    }

    // ---------- Enganches para el controlador ----------

    /** Borra todas las filas. */
    public void limpiarPuntajes() {
        filasTabla.setRowCount(0);
    }

    /** Agrega una fila con datos simples, sin conocer el modelo. */
    public void agregarPuntaje(int posicion, String jugador, int puntos) {
        filasTabla.addRow(new Object[]{posicion + ".", jugador, puntos});
    }

    public void onCerrar(ActionListener listener) {
        cerrar.addActionListener(listener);
    }

    // ---------- Estilo de la tabla ----------

    private JTable crearTabla() {
        JTable tabla = new JTable(filasTabla);
        tabla.setRowHeight(34);
        tabla.setFont(new Font("Serif", Font.BOLD, 18));
        tabla.setForeground(texto);
        tabla.setBackground(fondoFila);
        tabla.setGridColor(borde);
        tabla.setShowVerticalLines(false);
        tabla.setFocusable(false);
        tabla.setRowSelectionAllowed(false);

        // Anchos de las columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);

        // Encabezado
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setFont(new Font("Serif", Font.BOLD, 20));
        encabezado.setBackground(boton);
        encabezado.setForeground(dorado);
        encabezado.setReorderingAllowed(false);
        encabezado.setResizingAllowed(false);
        DefaultTableCellRenderer rendererEncabezado = new DefaultTableCellRenderer();
        rendererEncabezado.setHorizontalAlignment(SwingConstants.CENTER);
        rendererEncabezado.setBackground(boton);
        rendererEncabezado.setForeground(dorado);
        rendererEncabezado.setFont(new Font("Serif", Font.BOLD, 20));
        rendererEncabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, borde));
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(rendererEncabezado);
        }

        // Celdas centradas, con filas alternadas para leer mejor
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setBackground(fila % 2 == 0 ? fondoFila : fondoFilaAlterna);
                setForeground(fila == 0 ? dorado : texto);   // el primero, en dorado
                return this;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.setDefaultRenderer(Object.class, renderer);
        tabla.setDefaultRenderer(Integer.class, renderer);

        return tabla;
    }
}