package modelo;

import java.util.List;

/**
 * Estado de la pantalla de selección: qué héroes hay, cuál se está mirando
 * y cuál se eligió. Los héroes se identifican por el id de CatalogoPersonajes.
 */
public class ModeloMenu {

    private final List<String> personajes;
    private int indiceActual = 0;
    private String personajeElegido;

    public ModeloMenu(List<String> idsPersonajes) {
        if (idsPersonajes == null || idsPersonajes.isEmpty()) {
            throw new IllegalArgumentException("Tiene que haber al menos un héroe para elegir");
        }
        this.personajes = List.copyOf(idsPersonajes);
    }

    public String getPersonajeActual() {
        return personajes.get(indiceActual);
    }

    public int getCantidadPersonajes() {
        return personajes.size();
    }

    public void moverSeleccion(int direccion) {
        indiceActual = Math.floorMod(indiceActual + direccion, personajes.size());
    }

    public void reiniciarSeleccion() {
        indiceActual = 0;
    }

    public String getPersonajeElegido() {
        return personajeElegido;
    }

    public void setPersonajeElegido(String personajeElegido) {
        this.personajeElegido = personajeElegido;
    }
}
