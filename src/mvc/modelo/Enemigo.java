package mvc.modelo;


public class Enemigo extends Entidad{

    private int resistenciaDanio;

    public Enemigo(String nombre, int vida, int ataque,int resistenciaDanio, int cantidadHabilidadMax){
        super(nombre, vida, ataque, cantidadHabilidadMax);
        this.resistenciaDanio = resistenciaDanio;
    }

}
