package modelo;

public class HabilidadAturdir extends Habilidad {

    public HabilidadAturdir(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        super(nombreHabilidad, descripcion, valorBase, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        // Hace daño usando el valorBase
        objetivo.recibirDanio(getValorBase());
        
        // Aplica el aturdimiento usando el método correcto de Entidad
        objetivo.aplicarAturdimiento();
        
        System.out.println(objetivo.getNombre() + " fue aturdido por " + getNombreHabilidad() + "!");
        
        // Activa el enfriamiento
        activarcooldown();
    }
}