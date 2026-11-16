/*Clase intermedia, para evitar repetir codigo en las habilidades que hacen algun efecto*/
package modelo.habilidades;

import modelo.EfectoPorTurno;
import modelo.Entidad;
import modelo.Habilidad;

/**
 * Molde intermedio para habilidades que duran varios turnos.
 * Resuelve toda la lógica repetitiva de EfectoPorTurno.
 */
public abstract class HabilidadConEfecto extends Habilidad implements EfectoPorTurno {
    
    private final int duracion;
    private Entidad objetivo;
    private int turnosRestantes = 0;

    public HabilidadConEfecto(String nombre, String descripcion, int danioBase, int cooldown, int duracion) {
        super(nombre, descripcion, danioBase, cooldown);
        if (duracion < 1) throw new IllegalArgumentException("El efecto debe durar al menos un turno");
        this.duracion = duracion;
    }

    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        if (getValorBase() > 0) {
            objetivo.recibirDanio(getValorBase());
        }
        this.objetivo = objetivo;
        this.turnosRestantes = duracion;
        objetivo.agregarEfecto(this);
    }

    @Override
    public boolean afectaA(Entidad entidad) {
        return entidad != null && entidad == objetivo && turnosRestantes > 0;
    }

    /** 
     * Método Plantilla para el turno: descuenta turnos y limpia automáticamente.
     * Es final para que las hijas no lo rompan, delegando la acción a aplicarEfectoDeTurno.
     */
    @Override
    public final void procesarTurno(Entidad afectado) {
        if (!afectaA(afectado)) return;
        
        aplicarEfectoDeTurno(afectado);
        turnosRestantes--;
        
        if (turnosRestantes == 0) {
            alTerminarEfecto(afectado);
            objetivo = null;
        }
    }

    /** Obliga a las hijas a definir qué pasa en cada tick (daño de veneno, sangrado, etc.) */
    protected abstract void aplicarEfectoDeTurno(Entidad afectado);

    /** por si la hija necesita revertir algo al expirar, como el aturdimiento */
    protected void alTerminarEfecto(Entidad afectado) { }

    @Override
    public int getTurnosEfecto() { return turnosRestantes; }

    @Override
    public void limpiarEfecto() {
        if (objetivo != null) {
            alTerminarEfecto(objetivo);
        }
        objetivo = null;
        turnosRestantes = 0;
    }

    // Un getter protegido por si alguna hija necesita acceder a la duración original
    protected int getDuracion() { return duracion; }
    
    // Un getter protegido por si alguna hija necesita saber cuántos turnos quedan
    protected int getTurnosRestantes() { return turnosRestantes; }
}