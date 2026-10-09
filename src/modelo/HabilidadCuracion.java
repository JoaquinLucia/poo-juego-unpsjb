package modelo;

public class HabilidadCuracion extends Habilidad {

    public HabilidadCuracion(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        // En este caso, el "valorBase" representa cuántos puntos de vida cura
        super(nombreHabilidad, descripcion, valorBase, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        // La curación se aplica al usuario que lanza la habilidad, no al enemigo
        usuario.sumarVida(getValorBase());
        
        System.out.println(usuario.getNombre() + " usó " + getNombreHabilidad() + " y recuperó " + getValorBase() + " puntos de vida.");
        
        // Reiniciamos el contador para que deba esperar los turnos de enfriamiento
        activarcooldown();
    }
}