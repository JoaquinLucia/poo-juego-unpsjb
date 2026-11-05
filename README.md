# Proyecto: RPG/Roguelite/Point & Click - Senderos de las cenizas
## 1. Integrantes del Equipo
- Lucia, Joaquin
- Porco Velasquez, Ariel 
- Miranda, Juan Manuel A. 
- Molina, David

## 2. Dominio y Alcance del Sistema 

### Descripción del Problema
Se busca desarrollar un juego de escritorio del estilo RPG por turnos con elementos de Roguelite y Point and Click, donde el jugador controla un ‘aventurero’, que tiene que avanzar por un camino, donde éste debe tomar decisiones para avanzar en el juego. El jugador tiene la opción de elegir entre distintos caminos, los cuales cada uno tiene un cartel y tienen una dificultad distinta y diferentes recompensas de acuerdo al nivel de dificultad de la elección.

### Objetivo del Sistema
El sistema será un juego funcional y extensible que permita al jugador experimentar las mecánicas básicas de un RPG de combate por turnos y exploración mediante el uso del mouse para seleccionar las distintas opciones que estén disponibles en pantalla, combatir enemigos mediante un sistema de turnos, obtener habilidades y recompensas para mejorar las características de su personaje y enfrentarse a enemigos cada vez más poderosos en un estilo de dificultad progresiva hasta alcanzar al jefe final.

### Funcionalidades Principales (Features)
Sistema de personaje

El jugador contará con un personaje que tendrá diferentes atributos:
(Por el momento)
Vida.
Ataque.
Defensa.
Habilidad.
También podrá obtener nuevas habilidades y mejoras durante la partida.

**Mecánicas de Juego:**
Sistema de combate

El combate será por turnos.
Durante su turno, el jugador podrá realizar diferentes acciones:

Atacar.
Defenderse.
Utilizar una habilidad.

Los enemigos tendrán comportamientos y características diferentes.

El jugador deberá realizar una elección en la interfaz de la pantalla de juego para poder avanzar, las cuales tienen se representan mediante carteles (botones). De acuerdo a la decisión del jugador se activará un evento de batalla.

Combate
El jugador enfrenta un enemigo.

Combate élite
Enfrenta enemigos más fuertes pero recibe mejores recompensas.

Recompensa
Después de cada combates el jugador podrá elegir entre diferentes recompensas (Robar una habilidad del enemigo, mejora de habilidad o un aumento de estadísticas a excepto del ataque).

Interfaz Gráfica (IGU):

Indicadores visuales de estado (barras de vida, turno activo y registro de combate).

Persistencia:

Guardado y consulta del historial de batallas y mejores clasificaciones (High Scores) en una base de datos SQLite.

Nuestro grupo pensó en utilizar la herencia en este juego a través de la creación de una clase abstracta llamada "Entidad", la cual contiene los atributos generales que posteriormente van a ser utilizados por sus respectivas subclases: "Heroe" y "Enemigo.
Los personajes representados por éstas clases tendrán comportamientos y características particulares, además de las heredadas por su clase padre.
Por una parte, esto hace referencia al polimorfismo, ya que en la forma que se utiliza herencia permite que las subclases puedan utilizar los comportamientos de la clase padre, aplicados a su propia manera.
Por otra parte, tenemos en consideración implementar varias interfaces que nos permitan reutilizar código mediante el polimorfismo, así poder  aplicar los comportamientos asociados a éstos a distintas clases que lo requieran.

Herencia: Habilidad abstracta

Todas las habilidades (curar, aturdir, desangrar) son una Habilidad y comparten nombre, descripción, valor base y toda la lógica de cooldown. La herencia evita duplicar ese código en cada clase.

Además, ejecutarHabilidad es final y define el flujo fijo (verificar cooldown → aplicar efecto → activar cooldown → calcular daño). Lo único que varía es aplicarEfecto, que cada subclase implementa.Es final Así ninguna habilidad puede sobreescribirlo / modificarlo.

Gracias al polimorfismo, Entidad trabaja con una List<Habilidad> sin conocer los tipos concretos: se pueden agregar habilidades nuevas sin modificarla.

Interface: EfectoPorTurno

No todas las habilidades dejan un efecto que dura varios turnos: HabilidadCurar actúa una sola vez, mientras que HabilidadAturdir y HabilidadDesangrar persisten sobre el objetivo. Poner ese comportamiento en Habilidad obligaría a HabilidadCurar a heredar métodos que no necesita.

Se usa interface porque Java no permite herencia múltiple y porque “durar varios turnos” es una capacidad, no una identidad. Las clases siguen heredando de Habilidad y además implementan el contrato si les corresponde.

Entidad guarda una List<EfectoPorTurno> y en finalizarTurno() llama a procesarTurno sobre cada uno, sin saber si es aturdimiento o desangrado. Depende de la abstracción, no de las clases concretas.
--------
Patrón Command
Problema

En un combate por turnos hay que ejecutar acciones distintas (atacar, usar habilidad, defender), tanto del héroe (botones) como del enemigo (su propia decisión). En nuestro caso teníamos varias métodos para cada accion sea del heroe como del enemigo y repetian reglas comunes aturdimiento, verificación si la batalla termino 

Solución
El controladorbatalla asigna cada botón a un comando con un EnumMap y se lo entrega a Batalla, que lo ejecuta sin saber qué hace.

Qué nos resolvió
Desacoplamiento: el controlador no conoce la lógica de daño ni de habilidades.
Tratamiento uniforme: el héroe y el enemigo usan el mismo mecanismo.
Reglas centralizadas: aturdimiento e historial se resuelven en un solo lugar.
Extensibilidad: una acción nueva es una clase más, sin modificar Batalla (Abierto/Cerrado).

Por qué no implementamos deshacer()
La interfaz Comando tiene ejecutar() y deshacer() “si hace falta”: deshacer es una posibilidad del patrón, no una parte obligatoria. La esencia de Command es convertir un pedido en un objeto que el invocador ejecuta sin saber qué hace.
Cada acción es un objeto que implementa la interfaz Comando con un único método ejecutar():
-----


## 3. Arquitectura y Diseño

### Patrón de Diseño Adicional:

### Diagramas de Diseño

#### **Diagrama de Clases UML (Conceptual)**

#### **Prototipo de la IGU (Wireframe)**

## 4. Stack Tecnológico 

- **Lenguaje:** Java 26
- **IDE:** Visual Studio Code
- **Base de Datos:** SQLite (para persistencia de High Scores)
- **Framework de IGU:** Java Swing
- **Control de Versiones:** Git y GitHub Classroom
