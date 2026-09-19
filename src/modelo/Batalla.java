package modelo;

public class Batalla {

    public enum Turno {HEROE, ENEMIGO};

    private Heroe heroe;
    private Enemigo enemigo;
    private boolean turnoheroe;
    private Turno turnoActual = Turno.HEROE;


public Batalla(Heroe heroe,Enemigo enemigo){
this.heroe=heroe;
this.enemigo=enemigo;
}

public void combate() {

        while (heroe.estaVivo() && enemigo.estaVivo()) {

            if (turnoheroe) {
                heroe.atacar(enemigo);
                turnoheroe = false;
            } else {
                Habilidad habilidad = enemigo.getHabilidad(0);
                habilidad.ejecutarHabilidad(enemigo, heroe);

                enemigo.atacar(heroe);
                turnoheroe = true;
            }
        }
        
        if (heroe.estaVivo()) {
            System.out.println(heroe.getNombre() + " ganó la batalla.");
        } else {
            System.out.println(enemigo.getNombre() + " ganó la batalla.");
        }
    }

    public Heroe getHeroe() { return heroe; }
    public Enemigo getEnemigo() { return enemigo; }
}

