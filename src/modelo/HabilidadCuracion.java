package modelo;

public class HabilidadCuracion extends Habilidad {

    // Constructor por defecto
    public HabilidadCuracion() {
        super("Sanación Oscura", "Recupera una gran cantidad de vida propia.", 40, 3);
    }

    // Constructor personalizado para elegir los valores desde el catálogo
    public HabilidadCuracion(String nombre, String descripcion, int valorCuracion, int cooldown) {
        super(nombre, descripcion, valorCuracion, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad usuario, Entidad objetivo) {
        // La curación se aplica al usuario que lanza la habilidad, no al enemigo
        usuario.sumarVida(getValorBase());
        
        System.out.println(usuario.getNombre() + " usó " + getNombreHabilidad() + " y recuperó " + getValorBase() + " puntos de vida.");
        
        // Activamos el enfriamiento para que deba esperar los turnos correspondientes
        activarcooldown();
    }
}