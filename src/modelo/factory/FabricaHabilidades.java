package modelo.factory;


import modelo.Habilidad;

public interface FabricaHabilidades {
    Habilidad crearHabilidad(String id);
}