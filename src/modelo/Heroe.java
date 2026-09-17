package modelo;

import java.util.ArrayList;
import java.util.List;

public class Heroe extends Entidad {
    private int nivel;
    
    // 1. Lista de observadores y mensaje de estado del modelo
    private final List<ObservadorJuego> observadores = new ArrayList<>();
    private String ultimoMensaje = "¡Comienza la batalla!";

    public Heroe(String nombre, int vida, int ataque, int nivel, int cantidadHabilidadMax) {
        super(nombre, vida, ataque, cantidadHabilidadMax);
        if (nivel < 1) {
            throw new IllegalArgumentException("El nivel debe ser mayor o igual a 1");
        }
        this.nivel = nivel;
    }

    // 2. Métodos para gestionar los observadores (Patrón Observer)
    public void agregarObservador(ObservadorJuego observador) {
        this.observadores.add(observador);
    }

    private void notificar() {
        for (ObservadorJuego obs : this.observadores) {
            obs.actualizarEstadoJuego();
        }
    }

    // Getter del mensaje de estado del juego
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getNivel() {
        return nivel;
    }

    public void subirNivel() {
        this.nivel++;
        this.ultimoMensaje = getNombre() + " subió al nivel " + this.nivel + "!";
        this.notificar(); 
    }

    // Si en Entidad.java NO se llamaban igual o no existían, quitamos temporalmente el @Override para evitar el error del compilador
    public void atacar(Entidad objetivo) {
        super.atacar(objetivo);
        this.ultimoMensaje = "Atacaste a " + objetivo.getNombre() + ". Vida enemigo: " + objetivo.getVida();
        this.notificar(); 
    }

    public void robarHabilidad(Entidad objetivo) {
        if (objetivo.estaVivo()) {
            this.ultimoMensaje = "Todavía no podés robar la habilidad: " + objetivo.getNombre() + " está vivo.";
            this.notificar();
            return;
        }

        if (objetivo instanceof Robable) {
            Robable robable = (Robable) objetivo;
            Habilidad nuevaHabilidad = robable.obtenerHabilidad();
            this.agregarHabilidad(nuevaHabilidad);
            this.ultimoMensaje = "¡Derrotaste a " + objetivo.getNombre() + " y le robaste: " + nuevaHabilidad.getNombreHabilidad() + "!";
        } else {
            this.ultimoMensaje = "El objetivo no tiene habilidades para robar.";
        }
        this.notificar(); 
    }

    public void usarHabilidad(int indice, Entidad objetivo) {
        if (indice >= 0 && indice < getHabilidades().size()) {
            Habilidad h = getHabilidades().get(indice);
            h.ejecutarHabilidad(this, objetivo);
            this.ultimoMensaje = "Usaste " + h.getNombreHabilidad() + " contra " + objetivo.getNombre();
        } else {
            this.ultimoMensaje = "No hay ninguna habilidad en esa posición.";
        }
        this.notificar(); 
    }
}