
public class Heroe extends Entidad {

    private int nivel;
    private int cantidadHabilidadMax;

    public Heroe(int nivel, int cantidadHabilidadMax){
        super(nombre,vida,ataque);
        this.nivel = nivel;
        this.cantidadHabilidadMax = cantidadHabilidadMax;        
    }

    private void subirNivel(){
        this.nivel++;
    }

    private void usarHabilidad(){
        
    }

    private void robarHabilidad(){

    }
}
