package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Murcielago {
    public double x, y;
    public int vida; // Campo de vida
    private Entorno entorno;
    private Image imagen;
    private double velocidad = 2.2;

    public Murcielago(Entorno entorno) {
        this.entorno = entorno;
        this.vida = 20; // Vida por defecto
        this.imagen = Herramientas.cargarImagen("img/murcielago6.png").getScaledInstance(50, 40, Image.SCALE_SMOOTH);
    }

    public Murcielago(Entorno entorno, int vidaInicial) {
        this.entorno = entorno;
        this.vida = vidaInicial; // Vida inicial personalizada
        this.imagen = Herramientas.cargarImagen("img/murcielago6.png").getScaledInstance(50, 40, Image.SCALE_SMOOTH);
    }

    public void seguirMago(Mago mago) {
        double dx = mago.x - x;
        double dy = mago.y - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia > 0) {
            x += (dx / distancia) * velocidad;
            y += (dy / distancia) * velocidad;
            int margen = 50;
            int limiteDerecho = 600 - margen;
            int limiteInferior = entorno.alto() - margen;
            x = Math.max(margen, Math.min(x, limiteDerecho));
            y = Math.max(margen, Math.min(y, limiteInferior));
        }
    }

    public void dibujar() {
        entorno.dibujarImagen(imagen, x, y, 0.0);
    }
}