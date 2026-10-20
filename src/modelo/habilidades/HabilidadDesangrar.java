package modelo.habilidades;

import modelo.EfectoPorTurno;
import modelo.Entidad;
import modelo.Habilidad;

/** La herida se abre cada vez más: el daño sube 1 punto por turno. */
public class HabilidadDesangrar extends Habilidad implements EfectoPorTurno {
    private final int danioInicial;
    private final int duracion;

    private Entidad objetivo;
    private int turnosRestantes = 0;
    private int danioActual;

    public HabilidadDesangrar(String nombre, String descripcion, int danioBase, int cooldown,
                              int danioInicial, int duracion) {
        super(nombre, descripcion, danioBase, cooldown);
        if (duracion < 1) throw new IllegalArgumentException("El desangrado debe durar al menos un turno");
        if (danioInicial < 0) throw new IllegalArgumentException("El daño inicial no puede ser negativo");
        this.danioInicial = danioInicial;
        this.duracion = duracion;
        this.danioActual = danioInicial;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        if (getValorBase() > 0) objetivo.recibirDanio(getValorBase());
        this.objetivo = objetivo;
        this.turnosRestantes = duracion;
        this.danioActual = danioInicial; // cada aplicación arranca de cero
        objetivo.agregarEfecto(this);
    }

    @Override
    public boolean afectaA(Entidad entidad) {
        return entidad != null && entidad == objetivo && turnosRestantes > 0;
    }

    /** Se llama al final del turno del afectado: daño creciente. */
    @Override
    public void procesarTurno(Entidad afectado) {
        if (!afectaA(afectado)) return;
        afectado.recibirDanio(danioActual);
        turnosRestantes--;
        System.out.println("[Desangrado] " + afectado.getNombre() + " recibe " + danioActual
                + " de daño | turnos restantes: " + turnosRestantes);
        danioActual++; // la herida se abre más
        if (turnosRestantes == 0) objetivo = null;
    }

    @Override
    public int getTurnosEfecto() { return turnosRestantes; }

    @Override
    public void limpiarEfecto() {
        objetivo = null;
        turnosRestantes = 0;
        danioActual = danioInicial;
    }

    /** Daño que hará en el próximo tick. */
    public int getDanioActual() { return danioActual; }
}