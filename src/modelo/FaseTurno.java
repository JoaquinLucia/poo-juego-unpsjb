package modelo;

public interface FaseTurno {
    String atacar(Batalla batalla);
    String usarHabilidad(Batalla batalla, int indiceHabilidad);
    String jugarEnemigo(Batalla batalla);
    boolean estaTerminada();
    
}
