package mvc.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModeloTablaPuntuacion {
    private final List<ModeloPuntuacion> puntuaciones = new ArrayList<>();
 
    public void agregar(ModeloPuntuacion puntuacion) {
        puntuaciones.add(puntuacion);
    }
 
    // Devuelve una copia ordenada de mayor a menor puntaje
    public List<ModeloPuntuacion> getPuntuacionesOrdenadas() {
        List<ModeloPuntuacion> copia = new ArrayList<>(puntuaciones);
        copia.sort(Comparator.comparingInt(ModeloPuntuacion::getPuntos).reversed());
        return copia;
    }
}
