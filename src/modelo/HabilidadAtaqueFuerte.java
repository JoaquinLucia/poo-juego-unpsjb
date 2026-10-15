package modelo;

public class HabilidadAtaqueFuerte extends Habilidad {

    public HabilidadAtaqueFuerte(String nombre, String descripcion, int valorDanio, int cooldown) {
        super(nombre, descripcion, valorDanio, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        // Aplica un golpe con el daño base configurado
        objetivo.recibirDanio(getValorBase());
        
        // Activa su enfriamiento
        activarcooldown();
    }
}