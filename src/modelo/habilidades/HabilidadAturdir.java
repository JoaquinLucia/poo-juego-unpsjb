package modelo.habilidades;

import modelo.Entidad;

public class HabilidadAturdir extends HabilidadConEfecto {

    public HabilidadAturdir(String nombre, String descripcion, int danioBase, int cooldown, int duracion) {
        super(nombre, descripcion, danioBase, cooldown, duracion);
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        super.aplicarEfecto(usuario, objetivo);
        objetivo.setAturdida(true);
    }

    @Override
    protected void aplicarEfectoDeTurno(Entidad afectado) {
        System.out.println("[Aturdimiento] " + afectado.getNombre()
                + " | turnos restantes: " + (getTurnosRestantes() - 1));
    }

    @Override
    protected void alTerminarEfecto(Entidad afectado) {
        afectado.setAturdida(false);
    }

    @Override
    public String getResumenDeUsoHab(String usuario, String objetivo) {
        return usuario + " usó " + getNombreHabilidad() + " e hizo " + getValorBase() + " de daño a " + objetivo + ", queda aturdido";
    }
}