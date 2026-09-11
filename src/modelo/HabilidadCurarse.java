package modelo;

public class HabilidadCurarse extends Habilidad {


public HabilidadCurarse(String nombreHabilidad,String descripcion,int valorBase,int cooldown){
    super(nombreHabilidad, descripcion, valorBase, cooldown);
}
@Override 
public void ejecutarHabilidad(Entidad usuario, Entidad objetivo){
    if (getCooldownActual()==0) {
        usuario.sumarVida(getValorBase());
        activarcooldown();
    }
}

}

