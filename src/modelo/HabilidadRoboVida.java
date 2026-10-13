package modelo;

public class HabilidadRoboVida extends Habilidad {

    public HabilidadRoboVida(String nombre, String descripcion, int valorDanio, int cooldown) {
        super(nombre, descripcion, valorDanio, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        int valor = getValorBase();
        
        // 1. Causamos daño al objetivo
        objetivo.recibirDanio(valor);
        
        // 2. Curamos al lanzador con la misma cantidad de vida robada
        lanzador.sumarVida(valor);
        
        // 3. Activamos el enfriamiento de la habilidad
        activarcooldown();
    }
}