package juego;

import java.awt.Image;
import entorno.Herramientas;
import entorno.Entorno;

public class Mago {

	// Imagen del mago
	public Image imagenMago;
	
	public int vidasMaximas = 100;
    public int energiaMagicaMaxima = 100;
    
	// Coordenadas
	public int x, y;

	// Velocidad de movimiento
	private int velocidad;

	// Estado de salto
	public boolean estaSaltando;

	// Entorno del juego
	private Entorno entorno;

	// Vida y energía
	public int vidas;
	public int energiaMagica = 100;

	// Estado de invulnerabilidad
	public boolean invulnerable = false;
	private int framesInvul = 120;
	public int coolInvul = 0;

	// Dirección del mago
	public String direccion = "derecha";

	// Constructor
	public Mago(int x, int y, Entorno entorno) {
		this.imagenMago = Herramientas.cargarImagen("img/mago.png").getScaledInstance(50, 40, Image.SCALE_SMOOTH);
		this.x = x;
		this.y = y;
		this.velocidad = 1;
		this.vidas = 100;
		this.entorno = entorno;
		this.estaSaltando = false;
	}

	// Método para reducir la vida del mago
	public void reducirVida() {
		if (vidas > 0 && invulnerable == false) {
			vidas = vidas - 20;
			invulnerable = true;
			coolInvul = 0;
		}
	}

	// Método para controlar los frames de invulnerabilidad
	public void invulnerabilidad() {
		if (invulnerable == true) {
			coolInvul++;
			if (coolInvul >= framesInvul) {
				invulnerable = false;
			}
		}
	}

	// Dibuja la imagen del mago
	public void dibujar() {
	        entorno.dibujarImagen(this.imagenMago, this.x, this.y, 0.0);
	      
	    }

	
	

	// Movimiento hacia la derecha
	public void moverDerecha() {
		if (this.x + velocidad < entorno.ancho() - 200) {
			this.x += velocidad;
			this.direccion = "derecha";
		}
	}

	// Movimiento hacia la izquierda
	public void moverIzquierda() {
		if (this.x - velocidad > 0) {
			this.x -= velocidad;
			this.direccion = "izquierda";
		}
	}

	// Movimiento hacia arriba
	public void moverArriba() {
		if (this.y - velocidad > 0) {
			this.y -= velocidad;
		}
	}

	// Movimiento hacia abajo
	public void moverAbajo() {
		if (this.y + velocidad < entorno.alto()) {
			this.y += velocidad;
		}
	}

	// Movimiento vertical (salto o caída)
	public void movVertical() {
		if (estaSaltando) {
			this.y -= 10; // Salto hacia arriba
		} else {
			this.y += 10; // Simulación de caída
		}
	}

	// Getters
	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
}
