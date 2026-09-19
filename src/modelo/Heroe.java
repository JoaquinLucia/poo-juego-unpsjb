package modelo;

public class Heroe extends Entidad {
    private int nivel;

    public Heroe(String nombre, int vida, int ataque, int nivel, int cantidadHabilidadMax) {
        super(nombre, vida, ataque,cantidadHabilidadMax);
        if (nivel < 1) {
            throw new IllegalArgumentException("El nivel debe ser mayor o igual a 1");
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