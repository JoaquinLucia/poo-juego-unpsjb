package modelo;

public class EstrategiaAgresiva implements EstrategiaAtaque {
    @Override
    public Habilidad elegirAccion(Enemigo enemigoActual, Heroe heroeObjetivo) {
        // Lógica simple: busca entre sus habilidades disponibles la primera que tenga el CD listo
        for (Habilidad h : enemigoActual.getHabilidades()) {
            if (h.getCdListo()) {
                return h;
            }
        }
        return null; // Si ninguna está lista, podría hacer ataque básico o null
    }
}