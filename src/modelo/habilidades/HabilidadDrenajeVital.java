package modelo.habilidades;

import modelo.Entidad;

/**
 * Maldición que daña al enemigo y cura al usuario al final de cada turno.
 */
public class HabilidadDrenajeVital extends HabilidadConEfecto {
    
    private final int danioPorTurno;
    private Entidad lanzador; //Guardamos quién la tiró para poder curarlo después, asi es indistinto si la usa heroe o enemigo

    public HabilidadDrenajeVital(String nombre, String descripcion, int danioBase, int cooldown,
                                 int danioPorTurno, int duracion) {
        super(nombre, descripcion, danioBase, cooldown, duracion);
        if (danioPorTurno < 0) throw new IllegalArgumentException("El daño por turno no puede ser negativo");
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        this.lanzador = usuario; 
        super.aplicarEfecto(usuario, objetivo);
    }

    @Override
    protected void aplicarEfectoDeTurno(Entidad afectado) {
        // Le restamos vida a la víctima
        afectado.recibirDanio(danioPorTurno);
        
        // Le sumamos vida al lanzador usando la referencia que guardamos
        int curado = lanzador.sumarVida(danioPorTurno);
        
        System.out.println("[Drenaje Vital] " + afectado.getNombre() + " pierde " + danioPorTurno 
                + " HP | " + lanzador.getNombre() + " recupera " + curado + " HP | turnos restantes: " 
                + (getTurnosRestantes() - 1));
    }

    @Override
    public void limpiarEfecto() {
        super.limpiarEfecto();
        this.lanzador = null; // Limpiamos la referencia para no dejar basura en memoria
    }

    @Override 
    public String getResumenDeUsoHab(String usuario, String objetivo){
        return usuario + " usó " + getNombreHabilidad() + " sobre " + objetivo +
        ", robando " + danioPorTurno + " de vida por turno durante " + getDuracion() + " turnos";
    }
}