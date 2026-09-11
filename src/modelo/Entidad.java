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
        this.habilidades = new ArrayList<>();
    }

    public void recibirDanio(int danio){
        this.vida -= danio ;
        if (this.vida < 0){
            this.vida = 0;
        }
    }
    public void atacar(Entidad objetivo){
        objetivo.recibirDanio(this.ataque);
    }    

    public boolean estaVivo(){
        return this.vida > 0;
    }
    public final  void agregarHabilidad(Habilidad habilidad) {
        if (this.habilidades.size() < this.cantidadHabilidadMax) {
            this.habilidades.add(habilidad);
        }
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
        this.vida=+cantidad;
    }
    public void sacarAturdimiento(){
        this.aturdir=false;
    }
    public void aplicarAturdimiento(){
        this.aturdir=true;
    }
}