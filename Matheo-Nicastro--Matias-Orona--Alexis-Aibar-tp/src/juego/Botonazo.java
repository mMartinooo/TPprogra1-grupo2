package juego;
import java.awt.Image;

import entorno.Entorno;

public class Botonazo {
	private Entorno entorno;
	private double x;
	private double y;
	public int ancho;
	public int alto;
	//Ancho y largo de click y colision

public Botonazo(int x, int y, int ancho, int alto,Entorno entorno) {
    this.x = x;
    this.y = y;
    this.ancho = ancho;
    this.alto = alto;
    this.entorno = entorno;
}
public void dibujarBoton(Image imagen) {
	//centrar imagen
    entorno.dibujarImagen(imagen, this.x + this.ancho / 2, this.y + this.alto / 2 , 0.0);
}

public boolean contienePunto(int mouseX, int mouseY) {
    return mouseX >= this.x && mouseX <= (this.x + this.ancho) &&
           mouseY >= this.y && mouseY <= (this.y + this.alto);
}						

}