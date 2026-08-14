# Proyecto: Tower Defense - El Último Bastión

## 1. Integrantes del Equipo
- Lucia, Joaquin
- Porco Velasquez, Ariel 
- Miranda, Juan Manuel A. 
- Apellido, Nombre

## 2. Dominio y Alcance del Sistema 

### Descripción del Problema
Se busca desarrollar un juego de escritorio del estilo RPG por turnos con elementos de Roguelite, donde el jugador controla un ‘aventurero’, que tiene que avanzar por un mapa de casilla en casilla (nodos), con la opción de elegir entre tres tipos (por el momento puede ser 1) de heroes, diferenciados entre sí por los puntos de Ataque y Defensa.

### Objetivo del Sistema
El sistema será un juego funcional y extensible que permita al jugador experimentar las mecánicas básicas de un RPG de combate por turnos y exploración en un mapa 2D lineal que esté ramificado en casilleros o nodos, combatir enemigos mediante un sistema de turnos, obtener habilidades y recompensas para mejorar las características de su personaje y enfrentarse a enemigos cada vez más poderosos hasta alcanzar al jefe final.

El sistema buscará generar partidas diferentes y rejugables, donde las decisiones tomadas por el jugador determinen su estrategia y posibilidades de supervivencia.

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
Utilizar un objeto.
Finalizar el turno.

Los enemigos tendrán comportamientos y características diferentes.

El jugador recorrerá un mapa generado para la partida, compuesto por diferentes casillas.

Combate
El jugador enfrenta uno o varios enemigos.

Combate élite
Enfrenta enemigos más fuertes pero recibe mejores recompensas.

Evento
Casillas de bonificación que pueden aumentar las estadísticas del personaje a elección.

Recompensa
Después de ciertos combates el jugador podrá elegir entre diferentes recompensas (Robar una habilidad del enemigo o un aumento de estadísticas).

Interfaz Gráfica (IGU):

Indicadores visuales de estado (barras de vida, turno activo y registro de combate).

Persistencia:

Guardado y consulta del historial de batallas y mejores calificaciones (High Scores) en una base de datos SQLite.

## 3. Arquitectura y Diseño

### Patrón de Diseño Adicional:

### Diagramas de Diseño

#### **Diagrama de Clases UML (Conceptual)**


#### **Prototipo de la IGU (Wireframe)**

## 4. Stack Tecnológico 

- **Lenguaje:** Java 17
- **IDE:** Visual Studio Code
- **Base de Datos:** SQLite (para persistencia de High Scores)
- **Framework de IGU:** Java Swing
- **Control de Versiones:** Git y GitHub Classroom
