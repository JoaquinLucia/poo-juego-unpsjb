package modelo;

public class HabilidadRoboVida extends Habilidad {

    public HabilidadRoboVida(String nombre, String descripcion, int valorDanio, int cooldown) {
        super(nombre, descripcion, valorDanio, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        // 1. Causamos daño al objetivo
        objetivo.recibirDanio(getValorBase());
        
        // 2. Curamos al lanzador usando tu método existente sumarVida()
        lanzador.sumarVida(getValorBase());
        
        // 3. Activamos el enfriamiento
        activarcooldown();
    }
}