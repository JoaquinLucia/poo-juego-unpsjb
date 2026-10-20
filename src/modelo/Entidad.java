package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Entidad {
    private String nombre;
    protected int vida;
    protected int vidaActual;
    private final int vidaMaxima; // la vida con la que arranca; tope para las curaciones
    protected int ataque;
    private List<Habilidad> habilidades;
    private int cantidadHabilidadMax;
    private String sprite;
    private String spriteFrontal;

    private boolean aturdida = false;
    private final List<EfectoPorTurno> efectos = new ArrayList<>(); // efectos que tiene ENCIMA

    public Entidad(String nombre, int vida, int ataque, int cantidadHabilidadMax,
                   List<Habilidad> habilidades, String sprite, String spriteFrontal) {
        if (vida <= 0) {
            throw new IllegalArgumentException("La vida debe ser mayor a 0");
        }
        if (ataque < 0) {
            throw new IllegalArgumentException("El ataque no puede ser negativo");
        }
        if (cantidadHabilidadMax < 1) {
            throw new IllegalArgumentException("La entidad debe poder llevar al menos una habilidad");
        }
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataque = ataque;
        this.cantidadHabilidadMax = cantidadHabilidadMax;
        this.habilidades = new ArrayList<>(habilidades);
        this.sprite = sprite;
        this.spriteFrontal = spriteFrontal;
    }

    // ---------- Combate ----------
    public void recibirDanio(int danio) {
        this.vida -= danio;
        if (this.vida < 0) {
            this.vida = 0;
        }
    }

    public void atacar(Entidad objetivo) {
        objetivo.recibirDanio(this.ataque);
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    /** Cura sin pasarse de la vida máxima. Devuelve cuánto curó realmente. */
    public int sumarVida(int cantidad) {
        if (cantidad <= 0 || !estaVivo()) return 0;
        int vidaAntes = vida;
        vida = Math.min(vida + cantidad, vidaMaxima);
        return vida - vidaAntes;
    }

    // ---------- Habilidades ----------
    public void agregarHabilidad(Habilidad habilidad) {
        if (habilidad == null) {
            throw new IllegalArgumentException("La habilidad no puede ser null");
        }
        if (getCantidadHabilidades() >= getCantidadHabilidadMax()) {
            throw new IllegalStateException(getNombre() + " ya tiene el máximo de habilidades");
        }
        habilidades.add(habilidad);
    }

    public void usarHabilidad(int posicion, Entidad objetivo) {
        if (posicion < 0 || posicion >= getCantidadHabilidades()) {
            throw new IllegalArgumentException("Habilidad inválida");
        }
        habilidades.get(posicion).ejecutarHabilidad(this, objetivo);
    }

    public boolean puedeUsarHabilidad(int posicion) {
        return posicion >= 0 && posicion < getCantidadHabilidades()
            && habilidades.get(posicion).getCdListo();
    }

    public void reducirCooldowns() {
        for (Habilidad h : habilidades) {
            h.cronoCooldown();
        }
    }

    // ---------- Efectos ----------
    // Las habilidades del rival se registran acá al aplicarse sobre esta entidad.

    /** Lo usa la habilidad que aturde (o libera) a esta entidad. */
    public void setAturdida(boolean aturdida) { this.aturdida = aturdida; }

    /** False si está aturdida. */
    public boolean puedeActuar() { return !aturdida; }

    /** Lo llama la habilidad del rival al aplicarse sobre esta entidad. */
    public void agregarEfecto(EfectoPorTurno efecto) {
        if (!efectos.contains(efecto)) efectos.add(efecto);
    }

    /**
     * Al terminar su turno: bajan sus cooldowns y los efectos que tiene encima
     * le aplican su daño (veneno, desangrado) y descuentan sus turnos.
     */
    public void finalizarTurno() {
        reducirCooldowns();
        for (EfectoPorTurno e : efectos) {
            e.procesarTurno(this);
        }
        efectos.removeIf(e -> !e.afectaA(this));
    }

    /** Efectos activos sobre esta entidad (para la interfaz). */
    public List<EfectoPorTurno> getEfectosActivos() {
        return new ArrayList<>(efectos);
    }

    /** Se saca de encima los efectos que tenga y reinicia sus cooldowns. */
    public void prepararParaBatalla() {
        for (EfectoPorTurno e : efectos) {
            e.limpiarEfecto();
        }
        efectos.clear();
        aturdida = false;
        for (Habilidad h : habilidades) {
            h.reiniciarCooldown();
        }
    }

    public Habilidad getHabilidad(int posicion) {
        return this.habilidades.get(posicion);
    }

    public int getCantidadHabilidades() {
        return habilidades.size();
    }

    // ---------- Getters ----------
    public String getNombre()          { return this.nombre; }
    public int getVida()               { return this.vida; }
    public int getVidaActual()         { return this.vidaActual; }
    public int getVidaMaxima()         { return this.vidaMaxima; }
    public String getSprite()          { return sprite; }
    public String getSpriteFrontal()   { return spriteFrontal; }
    public int getAtaque()             { return this.ataque; }
    public List<Habilidad> getHabilidades() { return this.habilidades; }
    public int getCantidadHabilidadMax()    { return cantidadHabilidadMax; }

    // ---------- Setters ----------
    public void setSprite(String sprite) { this.sprite = sprite; }
}