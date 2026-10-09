package modelo;

public class HabilidadRoboVida extends Habilidad {
    private final int danio;

    public HabilidadRoboVida(String nombre, String descripcion, int danio, int cooldown) {
        super(nombre, descripcion, danio, cooldown);
        this.danio = danio;
    }

    @Override
    public void ejecutarHabilidad(Entidad atacante, Entidad objetivo) {
        // Le resta vida al objetivo
        objetivo.recibirDanio(danio);
        // Le suma esa misma vida al atacante (chupavidas)
        atacante.sumarVida(danio);
        activarcooldown();
    }
}