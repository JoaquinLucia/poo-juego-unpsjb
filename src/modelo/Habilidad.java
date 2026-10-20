package modelo;

public abstract class Habilidad {
    private String nombreHabilidad, descripcion;
    private int cooldown, cooldownActual;
    private int valorBase;

    public Habilidad(String nombreHabilidad, String descripcion, int valorBase, int cooldown) {
        this.nombreHabilidad = nombreHabilidad;
        this.descripcion = descripcion;
        this.valorBase = valorBase;
        this.cooldown = cooldown;
        this.cooldownActual = 0;
    }

    /** Pasos comunes a todas las habilidades. Las subclases no lo sobrescriben. */
    public final void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        if (!getCdListo()) return;
        aplicarEfecto(usuario, objetivo);
        activarcooldown();
    }

    /** Lo que hace la habilidad al usarse (daño, curación, efecto, etc.). */
    protected abstract void aplicarEfecto(Entidad usuario, Entidad objetivo);

    // ---------- Cooldown ----------
    public void cronoCooldown()     { if (cooldownActual > 0) cooldownActual--; }
    public void activarcooldown()   { this.cooldownActual = this.cooldown; }
    public void reiniciarCooldown() { this.cooldownActual = 0; }

    // ---------- Getters ----------
    public String getNombreHabilidad() { return nombreHabilidad; }
    public String getDescripcion()     { return descripcion; }
    public int getValorBase()          { return valorBase; }
    public int getCooldown()           { return cooldown; }
    public int getCooldownActual()     { return cooldownActual; }
    public boolean getCdListo()        { return cooldownActual == 0; }
}