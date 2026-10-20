package modelo.habilidades;

import modelo.Entidad;
import modelo.Habilidad;

/** Cura al que la usa. valorBase es la cantidad de vida que recupera. */
public class HabilidadCurar extends Habilidad {
    private int ultimaCuracion = 0;

    public HabilidadCurar(String nombre, String descripcion, int cantidadCuracion, int cooldown) {
        super(nombre, descripcion, cantidadCuracion, cooldown);
        if (cantidadCuracion <= 0) {
            throw new IllegalArgumentException("La curación debe ser mayor a 0");
        }
    }

    /** Cura al usuario; el objetivo no recibe nada. */
    @Override
    protected void aplicarEfecto(Entidad usuario, Entidad objetivo) {
        ultimaCuracion = usuario.sumarVida(getValorBase());
        System.out.println("[Curación] " + usuario.getNombre() + " recupera " + ultimaCuracion
                + " de vida (" + usuario.getVida() + "/" + usuario.getVidaMaxima() + ")");
    }

    /** Cuánto curó realmente la última vez (puede ser menos si estaba casi lleno). */
    public int getUltimaCuracion() { return ultimaCuracion; }

}