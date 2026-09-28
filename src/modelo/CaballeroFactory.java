package modelo;

public class CaballeroFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        HabilidadCurarse cura = new HabilidadCurarse("Segunda Voluntad", "Recupera gran parte de la salud", 80, 2);
        
        Heroe caballero = new Heroe("Caballero", 600, 25, 1, 5); 
        
        caballero.agregarHabilidad(cura); // <-- Descomentado
        
        return caballero;
    }
}