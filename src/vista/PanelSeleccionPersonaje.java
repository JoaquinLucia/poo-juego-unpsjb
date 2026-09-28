package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Timer;

import modelo.Arquetipo;

/**
 * Pantalla de selección de personaje. Usa el mismo fondo animado que el menú
 * (hereda de PanelFondo) y dibuja encima el personaje, su ficha y los botones.
 *
 * Todo se dibuja en un espacio de diseño de 800x600 (el mismo que VistaMenu en
 * modo ventana) y se escala al tamaño real del panel, así que en pantalla
 * completa queda proporcionado igual que el menú.
 *
 * Es solo vista: dibuja el personaje que le indican y, para el controlador,
 * informa qué zona hay bajo el puntero (zonaEn). Los controles de teclado y
 * mouse están en controlador.ControladorSeleccion.
 */
public class PanelSeleccionPersonaje extends PanelFondo {

    private static final String RUTA_IMAGENES = "/recursos/imagenes_menu/";

    // Espacio de diseño (igual que VistaMenu en modo ventana)
    private static final int ANCHO_DISENO = 800;
    private static final int ALTO_DISENO = 600;
    private static final double ESCALA_IMAGENES = 0.7; // igual que en VistaMenu

    // Paleta tomada del fondo: noche azulada, luna pálida y el rojo del horizonte
    private static final Color HUESO        = new Color(220, 216, 207);
    private static final Color NIEBLA       = new Color(142, 151, 168);
    private static final Color LUNA         = new Color(201, 211, 230);
    private static final Color BRASA        = new Color(181, 80, 63);
    private static final Color PANEL        = new Color(8, 10, 16, 190);
    private static final Color BORDE        = new Color(92, 102, 122);
    private static final Color BORDE_OSCURO = new Color(34, 39, 50);
    private static final Color BARRA_VACIA  = new Color(34, 38, 48, 220);

    private static final String FUENTE = Font.SERIF;

    // Ubicación de los elementos (en coordenadas de diseño)
    private static final int CENTRO_PERSONAJE_X = 220;
    private static final int Y_PERSONAJE = 146;     // imagen PNG
    private static final int ALTO_PERSONAJE = 248;
    private static final double CENTRO_CUERPO = 0.42; // dónde cae el cuerpo dentro del PNG (0 a 1)
    private static final Rectangle FICHA = new Rectangle(390, 150, 350, 272);
    private static final int Y_BOTONES = 494;

    /** Partes clickeables de la pantalla. */
    public enum Zona { COMENZAR, VOLVER, FLECHA_IZQ, FLECHA_DER, NINGUNA }

    // Lo que se muestra (lo decide el controlador)
    private Arquetipo actual = Arquetipo.values()[0];
    private boolean hayVarios = false;
    private Zona hover = Zona.NINGUNA;

    private final BufferedImage imagenComenzar = cargarImagen("boton_comenzar.png");
    private final BufferedImage imagenVolver = cargarImagen("boton_volver.png"); // opcional
    private final BufferedImage imagenComenzarHover = aclarar(imagenComenzar);
    private final BufferedImage imagenVolverHover = aclarar(imagenVolver);

    // Zonas clickeables en coordenadas de diseño
    private final Rectangle zonaComenzar = new Rectangle();
    private final Rectangle zonaVolver = new Rectangle();
    private final Rectangle zonaFlechaIzq = new Rectangle();
    private final Rectangle zonaFlechaDer = new Rectangle();

    // Transformación diseño -> pantalla del último repintado (para el mouse)
    private AffineTransform transformacion = new AffineTransform();

    // Imágenes de los personajes, cargadas desde la ruta de cada Arquetipo
    private final Map<Arquetipo, BufferedImage> imagenesPersonajes = new EnumMap<>(Arquetipo.class);
    private BufferedImage retratoEscalado;
    private Arquetipo retratoDe;
    private int retratoAlto = -1;

    private final Timer animacion;
    private int tick = 0;

    public PanelSeleccionPersonaje() {
        super(RUTA_IMAGENES + "fondo.gif", null);
        setBackground(new Color(8, 10, 16)); // mientras carga el GIF
        setFocusable(true);

        for (Arquetipo a : Arquetipo.values()) {
            BufferedImage imagen = cargarRecurso(a.getRutaImagen());
            if (imagen != null) {
                imagenesPersonajes.put(a, imagen);
            } else {
                System.err.println("No se encontró la imagen del personaje: " + a.getRutaImagen());
            }
        }

        animacion = new Timer(40, e -> {
            tick++;
            repaint();
        });


        // La animación corre solo mientras la pantalla está a la vista
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                animacion.start();
                requestFocusInWindow();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                animacion.stop();
            }
        });
    }

    // ---------- Lo que usa el controlador ----------

    /** Muestra un personaje. hayVarios indica si se dibujan las flechas. */
    public void mostrarPersonaje(Arquetipo personaje, boolean hayVarios) {
        this.actual = personaje;
        this.hayVarios = hayVarios;
        repaint();
    }

    /** Resalta la zona que tiene el mouse encima y cambia el cursor. */
    public void setHover(Zona zona) {
        if (zona == hover) {
            return;
        }
        hover = zona;
        setCursor(Cursor.getPredefinedCursor(zona == Zona.NINGUNA ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        repaint();
    }

    /** Qué zona de la pantalla hay en ese punto (en coordenadas del panel). */
    public Zona zonaEn(Point enPantalla) {
        Point p = aDiseno(enPantalla);
        if (zonaComenzar.contains(p)) return Zona.COMENZAR;
        if (zonaVolver.contains(p)) return Zona.VOLVER;
        if (hayVarios && zonaFlechaIzq.contains(p)) return Zona.FLECHA_IZQ;
        if (hayVarios && zonaFlechaDer.contains(p)) return Zona.FLECHA_DER;
        return Zona.NINGUNA;
    }

    @Override
    public void removeNotify() {
        animacion.stop();
        super.removeNotify();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (isVisible()) {
            animacion.start();
        }
    }

    private Point aDiseno(Point enPantalla) {
        try {
            Point2D p = transformacion.inverseTransform(enPantalla, null);
            return new Point((int) p.getX(), (int) p.getY());
        } catch (NoninvertibleTransformException ex) {
            return new Point(-1, -1);
        }
    }

    // ---------- Dibujo ----------

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0); // el fondo animado

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        oscurecerFondo(g);

        // Escala el diseño de 800x600 al tamaño real, centrado y sin deformar
        double escala = Math.min(getWidth() / (double) ANCHO_DISENO, getHeight() / (double) ALTO_DISENO);
        AffineTransform t = new AffineTransform();
        t.translate((getWidth() - ANCHO_DISENO * escala) / 2, (getHeight() - ALTO_DISENO * escala) / 2);
        t.scale(escala, escala);
        transformacion = t;
        g.transform(t);

        dibujarTitulo(g);
        dibujarPersonaje(g, actual);
        dibujarFicha(g, actual);
        dibujarBotones(g);

        g.dispose();
    }

    /** Baja el brillo del fondo y agrega viñeta para que el texto se lea bien. */
    private void oscurecerFondo(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();
        g.setColor(new Color(4, 6, 10, 90));
        g.fillRect(0, 0, w, h);

        float radio = (float) Math.max(w, h) * 0.75f;
        g.setPaint(new RadialGradientPaint(w / 2f, h / 2f, radio,
                new float[] {0.45f, 1f},
                new Color[] {new Color(0, 0, 0, 0), new Color(0, 0, 0, 170)}));
        g.fillRect(0, 0, w, h);
    }

    private void dibujarTitulo(Graphics2D g) {
        Font fuente = new Font(FUENTE, Font.BOLD, 34);
        g.setFont(fuente);
        String titulo = "ELIGE TU PORTADOR";
        int tx = centrar(g, titulo, 0, ANCHO_DISENO);
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(titulo, tx + 2, 80);
        g.setColor(HUESO);
        g.drawString(titulo, tx, 78);

        // Filete con un rombo en el medio
        int yLinea = 96;
        g.setPaint(new GradientPaint(220, yLinea, new Color(92, 102, 122, 0), 400, yLinea, BORDE, true));
        g.fillRect(220, yLinea, 360, 1);
        int[] xs = {400, 405, 400, 395};
        int[] ys = {yLinea - 5, yLinea, yLinea + 5, yLinea};
        g.setColor(BRASA);
        g.fillPolygon(xs, ys, 4);

        g.setFont(new Font(FUENTE, Font.ITALIC, 15));
        g.setColor(NIEBLA);
        String sub = "Las cenizas recuerdan a quien camina entre ellas";
        g.drawString(sub, centrar(g, sub, 0, ANCHO_DISENO), 124);
    }

    private void dibujarPersonaje(Graphics2D g, Arquetipo a) {
        BufferedImage imagen = imagenesPersonajes.get(a);
        int alto = ALTO_PERSONAJE;
        int yArriba = Y_PERSONAJE;
        int yPiso = yArriba + alto;
        int yCentro = yArriba + alto / 2;

        // Halo de luz de luna detrás del personaje (late despacio)
        float pulso = 0.85f + 0.15f * (float) Math.sin(tick * 0.05);
        int alfaHalo = Math.round(70 * pulso);
        g.setPaint(new RadialGradientPaint(CENTRO_PERSONAJE_X, yCentro, 160,
                new float[] {0f, 1f},
                new Color[] {new Color(LUNA.getRed(), LUNA.getGreen(), LUNA.getBlue(), alfaHalo),
                             new Color(LUNA.getRed(), LUNA.getGreen(), LUNA.getBlue(), 0)}));
        g.fillOval(CENTRO_PERSONAJE_X - 160, yCentro - 160, 320, 320);

        // Niebla en el piso y sombra
        g.setPaint(new RadialGradientPaint(CENTRO_PERSONAJE_X, yPiso, 120,
                new float[] {0f, 1f},
                new Color[] {new Color(150, 160, 180, 60), new Color(150, 160, 180, 0)}));
        g.fillOval(CENTRO_PERSONAJE_X - 120, yPiso - 18, 240, 36);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillOval(CENTRO_PERSONAJE_X - 58, yPiso - 7, 116, 14);

        if (imagen != null) {
            dibujarImagenPersonaje(g, a, imagen, yArriba);
        }
        dibujarNombre(g, a, yPiso);
    }

    /**
     * Dibuja el PNG del personaje. La imagen se reescala una sola vez al tamaño
     * real en pantalla (con buena calidad) y se guarda, así no se pixela ni se
     * recalcula en cada cuadro.
     */
    private void dibujarImagenPersonaje(Graphics2D g, Arquetipo a, BufferedImage imagen, int yArriba) {
        double proporcion = imagen.getWidth() / (double) imagen.getHeight();
        int ancho = (int) Math.round(ALTO_PERSONAJE * proporcion);
        // El cuerpo no está en el centro del PNG (la espada sale hacia la derecha)
        int x = CENTRO_PERSONAJE_X - (int) Math.round(ancho * CENTRO_CUERPO);

        double escala = transformacion.getScaleY();
        int altoReal = Math.max(1, (int) Math.round(ALTO_PERSONAJE * escala));
        if (retratoEscalado == null || retratoDe != a || retratoAlto != altoReal) {
            int anchoReal = Math.max(1, (int) Math.round(altoReal * proporcion));
            retratoEscalado = escalarConCalidad(imagen, anchoReal, altoReal);
            retratoDe = a;
            retratoAlto = altoReal;
        }

        // Respiración: el cuerpo se estira apenas hacia arriba, con los pies fijos
        double respiro = (Math.sin(tick * 0.08) + 1) * 1.1;
        int altoDibujo = (int) Math.round(ALTO_PERSONAJE + respiro);
        int yDibujo = yArriba + ALTO_PERSONAJE - altoDibujo;

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(retratoEscalado, x, yDibujo, ancho, altoDibujo, null);
    }

    private static BufferedImage escalarConCalidad(BufferedImage original, int ancho, int alto) {
        Image escalada = original.getScaledInstance(ancho, alto, Image.SCALE_AREA_AVERAGING);
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        g.drawImage(escalada, 0, 0, null);
        g.dispose();
        return resultado;
    }

    private void dibujarNombre(Graphics2D g, Arquetipo a, int yPiso) {
        // Nombre y rol
        g.setFont(new Font(FUENTE, Font.BOLD, 20));
        String nombre = a.getNombre().toUpperCase();
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(nombre, centrar(g, nombre, CENTRO_PERSONAJE_X - 150, 300) + 1, yPiso + 38);
        g.setColor(HUESO);
        g.drawString(nombre, centrar(g, nombre, CENTRO_PERSONAJE_X - 150, 300), yPiso + 37);

        g.setFont(new Font(FUENTE, Font.ITALIC, 15));
        g.setColor(BRASA);
        String rol = "~ " + a.getRol() + " ~";
        g.drawString(rol, centrar(g, rol, CENTRO_PERSONAJE_X - 150, 300), yPiso + 58);

        if (hayVarios) {
            dibujarFlechas(g, yPiso - 110);
        }
    }

    private void dibujarFlechas(Graphics2D g, int yCentro) {
        zonaFlechaIzq.setBounds(CENTRO_PERSONAJE_X - 140, yCentro - 20, 30, 40);
        zonaFlechaDer.setBounds(CENTRO_PERSONAJE_X + 110, yCentro - 20, 30, 40);
        g.setColor(HUESO);
        g.fillPolygon(new int[] {zonaFlechaIzq.x + 22, zonaFlechaIzq.x + 8, zonaFlechaIzq.x + 22},
                      new int[] {yCentro - 12, yCentro, yCentro + 12}, 3);
        g.fillPolygon(new int[] {zonaFlechaDer.x + 8, zonaFlechaDer.x + 22, zonaFlechaDer.x + 8},
                      new int[] {yCentro - 12, yCentro, yCentro + 12}, 3);
    }

    private void dibujarFicha(Graphics2D g, Arquetipo a) {
        Rectangle f = FICHA;
        dibujarMarco(g, f);

        int margen = 22;
        int x = f.x + margen;
        int ancho = f.width - margen * 2;

        g.setFont(new Font(FUENTE, Font.BOLD, 13));
        g.setColor(NIEBLA);
        g.drawString("HISTORIA", x, f.y + 30);

        g.setFont(new Font(FUENTE, Font.PLAIN, 15));
        g.setColor(HUESO);
        int y = dibujarParrafo(g, a.getDescripcion(), x, f.y + 52, ancho);

        g.setFont(new Font(FUENTE, Font.ITALIC, 14));
        g.setColor(new Color(205, 120, 100));
        y = dibujarParrafo(g, a.getHabilidad(), x, y + 6, ancho);

        // Separador
        y += 4;
        g.setColor(BORDE_OSCURO);
        g.fillRect(x, y, ancho, 1);
        y += 20;

        int[] stats = a.getStats();
        int xBarra = x + 82;
        int anchoBarra = ancho - 82;
        int segmento = anchoBarra / Arquetipo.STAT_MAXIMO;
        g.setFont(new Font(FUENTE, Font.BOLD, 14));
        for (int i = 0; i < stats.length; i++) {
            int yFila = y + i * 27;
            g.setColor(NIEBLA);
            g.drawString(Arquetipo.NOMBRES_STATS[i], x, yFila + 12);
            for (int s = 0; s < Arquetipo.STAT_MAXIMO; s++) {
                g.setColor(s < stats[i] ? colorStat(i) : BARRA_VACIA);
                g.fillRect(xBarra + s * segmento, yFila, segmento - 3, 13);
            }
        }
    }

    /** Marco doble con esquinas marcadas, al estilo de los paneles pixel art. */
    private void dibujarMarco(Graphics2D g, Rectangle r) {
        g.setColor(PANEL);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setStroke(new BasicStroke(1));
        g.setColor(BORDE);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setColor(BORDE_OSCURO);
        g.drawRect(r.x + 4, r.y + 4, r.width - 8, r.height - 8);

        g.setColor(BRASA);
        int t = 5;
        g.fillRect(r.x - 2, r.y - 2, t, t);
        g.fillRect(r.x + r.width - 2, r.y - 2, t, t);
        g.fillRect(r.x - 2, r.y + r.height - 2, t, t);
        g.fillRect(r.x + r.width - 2, r.y + r.height - 2, t, t);
    }

    private Color colorStat(int indice) {
        switch (indice) {
            case 0:  return new Color(158, 59, 53);   // vida
            case 1:  return new Color(141, 151, 168); // defensa
            default: return new Color(176, 112, 63);  // ataque
        }
    }

    private void dibujarBotones(Graphics2D g) {
        int separacion = 30;
        Rectangle tamVolver = medidaBoton(imagenVolver, 150);
        Rectangle tamComenzar = medidaBoton(imagenComenzar, 200);
        int anchoFila = tamVolver.width + separacion + tamComenzar.width;
        int x = (ANCHO_DISENO - anchoFila) / 2;
        int altoFila = Math.max(tamVolver.height, tamComenzar.height);

        zonaVolver.setBounds(x, Y_BOTONES + (altoFila - tamVolver.height) / 2,
                tamVolver.width, tamVolver.height);
        zonaComenzar.setBounds(x + tamVolver.width + separacion,
                Y_BOTONES + (altoFila - tamComenzar.height) / 2,
                tamComenzar.width, tamComenzar.height);

        dibujarBoton(g, zonaVolver, "Volver", imagenVolver, imagenVolverHover, hover == Zona.VOLVER, false);
        dibujarBoton(g, zonaComenzar, "Comenzar", imagenComenzar, imagenComenzarHover, hover == Zona.COMENZAR, true);

        g.setFont(new Font(FUENTE, Font.PLAIN, 13));
        g.setColor(NIEBLA);
        String ayuda = hayVarios
                ? "← →  elegir     Enter  comenzar     Esc  volver"
                : "Enter  comenzar     Esc  volver";
        g.drawString(ayuda, centrar(g, ayuda, 0, ANCHO_DISENO), 578);
    }

    /** Tamaño del botón en coordenadas de diseño: el del PNG escalado, o uno fijo si no hay PNG. */
    private Rectangle medidaBoton(BufferedImage imagen, int anchoTexto) {
        if (imagen != null) {
            return new Rectangle(0, 0,
                    (int) Math.round(imagen.getWidth() * ESCALA_IMAGENES),
                    (int) Math.round(imagen.getHeight() * ESCALA_IMAGENES));
        }
        return new Rectangle(0, 0, anchoTexto, 46);
    }

    private void dibujarBoton(Graphics2D g, Rectangle r, String texto,
                              BufferedImage imagen, BufferedImage imagenHover,
                              boolean hover, boolean principal) {
        if (imagen != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(hover ? imagenHover : imagen, r.x, r.y, r.width, r.height, null);
            return;
        }

        // Sin PNG: botón dibujado con el mismo estilo que la ficha
        g.setColor(hover ? new Color(26, 30, 42, 225) : new Color(8, 10, 16, 200));
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(hover || principal ? BORDE.brighter() : BORDE);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setColor(BORDE_OSCURO);
        g.drawRect(r.x + 3, r.y + 3, r.width - 6, r.height - 6);
        if (principal) {
            g.setColor(BRASA);
            g.fillRect(r.x - 2, r.y + r.height / 2 - 2, 5, 5);
            g.fillRect(r.x + r.width - 2, r.y + r.height / 2 - 2, 5, 5);
        }

        g.setFont(new Font(FUENTE, Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(hover ? Color.WHITE : HUESO);
        g.drawString(texto, centrar(g, texto, r.x, r.width), ty);
    }

    // ---------- Utilidades ----------

    private int centrar(Graphics2D g, String texto, int x, int ancho) {
        return x + (ancho - g.getFontMetrics().stringWidth(texto)) / 2;
    }

    /** Escribe el texto cortando por palabras; devuelve la Y de la línea siguiente. */
    private int dibujarParrafo(Graphics2D g, String texto, int x, int y, int ancho) {
        FontMetrics fm = g.getFontMetrics();
        StringBuilder linea = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String prueba = linea.length() == 0 ? palabra : linea + " " + palabra;
            if (fm.stringWidth(prueba) > ancho && linea.length() > 0) {
                g.drawString(linea.toString(), x, y);
                y += fm.getHeight();
                linea = new StringBuilder(palabra);
            } else {
                linea = new StringBuilder(prueba);
            }
        }
        if (linea.length() > 0) {
            g.drawString(linea.toString(), x, y);
            y += fm.getHeight();
        }
        return y;
    }

    private static BufferedImage cargarImagen(String nombre) {
        return cargarRecurso(RUTA_IMAGENES + nombre); // si no está, se dibuja el botón con texto
    }

    private static BufferedImage cargarRecurso(String ruta) {
        if (ruta == null) {
            return null;
        }
        URL url = PanelSeleccionPersonaje.class.getResource(ruta);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("No se pudo leer la imagen: " + ruta);
            return null;
        }
    }

    private static BufferedImage aclarar(BufferedImage imagen) {
        if (imagen == null) {
            return null;
        }
        BufferedImage argb = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = argb.createGraphics();
        g.drawImage(imagen, 0, 0, null);
        g.dispose();
        RescaleOp op = new RescaleOp(new float[] {1.2f, 1.2f, 1.2f, 1f}, new float[] {0f, 0f, 0f, 0f}, null);
        return op.filter(argb, null);
    }
}