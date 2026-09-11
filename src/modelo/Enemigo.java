package modelo;


public class Enemigo extends Entidad{

    //private int resistenciaDanio;

public Enemigo(String nombre, int vida, int ataque,int cantidadHabilidadMax, Habilidad habilidadInicial) {

    super(nombre, vida, ataque, cantidadHabilidadMax);
    if (habilidadInicial==null){
            throw new IllegalArgumentException("El enemigo debe tener una habilidad inicial");
    }
    agregarHabilidad(habilidadInicial);
}
      //  this.resistenciaDanio = resistenciaDanio;

}


