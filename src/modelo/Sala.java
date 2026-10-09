package modelo;

/**
 * Una sala del nivel: el escenario que se explora y el id del enemigo
 * que aparece ahí (el enemigo se crea con CatalogoPersonajes al entrar a pelear).
 */
public class Sala {
    private final Escenario escenario;
    private final String idEnemigo;

    public Sala(Escenario escenario, String idEnemigo) {
        this.escenario = escenario;
        this.idEnemigo = idEnemigo;
    }

    public Escenario getEscenario() { return escenario; }
    public String getIdEnemigo() { return idEnemigo; }
}
