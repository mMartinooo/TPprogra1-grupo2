package juego;

public class Hechizo {
    String nombre;
    int costoMagia;
    int area;
    boolean seleccionado;

    public Hechizo(String nombre, int costoMagia, int area) {
        this.nombre = nombre;
        this.costoMagia = costoMagia;
        this.area = area;
        this.seleccionado = false;
    }

    public void seleccionar() {
        this.seleccionado = true;
    }

    public void deseleccionar() {
        this.seleccionado = false;
    }
}

