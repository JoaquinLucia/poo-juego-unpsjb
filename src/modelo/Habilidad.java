package modelo;

public abstract class Habilidad {
    private String nombreHabilidad,descripcion;
    private int cooldown,cooldownActual;
    private int valorBase; //seria en daño base o en otro caso cuanto se cura


    public Habilidad(String nombreHabilidad,String descripcion, int valorBase, int cooldown){
        this.nombreHabilidad = nombreHabilidad;
        this.descripcion=descripcion;
        this.valorBase = valorBase;
        this.cooldown = cooldown;
        this.cooldownActual = 0;
    }



public abstract void ejecutarHabilidad(Entidad usuario, Entidad objetivo);


public void cronoCooldown() { 
        if (cooldownActual > 0) {
            cooldownActual--; //decrementa el cooldown
        }
}
public void activarcooldown(){
    this.cooldownActual=this.cooldown;
}

// gett
public String getNombreHabilidad() {
    return nombreHabilidad;
}
public String getDescripcion() {
    return descripcion;
}
public int getValorBase() {
    return valorBase;
}

public int getCooldown() {
    return cooldown;
}
public boolean getCdListo() {
    return cooldownActual == 0;
}
public int getCooldownActual() {
    return cooldownActual;
}
}
