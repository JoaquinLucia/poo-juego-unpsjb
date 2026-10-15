package modelo;

public interface EstrategiaAtaque {
    Habilidad elegirAccion(Enemigo enemigoActual, Heroe heroeObjetivo);
}