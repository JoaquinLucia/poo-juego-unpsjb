package modelo;

public class CaballeroFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        HabilidadCurarse cura = new HabilidadCurarse("Segunda Voluntad", "Recupera gran parte de la salud", 80, 2);
        
        // (Nombre, Vida, Daño, Nivel, Pociones)
        Heroe caballero = new Heroe("Caballero", 600, 25, 1, 5); 
        
        // Si tu Heroe tiene un método para equipar la habilidad, lo usás acá:
        // caballero.setHabilidad(cura);
        
        return caballero;
    }
}