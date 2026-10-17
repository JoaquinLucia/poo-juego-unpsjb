package modelo;

public class HabilidadInmunidad extends Habilidad {

    public HabilidadInmunidad(String nombreHabilidad, String descripcion, int cooldown) {
        super(nombreHabilidad, descripcion, 0, cooldown); // Daño base 0
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        // La habilidad se la aplica el enemigo a sí mismo
        lanzador.setInmune(true);
        activarcooldown(); // Activa el contador de cooldown
    }
}