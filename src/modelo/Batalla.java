package modelo;

public class Batalla {
    private Heroe heroe;
    private Enemigo enemigo;
    private FaseTurno faseActual;
    private EstrategiaAtaque estrategiaEnemigo;

    public Batalla(Heroe heroe, Enemigo enemigo) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        // Arrancamos la batalla en el turno del jugador usando el Patrón State
        this.faseActual = new TurnoJugador();
        // Asignamos por defecto la estrategia táctica (o agresiva) al enemigo (Patrón Strategy)
        this.estrategiaEnemigo = new EstrategiaTactica();
    }

    // Método clave para el Patrón State (¡Este es el que te marcaba en rojo!)
    public void cambiarFase(FaseTurno nuevaFase) {
        this.faseActual = nuevaFase;
    }

    public String ejecutarAtaqueHeroe() {
        if (!heroe.estaVivo()) return "El héroe está derrotado.";
        String resultado = faseActual.atacar(this);
        reducirCooldownsHeroe();
        return resultado;
    }

    public String ejecutarHabilidadHeroe(int indiceHabilidad) {
        if (!heroe.estaVivo()) return "El héroe está derrotado.";
        
        // Validación para evitar errores si no hay habilidad asignada
        if (heroe.getHabilidades() == null || heroe.getCantidadHabilidades() <= indiceHabilidad || heroe.getHabilidad(indiceHabilidad) == null) {
            return "No hay habilidad en ese espacio.";
        }

        String resultado = faseActual.usarHabilidad(this, indiceHabilidad);
        reducirCooldownsHeroe();
        return resultado;
    }

    public String ejecutarDefensaHeroe() {
        if (!heroe.estaVivo()) return "El héroe está derrotado.";
        String resultado = faseActual.defender(this);
        reducirCooldownsHeroe();
        return resultado;
    }

    public String ejecutarTurnoEnemigo() {
        if (!enemigo.estaVivo()) return "";

        // Si el enemigo está aturdido, pierde su turno de forma limpia
        if (enemigo.getaturdir()) {
            enemigo.sacarAturdimiento();
            cambiarFase(new TurnoJugador());
            return enemigo.getNombre() + " está aturdido y pierde su turno.";
        }

        // Usamos la estrategia para decidir la acción
        Habilidad habilidadElegida = estrategiaEnemigo.elegirAccion(enemigo, heroe);
        String mensaje;

        if (habilidadElegida != null && habilidadElegida.getCdListo()) {
            habilidadElegida.ejecutarHabilidad(enemigo, heroe);
            mensaje = enemigo.getNombre() + " usó su habilidad " + habilidadElegida.getNombreHabilidad() + ".";
        } else {
            enemigo.atacar(heroe);
            mensaje = enemigo.getNombre() + " atacó a " + heroe.getNombre() + " causando " + enemigo.getAtaque() + " de daño.";
        }

        reducirCooldownsEnemigo();
        cambiarFase(new TurnoJugador());
        
        return mensaje;
    }

    private void reducirCooldownsHeroe() {
        if (heroe.getHabilidades() != null) {
            for (Habilidad h : heroe.getHabilidades()) {
                if (h != null) h.cronoCooldown();
            }
        }
    }

    private void reducirCooldownsEnemigo() {
        if (enemigo.getHabilidades() != null) {
            for (Habilidad h : enemigo.getHabilidades()) {
                if (h != null) h.cronoCooldown();
            }
        }
    }

    public boolean estaTerminada() {
        return !heroe.estaVivo() || !enemigo.estaVivo();
    }

    public boolean ganoHeroe() {
        return heroe.estaVivo() && !enemigo.estaVivo();
    }

    // Getters
    public Heroe getHeroe() { return heroe; }
    public Enemigo getEnemigo() { return enemigo; }
    public FaseTurno getFaseActual() { return faseActual; }
}