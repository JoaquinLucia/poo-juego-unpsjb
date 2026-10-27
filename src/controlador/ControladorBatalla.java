package controlador;

import javax.swing.Timer;
import modelo.Batalla;
import modelo.Entidad;
import modelo.ObservadorBatalla;
import vista.VistaBatalla;

/*
 * Controla la pantalla de combate. Es un Observador de Batalla: no pregunta
 * "¿terminó?" ni "¿está aturdido?", Batalla le avisa y él lo refleja en la vista.
 * Los botones se traducen en pedidos a Batalla.
 */
public class ControladorBatalla implements ObservadorBatalla {

    private final VistaBatalla vista;
    private final Batalla batalla;
    private final ControladorMaestro controlador;

    private final int pausaEnemigoMs = 2500;    // para que se vea qué hizo cada uno
    private final int pausaFinalMs = 1500;     // para que se vea el resultado antes de cambiar de pantalla

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controlador) {
        this.vista = vista;
        this.batalla = batalla;
        this.controlador = controlador;

        batalla.suscribir(this);
        
        // Conectado con animación previa al ataque o habilidad del héroe
        vista.onAtacar(e -> ejecutarAtaqueHeroeConAnimacion());
        vista.onHabilidad(e -> usarHabilidadHeroeConAnimacion(0));
    }

    /** Muestra el estado inicial y arranca la pelea. */
    public void iniciar() {
        vista.mostrarFondo(batalla.getLugar().getRutaCombate());
        vista.mostrarRetratos(batalla.getHeroe().getSpriteFrontal(),
                            batalla.getEnemigo().getSpriteFrontal());
        
        // Carga el sprite inicial del enemigo en reposo (primer frame)
        String rutaSpriteEnemigo = obtenerRutaSpriteAtaque(batalla.getEnemigo().getNombre());
        vista.mostrarSpriteEnemigo(rutaSpriteEnemigo);

        // Carga el sprite inicial del héroe en reposo (primer frame)
        String rutaSpriteHeroe = obtenerRutaSpriteHeroe(batalla.getHeroe().getNombre());
        vista.mostrarSpriteHeroe(rutaSpriteHeroe);

        refrescarVidas();
        batalla.comenzar();   // llega turnoCambiado con el primer turno
    }

    /** Lo consulta el controlador maestro después de recibir BATALLA_TERMINADA. */
    public boolean gano() {
        return batalla.resultadoBatalla();
    }

    private void ejecutarAtaqueHeroeConAnimacion() {
        vista.setBotonesHabilitados(false);
        String rutaHeroe = obtenerRutaSpriteHeroe(batalla.getHeroe().getNombre());
        
        vista.animarAtaqueHeroe(rutaHeroe, () -> {
            batalla.ejecutarAtaqueHeroe();
        });
    }

    private void usarHabilidadHeroeConAnimacion(int indice) {
        if (getTurnosEnfriamiento(indice) > 0) {
            vista.mostrarEnfriamiento(getNombreHabilidad(indice), getTurnosEnfriamiento(indice));
            return;
        }

        vista.setBotonesHabilitados(false);
        String rutaHeroe = obtenerRutaSpriteHeroe(batalla.getHeroe().getNombre());

        vista.animarAtaqueHeroe(rutaHeroe, () -> {
            batalla.ejecutarHabilidadHeroe(indice);
        });
    }

    // ---------- Avisos de la batalla ----------

    @Override
    public void ataqueRealizado(Entidad atacante, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarAtaque(atacante.getNombre(), objetivo.getNombre(), danio);
        refrescarVidas();
    }

    @Override
    public void habilidadUsada(Entidad usuario, String habilidad, Entidad objetivo, int danio) {
        vista.efectoTemblor();
        vista.mostrarHabilidad(usuario.getNombre(), habilidad, objetivo.getNombre(), danio);
        refrescarVidas();
    }

    @Override
    public void turnoPerdido(Entidad aturdida) {
        vista.mostrarAturdido(aturdida.getNombre());
    }

    @Override
    public void turnoCambiado(Entidad aQuienLeToca) {
        refrescarVidas();   // un veneno pudo bajar vida al cerrar el turno anterior
        vista.mostrarTurno(aQuienLeToca.getNombre());
        
        if (aQuienLeToca == batalla.getEnemigo()) {
            vista.setBotonesHabilitados(false);
            
            String rutaSpriteEnemigo = obtenerRutaSpriteAtaque(batalla.getEnemigo().getNombre());
            
            despuesDe(pausaEnemigoMs, () -> {
                // Reproduce la animación del enemigo y al terminar ejecuta el golpe en el modelo
                vista.animarAtaqueEnemigo(rutaSpriteEnemigo, () -> {
                    batalla.ejecutarTurnoEnemigo();
                });
            });
        } else {
            vista.setBotonesHabilitados(true);   // si está aturdido, al tocar un botón pierde el turno
        }
    }

    @Override
    public void finDeBatalla(boolean victoria) {
        refrescarVidas();
        vista.setBotonesHabilitados(false);

        if (victoria) {
            // Si ganamos, reproducimos la animación de derrota del enemigo antes de cerrar
            String rutaDerrota = obtenerRutaSpriteDerrota(batalla.getEnemigo().getNombre());
            vista.animarDerrotaEnemigo(rutaDerrota, () -> {
                finalizarConRetraso(victoria);
            });
        } else {
            // Si perdimos (muere el héroe), reproducimos su animación de derrota antes de cerrar
            String rutaDerrotaHeroe = obtenerRutaSpriteDerrotaHeroe(batalla.getHeroe().getNombre());
            vista.animarDerrotaHeroe(rutaDerrotaHeroe, () -> {
                finalizarConRetraso(victoria);
            });
        }
    }

    private void finalizarConRetraso(boolean victoria) {
        vista.mostrarResultado(victoria);
        despuesDe(pausaFinalMs, () -> {
            batalla.desuscribir(this);   // ya no necesita avisos de esta batalla
            controlador.batallaTerminada();
        });
    }

    // ---------- Auxiliares ----------

    private String obtenerRutaSpriteAtaque(String nombreEnemigo) {
        return "/recursos/sprites/" + nombreEnemigo.toLowerCase() + "_ataque.png";
    }

    private String obtenerRutaSpriteHeroe(String nombreHeroe) {
        return "/recursos/sprites/" + nombreHeroe.toLowerCase() + "_ataque.png";
    }

    private String obtenerRutaSpriteDerrota(String nombreEnemigo) {
        return "/recursos/sprites/" + nombreEnemigo.toLowerCase() + "_derrota.png";
    }

    private String obtenerRutaSpriteDerrotaHeroe(String nombreHeroe) {
        return "/recursos/sprites/" + nombreHeroe.toLowerCase() + "_derrota.png";
    }

    private void refrescarVidas() {
        vista.actualizarEstadisticas(
                batalla.getHeroe().getNombre(), batalla.getHeroe().getVida(),
                batalla.getEnemigo().getNombre(), batalla.getEnemigo().getVida());
    }

    /** Ejecuta una acción una sola vez después de una pausa. */
    private void despuesDe(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }

    public String getNombreHabilidad(int indice) {
        return batalla.getHeroe().getHabilidad(indice).getNombreHabilidad();
    }

    public int getTurnosEnfriamiento(int indice) {
        return batalla.getHeroe().getHabilidad(indice).getCooldownActual();
    }
}