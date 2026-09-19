
import modelo.Batalla;
import modelo.Enemigo;
import modelo.HabilidadCurarse;
import modelo.Heroe;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

HabilidadCurarse cura=new HabilidadCurarse("curamaxima", "te roba bvida", 12, 1);
Heroe heroe1=new Heroe("sasuke ",400,34,1,4);
Enemigo enemigo1= new Enemigo("Itachi ", 400, 56, 1, cura);
Batalla batalla1=new Batalla(heroe1, enemigo1);
batalla1.combate();

}
}