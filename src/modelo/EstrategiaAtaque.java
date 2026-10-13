package modelo;

public interface EstrategiaAtaque {
    // Define el contrato: la estrategia recibe al enemigo que actúa y al objetivo (el héroe)
    // y devuelve la Habilidad que decide usar en este turno.
    Habilidad elegirAccion(Enemigo enemigoActual, Heroe heroeObjetivo);
}
