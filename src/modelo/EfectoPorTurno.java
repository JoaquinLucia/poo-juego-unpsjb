package modelo;

/* Habilidades que el efecto dura varios turnos sobre alguna entidad */
public interface EfectoPorTurno{
    boolean afectaA(Entidad entidad);
    void procesarTurno(Entidad afectado);
    int getTurnosEfecto();
    void limpiarEfecto();
}