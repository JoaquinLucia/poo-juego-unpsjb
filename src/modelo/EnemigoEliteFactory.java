package modelo;

public class EnemigoEliteFactory implements EnemigoFactory {
    
    @Override
    public Enemigo crearEnemigo() {
        // 1. Creamos la habilidad obligatoria que te pide tu constructor
        HabilidadAturdir golpeSucio = new HabilidadAturdir("Golpe Sucio", "Un golpe bajo que aturde", 15, 3);
        
        // 2. Pasamos los 5 parámetros exactos: Nombre, Vida, Ataque, CantidadHabilidades, HabilidadInicial
        Enemigo esclavo = new Enemigo("Esclavo", 250, 18, 1, golpeSucio);
        
        return esclavo;
    }
}