package modelo;

public abstract class Habilidad {
    private  String nombreHabilidad, descripcion;
    private int cooldown, cooldownActual;
    private int valorBase;
    private boolean recienUsada = false;   // para no descontar en el mismo turno en que se usó


    public Habilidad(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        this.nombreHabilidad = nombreHabilidad;
        this.descripcion = descripcion;
        this.valorBase = valorBase;
        this.cooldown = cooldown;
        this.cooldownActual = 0;
    }

        
    /** Pasos comunes a todas las habilidades. Devuelve el daño que le hizo al objetivo. */
    public final int ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        if (!getCdListo()) return 0;
        int vidaAntes = objetivo.getVida();
        aplicarEfecto(usuario, objetivo);
        activarCooldown();
        return vidaAntes - objetivo.getVida();
    }

    /** Lo que hace la habilidad al usarse (daño, curación, efecto, etc.). */
    protected abstract void aplicarEfecto(Entidad usuario, Entidad objetivo);

    // ---------- Cooldown ----------
    /** Se llama al final de cada turno del dueño. El turno en que se usó no cuenta. */
    public void cronoCooldown() {
        if (recienUsada) {
            recienUsada = false;
            return;
        }
        if (cooldownActual > 0) cooldownActual--;
    }

    public void reiniciarCooldown() {
        this.cooldownActual = 0;
        this.recienUsada = false;
    }

    private void activarCooldown() {
        this.cooldownActual = this.cooldown;
        this.recienUsada = true;
    }
public  abstract String getResumenDeUsoHab(String usuario, String objetivo);
    // ---------- Getters ----------
    public String getNombreHabilidad() { return nombreHabilidad; }
    public String getDescripcion()     { return descripcion; }
    public int getValorBase()          { return valorBase; }
    public int getCooldown()           { return cooldown; }
    public int getCooldownActual()     { return cooldownActual; }
    public boolean getCdListo()        { return cooldownActual == 0; }
}