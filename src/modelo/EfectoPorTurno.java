package modelo;

/** Habilidades cuyo efecto dura varios turnos sobre una entidad. */
public interface EfectoPorTurno {
    boolean afectaA(Entidad entidad);
    void procesarTurno(Entidad afectado);
    int getTurnosEfecto();
    void limpiarEfecto();
}