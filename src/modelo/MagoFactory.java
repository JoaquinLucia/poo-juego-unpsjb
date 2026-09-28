package modelo;

public class MagoFactory implements HeroeFactory {
    @Override
    public Heroe crearHeroe() {
        HabilidadAturdir congelar = new HabilidadAturdir("Prisión de Hielo", "Congela al enemigo por un turno", 0, 3);
        
        Heroe mago = new Heroe("Mago", 250, 65, 1, 2);
        
        // mago.setHabilidad(congelar);
        
        return mago;
    }
}