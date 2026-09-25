package Controlador;

import Vista.VistaBatalla;
import modelo.Batalla;
import modelo.Enemigo;
import modelo.Heroe;

public class ControladorBatalla{

    private VistaBatalla vistaBatalla;
    private Batalla modeloBatalla;
    private Heroe modeloHeroe;
    private Enemigo modeloEnemigo;

    public ControladorBatalla(VistaBatalla vistaBatalla, Batalla modeloBatalla){
        this.vistaBatalla = vistaBatalla;
        this.modeloBatalla = modeloBatalla;
        this.modeloHeroe = modeloBatalla.getHeroe();     // se obtienen del modelo
        this.modeloEnemigo = modeloBatalla.getEnemigo();
        this.vistaBatalla.getBotonHeroe().addActionListener(evento -> this.atacaHeroe());
        this.vistaBatalla.getBotonEnemigo().addActionListener(evento -> this.atacaEnemigo());
        
    }

        private void atacaHeroe() {
        this.vistaBatalla.mostraeResultado(this.modeloHeroe.getNombre(), this.modeloHeroe.getAtaque());
        }

        private void atacaEnemigo() {
        vistaBatalla.mostraeResultado(modeloEnemigo.getNombre(), modeloEnemigo.getAtaque());
    
        }
}
    
