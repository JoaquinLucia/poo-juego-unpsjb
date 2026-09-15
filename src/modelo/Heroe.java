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

    public void robarHabilidad(Entidad objetivo) {

        if (objetivo.estaVivo()) {
            System.out.println("Todavia no podes la habilidad" + objetivo.getNombre() + " esta vivo");
            return;
        }

        if(objetivo instanceof Robable) {
            Robable robable = (Robable) objetivo;
            Habilidad nuevaHabilidad = robable.obtenerHabilidad();
            //agregar la habilidad al listado del heroe
            this.agregarHabilidad(nuevaHabilidad);
            System.out.println("Ganaste " + getNombre() + " derrotaste a " + objetivo.getNombre() + " y le robaste la habilidad: " + nuevaHabilidad.getNombreHabilidad());
        } else {
            System.out.println("El objetivo no tiene habilidades para robar.");
        }
    }

}
