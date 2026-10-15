package modelo;

public class HabilidadAturdimiento extends Habilidad {

    // Constructor personalizado para configurar nombre, descripción, daño opcional y cooldown
    public HabilidadAturdimiento(String nombre, String descripcion, int valor, int cooldown) {
        super(nombre, descripcion, valor, cooldown);
    }

    @Override
    public void ejecutarHabilidad(Entidad lanzador, Entidad objetivo) {
        // Opcional: Si quieres que además del aturdimiento golpee al objetivo
        if (getValorBase() > 0) {
            objetivo.recibirDanio(getValorBase());
        }

        // Aplicamos el estado de aturdimiento al objetivo
        objetivo.aplicarAturdimiento();

       // Activamos el enfriamiento de la habilidad
        activarcooldown();
    }
}
    