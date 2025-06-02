package juego;

import entorno.Herramientas;
import entorno.Entorno;
import java.awt.*;

public class MenuPrincipal {
	private Image fondo;
	private Entorno entorno;
	private Boton boton;
	
public MenuPrincipal(Entorno entorno) {

	    this.fondo = Herramientas.cargarImagen("img/gif.gif").getScaledInstance(800, 630, Image.SCALE_SMOOTH);
        this.entorno = entorno;
	}

public void dibujarMenuPrincipal(int x,int y) {
    entorno.dibujarImagen(this.fondo, x, y, 0.0);
}
}
