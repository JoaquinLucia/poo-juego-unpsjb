package mvc.modelo;

public class Heroe extends Entidad {
    private int nivel;

    public Heroe(String nombre, int vida, int ataque, int nivel, int cantidadHabilidadMax) {
        super(nombre, vida, ataque,cantidadHabilidadMax);
        if (cantidadHabilidadMax < 1) {
            throw new IllegalArgumentException("El héroe debe poder llevar al menos una habilidad");
        }
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public void subirNivel(){
        this.nivel++;
    }
 
}
