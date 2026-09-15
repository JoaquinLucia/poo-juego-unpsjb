package controlador;

import modelo.*;
import modelo.enemigos.*;

public class Juego {

    public void iniciarJuego() {
        Heroe heroe = new Heroe("Lancelot", 100, 15, 1, 4);

        // Definimos los escenarios y enemigos a enfrentar en orden
        Enemigo[] enemigos = { new Goblin(), new Esqueleto() };

        System.out.println("¡Comienza la aventura de " + heroe.getNombre() + "!\n");

        for (int i = 0; i < enemigos.length; i++) {
            Enemigo enemigoActual = enemigos[i];
            System.out.println("-- ESCENARIO " + (i + 1) + ": Enfrentando a " + enemigoActual.getNombre() + " --");

            ejecutarCombate(heroe, enemigoActual);

            // Recompensa post-combate
            heroe.robarHabilidad(enemigoActual);
            System.out.println();
        }

        mostrarResumen(heroe);
    }

    private void ejecutarCombate(Heroe heroe, Enemigo enemigo) {
        while (heroe.estaVivo() && enemigo.estaVivo()) {
            heroe.atacar(enemigo);
        }
    }

    private void mostrarResumen(Heroe heroe) {
        System.out.println("-- FIN DEL CAMINO --");
        System.out.println("Las habilidades que " + heroe.getNombre() + " obtuvo son:");
        for (Habilidad h : heroe.getHabilidades()) {
            System.out.println("- " + h.getNombreHabilidad());
        }
    }
}