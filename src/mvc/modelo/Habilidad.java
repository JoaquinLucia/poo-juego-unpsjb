public abstract class Habilidad {
    private String nombreHabilidad;
    private int danioHabilidad;
    private int cooldown;
    private boolean cdListo;

    public Habilidad(String nombreHabilidad, int danioHabilidad, int cooldown){
        this.nombreHabilidad = nombreHabilidad;
        this.danioHabilidad = danioHabilidad;
        this.cooldown = cooldown;
    }

    public boolean estaLista( boolean cdListo){
        if(cdListo){
            this.cooldown = 0;
        }
        return cdListo;
    }
}
