package modelo;

import java.util.ArrayList;

public abstract class Entidad { 
    private  String nombre;
    private  int vida;
    private  int ataque;
    private ArrayList <Habilidad> habilidades;
    private  int cantidadHabilidadMax;
    private boolean aturdir;
    //private boolean estaVivo; tenemos un metodo que nos dice que esta vivo
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


    public void recibirDanio(int danio){
        this.vida -= danio ;
        if (this.vida < 0){
            this.vida = 0;
        }
    }
    public void atacar(Entidad objetivo){
        objetivo.recibirDanio(this.ataque);
        System.out.println(objetivo.getNombre()+" recibio un danio de " + getAtaque() +","+ objetivo.getNombre() + "  vida : " + objetivo.getVida());
    }    

    public boolean estaVivo(){
        return this.vida > 0;
    }
//HABILIDADES
    public final  void agregarHabilidad(Habilidad habilidad) {
        if (this.habilidades.size() < this.cantidadHabilidadMax) {
            this.habilidades.add(habilidad);
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

    //GETTERS 
    public String getNombre() { return this.nombre; }
    public int getVida()      { return this.vida; }
    public int getAtaque()    { return this.ataque; }
    public ArrayList<Habilidad> getHabilidades() { return this.habilidades; }
    public int getCantidadHabilidadMax() { return cantidadHabilidadMax;}
    public boolean  getaturdir() { return this.aturdir;}

    //set 
    public void sumarVida(int cantidad)      { 
        this.vida+=cantidad;
    }
    public void sacarAturdimiento(){
        this.aturdir=false;
    }
    public void aplicarAturdimiento(){
        this.aturdir=true;
    }
}