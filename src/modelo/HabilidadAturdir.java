package modelo;

public class HabilidadAturdir extends Habilidad {
    
    public HabilidadAturdir(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        super(nombreHabilidad, descripcion, valorBase, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        if (this.getCdListo()) {
            
            // 1. Aplica el estado de aturdimiento para que pierda el turno
            objetivo.aplicarAturdimiento();
            
            // 2. Le hace daño al enemigo usando el "valorBase" (los 40 que configuramos en el Factory)
            objetivo.recibirDanio(this.getValorBase()); 
            
            // 3. Reinicia el contador de turnos de la habilidad
            this.activarcooldown();
        }
    }
}