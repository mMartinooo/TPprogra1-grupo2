package juego;

import entorno.Entorno;
import java.awt.Image;

public class Boton {
    private int x, y, ancho, alto;
    private Image imagen;
    private boolean seleccionado;
    private Entorno entorno;

    public Boton(int x, int y, int ancho, int alto, Image imagen, Entorno entorno) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.imagen = imagen;
        this.entorno = entorno;
        this.seleccionado = false;
    }

    public void dibujar() {
        entorno.dibujarImagen(imagen, x + ancho / 2, y + alto / 2, 0, 1.0);
        // Resaltar si está seleccionado
        if (seleccionado) {
            entorno.dibujarRectangulo(x + ancho / 2, y + alto / 2, ancho + 4, alto + 4, 0, new java.awt.Color(255, 215, 0)); // Marco dorado
        }
    }

    public boolean estaEnRango(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + ancho && mouseY >= y && mouseY <= y + alto;
    }

    public void seleccionar() {
        this.seleccionado = true;
    }

    public void deseleccionar() {
        this.seleccionado = false;
    }

    public boolean estaSeleccionado() {
        return seleccionado;
    }

	public boolean contienePunto(int mouseX, int mouseY) {
    return mouseX >= x - ancho / 2 &&
           mouseX <= x + ancho / 2 &&
           mouseY >= y - alto / 2 &&
           mouseY <= y + alto / 2;
}
}