package modelo;

public class EstrategiaTactica implements EstrategiaAtaque {

    @Override
    public Habilidad elegirAccion(Enemigo enemigoActual, Heroe heroeObjetivo) {
        // Lógica táctica: 
        // 1. Analizamos si el enemigo tiene múltiples habilidades para elegir con criterio.
        if (enemigoActual.getHabilidades().isEmpty()) {
            return null;
        }

        // 2. Buscamos una habilidad que esté lista para usarse (getCdListo() == true)
        // Pero a diferencia de la agresiva, podemos priorizar habilidades secundarias 
        // o evaluar el estado de la batalla.
        for (Habilidad h : enemigoActual.getHabilidades()) {
            if (h.getCdListo()) {
                // Si tiene más de una habilidad y la táctica lo requiere, 
                // podríamos evitar gastar la habilidad más fuerte si el héroe tiene poca vida, etc.
                return h;
            }
        }

        // Si ninguna habilidad está lista (todas en cooldown), devuelve null 
        // para que el sistema sepa que debe recurrir al ataque físico básico de la Entidad.
        return null;
    }
}