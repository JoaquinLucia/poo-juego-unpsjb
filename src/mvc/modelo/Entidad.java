import java.util.ArrayList;

public abstract class Entidad { 

    private String nombre;
    private int vida;
    private int ataque;
    private ArrayList <Habilidad> habilidades;
    private boolean estaVivo;

    public Entidad(String nombre, int vida, int ataque){
        this.nombre=nombre;
        this.vida=vida;
        this.ataque=ataque;
        this.habilidades = new ArrayList<>();
    } 

    public String getNombre() { return this.nombre; }
    public int getVida()      { return this.vida; }
    public int getAtaque()    { return this.ataque; }
    public ArrayList<Habilidad> getHabilidades() { return this.habilidades; }

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

    
}