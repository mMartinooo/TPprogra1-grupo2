package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
    // Constantes que definen parámetros del juego
    private static final int MARGEN = 50;
    private static final int[] MURCIELAGOS_POR_SPAWN = {3, 3, 3, 3, 4};
    private static final int[] INTERVALOS_SPAWN = {200, 180, 160, 140, 135};
    private static final int[] ENEMIGOS_POR_OLEADA = {15, 25, 35, 45, 50};

    // Variables de estado del juego
    private boolean juegoTerminado = false;
    private boolean juegoGanado = false;
    private boolean reset = false;
    private int numeroOleada = 0;
    private int murcielagosEliminadosOleada5 = 0;
    private int tiempoTranscurrido = 0;
    private int enemigosEliminados = 0;
    private int contadorSpawn = 0;
    private String mensajeOleada = null;
    private int framesMensajeOleada = 0;

    // Objetos principales del juego
    private Entorno entorno;
    private MenuPrincipal menuPrincipal;
    private Mago mago;
    private ArrayList<Murcielago> murcielagos;
    private Roca[] rocas;
    private Objetos objetos;
    private ArrayList<Objetos.Pocion> pociones;
    private ArrayList<Objetos.Potenciador> potenciadores;
    private boolean seleccionPotenciador = false;
    private Hechizo rayo;
    private Hechizo centella;
    private Hechizo hechizoLanzado = null;

    // Recursos gráficos
    private Image fondo;
    private Image fin;
    private Image ganaste;
    private Image imgRayoLanzado;
    private Image imgCentellaLanzado;

    public Juego() {
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo XX - v1", 800, 600);
        this.menuPrincipal = new MenuPrincipal(entorno);
        this.fondo = Herramientas.cargarImagen("img/fondo10.png").getScaledInstance(1050, 805, Image.SCALE_SMOOTH);
        this.fin = Herramientas.cargarImagen("img/seTermino.png");
        this.ganaste = Herramientas.cargarImagen("img/ganaste.png");
        this.imgRayoLanzado = Herramientas.cargarImagen("img/rayo.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.imgCentellaLanzado = Herramientas.cargarImagen("img/hechizoArea.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        this.rayo = new Hechizo("RAYO", 0, 150, imgRayoLanzado, entorno);
        this.centella = new Hechizo("CENTELLA", 50, 80, imgCentellaLanzado, entorno);
        this.mago = new Mago(400, 300, entorno);
        this.murcielagos = new ArrayList<>();
        this.objetos = new Objetos(entorno);
        this.pociones = new ArrayList<>();
        this.potenciadores = new ArrayList<>();
        this.rocas = new Roca[] {
                new Roca(200, 150), new Roca(550, 200), new Roca(250, 450),
                new Roca(450, 150), new Roca(150, 350)
        };
        this.entorno.iniciar();
    }

    private void generarMurcielagosOleada(int cant) {
        int murcielagosGenerados = 0;
        while (murcielagosGenerados < cant) {
            Murcielago nuevoMurcielago;
            boolean superpuesto;
            boolean demasiadoCerca;
            int intentos = 0;
            do {
                int vidaInicial = (numeroOleada == 3) ? 35 : (numeroOleada == 4) ? 140 : 20;
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0:
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1:
                        nuevoMurcielago.x = 600 - MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                    case 2:
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = 600 - MARGEN;
                        break;
                    case 3:
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                }
                superpuesto = estaMurcielagoSuperpuesto(nuevoMurcielago);
                demasiadoCerca = estaMurcielagoCercaDeMago(nuevoMurcielago);
                intentos++;
            } while ((superpuesto || demasiadoCerca) && intentos < 50);
            if (!superpuesto && !demasiadoCerca) {
                murcielagos.add(nuevoMurcielago);
                murcielagosGenerados++;
            }
        }
    }

    private void generarMurcielagos() {
        if (murcielagos.size() >= 10) {
            return;
        }
        int murcielagosAGenerar = Math.min(MURCIELAGOS_POR_SPAWN[numeroOleada], 10 - murcielagos.size());
        if (murcielagosAGenerar > 0) {
            generarMurcielagosOleada(murcielagosAGenerar);
        }
    }

    private boolean estaMurcielagoSuperpuesto(Murcielago nuevo) {
        for (Murcielago murci : murcielagos) {
            if (Math.abs(nuevo.x - murci.x) < 50 && Math.abs(nuevo.y - murci.y) < 50) {
                return true;
            }
        }
        return false;
    }

    private boolean estaMurcielagoCercaDeMago(Murcielago nuevo) {
        double distanciaAlMago = Math.sqrt(
                Math.pow(nuevo.x - mago.getX(), 2) + Math.pow(nuevo.y - mago.getY(), 2)
        );
        return distanciaAlMago < 100;
    }

    private void mantenerDistanciaMurcielagos() {
        for (Murcielago murci : murcielagos) {
            for (Murcielago otroMurci : murcielagos) {
                double dx = murci.x - otroMurci.x;
                double dy = murci.y - otroMurci.y;
                double distancia = Math.sqrt(dx * dx + dy * dy);
                if (distancia < 50 && distancia > 0) {
                    murci.x += dx / distancia * 1.5;
                    murci.y += dy / distancia * 1.5;
                }
            }
        }
    }

    private void soltarObjetosAlMorir(Murcielago m) {
        if (mago.vidas <= 80 && Math.random() < 0.05) {
            Objetos.Pocion nuevaPocion = objetos.new Pocion((int)m.x, (int)m.y, objetos);
            if (nuevaPocion.x >= 600) {
                nuevaPocion.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            pociones.add(nuevaPocion);
        } else if (Math.random() < 0.02) {
            Objetos.Potenciador nuevoPotenciador = objetos.new Potenciador((int)m.x, (int)m.y, objetos);
            if (nuevoPotenciador.x >= 600) {
                nuevoPotenciador.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            potenciadores.add(nuevoPotenciador);
        }
        enemigosEliminados++;
        if (numeroOleada == 4) {
            murcielagosEliminadosOleada5++;
        }
    }

    private void reiniciarJuego() {
        juegoTerminado = false;
        juegoGanado = false;
        mago = new Mago(400, 300, entorno);
        murcielagos.clear();
        tiempoTranscurrido = 0;
        enemigosEliminados = 0;
        murcielagosEliminadosOleada5 = 0;
        hechizoLanzado = null;
        rayo.deseleccionar();
        centella.deseleccionar();
        pociones.clear();
        potenciadores.clear();
        seleccionPotenciador = false;
        numeroOleada = 0;
        mensajeOleada = null;
        framesMensajeOleada = 0;
        contadorSpawn = 0;
        menuPrincipal.setMenu(true);
        menuPrincipal.setJuego(false);
    }

    private void dibujarMensajeOleada() {
        if (mensajeOleada != null && framesMensajeOleada > 0) {
            entorno.cambiarFont("Arial", 40, Color.YELLOW);
            entorno.escribirTexto(mensajeOleada, 200, 300);
            framesMensajeOleada--;
            if (framesMensajeOleada == 0) {
                mensajeOleada = null;
            }
        }
    }

    @Override
    public void tick() {
        if (menuPrincipal.isMenu()) {
            if (menuPrincipal.manejarMenuPrincipal()) {
                menuPrincipal.setMenu(false);
                menuPrincipal.setJuego(true);
                mensajeOleada = "Oleada 1";
                framesMensajeOleada = 100;
                generarMurcielagos();
                contadorSpawn = 0;
            }
        } else if (menuPrincipal.isJuego()) {
            if (juegoGanado) {
                manejarPantallaVictoria();
            } else if (juegoTerminado) {
                manejarPantallaDerrota();
            } else {
                manejarJuego();
            }
        }

        if (menuPrincipal.isJuego() && !juegoTerminado && !juegoGanado) {
            contadorSpawn++;
            if (contadorSpawn >= INTERVALOS_SPAWN[numeroOleada]) {
                if (numeroOleada < 4 && enemigosEliminados < ENEMIGOS_POR_OLEADA[numeroOleada] ||
                    numeroOleada == 4 && murcielagosEliminadosOleada5 < ENEMIGOS_POR_OLEADA[4]) {
                    generarMurcielagos();
                }
                contadorSpawn = 0;
            }

            if (numeroOleada == 0 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[0] && murcielagos.size() == 0) {
                numeroOleada = 1;
                mensajeOleada = "Oleada 2";
                framesMensajeOleada = 100;
                contadorSpawn = 0;
                mago.resetearPosicion();
                enemigosEliminados = 0;
            } else if (numeroOleada == 1 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[1] && murcielagos.size() == 0) {
                numeroOleada = 2;
                mensajeOleada = "Oleada 3";
                framesMensajeOleada = 100;
                contadorSpawn = 0;
                mago.resetearPosicion();
                enemigosEliminados = 0;
            } else if (numeroOleada == 2 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[2] && murcielagos.size() == 0) {
                numeroOleada = 3;
                mensajeOleada = "Oleada 4";
                framesMensajeOleada = 100;
                contadorSpawn = 0;
                mago.resetearPosicion();
                enemigosEliminados = 0;
            } else if (numeroOleada == 3 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[3] && murcielagos.size() == 0) {
                numeroOleada = 4;
                mensajeOleada = "Oleada 5";
                framesMensajeOleada = 100;
                contadorSpawn = 0;
                mago.resetearPosicion();
                enemigosEliminados = 0;
            } else if (numeroOleada == 4 && murcielagosEliminadosOleada5 >= ENEMIGOS_POR_OLEADA[4] && murcielagos.size() == 0) {
                juegoGanado = true;
            }

            if (mago.vidas <= 0) {
                juegoTerminado = true;
            }
        }

        if ((juegoTerminado || juegoGanado) && entorno.sePresiono('r')) {
            reset = true;
        }
        if (reset) {
            reiniciarJuego();
            reset = false;
        }
    }

    private void manejarPantallaVictoria() {
        menuPrincipal.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(ganaste, 400, 300, 0.0);
    }

    private void manejarPantallaDerrota() {
        menuPrincipal.dibujarMenuDerrota(400, 300);
        entorno.dibujarImagen(fin, 400, 300, 0.0);
    }

    private void manejarJuego() {
        tiempoTranscurrido++;
        entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0.0, 0.75);
        menuPrincipal.dibujarFondoUI(mago, rayo, centella, tiempoTranscurrido, enemigosEliminados, numeroOleada);
        
        // Actualizar hechizoLanzado con el resultado de manejarEntradaHechizos
        Hechizo hechizoNuevo = menuPrincipal.manejarEntradaHechizos(seleccionPotenciador, rayo, centella, mago);
        if (hechizoNuevo != null) {
            hechizoLanzado = hechizoNuevo;
        }

        if (hechizoLanzado != null) {
            ArrayList<Murcielago> eliminados = new ArrayList<>();
            hechizoLanzado.actualizar(murcielagos, eliminados);
            for (Murcielago m : eliminados) {
                soltarObjetosAlMorir(m);
            }
            murcielagos.removeAll(eliminados);
            enemigosEliminados += eliminados.size();
            if (hechizoLanzado.estaActivo()) {
                hechizoLanzado.dibujar();
            } else {
                hechizoLanzado = null;
            }
        }

        mago.mover(rocas, seleccionPotenciador, hechizoLanzado != null && hechizoLanzado.estaActivo());
        mago.interactuarConObjetos(pociones, potenciadores, seleccionPotenciador);
        seleccionPotenciador = mago.manejarSeleccionPotenciador(seleccionPotenciador);
        pociones.removeIf(pocion -> !pocion.activa);
        potenciadores.removeIf(potenciador -> !potenciador.activa);
        actualizarYDibujarEnemigos();
        mago.invulnerabilidad();
        mago.regenerarEnergia(seleccionPotenciador);
        dibujarObjetosJuego();
        dibujarMensajeOleada();
    }

    private void dibujarObjetosJuego() {
        for (Roca roca : rocas) {
            roca.dibujar(entorno);
        }
        for (Objetos.Pocion pocion : pociones) {
            pocion.dibujar();
        }
        for (Objetos.Potenciador potenciador : potenciadores) {
            potenciador.dibujar();
        }
        mago.dibujar();
    }

    private void actualizarYDibujarEnemigos() {
        if (seleccionPotenciador) {
            return;
        }
        mantenerDistanciaMurcielagos();
        for (int i = 0; i < murcielagos.size(); i++) {
            Murcielago murci = murcielagos.get(i);
            if (murci.x < 600) {
                murci.dibujar();
            }
            murci.seguirMago(mago);
            murci.x = Math.max(MARGEN, Math.min(murci.x, 600 - MARGEN));
            murci.y = Math.max(MARGEN, Math.min(murci.y, 600 - MARGEN));
            if (!mago.invulnerable) {
                double dx = murci.x - mago.getX();
                double dy = murci.y - mago.getY();
                if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                    mago.reducirVida();
                    murcielagos.remove(i);
                    i--;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}