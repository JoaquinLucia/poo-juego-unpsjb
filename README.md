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
