package modelo;

public class EnemigoEliteFactory extends EnemigoFactory {
    @Override
    public Enemigo crearEnemigo() {
        Habilidad cura = new HabilidadCurarse("Poción Oscura", "Restaura vida", 25, 4);
        return new Enemigo("Orco Blindado", 100, 15, 1, cura);
    }
}