package modelo;

import java.util.ArrayList;

public abstract class Entidad { 
    private String nombre;
    private int vida;
    private int ataque;
    private ArrayList<Habilidad> habilidades;
    private int cantidadHabilidadMax;
    private boolean aturdir;
    private boolean defendiendo = false;
    private boolean inmune = false; // Variable de inmunidad

    public Entidad(String nombre, int vida, int ataque, int cantidadHabilidadMax) {
        if (vida <= 0) {
            throw new IllegalArgumentException("La vida debe ser mayor a 0");
        }
        if (ataque < 0) {
            throw new IllegalArgumentException("El ataque no puede ser negativo");
        }
        if (cantidadHabilidadMax < 1) {
            throw new IllegalArgumentException(
                "La entidad debe poder llevar al menos una habilidad"
            );
        }

        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
        this.cantidadHabilidadMax = cantidadHabilidadMax;
        this.habilidades = new ArrayList<>(cantidadHabilidadMax);
    }

    public void recibirDanio(int danio) {
        // Verificamos si la entidad es inmune primero
        if (this.inmune) {
            danio = 0;
            System.out.println(this.nombre + " es inmune y evitó todo el daño.");
        } else if (this.defendiendo) {
            double porcentajeReduccion = 0.4; // Valor por defecto
        
            // Asignamos la efectividad de la defensa según la clase
            if (this.nombre.equalsIgnoreCase("Caballero")) {
                porcentajeReduccion = 0.7; // 70% de reducción (Muy defensivo)
            } else if (this.nombre.equalsIgnoreCase("Arquero")) {
                porcentajeReduccion = 0.5; // 50% de reducción (Equilibrado)
            } else if (this.nombre.equalsIgnoreCase("Mago")) {
                porcentajeReduccion = 0.3; // 30% de reducción (Frágil, le cuesta defenderse)
            }
        
            danio = (int) (danio * (1.0 - porcentajeReduccion));
            System.out.println(this.nombre + " se defendió, mitigando parte del daño.");
        }

        this.vida -= danio;
        if (this.vida < 0) {
            this.vida = 0; // Evita que la vida sea negativa
        }
    }

    public void atacar(Entidad objetivo){
        objetivo.recibirDanio(this.ataque);
        System.out.println(objetivo.getNombre() + " recibio un danio de " + getAtaque() + ", " + objetivo.getNombre() + " vida : " + objetivo.getVida());
    }    

    public boolean estaVivo(){
        return this.vida > 0;
    }

    // HABILIDADES
    public final void agregarHabilidad(Habilidad habilidad) {
        if (this.habilidades.size() < this.cantidadHabilidadMax) {
            this.habilidades.add(habilidad);
        }
    }

    // Resetea todas las habilidades de la entidad para que arranquen listas
    public void reiniciarTodasLasHabilidades() {
        for (Habilidad hab : habilidades) {
            while (!hab.getCdListo()) {
                hab.cronoCooldown();
            }
        }
    }

    public void reemplazarHabilidad(int posicion, Habilidad nuevaHabilidad) {
        if (nuevaHabilidad == null) {
            throw new IllegalArgumentException("La habilidad no puede ser null");
        }

        if (posicion < 0 || posicion >= this.habilidades.size()) {
            throw new IllegalArgumentException("La posición de la habilidad no es válida");
        }

        this.habilidades.set(posicion, nuevaHabilidad);
    }

    public Habilidad getHabilidad(int posicion) {
        return this.habilidades.get(posicion);
    }

    public int getCantidadHabilidades() {
        return this.habilidades.size();
    }

    // GETTERS 
    public String getNombre() { return this.nombre; }
    public int getVida() { return this.vida; }
    public int getAtaque() { return this.ataque; }
    public ArrayList<Habilidad> getHabilidades() { return this.habilidades; }
    public int getCantidadHabilidadMax() { return cantidadHabilidadMax; }
    public boolean getaturdir() { return this.aturdir; }

    // SETTERS Y ESTADOS
    public void sumarVida(int cantidad) { 
        this.vida += cantidad;
    }

    public void sacarAturdimiento(){
        this.aturdir = false;
    }

    public void aplicarAturdimiento(){
        this.aturdir = true;
    }

    public void setDefendiendo(boolean estado) {
        this.defendiendo = estado;
    }

    public boolean isDefendiendo() {
        return this.defendiendo;
    }

    public void setInmune(boolean estado) {
        this.inmune = estado;
    }

    public boolean isInmune() {
        return this.inmune;
    }
}