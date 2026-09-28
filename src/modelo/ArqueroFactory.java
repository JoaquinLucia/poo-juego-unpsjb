package modelo;

public class ArqueroFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        HabilidadAturdir flechaCegadora = new HabilidadAturdir("Flecha Cegadora", "Ciega al enemigo", 15, 2);
        
        Heroe arquero = new Heroe("Arquero", 400, 40, 1, 3);
        
        arquero.agregarHabilidad(flechaCegadora); // <-- Descomentado
        
        return arquero;
    }
}