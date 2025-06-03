package juego;

import entorno.Herramientas;
import entorno.Entorno;
import java.awt.*;

public class MenuPrincipal {
    private Image fondo;
    private Image menuImage;
    private Image titulo;
    private Image imagenBotonMenu;
    private Image imagenBotonMenu2;
    private Image imgBotonRayo;
    private Image imgBotonCentella;
    private Image rayomenuseleccionado;
    private Image centellamenuseleccionado;
    private Botonazo botonMenu;
    private Boton botonRayo;
    private Boton botonCentella;
    private Entorno entorno;
    private boolean menu;
    private boolean juego;

    public MenuPrincipal(Entorno entorno) {
        this.entorno = entorno;
        this.menu = true;
        this.juego = false;

        // Carga y escala imágenes
        this.fondo = Herramientas.cargarImagen("img/gif.gif").getScaledInstance(800, 630, Image.SCALE_SMOOTH);
        this.menuImage = Herramientas.cargarImagen("img/menu.jpg").getScaledInstance(550, 805, Image.SCALE_SMOOTH);
        this.titulo = Herramientas.cargarImagen("img/title.png").getScaledInstance(600, 200, Image.SCALE_SMOOTH);
        this.imagenBotonMenu = Herramientas.cargarImagen("img/iniciar.png").getScaledInstance(150, 29, Image.SCALE_SMOOTH);
        this.imagenBotonMenu2 = Herramientas.cargarImagen("img/iniciarArriba.png").getScaledInstance(150, 29, Image.SCALE_SMOOTH);
        this.imgBotonRayo = Herramientas.cargarImagen("img/rayomenu.png").getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.imgBotonCentella = Herramientas.cargarImagen("img/centellamenu.png").getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.rayomenuseleccionado = Herramientas.cargarImagen("img/rayomenuseleccionado.png").getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.centellamenuseleccionado = Herramientas.cargarImagen("img/centellamenuseleccionado.png").getScaledInstance(400, 350, Image.SCALE_SMOOTH);

        // Inicializa botones
        this.botonMenu = new Botonazo(600, 200, 150, 29, entorno);
        this.botonRayo = new Boton(700, 50, 120, 40, imgBotonRayo, entorno);
        this.botonCentella = new Boton(700, 100, 120, 40, imgBotonCentella, entorno);
    }

    public void dibujarMenuPrincipal(int x, int y) {
        entorno.dibujarImagen(fondo, x, y, 0.0);
        entorno.dibujarImagen(titulo, 400, 90, 0.0);
        Image botonImagen = imagenBotonMenu;
        if (botonMenu.contienePunto(entorno.mouseX(), entorno.mouseY())) {
            botonImagen = imagenBotonMenu2;
        }
        botonMenu.dibujarBoton(botonImagen);
    }
    public void dibujarMenuDerrota(int x, int y) {
        entorno.dibujarImagen(fondo, x, y, 0.0);
    }

    public boolean manejarMenuPrincipal() {
        if (!menu) {
            return false;
        }
        dibujarMenuPrincipal(400, 300);
        if (botonMenu.contienePunto(entorno.mouseX(), entorno.mouseY()) &&
                entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            menu = false;
            juego = true;
            return true;
        }
        return false;
    }

    public void dibujarFondoUI(Mago mago, Hechizo rayo, Hechizo centella, int tiempoTranscurrido, int enemigosEliminados, int numeroOleada) {
        entorno.dibujarImagen(menuImage, entorno.ancho(), entorno.alto() / 2, 0.0, 0.75);
        entorno.dibujarImagen(imgBotonRayo, 700, 50, 0);
        entorno.dibujarImagen(imgBotonCentella, 700, 100, 0);
        if (rayo.seleccionado) {
            entorno.dibujarImagen(rayomenuseleccionado, 700, 50, 0.0);
        } else if (centella.seleccionado) {
            entorno.dibujarImagen(centellamenuseleccionado, 700, 100, 0.0);
        }
        mago.dibujarBarrasEstado();
        dibujarEstadisticasJuego(tiempoTranscurrido, enemigosEliminados, numeroOleada);
    }

    private void dibujarEstadisticasJuego(int tiempoTranscurrido, int enemigosEliminados, int numeroOleada) {
        entorno.cambiarFont("MV Boli", 30, Color.WHITE);
        entorno.escribirTexto(String.format("%d s", tiempoTranscurrido / 60), entorno.ancho() - 140, 550);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENEMIGOS", 650, 240);
        entorno.escribirTexto("ELIMINADOS", 645, 270);
        entorno.escribirTexto("" + enemigosEliminados, 700, 300);
        entorno.escribirTexto("OLEADA", 650, 150);
        entorno.escribirTexto("" + (numeroOleada + 1), 700, 180);
    }

    public Hechizo manejarEntradaHechizos(boolean seleccionPotenciador, Hechizo rayo, Hechizo centella, Mago mago) {
        if (!seleccionPotenciador && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            int mouseX = entorno.mouseX();
            int mouseY = entorno.mouseY();
            if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 50 - 20 && mouseY <= 50 + 20) {
                rayo.seleccionar();
                centella.deseleccionar();
                return null;
            } else if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 100 - 20 && mouseY <= 100 + 20) {
                centella.seleccionar();
                rayo.deseleccionar();
                return null;
            } else if (mouseX < entorno.ancho() - 200) {
                Hechizo hechizo = rayo.seleccionado ? rayo : (centella.seleccionado ? centella : null);
                if (hechizo != null) {
                    hechizo.lanzar(mago, mouseX, mouseY);
                    hechizo.deseleccionar();
                    return hechizo;
                }
            }
        }
        return null;
    }

    public boolean isMenu() {
        return menu;
    }

    public boolean isJuego() {
        return juego;
    }

    public void setJuego(boolean juego) {
        this.juego = juego;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }
}