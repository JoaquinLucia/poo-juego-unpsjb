package modelo.habilidades;

import modelo.Entidad;

public class HabilidadDesangrar extends HabilidadConEfecto {
    private final int danioInicial;
    private int danioActual;

    public HabilidadDesangrar(String nombre, String descripcion, int danioBase, int cooldown,
                              int danioInicial, int duracion) {
        super(nombre, descripcion, danioBase, cooldown, duracion);
        if (danioInicial < 0) throw new IllegalArgumentException("El daño inicial no puede ser negativo");
        this.danioInicial = danioInicial;
        this.danioActual = danioInicial;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        super.aplicarEfecto(usuario, objetivo); 
        this.danioActual = danioInicial; // Reinicia el acumulador
    }

    @Override
    protected void aplicarEfectoDeTurno(Entidad afectado) {
        afectado.recibirDanio(danioActual);
        System.out.println("[Desangrado] " + afectado.getNombre() + " recibe " + danioActual
                + " de daño | turnos restantes: " + (getTurnosRestantes() - 1));
        danioActual++; // la herida se abre más para el próximo turno
    }

    @Override
    public void limpiarEfecto() {
        super.limpiarEfecto();
        danioActual = danioInicial;
    }

    @Override 
    public String getResumenDeUsoHab(String usuario, String objetivo){
        return usuario + " usó " + getNombreHabilidad() + " sobre " + objetivo +
        ", empieza a sangrar (" + danioActual + " de daño, sube 1 por turno durante " + getDuracion() + " turnos)";
    }

    public int getDanioActual() { return danioActual; }
}