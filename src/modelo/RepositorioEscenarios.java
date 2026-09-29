package modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Guarda todos los escenarios del juego y arma los niveles con ellos.
 */
public class RepositorioEscenarios {
    private static final String RUTA = "/recursos/mazmorra/";

    private final Map<String, Escenario> escenarios = new HashMap<>();

    public RepositorioEscenarios() {
        cargarEscenarios();
    }

        private void cargarEscenarios() {
        agregar(Escenario.salaNormal("bosque", RUTA + "bosque.gif",
                "Árboles muertos rodean un claro de lápidas rotas. La niebla se arrastra entre las ruinas. "
                        + "Pasá el mouse sobre una flecha para examinar el camino.",
                "Entre los muros derrumbados, una lápida sin nombre marca un sendero cubierto de raíces.",
                "Bajo el arco en ruinas, unos escalones de piedra se hunden en la niebla.",
                "Tras las columnas quebradas, las tumbas se pierden entre los árboles secos."));

        agregar(Escenario.salaNormal("cueva", RUTA + "cueva.gif",
                "Entrás a una cueva húmeda. El agua gotea desde la roca y el eco repite cada paso.",
                "Un túnel angosto donde el aire trae olor a ceniza y a carne quemada.",
                "La galería principal se ensancha hacia la oscuridad. Algo se arrastra a lo lejos.",
                "Una grieta en la roca deja pasar una corriente helada y un murmullo lejano."));

        agregar(Escenario.salaNormal("mazmorra", RUTA + "mazmorra.gif",
                "Una sala de columnas iluminada por velas. Tres pasajes se abren entre los arcos.",
                "Un resplandor rojo late al final de la escalera. El aire huele a azufre.",
                "Una escalera ancha sube hacia la oscuridad total. No se escucha ni el eco.",
                "Una luz azul y fría ilumina los escalones. Parece que alguien reza en voz baja."));

        agregar(Escenario.salaDelJefe("jefe_puerta", RUTA + "jefe_puerta.gif",
                "Una puerta colosal, marcada con runas de fuego, se alza frente a vos. "
                        + "Detrás se oye una respiración lenta y profunda.",
                "Empujá la puerta y enfrentá lo que duerme entre las cenizas. No hay vuelta atrás."));
    }

    private void agregar(Escenario escenario) {
        escenarios.put(escenario.getId(), escenario);
    }

    public Escenario buscar(String id) {
        Escenario escenario = escenarios.get(id);
        if (escenario == null) {
            throw new IllegalArgumentException("No existe el escenario: " + id);
        }
        return escenario;
    }

    /** Nivel 1: 3 salas normales y la sala del jefe. */
    public Nivel crearNivelUno() {
        return new Nivel.Builder("Catacumbas")
                .sala(buscar("bosque"),      "esclavo")
                .sala(buscar("cueva"),       "guardia_hueco")
                .sala(buscar("mazmorra"),    "sacerdotisa")
                .jefe(buscar("jefe_puerta"), "guardian_cenizas")
                .build();
    }
}
