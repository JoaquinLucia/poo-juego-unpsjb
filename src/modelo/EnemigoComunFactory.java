package modelo;

public class EnemigoComunFactory extends EnemigoFactory {
    @Override
    public Enemigo crearEnemigo() {
        // Los parámetros son: Nombre, Vida, Ataque, MaxHabilidades, HabilidadInicial
        Habilidad aturdir = new HabilidadAturdir("Golpe Trapero", "Aturde al objetivo", 0, 3);
        return new Enemigo("Esqueleto", 50, 10, 1, aturdir);
    }
}
