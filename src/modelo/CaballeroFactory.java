package modelo;

public class CaballeroFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        // Habilidad: Nombre, Descripción, Valor (Daño base en este caso), Cooldown
        HabilidadAturdir golpeAturdidor = new HabilidadAturdir(
            "Golpe Aturdidor", 
            "Daña y provoca aturdimiento al enemigo", 
            40, // 40 de daño por el golpe
            4   // 4 turnos de cooldown para que no esté aturdiendo todo el tiempo
        );
        
        // (Nombre, Vida, Daño, Nivel, Pociones)
        Heroe caballero = new Heroe("Caballero", 600, 25, 1, 5); 
        
        // Le equipamos la habilidad al héroe
        caballero.agregarHabilidad(golpeAturdidor);
        
        return caballero;
    }
}