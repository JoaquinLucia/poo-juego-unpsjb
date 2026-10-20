package modelo.factory;
 
import modelo.Entidad;
 
/**
 * Crea un personaje a partir de sus datos.
 * No sabe de dónde salen los datos (base, archivo...): solo crea.
 */
public interface FabricaPersonaje {
    Entidad crearPersonaje(String tipo);
}
 