package juego;

import java.awt.Image;
import java.awt.Color;
import java.util.ArrayList;

import entorno.Herramientas;
import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
    // Constantes del juego
    private static final int ANCHO_MENU = 200;
    private static final int MARGEN = 50;
    private static final int LIMITE_DERECHO = 600 - MARGEN;
    private static final int LIMITE_INFERIOR = 600 - MARGEN;
    private static final int DURACION_HECHIZO = 20;
    private static final int FRAMES_REGEN_ENERGIA = 15;
    private static final int DURACION_MENSAJE_OLEADA = 120;
    private static final int MAX_MURCIELAGOS_EN_PANTALLA = 10; // CAMBIO NUEVO: Máximo de murciélagos en pantalla

    // Configuración de spawns por oleada
    private static final int[] MURCIELAGOS_POR_SPAWN = {2, 2, 3, 4, 3}; // Cantidad por spawn en oleadas 1-5
    private static final int[] INTERVALOS_SPAWN = {200, 180, 160, 140, 130}; // Frames entre spawns en oleadas 1-5
    private static final int[] ENEMIGOS_POR_OLEADA = {5, 10, 10, 20, 30}; // Enemigos necesarios para pasar oleada

    // Variables de estado del juego
    private boolean menu = true;
    private boolean juego = false;
    private boolean juegoTerminado = false;
    private boolean juegoganado = false;
    private boolean reset = false;
    private int numeroOleada = 0;
    private int murcielagosEliminadosOleada5 = 0; // CAMBIO: Seguimiento de murciélagos eliminados en oleada 5
    private int tiempoTranscurrido = 0;
    private int enemigosEliminados = 0;
    private int contadorRecuperacion = 0;
    private int contadorSpawn = 0; // Contador para intervalo de spawn
    private String mensajeOleada = null;
    private int framesMensajeOleada = 0;

    // Objetos del juego
    private Entorno entorno;
    private MenuPrincipal menuPrincipalF;
    private Mago mago;
    private ArrayList<Murcielago> murcielagos;
    private Roca[] rocas;
    private Objetos objetos;
    private ArrayList<Objetos.Pocion> pociones;
    private ArrayList<Objetos.Potenciador> potenciadores;
    private boolean seleccionPotenciador = false;
    private Mapa mapeado;

    // Variables de hechizos
    private Hechizo hechizo1, hechizo2;
    private Hechizo hechizoLanzado = null;
    private double hechizoX = -1;
    private double hechizoY = -1;
    private int hechizoFrames = 0;
    private double rayoTargetX = -1;
    private double rayoTargetY = -1;
    private double rayoVelX;
    private double rayoVelY;
    private double rayoAngle;

    // Imágenes
    private Image fondo;
    private Image menuImage;
    private Image fin;
    private Image ganaste;
    private Image titulo;
    private Image imagenBotonMenu;
    private Image imagenBotonMenu2;
    private Image imgBotonRayo, imgBotonCentella;
    private Image rayomenuseleccionado, centellamenuseleccionado;
    private Image imgRayoLanzado;
    private Image imgCentellaLanzado;

    // Botones
    private Botonazo botonMenu;
    private Boton botonRayo, botonCentella;

    // Constructor
    public Juego() {
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo XX - v1", 800, 600);
        this.menuPrincipalF = new MenuPrincipal(entorno);
        this.botonMenu = new Botonazo(600, 200, 150, 29, entorno);
        this.mapeado = new Mapa(800, 600, 0, entorno);
        this.fin = Herramientas.cargarImagen("img/seTermino.png");
        this.ganaste = Herramientas.cargarImagen("img/ganaste.png");
        this.imagenBotonMenu = Herramientas.cargarImagen("img/iniciar.png")
                .getScaledInstance(botonMenu.ancho, botonMenu.alto, Image.SCALE_SMOOTH);
        this.imagenBotonMenu2 = Herramientas.cargarImagen("img/iniciarArriba.png")
                .getScaledInstance(botonMenu.ancho, botonMenu.alto, Image.SCALE_SMOOTH);
        this.titulo = Herramientas.cargarImagen("img/title.png")
                .getScaledInstance(600, 200, Image.SCALE_SMOOTH);
        this.fondo = Herramientas.cargarImagen("img/fondo10.png")
                .getScaledInstance(1050, 805, Image.SCALE_SMOOTH);
        this.menuImage = Herramientas.cargarImagen("img/menu.jpg")
                .getScaledInstance(550, 805, Image.SCALE_SMOOTH);
        this.imgBotonRayo = Herramientas.cargarImagen("img/rayomenu.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.imgBotonCentella = Herramientas.cargarImagen("img/centellamenu.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.rayomenuseleccionado = Herramientas.cargarImagen("img/rayomenuseleccionado.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.centellamenuseleccionado = Herramientas.cargarImagen("img/centellamenuseleccionado.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        this.botonRayo = new Boton(700, 50, 120, 40, imgBotonRayo, entorno);
        this.botonCentella = new Boton(700, 100, 120, 40, imgBotonCentella, entorno);
        this.hechizo1 = new Hechizo("RAYO", 0, 150);
        this.hechizo2 = new Hechizo("CENTELLA", 50, 80);
        this.imgRayoLanzado = Herramientas.cargarImagen("img/rayo.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.imgCentellaLanzado = Herramientas.cargarImagen("img/hechizoArea.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.mago = new Mago(400, 300, entorno);
        this.mago.vidasMaximas = 100;
        this.mago.energiaMagicaMaxima = 100;
        this.mago.vidas = Math.min(mago.vidas, mago.vidasMaximas);
        this.mago.energiaMagica = Math.min(mago.energiaMagica, mago.energiaMagicaMaxima);
        this.murcielagos = new ArrayList<>();
        this.objetos = new Objetos(entorno);
        this.pociones = new ArrayList<>();
        this.potenciadores = new ArrayList<>();
        this.rocas = new Roca[] {
                new Roca(200, 150),
                new Roca(550, 200),
                new Roca(250, 450),
                new Roca(450, 150),
                new Roca(150, 350)
        };
        this.entorno.iniciar();
    }

    // Genera una cantidad específica de murciélagos para una oleada
    private void generarMurcielagosOleada(int cant) {
        int murcielagosGenerados = 0;
        while (murcielagosGenerados < cant) {
            Murcielago nuevoMurcielago;
            boolean superpuesto;
            boolean demasiadoCerca;
            int intentos = 0;
            do {
                int vidaInicial = (numeroOleada == 3) ? 40 : (numeroOleada == 4) ? 160 : 20;
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0:
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1:
                        nuevoMurcielago.x = LIMITE_DERECHO;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
                        break;
                    case 2:
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = LIMITE_INFERIOR;
                        break;
                    case 3:
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
                        break;
                }
                superpuesto = estaMurcielagoSuperpuesto(nuevoMurcielago);
                demasiadoCerca = estaMurcielagoCercaDeMago(nuevoMurcielago);
                intentos++;
            } while ((superpuesto || demasiadoCerca) && intentos < 50); // CAMBIO: Aumentado de 20 a 50 intentos
            if (!superpuesto && !demasiadoCerca) {
                murcielagos.add(nuevoMurcielago);
                murcielagosGenerados++;
            }
        }
    }

    // CAMBIO: Nuevo ,(Method to count successfully spawned bats)
    private int generarMurcielagosOleadaConContador(int cant) {
        int murcielagosGenerados = 0;
        while (murcielagosGenerados < cant) {
            Murcielago nuevoMurcielago;
            boolean superpuesto;
            boolean demasiadoCerca;
            int intentos = 0;
            do {
                int vidaInicial = (numeroOleada == 3) ? 40 : (numeroOleada == 4) ? 160 : 20;
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0:
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1:
                        nuevoMurcielago.x = LIMITE_DERECHO;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
                        break;
                    case 2:
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = LIMITE_INFERIOR;
                        break;
                    case 3:
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
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
        return murcielagosGenerados;
    }

    // Genera murciélagos para cualquier oleada con límite de 10 en pantalla
    private void generarMurcielagos() {
        if (murcielagos.size() >= MAX_MURCIELAGOS_EN_PANTALLA) { // CAMBIO NUEVO: Verificar límite
            return;
        }
        int murcielagosAGenerar = Math.min(MURCIELAGOS_POR_SPAWN[numeroOleada], 
                MAX_MURCIELAGOS_EN_PANTALLA - murcielagos.size()); // CAMBIO NUEVO: Calcular cantidad
        if (murcielagosAGenerar > 0) {
            generarMurcielagosOleada(murcielagosAGenerar);
        }
    }

    // Verifica si un murciélago se superpone con otros
    private boolean estaMurcielagoSuperpuesto(Murcielago nuevo) {
        for (Murcielago murci : murcielagos) {
            if (Math.abs(nuevo.x - murci.x) < 50 && Math.abs(nuevo.y - murci.y) < 50) {
                return true;
            }
        }
        return false;
    }

    // Verifica si un murciélago está demasiado cerca del mago
    private boolean estaMurcielagoCercaDeMago(Murcielago nuevo) {
        double distanciaAlMago = Math.sqrt(
                Math.pow(nuevo.x - mago.x, 2) + Math.pow(nuevo.y - mago.y, 2)
        );
        return distanciaAlMago < 100;
    }

    // Evita que los murciélagos se superpongan durante el movimiento
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

    // Lanza un hechizo hacia un objetivo
    private void lanzarHechizo(Hechizo hechizo, double targetX, double targetY) {
        if (mago.energiaMagica < hechizo.costoMagia) {
            return;
        }
        mago.energiaMagica -= hechizo.costoMagia;
        hechizoFrames = DURACION_HECHIZO;
        hechizoLanzado = hechizo;

        if (hechizo.nombre.equals("RAYO")) {
            configurarHechizoRayo(targetX, targetY);
        } else if (hechizo.nombre.equals("CENTELLA")) {
            configurarHechizoCentella();
        }
    }

    // Configura la trayectoria del hechizo Rayo
    private void configurarHechizoRayo(double targetX, double targetY) {
        hechizoX = mago.x;
        hechizoY = mago.y;
        rayoTargetX = targetX;
        rayoTargetY = targetY;
        double dx = targetX - mago.x;
        double dy = targetY - mago.y;
        rayoAngle = Math.atan2(dy, dx);
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

    // Configura el efecto del hechizo Centella
    private void configurarHechizoCentella() {
        hechizoX = mago.x;
        hechizoY = mago.y;
        ArrayList<Murcielago> eliminados = new ArrayList<>();
        int radio = 60;
        for (Murcielago m : murcielagos) {
            double distancia = Math.sqrt(
                    Math.pow(m.x - mago.x, 2) + Math.pow(m.y - mago.y, 2)
            );
            if (distancia <= radio) {
                m.vida -= hechizo2.area; // Aplicar daño de Centella (80)
                if (m.vida <= 0) {
                    eliminados.add(m);
                    soltarObjetosAlMorir(m);
                    if (numeroOleada == 4) {
                        murcielagosEliminadosOleada5++; // CAMBIO: Incrementar contador para oleada 5
                    }
                }
            }
        }
        enemigosEliminados += eliminados.size();
        murcielagos.removeAll(eliminados);
    }

    // Suelta pociones o potenciadores al derrotar un enemigo
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
    }

    // Reinicia el juego a su estado inicial
    private void reiniciarJuego() {
        juegoTerminado = false;
        juegoganado = false;
        mago = new Mago(400, 300, entorno);
        mago.vidasMaximas = 100;
        mago.energiaMagicaMaxima = 100;
        mago.vidas = Math.min(mago.vidas, mago.vidasMaximas);
        mago.energiaMagica = Math.min(mago.energiaMagica, mago.energiaMagicaMaxima);
        murcielagos.clear();
        tiempoTranscurrido = 0;
        enemigosEliminados = 0;
        murcielagosEliminadosOleada5 = 0; // CAMBIO: Reiniciar contador de oleada 5
        hechizoLanzado = null;
        hechizoFrames = 0;
        rayoTargetX = -1;
        rayoTargetY = -1;
        hechizo1.deseleccionar();
        hechizo2.deseleccionar();
        pociones.clear();
        potenciadores.clear();
        seleccionPotenciador = false;
        mapeado.setNumeroMapa(0);
        numeroOleada = 0;
        mensajeOleada = null;
        framesMensajeOleada = 0;
        contadorSpawn = 0;
        menu = true;
        juego = false;
    }

    // Dibuja el mensaje de la oleada
    private void dibujarMensajeOleada() {
        if (mensajeOleada != null && framesMensajeOleada > 0) {
            entorno.cambiarFont("Arial", 40, Color.YELLOW);
            entorno.escribirTexto(mensajeOleada, 200, 300);
            framesMensajeOleada--;
            if (framesMensajeOleada <= 0) {
                mensajeOleada = null;
            }
        }
    }

    // Bucle principal del juego
    @Override
    public void tick() {
        if (menu) {
            manejarMenuPrincipal();
        } else if (juego) {
            if (juegoganado) {
                manejarPantallaVictoria();
            } else if (juegoTerminado) {
                manejarPantallaDerrota();
            } else {
                manejarJuego();
            }
        }

        // Control de oleadas
        if (juego && !juegoTerminado && !juegoganado) {
            // Generación infinita de murciélagos para todas las oleadas
            contadorSpawn++;
            if (contadorSpawn >= INTERVALOS_SPAWN[numeroOleada]) {
                if (numeroOleada < 4 && enemigosEliminados < ENEMIGOS_POR_OLEADA[numeroOleada] ||
                    numeroOleada == 4 && murcielagosEliminadosOleada5 < ENEMIGOS_POR_OLEADA[4]) { // CAMBIO NUEVO
                    generarMurcielagos();
                }
                contadorSpawn = 0;
            }

            // Transiciones de oleadas
            if (numeroOleada == 0 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[0] && murcielagos.size() == 0) {
                numeroOleada = 1;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 2";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
            } else if (numeroOleada == 1 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[1] && murcielagos.size() == 0) {
                numeroOleada = 2;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 3";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
            } else if (numeroOleada == 2 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[2] && murcielagos.size() == 0) {
                numeroOleada = 3;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 4";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
            } else if (numeroOleada == 3 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[3] && murcielagos.size() == 0) {
                numeroOleada = 4;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 5";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                mago.x = 400;
                mago.y = 300;
            } else if (numeroOleada == 4 && murcielagosEliminadosOleada5 >= ENEMIGOS_POR_OLEADA[4] && murcielagos.size() == 0) { // CAMBIO
                juegoganado = true;
            }
            if (mago.vidas <= 0) {
                juegoTerminado = true;
            }
        }

        // Reinicio del juego
        if ((juegoTerminado || juegoganado) && entorno.sePresiono('r')) {
            reset = true;
        }
        if (reset) {
            reiniciarJuego();
            reset = false;
        }
    }

    // Maneja la lógica y dibujo del menú principal
    private void manejarMenuPrincipal() {
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(titulo, 400, 90, 0.0);
        Image botonImagen = imagenBotonMenu;
        if (botonMenu.contienePunto(entorno.mouseX(), entorno.mouseY())) {
            botonImagen = imagenBotonMenu2;
            if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
                menu = false;
                juego = true;
                mensajeOleada = "Oleada 1";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                generarMurcielagos();
                contadorSpawn = 0;
            }
        }
        botonMenu.dibujarBoton(botonImagen);
    }

    // Maneja la pantalla de victoria
    private void manejarPantallaVictoria() {
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(ganaste, 400, 300, 0.0);
        
    }

    // Maneja la pantalla de derrota
    private void manejarPantallaDerrota() {
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(fin, 400, 300, 0.0);
        
    }

    // Maneja la lógica principal del juego
    private void manejarJuego() {
        tiempoTranscurrido++;
        dibujarFondoYUI();
        manejarEntradaHechizos();
        actualizarYDibujarHechizo();
        dibujarObjetosJuego();
        manejarMovimientoMago();
        manejarInteraccionesObjetos();
        actualizarYDibujarEnemigos();
        actualizarEstadoMago();
        dibujarMensajeOleada();
    }

    // Dibuja el fondo y los elementos de la interfaz
    private void dibujarFondoYUI() {
        entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0.0, 0.75);
        entorno.dibujarImagen(menuImage, entorno.ancho(), entorno.alto() / 2, 0.0, 0.75);
        entorno.dibujarImagen(imgBotonRayo, 700, 50, 0);
        entorno.dibujarImagen(imgBotonCentella, 700, 100, 0);
        if (hechizo1.seleccionado) {
            entorno.dibujarImagen(rayomenuseleccionado, 700, 50, 0.0);
        } else if (hechizo2.seleccionado) {
            entorno.dibujarImagen(centellamenuseleccionado, 700, 100, 0.0);
        }
        dibujarBarrasEstado();
        dibujarEstadisticasJuego();
    }

    // Dibuja las barras de vida y energía
    private void dibujarBarrasEstado() {
        int barraX = 620, barraY = 400, anchoBarra = 150, altoBarra = 20;
        entorno.dibujarRectangulo(barraX + anchoBarra / 2, barraY + altoBarra / 2,
                anchoBarra, altoBarra, 0, Color.DARK_GRAY);
        double proporcionEnergia = mago.energiaMagica / (double) mago.energiaMagicaMaxima;
        int anchoRelleno = (int)(anchoBarra * proporcionEnergia);
        if (anchoRelleno > 0) {
            entorno.dibujarRectangulo(barraX + anchoRelleno / 2, barraY + altoBarra / 2,
                    anchoRelleno, altoBarra, 0, Color.BLUE);
        }
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(mago.energiaMagica + "/" + mago.energiaMagicaMaxima,
                barraX + 55, barraY + 15);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENERGIA", barraX + 35, barraY - 5);

        int vidaX = barraX, vidaY = 460, anchoVida = 150, altoVida = 20;
        entorno.dibujarRectangulo(vidaX + anchoVida / 2, vidaY + altoVida / 2,
                anchoVida, altoVida, 0, Color.DARK_GRAY);
        double proporcionVida = mago.vidas / (double)mago.vidasMaximas;
        int anchoRellenoVida = (int)(anchoVida * proporcionVida);
        if (anchoRellenoVida > 0) {
            entorno.dibujarRectangulo(vidaX + anchoRellenoVida / 2, vidaY + altoVida / 2,
                    anchoRellenoVida, altoVida, 0, Color.RED);
        }
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(mago.vidas + "/" + mago.vidasMaximas, vidaX + 55, vidaY + 15);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("VIDA", vidaX + 45, vidaY - 5);
    }

    // Dibuja estadísticas del juego
    private void dibujarEstadisticasJuego() {
        entorno.cambiarFont("MV Boli", 30, Color.WHITE);
        entorno.escribirTexto(String.format("%d s", tiempoTranscurrido / 60),
                entorno.ancho() - 140, 550);
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENEMIGOS", 650, 240);
        entorno.escribirTexto("ELIMINADOS", 645, 270);
        entorno.escribirTexto("" + enemigosEliminados, 700, 300);
        entorno.escribirTexto("OLEADA", 650, 150);
        entorno.escribirTexto("" + (numeroOleada + 1), 700, 180);
    }

    // Maneja la selección y lanzamiento de hechizos
    private void manejarEntradaHechizos() {
        if (!seleccionPotenciador && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            int mouseX = entorno.mouseX();
            int mouseY = entorno.mouseY();
            if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 50 - 20 && mouseY <= 50 + 20) {
                hechizo1.seleccionar();
                hechizo2.deseleccionar();
            } else if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 100 - 20 && mouseY <= 100 + 20) {
                hechizo2.seleccionar();
                hechizo1.deseleccionar();
            } else if (mouseX < entorno.ancho() - ANCHO_MENU) {
                Hechizo hechizo = hechizo1.seleccionado ? hechizo1 : (hechizo2.seleccionado ? hechizo2 : null);
                if (hechizo != null) {
                    lanzarHechizo(hechizo, mouseX, mouseY);
                    hechizo.deseleccionar();
                }
            }
        }
    }

    // Actualiza y dibuja el hechizo activo
    private void actualizarYDibujarHechizo() {
        if (hechizoFrames <= 0 || hechizoLanzado == null) {
            return;
        }
        if (hechizoLanzado.nombre.equals("RAYO")) {
            actualizarYDibujarRayo();
        } else if (hechizoLanzado.nombre.equals("CENTELLA")) {
            entorno.dibujarImagen(imgCentellaLanzado, hechizoX, hechizoY, 0.0);
        }
        hechizoFrames--;
        if (hechizoFrames == 0) {
            hechizoLanzado = null;
            rayoTargetX = -1;
            rayoTargetY = -1;
        }
    }

    // Actualiza y dibuja el hechizo Rayo
    private void actualizarYDibujarRayo() {
        hechizoX += rayoVelX;
        hechizoY += rayoVelY;
        ArrayList<Murcielago> eliminados = new ArrayList<>();
        double rayoAncho = 60, rayoAlto = 30;
        for (Murcielago m : murcielagos) {
            if (Math.abs(m.x - hechizoX) <= rayoAncho / 2 && Math.abs(m.y - hechizoY) <= rayoAlto / 2) {
                m.vida -= hechizo1.area; // Aplicar daño de Rayo (150)
                if (m.vida <= 0) {
                    eliminados.add(m);
                    soltarObjetosAlMorir(m);
                    if (numeroOleada == 4) {
                        murcielagosEliminadosOleada5++; // CAMBIO: Incrementar contador para oleada 5
                    }
                }
            }
        }
        enemigosEliminados += eliminados.size();
        murcielagos.removeAll(eliminados);
        if (hechizoX < 0 || hechizoX > entorno.ancho() - ANCHO_MENU ||
            hechizoY < 0 || hechizoY > entorno.alto()) {
            hechizoFrames = 0;
            hechizoLanzado = null;
        } else {
            entorno.dibujarImagen(imgRayoLanzado, hechizoX, hechizoY, rayoAngle);
        }
    }

    // Dibuja todos los objetos del juego
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

    // Maneja el movimiento del mago
    private void manejarMovimientoMago() {
        if (hechizoFrames > 0 || seleccionPotenciador) {
            return;
        }
        int nuevaX = mago.x;
        int nuevaY = mago.y;
        if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) {
            nuevaX += 3;
            mago.direccion = "derecha";
        }
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('a')) {
            nuevaX -= 3;
            mago.direccion = "izquierda";
        }
        if (entorno.estaPresionada(entorno.TECLA_ARRIBA) || entorno.estaPresionada('w')) {
            nuevaY -= 3;
            mago.direccion = "arriba";
        }
        if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('s')) {
            nuevaY += 3;
            mago.direccion = "abajo";
        }

        boolean hayColision = false;
        for (Roca roca : rocas) {
            if (roca.colisionaCon(nuevaX, nuevaY)) {
                hayColision = true;
                break;
            }
        }

        int margenX = 30, margenY = 40;
        if (!hayColision && nuevaX >= margenX && nuevaX <= LIMITE_DERECHO &&
                nuevaY >= margenY && nuevaY <= entorno.alto() - margenY) {
            mago.x = nuevaX;
            mago.y = nuevaY;
        }
    }

    // Maneja interacciones con pociones y potenciadores
    private void manejarInteraccionesObjetos() {
        for (Objetos.Pocion pocion : pociones) {
            if (pocion.colisionConMago(mago)) {
                mago.vidas = Math.min(mago.vidas + 30, mago.vidasMaximas);
                pocion.desactivar();
            }
        }
        for (Objetos.Potenciador potenciador : potenciadores) {
            if (potenciador.colisionConMago(mago) && !seleccionPotenciador) {
                seleccionPotenciador = true;
                potenciador.desactivar();
            }
        }
        if (seleccionPotenciador) {
            entorno.cambiarFont("Arial", 30, Color.WHITE);
            entorno.escribirTexto("Elige mejora:", 200, 250);
            entorno.escribirTexto("1 - Vida +10", 200, 280);
            entorno.escribirTexto("2 - Energía +10", 200, 310);
            if (entorno.sePresiono('1')) {
                mago.vidasMaximas += 10;
                mago.vidas = Math.min(mago.vidas + 10, mago.vidasMaximas);
                seleccionPotenciador = false;
            } else if (entorno.sePresiono('2')) {
                mago.energiaMagicaMaxima += 10;
                mago.energiaMagica = Math.min(mago.energiaMagica + 10, mago.energiaMagicaMaxima);
                seleccionPotenciador = false;
            }
        }
        pociones.removeIf(pocion -> !pocion.activa);
        potenciadores.removeIf(potenciador -> !potenciador.activa);
    }

    // Actualiza y dibuja los murciélagos
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
            murci.x = Math.max(MARGEN, Math.min(murci.x, LIMITE_DERECHO));
            murci.y = Math.max(MARGEN, Math.min(murci.y, LIMITE_INFERIOR));
            double dx = murci.x - mago.x;
            double dy = murci.y - mago.y;
            if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                mago.reducirVida();
                murcielagos.remove(i);
                i--;
                if (numeroOleada == 4) {
                    murcielagosEliminadosOleada5++; // CAMBIO: Incrementar contador para oleada 5
                }
            }
        }
    }

    // Actualiza el estado del mago
    private void actualizarEstadoMago() {
        mago.invulnerabilidad();
        if (!seleccionPotenciador && mago.energiaMagica < mago.energiaMagicaMaxima) {
            contadorRecuperacion++;
            if (contadorRecuperacion >= FRAMES_REGEN_ENERGIA) {
                mago.energiaMagica++;
                contadorRecuperacion = 0;
            }
        }
    }

    // Punto de entrada del programa
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}