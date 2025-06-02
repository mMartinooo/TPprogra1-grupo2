package juego;

import entorno.Entorno;

public class Mapa {
    private int numeroMapa; // Número del mapa actual (0 a 4 para 5 oleadas)
    private int ancho; // Ancho del área jugable
    private int alto; // Alto del área jugable
    private Entorno entorno; // Referencia al entorno del juego

    // Constructor
    public Mapa(int ancho, int alto, int numeroMapa, Entorno entorno) {
        this.ancho = ancho;
        this.alto = alto;
        this.numeroMapa = numeroMapa;
        this.entorno = entorno;
    }

    // Obtiene el número de mapa actual
    public int getNumeroMapa() {
        return numeroMapa;
    }

    // Establece el número de mapa
    public void setNumeroMapa(int num) {
        this.numeroMapa = num;
    }
}