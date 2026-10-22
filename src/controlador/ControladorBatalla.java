package controlador;

import java.awt.Image;
import javax.swing.Timer;
import modelo.Batalla;
import modelo.Habilidad;
import vista.AnimacionSprite;
import vista.VistaBatalla;
import controlador.utilidades.GestorDeSonido;

/*
 * Controla la pantalla de combate. Las reglas (turnos, estados, cooldowns)
 * viven en modelo.Batalla; este controlador traduce los botones en pedidos
 * a Batalla y refleja en la vista lo que pasó.
 * Cuando la pelea termina, se lo avisa al mediador; el mediador pregunta gano().
 */
public class ControladorBatalla {

    private final VistaBatalla vista;
    private final Batalla batalla;
    
    private final ControladorMaestro controlador;

    private final int pausaEnemigoMs = 800;    // para que se vea qué hizo cada uno
    private final int pausaFinalMs = 1500;     // para que se vea el resultado antes de cambiar de pantalla
    private boolean terminada = false;

    public ControladorBatalla(VistaBatalla vista, Batalla batalla, ControladorMaestro controlador) {
        this.vista = vista;
        this.batalla = batalla;
        this.controlador = controlador;
        vista.onAtacar(e -> atacar());
        vista.onHabilidad(e -> abrirMenuHabilidades());
        vista.onDefenderse(e -> defenderse());
    }

    /** Muestra el estado inicial de la pelea. */
    public void iniciar() {
        vista.actualizarRetratos(batalla.getHeroe().getSpriteFrontal(), batalla.getEnemigo().getSpriteFrontal());
        
        // Extraemos el primer fotograma (0) para usarlo de postura de reposo
        String nombre = getNombreHeroe().toLowerCase();
        String ruta = "/recursos/sprites/arquero_ataque.png"; 
        
        // Ahora chequea el nombre real del personaje
        if (nombre.contains("artorias") || nombre.contains("caballero")) ruta = "/recursos/sprites/caballero_ataque.png";
        else if (nombre.contains("merlin") || nombre.contains("mago")) ruta = "/recursos/sprites/mago_ataque.png";
        
        java.awt.Image[] frames = vista.recortarSpriteSheet(ruta, 4);
        if (frames != null && frames.length > 0) {
            vista.setSpriteHeroeBatalla(frames[0]);
        }
        
        actualizar();
    }

    /** Lo consulta el mediador después de recibir BATALLA_TERMINADA. */
    public boolean gano() {
        return batalla.resultadoBatalla();
    }

    // ---------- Acciones del héroe ----------
    private boolean puedeActuar() {
        return batalla.getTurnoActual() == Batalla.Turno.HEROE && !batalla.batallaTerminada();
    }

    private void atacar() {
        try {
            if (!puedeActuar()) return;

            try {
                    GestorDeSonido.getInstancia().reproducirEfecto("recursos/sonidos/sonidobotonesop.wav");
            } catch (Throwable t) {}
            
            String nombre = getNombreHeroe().toLowerCase();
            String rutaAtaque = "/recursos/sprites/arquero_ataque.png"; 
            if (nombre.contains("artorias") || nombre.contains("caballero")) rutaAtaque = "/recursos/sprites/caballero_ataque.png";
            else if (nombre.contains("merlin") || nombre.contains("mago")) rutaAtaque = "/recursos/sprites/mago_ataque.png";

            java.awt.Image[] framesAtaque = vista.recortarSpriteSheet(rutaAtaque, 4);
            if (framesAtaque != null) {
                vista.reproducirAnimacion(new vista.AnimacionSprite(framesAtaque, 120, null));
            }

            // ... (el resto del método atacar queda igual que como lo tenías)
            boolean aturdido = batalla.heroeAturdido();
            int hpEnemigoAntes = batalla.getEnemigo().getVida();
            batalla.ejecutarAtaqueHeroe(); 
            int danio = hpEnemigoAntes - batalla.getEnemigo().getVida();

            if (!aturdido) {
                vista.mostrarResultado(getNombreHeroe() + " asestó un golpe básico (" + Math.max(0, danio) + " daño)");
                vista.efectoTemblor(); 
            }
            actualizar();
            
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
private void usarHabilidad(int indice) {
        if (!puedeActuar()) return;

        boolean aturdido = batalla.heroeAturdido();
        if (!aturdido && !batalla.getHeroe().puedeUsarHabilidad(indice)) {
            vista.mostrarResultado("¡La habilidad aún se está recargando!");
            return;   
        }
        
        // Calculamos la vida de ambos por si es un ataque o una curación
        int hpEnemigoAntes = batalla.getEnemigo().getVida();
        int hpHeroeAntes = batalla.getHeroe().getVida();
        
        batalla.ejecutarHabilidadHeroe(indice);
        
        int danio = hpEnemigoAntes - batalla.getEnemigo().getVida();
        int cura = batalla.getHeroe().getVida() - hpHeroeAntes;
        
        if (!aturdido) {
            vista.efectoTemblor();
            if (cura > 0) {
                vista.mostrarResultado(getNombreHeroe() + " usó " + getNombreHabilidad(indice) + " (Curó " + cura + " HP)");
            } else {
                vista.mostrarResultado(getNombreHeroe() + " usó " + getNombreHabilidad(indice) + " (" + Math.max(0, danio) + " daño)");
            }
        }
        actualizar();
    }

    private void defenderse() {
        if (!puedeActuar()) return;

        boolean aturdido = batalla.heroeAturdido();
        batalla.ejecutarDefensaHeroe();
        
        if (!aturdido) {
            vista.mostrarResultado(getNombreHeroe() + " adopta una postura defensiva.");
        }
        actualizar();
    }

    // ---------- Pantalla  ----------
    /** Refleja el estado de la batalla y decide qué sigue. */
    private void actualizar() {
        vista.actualizarEstadisticas(
                getNombreHeroe(), batalla.getHeroe().getVida(),
                getNombreEnemigo(), batalla.getEnemigo().getVida());
 
        if (batalla.batallaTerminada()) {
            terminar();
        } else if (batalla.getTurnoActual() == Batalla.Turno.ENEMIGO) {
            vista.mostrarResultado("Turno de " + getNombreEnemigo());
            vista.setBotonesHabilitados(false);
            programarTurnoEnemigo();
        } else {
            vista.setBotonesHabilitados(true);
        }
    }

    /** El enemigo juega después de una pausa. */
    private void programarTurnoEnemigo() {
        Timer espera = new Timer(pausaEnemigoMs, e -> {
            
            // Verificamos si está aturdido antes de que intente actuar, antes atacaba aturdido
            boolean aturdido = !batalla.getEnemigo().puedeActuar();
            
            // Averiguamos qué va a usar
            String accion = "un ataque básico";
            if (!aturdido) {
                for (int i = 0; i < batalla.getEnemigo().getCantidadHabilidades(); i++) {
                    if (batalla.getEnemigo().puedeUsarHabilidad(i)) {
                        accion = batalla.getEnemigo().getHabilidad(i).getNombreHabilidad();
                        break;
                    }
                }
            }

            // Guardamos la vida de ambos
            int hpAntesHeroe = batalla.getHeroe().getVida();
            int hpAntesEnemigo = batalla.getEnemigo().getVida();
            
            batalla.ejecutarTurnoEnemigo();
            int danio = hpAntesHeroe - batalla.getHeroe().getVida();
            int cura = batalla.getEnemigo().getVida() - hpAntesEnemigo;

            // Mostramos el mensaje
            if (aturdido) {
                vista.mostrarResultado(getNombreEnemigo() + " está aturdido y pierde su turno.");
            } else {
                vista.efectoTemblor();
                if (cura > 0) {
                    vista.mostrarResultado(getNombreEnemigo() + " usó " + accion + " (Curó " + cura + " HP)");
                } else {
                    vista.mostrarResultado(getNombreEnemigo() + " usó " + accion + " (" + Math.max(0, danio) + " daño)");
                }
            }
            
            actualizar();
        });

        espera.setRepeats(false);
        espera.start();
    }

    /** Muestra el resultado y, tras una pausa, le avisa al mediador (una sola vez). */
 private void terminar() {
        if (terminada) { return; }
        terminada = true;
        
        vista.setBotonesHabilitados(false);
        vista.mostrarResultado(gano() ? "¡Victoria!" : "Derrota..."); 
        
        if (gano()) {
            // Buscamos una habilidad del enemigo que el héroe NO tenga
            modelo.Habilidad nueva = null;
            for (modelo.Habilidad hEnemigo : batalla.getEnemigo().getHabilidades()) {
                boolean repetida = false;
                for (modelo.Habilidad hHeroe : batalla.getHeroe().getHabilidades()) {
                    if (hHeroe.getNombreHabilidad().equals(hEnemigo.getNombreHabilidad())) {
                        repetida = true;
                        break;
                    }
                }
                if (!repetida) {
                    nueva = hEnemigo;
                    break;
                }
            }
            
            // Si por casualidad todas son repetidas, le damos la primera por defecto
            if (nueva == null) {
                nueva = batalla.getEnemigo().getHabilidad(0);
            }
            
            // CLAVE: Reseteamos el CD y los efectos por si el enemigo la usó antes de morir
            nueva.reiniciarCooldown(); 
            if (nueva instanceof modelo.EfectoPorTurno) {
                ((modelo.EfectoPorTurno) nueva).limpiarEfecto();
            }     
            
            int cantHabilidades = batalla.getHeroe().getCantidadHabilidades();
            Object[] opciones;
            
            if (cantHabilidades < 3) {
                opciones = new Object[]{"Aprender Habilidad", "Ignorar"};
            } else {
                opciones = new Object[]{
                    "Reemplazar: " + getNombreHabilidad(0),
                    "Reemplazar: " + getNombreHabilidad(1),
                    "Reemplazar: " + getNombreHabilidad(2),
                    "Ignorar"
                };
            }

            int eleccion = vista.mostrarBotin(getNombreEnemigo(), nueva, opciones);

            if (cantHabilidades < 3) {
                if (eleccion == 0) batalla.getHeroe().agregarHabilidad(nueva);
            } else {
                if (eleccion == 0) batalla.getHeroe().reemplazarHabilidad(0, nueva);
                else if (eleccion == 1) batalla.getHeroe().reemplazarHabilidad(1, nueva);
                else if (eleccion == 2) batalla.getHeroe().reemplazarHabilidad(2, nueva);
            }
        }
 
        despuesDe(pausaFinalMs, () -> controlador.batallaTerminada());
    }

    /** Ejecuta una acción una sola vez después de una pausa. */
    private void despuesDe(int ms, Runnable accion) {
        Timer timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }

private void abrirMenuHabilidades() {
        if (!puedeActuar()) return;
        
        int cant = batalla.getHeroe().getCantidadHabilidades();
        
        if (cant == 1) {
            usarHabilidad(0);
        } else if (cant > 1) {
            java.util.List<String> nombres = new java.util.ArrayList<>();
            for (int i = 0; i < cant; i++) {
                modelo.Habilidad hab = batalla.getHeroe().getHabilidad(i);
                String texto = hab.getNombreHabilidad();
                
                // Si la habilidad no está lista, le agregamos el aviso visual
                if (!hab.getCdListo()) {
                    texto += " (Espera: " + hab.getCooldownActual() + ")";
                }
                nombres.add(texto);
            }
            vista.mostrarMenuHabilidades(nombres, index -> usarHabilidad(index));
        }
    }

    ///////// GET  
    public String getNombreHeroe(){
        return batalla.getHeroe().getNombre();
    }
    public String getNombreEnemigo(){
        return batalla.getEnemigo().getNombre();
    }
    public String getNombreHabilidad(int indice){
        return batalla.getHeroe().getHabilidad(indice).getNombreHabilidad();
    }
}