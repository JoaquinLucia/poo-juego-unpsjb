package modelo;

public class ArqueroFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        // Podés usar aturdir o curarse según lo que prefieras
        HabilidadAturdir flechaCegadora = new HabilidadAturdir("Flecha Cegadora", "Ciega al enemigo", 15, 2);
        
        Heroe arquero = new Heroe("Arquero", 400, 40, 1, 3);
        
        // arquero.setHabilidad(flechaCegadora);
        
        return arquero;
    }
}