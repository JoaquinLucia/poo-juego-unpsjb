package modelo.habilidades;

import modelo.EfectoPorTurno;
import modelo.Entidad;
import modelo.Habilidad;

/** Hace un daño fijo al final de cada turno del objetivo. */
public class HabilidadVeneno extends Habilidad implements EfectoPorTurno {
    private final int danioPorTurno;
    private final int duracion;

    private Entidad objetivo;
    private int turnosRestantes = 0;

    public HabilidadVeneno(String nombre, String descripcion, int danioBase, int cooldown,
                           int danioPorTurno, int duracion) {
        super(nombre, descripcion, danioBase, cooldown);
        if (duracion < 1) throw new IllegalArgumentException("El veneno debe durar al menos un turno");
        if (danioPorTurno < 0) throw new IllegalArgumentException("El daño por turno no puede ser negativo");
        this.danioPorTurno = danioPorTurno;
        this.duracion = duracion;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        if (getValorBase() > 0) objetivo.recibirDanio(getValorBase());
        this.objetivo = objetivo;
        this.turnosRestantes = duracion; // reaplicar renueva la duración
        objetivo.agregarEfecto(this);
    }

    @Override
    public boolean afectaA(Entidad entidad) {
        return entidad != null && entidad == objetivo && turnosRestantes > 0;
    }

    /** Se llama al final del turno del afectado: daño fijo. */
    @Override
    public void procesarTurno(Entidad afectado) {
        if (!afectaA(afectado)) return;
        afectado.recibirDanio(danioPorTurno);
        turnosRestantes--;
        System.out.println("[Veneno] " + afectado.getNombre() + " recibe " + danioPorTurno
                + " de daño | turnos restantes: " + turnosRestantes);
        if (turnosRestantes == 0) objetivo = null;
    }

    @Override
    public int getTurnosEfecto() { return turnosRestantes; }

    @Override
    public void limpiarEfecto() {
        objetivo = null;
        turnosRestantes = 0;
    }

    public int getDanioPorTurno() { return danioPorTurno; }
}