
public class Enemigo extends Entidad{

    private int resistenciaDanio;

    public Enemigo(int resistenciaDanio){
        super(null, resistenciaDanio, resistenciaDanio);
        this.resistenciaDanio = resistenciaDanio;
    }

    
}
