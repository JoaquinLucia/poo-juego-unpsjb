package modelo;

public class HabilidadAturdir extends Habilidad {
    public HabilidadAturdir(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        super(nombreHabilidad, descripcion, valorBase, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        if (this.getCdListo()) {
            objetivo.aplicarAturdimiento();
            this.activarcooldown();
        }
    }
}