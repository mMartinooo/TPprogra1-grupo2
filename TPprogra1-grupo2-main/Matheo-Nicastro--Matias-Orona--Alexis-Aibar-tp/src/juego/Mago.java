package juego;

import java.awt.Image;
import java.awt.Color;
import java.util.ArrayList;
import entorno.Herramientas;
import entorno.Entorno;

public class Mago {
    // Imagen del mago
    public Image imagenMago;

    // Estadísticas del mago
    public int vidasMaximas = 100;
    public int energiaMagicaMaxima = 100;
    public int vidas;
    public int energiaMagica = 100;

    // Coordenadas
    public int x, y;

    // Velocidad de movimiento
    private int velocidad;

    // Estado de salto
    public boolean estaSaltando;

    // Entorno del juego
    private Entorno entorno;

    // Estado de invulnerabilidad
    public boolean invulnerable = false;
    private int framesInvul = 120;
    private int coolInvul = 0;

    // Dirección del mago
    public String direccion = "derecha";

    // Constantes para movimiento
    private static final int MARGEN_X = 30;
    private static final int MARGEN_Y = 40;
    private static final int LIMITE_DERECHO = 600 - 50;
    private static final int LIMITE_INFERIOR = 600 - 50;

    // Constantes para UI
    private static final int BARRA_X = 620;
    private static final int BARRA_Y = 400;
    private static final int VIDA_Y = 460;
    private static final int ANCHO_BARRA = 150;
    private static final int ALTO_BARRA = 20;

    // Constructor
    public Mago(int x, int y, Entorno entorno) {
        this.imagenMago = Herramientas.cargarImagen("img/mago.png").getScaledInstance(50, 40, Image.SCALE_SMOOTH);
        this.x = x;
        this.y = y;
        this.velocidad = 3;
        this.vidas = 100;
        this.entorno = entorno;
        this.estaSaltando = false;
    }

    // Método para reducir la vida del mago
    public void reducirVida() {
        if (vidas > 0 && !invulnerable) {
            vidas -= 20;
            invulnerable = true;
            coolInvul = 0;
        }
    }

    // Método para controlar los frames de invulnerabilidad
    public void invulnerabilidad() {
        if (invulnerable) {
            coolInvul++;
            if (coolInvul >= framesInvul) {
                invulnerable = false;
            }
        }
    }

    // Método para regenerar energía
    public void regenerarEnergia(boolean seleccionPotenciador) {
        if (!seleccionPotenciador && energiaMagica < energiaMagicaMaxima) {
            contadorRecuperacion++;
            if (contadorRecuperacion >= 15) {
                energiaMagica++;
                contadorRecuperacion = 0;
            }
        }
    }

    // Contador para regeneración de energía
    private int contadorRecuperacion = 0;

    // Dibuja la imagen del mago
    public void dibujar() {
        entorno.dibujarImagen(this.imagenMago, this.x, this.y, 0.0);
    }

    // Dibuja las barras de estado (vida y energía)
    public void dibujarBarrasEstado() {
        // Barra de energía
        entorno.dibujarRectangulo(BARRA_X + ANCHO_BARRA / 2, BARRA_Y + ALTO_BARRA / 2,
                ANCHO_BARRA, ALTO_BARRA, 0, Color.DARK_GRAY);
        double proporcionEnergia = energiaMagica / (double) energiaMagicaMaxima;
        int anchoRelleno = (int)(ANCHO_BARRA * proporcionEnergia);
        if (anchoRelleno > 0) {
            entorno.dibujarRectangulo(BARRA_X + anchoRelleno / 2, BARRA_Y + ALTO_BARRA / 2,
                    anchoRelleno, ALTO_BARRA, 0, Color.BLUE);
        }
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(energiaMagica + "/" + energiaMagicaMaxima,
                BARRA_X + 55, BARRA_Y + 15);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENERGIA", BARRA_X + 35, BARRA_Y - 5);

        // Barra de vida
        entorno.dibujarRectangulo(BARRA_X + ANCHO_BARRA / 2, VIDA_Y + ALTO_BARRA / 2,
                ANCHO_BARRA, ALTO_BARRA, 0, Color.DARK_GRAY);
        double proporcionVida = vidas / (double) vidasMaximas;
        int anchoRellenoVida = (int)(ANCHO_BARRA * proporcionVida);
        if (anchoRellenoVida > 0) {
            entorno.dibujarRectangulo(BARRA_X + anchoRellenoVida / 2, VIDA_Y + ALTO_BARRA / 2,
                    anchoRellenoVida, ALTO_BARRA, 0, Color.RED);
        }
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(vidas + "/" + vidasMaximas, BARRA_X + 55, VIDA_Y + 15);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("VIDA", BARRA_X + 45, VIDA_Y - 5);
    }

    // Maneja el movimiento del mago
    public void mover(Roca[] rocas, boolean seleccionPotenciador, boolean hechizoActivo) {
        if (hechizoActivo || seleccionPotenciador) {
            return;
        }
        int nuevaX = x;
        int nuevaY = y;
        if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) {
            nuevaX += velocidad;
            direccion = "derecha";
        }
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('a')) {
            nuevaX -= velocidad;
            direccion = "izquierda";
        }
        if (entorno.estaPresionada(entorno.TECLA_ARRIBA) || entorno.estaPresionada('w')) {
            nuevaY -= velocidad;
            direccion = "arriba";
        }
        if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('s')) {
            nuevaY += velocidad;
            direccion = "abajo";
        }

        boolean hayColision = false;
        for (Roca roca : rocas) {
            if (roca.colisionaCon(nuevaX, nuevaY)) {
                hayColision = true;
                break;
            }
        }

        if (!hayColision && nuevaX >= MARGEN_X && nuevaX <= LIMITE_DERECHO &&
                nuevaY >= MARGEN_Y && nuevaY <= entorno.alto() - MARGEN_Y) {
            x = nuevaX;
            y = nuevaY;
        }
    }

    // Maneja interacciones con pociones y potenciadores
    public void interactuarConObjetos(ArrayList<Objetos.Pocion> pociones, ArrayList<Objetos.Potenciador> potenciadores, boolean seleccionPotenciador) {
        if (!seleccionPotenciador) {
            for (Objetos.Pocion pocion : pociones) {
                if (pocion.colisionConMago(this)) {
                    vidas = Math.min(vidas + 30, vidasMaximas);
                    pocion.desactivar();
                }
            }
            for (Objetos.Potenciador potenciador : potenciadores) {
                if (potenciador.colisionConMago(this)) {
                    potenciador.desactivar();
                    return; // Indica que se activó la selección de potenciador
                }
            }
        }
    }

    // Maneja la selección de potenciadores
    public boolean manejarSeleccionPotenciador(boolean seleccionPotenciador) {
        if (seleccionPotenciador) {
            entorno.cambiarFont("Arial", 30, Color.WHITE);
            entorno.escribirTexto("Elige mejora:", 200, 250);
            entorno.escribirTexto("1 - Vida +10", 200, 280);
            entorno.escribirTexto("2 - Energía +10", 200, 310);
            if (entorno.sePresiono('1')) {
                vidasMaximas += 10;
                vidas = Math.min(vidas + 10, vidasMaximas);
                return false;
            } else if (entorno.sePresiono('2')) {
                energiaMagicaMaxima += 10;
                energiaMagica = Math.min(energiaMagica + 10, energiaMagicaMaxima);
                return false;
            }
        }
        return seleccionPotenciador;
    }

    // Resetea la posición del mago al centro
    public void resetearPosicion() {
        x = 400;
        y = 300;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}