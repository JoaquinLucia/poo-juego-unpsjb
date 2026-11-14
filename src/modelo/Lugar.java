package modelo;

import java.util.List;
import modelo.catalogos.CatalogoHabilidades;
import modelo.enemigos.EnemigoAgil;
import modelo.enemigos.EnemigoTanque;

/**
 * Los lugares del recorrido, en orden.
 * Los tres primeros son caminos (se elige por dónde seguir y hay batalla);
 * el último es la sala del jefe (pelea final).
 *
 * Cada lugar sabe crear su propio enemigo.
 * Las imágenes de cada lugar las resuelve la vista.
 */

public enum Lugar {

    BOSQUE {
        @Override
        public Enemigo crearEnemigo(CatalogoHabilidades h, Dificultad d) {
            return new EnemigoAgil("Jester", 80, 14, 1,
                    List.of(h.crearHabilidad("dardo_venenoso")),
                    "/recursos/personajes/mago.png", "/recursos/jesterFrontal.jpg",
                    "recursos/sonidos/jester_musica.wav", d);
        }
    },
    CUEVA {
        @Override
        public Enemigo crearEnemigo(CatalogoHabilidades h, Dificultad d) {
            return new EnemigoTanque("Esclavo", 120, 14, 1,
                    List.of(h.crearHabilidad("grito")),
                    "/recursos/personajes/mago.png", "/recursos/esclavoFrontal.jpg",
                    "recursos/sonidos/esclavo_musica.wav", d);
        }
    },
    MAZMORRA {
        @Override
        public Enemigo crearEnemigo(CatalogoHabilidades h, Dificultad d) {
            return new EnemigoAgil("Cazador", 150, 20, 1,
                    List.of(h.crearHabilidad("corte_profundo")),
                    "/recursos/personajes/mago.png", "/recursos/cazadorFrontal.jpg",
                    "recursos/sonidos/cazador_musica.wav", d);
        }
    },
    SALA_JEFE {
        @Override
        public Enemigo crearEnemigo(CatalogoHabilidades h, Dificultad d) {
            return new EnemigoTanque("Akuma", 270, 20, 3,
                    List.of(h.crearHabilidad("golpe_aturdidor"),
                            h.crearHabilidad("corte_profundo"),
                            h.crearHabilidad("curacion")),
                    "/recursos/personajes/mago.png", "/recursos/akumaFrontal.jpg",
                    "recursos/sonidos/akuma_musica.wav", d);
        }
    };

    public abstract Enemigo crearEnemigo(CatalogoHabilidades h, Dificultad d);
}