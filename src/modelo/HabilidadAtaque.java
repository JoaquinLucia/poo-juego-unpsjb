package modelo;

public class HabilidadAtaque extends Habilidad {

    public HabilidadAtaque(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        super(nombreHabilidad, descripcion, valorBase, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        if (getCooldownActual() == 0) {
            int danioTotal = getValorBase() + usuario.getAtaque();
            objetivo.recibirDanio(danioTotal);
            activarcooldown();
        }
    }
}