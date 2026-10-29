package modelo.habilidades;

import modelo.EfectoPorTurno;
import modelo.Entidad;
import modelo.Habilidad;

/** El objetivo pierde sus próximos turnos. */
public class HabilidadAturdir extends Habilidad implements EfectoPorTurno {
    private final int duracion;

    private Entidad objetivo;
    private int turnosRestantes = 0;

    public HabilidadAturdir(String nombre, String descripcion, int danioBase, int cooldown, int duracion) {
        super(nombre, descripcion, danioBase, cooldown);
        if (duracion < 1) throw new IllegalArgumentException("El aturdimiento debe durar al menos un turno");
        this.duracion = duracion;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        if (getValorBase() > 0) objetivo.recibirDanio(getValorBase());
        this.objetivo = objetivo;
        this.turnosRestantes = duracion;
        objetivo.setAturdida(true);
        objetivo.agregarEfecto(this);
    }

    @Override
    public boolean afectaA(Entidad entidad) {
        return entidad != null && entidad == objetivo && turnosRestantes > 0;
    }

    /** Se llama al final del turno del aturdido, después de que lo perdió. */
    @Override
    public void procesarTurno(Entidad afectado) {
        if (!afectaA(afectado)) return;
        turnosRestantes--;
        System.out.println("[Aturdimiento] " + afectado.getNombre()
                + " | turnos restantes: " + turnosRestantes);
        if (turnosRestantes == 0) {
            afectado.setAturdida(false);
            objetivo = null;
        }
    }

    @Override
    public int getTurnosEfecto() { return turnosRestantes; }

    @Override
    public void limpiarEfecto() {
        if (objetivo != null) objetivo.setAturdida(false);
        objetivo = null;
        turnosRestantes = 0;
    }
@Override
    public String getResumenDeUsoHab(String usuario, String objetivo) {
        return usuario + " usó " + getNombreHabilidad() + " e hizo " + getValorBase()+ " de daño a " + objetivo +  ", queda aturdido ";
    }
}