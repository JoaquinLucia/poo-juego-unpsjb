// 1. Definimos la plantilla (clase)
class Auto {
    String marca;
    String modelo;
    int anio;
}

// 2. Definimos la clase principal que ejecutará el programa
public class Main {
    
    // 3. El método main: el punto de entrada que Java te está pidiendo
    public static void main(String[] args) {
        
        // Todo el código de ejecución debe ir aquí adentro
        Auto miAuto = new Auto();
        miAuto.marca = null;
        miAuto.modelo = "";
        miAuto.anio = -3000;

        System.out.println("Marca: " + miAuto.marca);
        System.out.println("Modelo: " + miAuto.modelo);
        System.out.println("Año: " + miAuto.anio);
    }
}