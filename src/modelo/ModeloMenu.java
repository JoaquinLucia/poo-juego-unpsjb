package modelo;

public class ModeloMenu {

    private final Arquetipo[] personajes = Arquetipo.values();
    private int indiceActual = 0;
    private Arquetipo personajeElegido;

    public Arquetipo getPersonajeActual() {
        return personajes[indiceActual];
    }

    public int getCantidadPersonajes() {
        return personajes.length;
    }

    public void moverSeleccion(int direccion) {
        indiceActual = (indiceActual + direccion + personajes.length) % personajes.length;
    }

    public void reiniciarSeleccion() {
        indiceActual = 0;
    }

    public Arquetipo getPersonajeElegido() {
        return personajeElegido;
    }

    public void setPersonajeElegido(Arquetipo personajeElegido) {
        this.personajeElegido = personajeElegido;
    }
}