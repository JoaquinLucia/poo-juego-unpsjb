package modelo;

import java.util.List;

public class Heroe extends Entidad {
    private int nivel;
    private String descripcion;
    private double multiplicadorDefensa;
    private boolean defendiendo = false;         // estado: si está defendiéndose este turno

    public Heroe(String nombre, int vida, int ataque, int cantidadHabilidadMax, List<Habilidad> habilidades, String sprite, String sprite_Frontal, String descripcion, double multiplicadorDefensa) {
        super(nombre, vida, ataque, cantidadHabilidadMax, habilidades, sprite, sprite_Frontal);
        this.nivel = 1;
        this.descripcion = descripcion;
        this.multiplicadorDefensa = multiplicadorDefensa;
    }

    // ---------- DEFENSA ----------
    public void defender()          { defendiendo = true; }
    public void dejarDeDefender()   { defendiendo = false; }

    @Override
    public int recibirDanio(int danio) {
        if (defendiendo) {
            danio = (int) Math.round(danio * multiplicadorDefensa);
        }
        return super.recibirDanio(danio);   // Entidad se encarga de restar la vida
    }

    @Override
    public void prepararParaBatalla() {
        super.prepararParaBatalla();
        defendiendo = false;   // no arrastrar la defensa de otra batalla
    }

    // HABILIDADES
    public void reemplazarHabilidad(int posicion, Habilidad nuevaHabilidad) {
        if (nuevaHabilidad == null) {
            throw new IllegalArgumentException("La habilidad no puede ser null");
        }
        if (posicion < 0 || posicion >= getCantidadHabilidades()) {
            throw new IllegalArgumentException("La posición de la habilidad no es válida");
        }
        getHabilidades().set(posicion, nuevaHabilidad);
    }

    public int getNivel()          { return nivel; }
     //DEFENSA    
    public int getPorcentajeDefensaReducida() { return (int) Math.round((1.0 - multiplicadorDefensa) * 100);}

    public double getMultiplicadorDefensaPropio() { return multiplicadorDefensa; }
    //
    public String getDescripcion() { return this.descripcion; }

    public void subirNivel() { nivel++; }
}