package modelo.habilidades;

import modelo.Entidad;

public class HabilidadVeneno extends HabilidadConEfecto {
    private final int danioPorTurno;

    public HabilidadVeneno(String nombre, String descripcion, int danioBase, int cooldown,
                           int danioPorTurno, int duracion) {
        super(nombre, descripcion, danioBase, cooldown, duracion);
        if (danioPorTurno < 0) throw new IllegalArgumentException("El daño por turno no puede ser negativo");
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    protected void aplicarEfectoDeTurno(Entidad afectado) {
        afectado.recibirDanio(danioPorTurno);
        System.out.println("[Veneno] " + afectado.getNombre() + " recibe " + danioPorTurno
                + " de daño | turnos restantes: " + (getTurnosRestantes() - 1));
    }

    @Override 
    public String getResumenDeUsoHab(String usuario, String objetivo){
        return usuario + " usó " + getNombreHabilidad() + " sobre " + objetivo +
        " queda envenenado (" + danioPorTurno + " de daño por turno durante " + getDuracion() + " turnos)";
    }

    public int getDanioPorTurno() { return danioPorTurno; }
}