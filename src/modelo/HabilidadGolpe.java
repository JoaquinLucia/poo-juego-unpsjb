package modelo;

/**
 * Habilidad de ejemplo: daño directo al objetivo y activa el cooldown.
 * Reemplazala por tus habilidades reales en CatalogoPersonajes.
 */
public class HabilidadGolpe extends Habilidad {

    public HabilidadGolpe(String nombre, String descripcion, int danio, int cooldown) {
        super(nombre, descripcion, danio, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        objetivo.recibirDanio(getValorBase());
        activarcooldown();
    }
}
