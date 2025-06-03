package juego;

import java.awt.Image;
import entorno.Herramientas;
import entorno.Entorno;

public class Objetos {
    private Entorno entorno;
    private Image pocionImg;
    private Image potenciadorImg;

    public Objetos(Entorno entorno) {
        this.entorno = entorno;
        // Carga y escala las imágenes
        this.pocionImg = Herramientas.cargarImagen("img/pocion.png").getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        this.potenciadorImg = Herramientas.cargarImagen("img/potenciador.png").getScaledInstance(200, 200, Image.SCALE_SMOOTH);
    }

    // Clase interna no estática para representar una poción
    public class Pocion {
        double x, y;
        boolean activa;
        private Objetos objetos; // Referencia a la clase externa

        public Pocion(int x, int y, Objetos objetos) {
            this.x = x; // Convierte int a double implícitamente
            this.y = y; // Convierte int a double implícitamente
            this.activa = true;
            this.objetos = objetos; // Almacena la referencia a Objetos
        }

        public void dibujar() {
            if (activa) {
                entorno.dibujarImagen(objetos.pocionImg, x, y, 0.0);
            }
        }

        public boolean colisionConMago(Mago mago) {
            int pixeles = 20;
            return activa && Math.abs(mago.x - x) < pixeles && Math.abs(mago.y - y) < pixeles;
        }

        public void desactivar() {
            activa = false;
        }
    }

    // Clase interna no estática para representar un potenciador
    public class Potenciador {
        double x, y;
        boolean activa;
        private Objetos objetos; // Referencia a la clase externa

        public Potenciador(int x, int y, Objetos objetos) {
            this.x = x; // Convierte int a double implícitamente
            this.y = y; // Convierte int a double implícitamente
            this.activa = true;
            this.objetos = objetos; // Almacena la referencia a Objetos
        }

        public void dibujar() {
            if (activa) {
                entorno.dibujarImagen(objetos.potenciadorImg, x, y, 0.0);
            }
        }

        public boolean colisionConMago(Mago mago) {
            int pixeles = 20;
            return activa && Math.abs(mago.x - x) < pixeles && Math.abs(mago.y - y) < pixeles;
        }

        public void desactivar() {
            activa = false;
        }
    }
}