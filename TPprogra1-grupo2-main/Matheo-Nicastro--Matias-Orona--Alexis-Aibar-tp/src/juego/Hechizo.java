package juego;

import entorno.Entorno;
import java.awt.Image;
import java.util.ArrayList;

public class Hechizo {
    String nombre;
    int costoMagia;
    int area;
    boolean seleccionado;
    private double hechizoX;
    private double hechizoY;
    private int hechizoFrames;
    private double rayoVelX;
    private double rayoVelY;
    private double rayoAngulo;
    private Image imgHechizoLanzado;
    private Entorno entorno;

    public Hechizo(String nombre, int costoMagia, int area, Image imgHechizoLanzado, Entorno entorno) {
        this.nombre = nombre;
        this.costoMagia = costoMagia;
        this.area = area;
        this.seleccionado = false;
        this.hechizoX = 0;
        this.hechizoY = 0;
        this.hechizoFrames = 0;
        this.imgHechizoLanzado = imgHechizoLanzado;
        this.entorno = entorno;
    }

    public void seleccionar() {
        this.seleccionado = true;
    }

    public void deseleccionar() {
        this.seleccionado = false;
    }

    public boolean estaActivo() {
        return hechizoFrames > 0;
    }

    public void lanzar(Mago mago, double targetX, double targetY) {
        if (mago.energiaMagica < costoMagia) {
            // Nota: Asegúrate de que mago.energiaMagica se inicialice con un valor suficiente en la clase Mago
            return;
        }
        mago.energiaMagica -= costoMagia;
        hechizoFrames = 20;

        if (nombre.equals("RAYO")) {
            configurarHechizoRayo(mago, targetX, targetY);
        } else if (nombre.equals("CENTELLA")) {
            configurarHechizoCentella(mago);
        }
    }

    private void configurarHechizoRayo(Mago mago, double targetX, double targetY) {
        hechizoX = mago.x;
        hechizoY = mago.y;
        double dx = targetX - mago.x;
        double dy = targetY - mago.y;
        rayoAngulo = Math.atan2(dy, dx);
        double velocidad = 10.0;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia > 0) {
            rayoVelX = (dx / distancia) * velocidad;
            rayoVelY = (dy / distancia) * velocidad;
        } else {
            rayoVelX = 0;
            rayoVelY = 0;
        }
    }

    private void configurarHechizoCentella(Mago mago) {
        hechizoX = mago.x;
        hechizoY = mago.y;
    }

    public void actualizar(ArrayList<Murcielago> murcielagos, ArrayList<Murcielago> eliminados) {
        if (hechizoFrames <= 0) {
            return;
        }

        if (nombre.equals("RAYO")) {
            actualizarRayo(murcielagos, eliminados);
        } else if (nombre.equals("CENTELLA")) {
            actualizarCentella(murcielagos, eliminados);
        }

        hechizoFrames--;
    }

    private void actualizarRayo(ArrayList<Murcielago> murcielagos, ArrayList<Murcielago> eliminados) {
        hechizoX += rayoVelX;
        hechizoY += rayoVelY;
        double rayoAncho = 60, rayoAlto = 30;
        for (Murcielago m : murcielagos) {
            if (Math.abs(m.x - hechizoX) <= rayoAncho / 2 && Math.abs(m.y - hechizoY) <= rayoAlto / 2) {
                m.vida -= area;
                if (m.vida <= 0) {
                    eliminados.add(m);
                }
            }
        }
        if (hechizoX < 0 || hechizoX > entorno.ancho() - 200 || hechizoY < 0 || hechizoY > entorno.alto()) {
            hechizoFrames = 0;
        }
    }

    private void actualizarCentella(ArrayList<Murcielago> murcielagos, ArrayList<Murcielago> eliminados) {
        int radio = 60;
        for (Murcielago m : murcielagos) {
            double distancia = Math.sqrt(Math.pow(m.x - hechizoX, 2) + Math.pow(m.y - hechizoY, 2));
            if (distancia <= radio) {
                m.vida -= area;
                if (m.vida <= 0) {
                    eliminados.add(m);
                }
            }
        }
    }

    public void dibujar() {
        if (hechizoFrames <= 0) {
            return;
        }
        if (nombre.equals("RAYO")) {
            entorno.dibujarImagen(imgHechizoLanzado, hechizoX, hechizoY, rayoAngulo);
        } else if (nombre.equals("CENTELLA")) {
            entorno.dibujarImagen(imgHechizoLanzado, hechizoX, hechizoY, 0.0);
        }
    }
}