BorderLayout en detalle

Este es el que estás usando en VistaBatalla.

this.ventana.setLayout(new BorderLayout(10, 10));

this.ventana.setLayout(new BorderLayout());

this.ventana.add(panelResultado, BorderLayout.NORTH);
this.ventana.add(panelBotones, BorderLayout.SOUTH);
this.ventana.add(panelFondo, BorderLayout.CENTER);
this.ventana.add(panelFondo, BorderLayout.WEST);
this.ventana.add(panelFondo, BorderLayout.EAST);


Quedaría así:
NORTH: resultado del combate.
SOUTH: botones para atacar.
CENTER: área de batalla con la imagen de fondo.
/////////////////////////////////
FlowLayout

Coloca los componentes uno al lado del otro, como palabras en una línea. Si no entran, pasan a otra línea.
new FlowLayout(FlowLayout.LEFT)
Alineaciones: LEFT, CENTER, RIGHT.
Ideal para botones, etiquetas y pequeños grupos de controles.

/////////////////
3. GridLayout

Divide el contenedor en filas y columnas iguales. Todos los componentes ocupan una celda del mismo tamaño.
new GridLayout(2, 3, 8, 8)

Significa 2 filas, 3 columnas, 8 píxeles de separación horizontal y vertical.

Ideal para menús de habilidades o inventarios.
/////////////////////////////////
4. BoxLayout

Organiza los componentes en una sola fila o columna.

panel.setLayout(
    new BoxLayout(panel, BoxLayout.Y_AXIS)
);

Y_AXIS = vertical. X_AXIS = horizontal.

Útil para paneles de estadísticas, listas de atributos o menús verticales.
///////////////////////////
6. CardLayout

Permite tener varios paneles en el mismo espacio y mostrar uno a la vez.

CardLayout cartas = new CardLayout();
panel.setLayout(cartas);

Ideal para cambiar entre menú principal, selección de personaje, batalla y pantalla de derrota.