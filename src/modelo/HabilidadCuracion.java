package modelo;

public class HabilidadCuracion extends Habilidad {

    // Constructor por defecto (el que tenías)
    public HabilidadCuracion() {
        super("Sanación Oscura", "Recupera una gran cantidad de vida propia.", 40, 3);
    }

    // Nuevo constructor personalizado para elegir los valores desde el catálogo
    public HabilidadCuracion(String nombre, String descripcion, int valorCuracion, int cooldown) {
        super(nombre, descripcion, valorCuracion, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        lanzador.sumarVida(getValorBase());
        activarcooldown();
    }
}