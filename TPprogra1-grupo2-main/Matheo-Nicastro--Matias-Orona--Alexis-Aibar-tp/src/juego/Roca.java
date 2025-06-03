package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Roca {
    private int x, y;
    private Image imagen;
    

    public Roca(int x, int y) {
        this.x = x;
        this.y = y;
        this.imagen = Herramientas.cargarImagen("img/roca.png").getScaledInstance(40, 40, Image.SCALE_SMOOTH);
    }
    

    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, 0);
    }

    public boolean colisionaCon(int x, int y) {
        return Math.abs(this.x - x) < 30 && Math.abs(this.y - y) < 30;
    }



    public int getX() { return x; }
    public int getY() { return y; }
}