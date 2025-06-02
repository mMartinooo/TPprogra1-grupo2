package juego;

import java.awt.Image;
import java.awt.Color;
import java.util.ArrayList;

import entorno.Herramientas;
import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
    // Constantes que definen parámetros del juego
    private static final int MARGEN = 50; // Margen de la pantalla para spawn de enemigos
    private static final int MAX_MURCIELAGOS_EN_PANTALLA = 10; // Máximo de enemigos simultáneos en pantalla
    private static final int[] MURCIELAGOS_POR_SPAWN = {3, 3, 3, 3, 4}; // Cantidad de murciélagos por spawn por oleada
    private static final int[] INTERVALOS_SPAWN = {200, 180, 160, 140, 135}; // Intervalos de spawn en frames por oleada
    private static final int[] ENEMIGOS_POR_OLEADA = {15, 25, 35, 45, 55};// Total de enemigos por oleada

    // Variables de estado del juego
    private boolean menu = true; // Indica si el juego está en el menú principal
    private boolean juego = false; // Indica si el juego está activo
    private boolean juegoTerminado = false; // Indica si el jugador perdió
    private boolean juegoganado = false; // Indica si el jugador ganó
    private boolean reset = false; // Indica si se debe reiniciar el juego
    private int numeroOleada = 0; // Oleada actual (0 a 4)
    private int murcielagosEliminadosOleada5 = 0; // Contador de enemigos eliminados en la oleada 5
    private int tiempoTranscurrido = 0; // Tiempo en frames desde el inicio del juego
    private int enemigosEliminados = 0; // Total de enemigos eliminados en la oleada actual
    private int contadorSpawn = 0; // Contador para controlar el tiempo entre spawns
    private String mensajeOleada = null; // Mensaje de la oleada actual
    private int framesMensajeOleada = 0; // Duración en frames del mensaje de oleada

    // Objetos principales del juego
    private Entorno entorno; // Entorno gráfico del juego
    private MenuPrincipal menuPrincipal; // Menú principal
    private Mago mago; // Jugador (mago)
    private ArrayList<Murcielago> murcielagos; // Lista de enemigos (murciélagos)
    private Roca[] rocas; // Obstáculos fijos en el mapa
    private Objetos objetos; // Clase para manejar pociones y potenciadores
    private ArrayList<Objetos.Pocion> pociones; // Lista de pociones en el juego
    private ArrayList<Objetos.Potenciador> potenciadores; // Lista de potenciadores en el juego
    private boolean seleccionPotenciador = false; // Indica si se está seleccionando un potenciador
    private Hechizo rayo; // Hechizo de tipo rayo
    private Hechizo centella; // Hechizo de tipo centella (área)
    private Hechizo hechizoLanzado = null; // Hechizo actualmente activo

    // Recursos gráficos
    private Image fondo; // Imagen de fondo del juego
    private Image menuImage; // Imagen del menú lateral
    private Image fin; // Imagen de pantalla de derrota
    private Image ganaste; // Imagen de pantalla de victoria
    private Image titulo; // Imagen del título del juego
    private Image imgBotonRayo; // Imagen del botón para seleccionar rayo
    private Image imgBotonCentella; // Imagen del botón para seleccionar centella
    private Image rayomenuseleccionado; // Imagen del botón rayo seleccionado
    private Image centellamenuseleccionado; // Imagen del botón centella seleccionado
    private Image imgRayoLanzado; // Imagen del hechizo rayo al lanzarse
    private Image imgCentellaLanzado; // Imagen del hechizo centella al lanzarse
    private Botonazo botonMenu; // Botón para iniciar el juego desde el menú
    private Image imagenBotonMenu; // Imagen del botón de inicio (normal)
    private Image imagenBotonMenu2; // Imagen del botón de inicio (resaltado)

    // Constructor: inicializa el juego y sus componentes
    public Juego() {
        // Inicializa el entorno gráfico con una ventana de 800x600 píxeles
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo XX - v1", 800, 600);
        this.menuPrincipal = new MenuPrincipal(entorno); // Crea el menú principal
        this.botonMenu = new Botonazo(600, 200, 150, 29, entorno); // Botón de inicio en el menú

        // Carga y escala imágenes para pantallas, botones y hechizos
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
        
        // Crea botones para seleccionar hechizos
        new Boton(700, 50, 120, 40, imgBotonRayo, entorno); // Botón para rayo
        new Boton(700, 100, 120, 40, imgBotonCentella, entorno); // Botón para centella

        // Carga imágenes de hechizos lanzados
        this.imgRayoLanzado = Herramientas.cargarImagen("img/rayo.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.imgCentellaLanzado = Herramientas.cargarImagen("img/hechizoArea.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        // Inicializa hechizos con sus propiedades (nombre, costo, daño, imagen)
        this.rayo = new Hechizo("RAYO", 0, 150, imgRayoLanzado, entorno);
        this.centella = new Hechizo("CENTELLA", 50, 80, imgCentellaLanzado, entorno);

        // Inicializa el jugador, enemigos, obstáculos y objetos
        this.mago = new Mago(400, 300, entorno); // Mago en el centro inicial
        this.murcielagos = new ArrayList<>(); // Lista vacía para murciélagos
        this.objetos = new Objetos(entorno); // Objetos (pociones y potenciadores)
        this.pociones = new ArrayList<>(); // Lista vacía para pociones
        this.potenciadores = new ArrayList<>(); // Lista vacía para potenciadores
        this.rocas = new Roca[] {
                new Roca(200, 150), new Roca(550, 200), new Roca(250, 450),
                new Roca(450, 150), new Roca(150, 350) // Obstáculos fijos
        };
        
        // Inicia el entorno gráfico
        this.entorno.iniciar();
    }

    // Genera murciélagos para una oleada, asegurando que no se superpongan ni estén cerca del mago
    private void generarMurcielagosOleada(int cant) {
        int murcielagosGenerados = 0;
        while (murcielagosGenerados < cant) {
            Murcielago nuevoMurcielago;
            boolean superpuesto;
            boolean demasiadoCerca;
            int intentos = 0;
            do {
                // Define vida inicial según la oleada (más fuerte en oleadas 3 y 4)
                int vidaInicial = (numeroOleada == 3) ? 35 : (numeroOleada == 4) ? 140 : 20;
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                
                // Selecciona un borde aleatorio para el spawn
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0: // Arriba
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1: // Derecha
                        nuevoMurcielago.x = 600 - MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                    case 2: // Abajo
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = 600 - MARGEN;
                        break;
                    case 3: // Izquierda
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                }
                superpuesto = estaMurcielagoSuperpuesto(nuevoMurcielago); // Verifica superposición
                demasiadoCerca = estaMurcielagoCercaDeMago(nuevoMurcielago); // Verifica cercanía al mago
                intentos++;
            } while ((superpuesto || demasiadoCerca) && intentos < 50); // Limita intentos para evitar bucles infinitos
            if (!superpuesto && !demasiadoCerca) {
                murcielagos.add(nuevoMurcielago);
                murcielagosGenerados++;
            }
        }
    }

    // Genera murciélagos según la oleada, respetando el límite máximo
    private void generarMurcielagos() {
        if (murcielagos.size() >= MAX_MURCIELAGOS_EN_PANTALLA) {
            return; // No genera más si se alcanzó el límite
        }
        int murcielagosAGenerar = Math.min(MURCIELAGOS_POR_SPAWN[numeroOleada],
                MAX_MURCIELAGOS_EN_PANTALLA - murcielagos.size());
        if (murcielagosAGenerar > 0) {
            generarMurcielagosOleada(murcielagosAGenerar);
        }
    }

    // Verifica si un murciélago está superpuesto con otro
    private boolean estaMurcielagoSuperpuesto(Murcielago nuevo) {
        for (Murcielago murci : murcielagos) {
            if (Math.abs(nuevo.x - murci.x) < 50 && Math.abs(nuevo.y - murci.y) < 50) {
                return true; // Detecta colisión si están a menos de 50 píxeles
            }
        }
        return false;
    }

    // Verifica si un murciélago está demasiado cerca del mago
    private boolean estaMurcielagoCercaDeMago(Murcielago nuevo) {
        double distanciaAlMago = Math.sqrt(
                Math.pow(nuevo.x - mago.getX(), 2) + Math.pow(nuevo.y - mago.getY(), 2)
        );
        return distanciaAlMago < 100; // Considera "cerca" si está a menos de 100 píxeles
    }

    // Mantiene la distancia mínima entre murciélagos para evitar superposición
    private void mantenerDistanciaMurcielagos() {
        for (Murcielago murci : murcielagos) {
            for (Murcielago otroMurci : murcielagos) {
                double dx = murci.x - otroMurci.x;
                double dy = murci.y - otroMurci.y;
                double distancia = Math.sqrt(dx * dx + dy * dy);
                if (distancia < 50 && distancia > 0) {
                    // Ajusta posiciones para mantener separación
                    murci.x += dx / distancia * 1.5;
                    murci.y += dy / distancia * 1.5;
                }
            }
        }
    }

    // Genera pociones o potenciadores al morir un murciélago
    private void soltarObjetosAlMorir(Murcielago m) {
        // 5% de probabilidad de soltar poción si el mago tiene poca vida
        if (mago.vidas <= 80 && Math.random() < 0.05) {
            Objetos.Pocion nuevaPocion = objetos.new Pocion((int)m.x, (int)m.y, objetos);
            if (nuevaPocion.x >= 600) {
                nuevaPocion.x = (int)(Math.random() * (600 - 50)) + 50; // Ajusta posición si está fuera del área jugable
            }
            pociones.add(nuevaPocion);
        // 2% de probabilidad de soltar potenciador
        } else if (Math.random() < 0.02) {
            Objetos.Potenciador nuevoPotenciador = objetos.new Potenciador((int)m.x, (int)m.y, objetos);
            if (nuevoPotenciador.x >= 600) {
                nuevoPotenciador.x = (int)(Math.random() * (600 - 50)) + 50; // Ajusta posición si está fuera del área
            }
            potenciadores.add(nuevoPotenciador);
        }
        enemigosEliminados++;
        if (numeroOleada == 4) {
            murcielagosEliminadosOleada5++; // Contador específico para oleada 5
        }
    }

    // Reinicia el juego a su estado inicial
    private void reiniciarJuego() {
        juegoTerminado = false;
        juegoganado = false;
        mago = new Mago(400, 300, entorno); // Reinicia el mago en el centro
        murcielagos.clear(); // Limpia la lista de murciélagos
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
        menu = true; // Vuelve al menú principal
        juego = false;
    }

    // Dibuja el mensaje de la oleada actual en pantalla
    private void dibujarMensajeOleada() {
        if (mensajeOleada != null && framesMensajeOleada > 0) {
            entorno.cambiarFont("Arial", 40, Color.YELLOW);
            entorno.escribirTexto(mensajeOleada, 200, 300); // Muestra el mensaje centrado
            framesMensajeOleada--;
            if (framesMensajeOleada == 0) {
                mensajeOleada = null; // Corrige posible error en el código original
            }
        }
    }

    // Método principal del bucle del juego, ejecutado cada frame
    @Override
    public void tick() {
        if (menu) {
            manejarMenuPrincipal(); // Maneja la lógica del menú principal
        } else if (juego) {
            if (juegoganado) {
                manejarPantallaVictoria(); // Muestra pantalla de victoria
            } else if (juegoTerminado) {
                manejarPantallaDerrota(); // Muestra pantalla de derrota
            } else {
                manejarJuego(); // Ejecuta la lógica del juego activo
            }
        }

        // Controla el spawn de murciélagos y la progresión de oleadas
        if (juego && !juegoTerminado && !juegoganado) {
            contadorSpawn++;
            if (contadorSpawn >= INTERVALOS_SPAWN[numeroOleada]) {
                // Genera murciélagos si no se ha alcanzado el límite de enemigos por oleada
                if (numeroOleada < 4 && enemigosEliminados < ENEMIGOS_POR_OLEADA[numeroOleada] ||
                    numeroOleada == 4 && murcielagosEliminadosOleada5 < ENEMIGOS_POR_OLEADA[4]) {
                    generarMurcielagos();
                }
                contadorSpawn = 0; // Reinicia el contador de spawn
            }

            // Avanza a la siguiente oleada cuando se eliminan todos los enemigos
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
                juegoganado = true; // El jugador gana al completar la oleada 5
            }

            // Termina el juego si el mago se queda sin vidas
            if (mago.vidas <= 0) {
                juegoTerminado = true;
            }
        }

        // Reinicia el juego si se presiona 'r' en pantalla de victoria o derrota
        if ((juegoTerminado || juegoganado) && entorno.sePresiono('r')) {
            reset = true;
        }
        if (reset) {
            reiniciarJuego();
            reset = false;
        }
    }

    // Maneja la lógica y renderizado del menú principal
    private void manejarMenuPrincipal() {
        menuPrincipal.dibujarMenuPrincipal(400, 300); // Dibuja el fondo del menú
        entorno.dibujarImagen(titulo, 400, 90, 0.0); // Dibuja el título del juego
        Image botonImagen = imagenBotonMenu;
        // Cambia la imagen del botón si el ratón está sobre él
        if (botonMenu.contienePunto(entorno.mouseX(), entorno.mouseY())) {
            botonImagen = imagenBotonMenu2;
            if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
                // Inicia el juego al hacer clic
                menu = false;
                juego = true;
                mensajeOleada = "Oleada 1";
                framesMensajeOleada = 100;
                generarMurcielagos();
                contadorSpawn = 0;
            }
        }
        botonMenu.dibujarBoton(botonImagen); // Dibuja el botón de inicio
    }

    // Muestra la pantalla de victoria
    private void manejarPantallaVictoria() {
        menuPrincipal.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(ganaste, 400, 300, 0.0); // Dibuja imagen de victoria
    }

    // Muestra la pantalla de derrota
    private void manejarPantallaDerrota() {
        menuPrincipal.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(fin, 400, 300, 0.0); // Dibuja imagen de derrota
    }

    // Maneja la lógica principal del juego activo
    private void manejarJuego() {
        tiempoTranscurrido++; // Incrementa el contador de tiempo
        dibujarFondoUI(); // Dibuja fondo y UI (barras, botones, estadísticas)
        manejarEntradaHechizos(); // Procesa clics para seleccionar/lanzar hechizos

        // Actualiza el hechizo activo y elimina murciélagos impactados
        if (hechizoLanzado != null) {
            ArrayList<Murcielago> eliminados = new ArrayList<>();
            hechizoLanzado.actualizar(murcielagos, eliminados);
            for (Murcielago m : eliminados) {
                soltarObjetosAlMorir(m); // Genera objetos al eliminar un murciélago
            }
            murcielagos.removeAll(eliminados);
            enemigosEliminados += eliminados.size();
            if (hechizoLanzado.estaActivo()) {
                hechizoLanzado.dibujar(); // Dibuja el hechizo activo
            } else {
                hechizoLanzado = null; // Libera el hechizo al terminar
            }
        }

        // Mueve al mago, respetando obstáculos y estados
        mago.mover(rocas, seleccionPotenciador, hechizoLanzado != null && hechizoLanzado.estaActivo());
        mago.interactuarConObjetos(pociones, potenciadores, seleccionPotenciador); // Maneja colisiones con objetos
        seleccionPotenciador = mago.manejarSeleccionPotenciador(seleccionPotenciador); // Actualiza estado de potenciadores
        pociones.removeIf(pocion -> !pocion.activa); // Elimina pociones inactivas
        potenciadores.removeIf(potenciador -> !potenciador.activa); // Elimina potenciadores inactivos
        actualizarYDibujarEnemigos(); // Actualiza y dibuja los murciélagos
        mago.invulnerabilidad(); // Gestiona el estado de invulnerabilidad
        mago.regenerarEnergia(seleccionPotenciador); // Regenera energía del mago
        dibujarObjetosJuego(); // Dibuja rocas, pociones, potenciadores y mago
        dibujarMensajeOleada(); // Muestra el mensaje de oleada si está activo
    }

    // Dibuja el fondo y la interfaz de usuario
    private void dibujarFondoUI() {
        entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0.0, 0.75); // Fondo del juego
        entorno.dibujarImagen(menuImage, entorno.ancho(), entorno.alto() / 2, 0.0, 0.75); // Menú lateral
        entorno.dibujarImagen(imgBotonRayo, 700, 50, 0); // Botón de rayo
        entorno.dibujarImagen(imgBotonCentella, 700, 100, 0); // Botón de centella
        // Resalta el hechizo seleccionado
        if (rayo.seleccionado) {
            entorno.dibujarImagen(rayomenuseleccionado, 700, 50, 0.0);
        } else if (centella.seleccionado) {
            entorno.dibujarImagen(centellamenuseleccionado, 700, 100, 0.0);
        }
        mago.dibujarBarrasEstado(); // Dibuja barras de vida y energía
        dibujarEstadisticasJuego(); // Muestra estadísticas del juego
    }

    // Muestra estadísticas del juego en el menú lateral
    private void dibujarEstadisticasJuego() {
        entorno.cambiarFont("MV Boli", 30, Color.WHITE);
        entorno.escribirTexto(String.format("%d s", tiempoTranscurrido / 60), entorno.ancho() - 140, 550); // Tiempo en segundos
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENEMIGOS", 650, 240);
        entorno.escribirTexto("ELIMINADOS", 645, 270);
        entorno.escribirTexto("" + enemigosEliminados, 700, 300); // Enemigos eliminados
        entorno.escribirTexto("OLEADA", 650, 150);
        entorno.escribirTexto("" + (numeroOleada + 1), 700, 180); // Oleada actual
    }

    // Maneja clics del ratón para seleccionar y lanzar hechizos
    private void manejarEntradaHechizos() {
        if (!seleccionPotenciador && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            int mouseX = entorno.mouseX();
            int mouseY = entorno.mouseY();
            // Selecciona el hechizo rayo
            if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 50 - 20 && mouseY <= 50 + 20) {
                rayo.seleccionar();
                centella.deseleccionar();
            // Selecciona el hechizo centella
            } else if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 100 - 20 && mouseY <= 100 + 20) {
                centella.seleccionar();
                rayo.deseleccionar();
            // Lanza el hechizo seleccionado en la posición del clic
            } else if (mouseX < entorno.ancho() - 200) {
                Hechizo hechizo = rayo.seleccionado ? rayo : (centella.seleccionado ? centella : null);
                if (hechizo != null) {
                    hechizo.lanzar(mago, mouseX, mouseY);
                    hechizoLanzado = hechizo;
                    hechizo.deseleccionar();
                }
            }
        }
    }

    // Dibuja todos los objetos del juego (rocas, pociones, potenciadores, mago)
    private void dibujarObjetosJuego() {
        for (Roca roca : rocas) {
            roca.dibujar(entorno); // Dibuja obstáculos
        }
        for (Objetos.Pocion pocion : pociones) {
            pocion.dibujar(); // Dibuja pociones
        }
        for (Objetos.Potenciador potenciador : potenciadores) {
            potenciador.dibujar(); // Dibuja potenciadores
        }
        mago.dibujar(); // Dibuja al mago
    }

    // Actualiza y dibuja los murciélagos (enemigos)
    private void actualizarYDibujarEnemigos() {
        if (seleccionPotenciador) {
            return; // No actualiza enemigos si se está seleccionando un potenciador
        }
        mantenerDistanciaMurcielagos(); // Evita superposición de murciélagos
        for (int i = 0; i < murcielagos.size(); i++) {
            Murcielago murci = murcielagos.get(i);
            if (murci.x < 600) {
                murci.dibujar(); // Dibuja murciélagos dentro del área jugable
            }
            murci.seguirMago(mago); // Mueve murciélagos hacia el mago
            // Limita el movimiento dentro de los márgenes
            murci.x = Math.max(MARGEN, Math.min(murci.x, 600 - MARGEN));
            murci.y = Math.max(MARGEN, Math.min(murci.y, 600 - MARGEN));
            // Detecta colisión con el mago si no es invulnerable
            if (!mago.invulnerable) {
                double dx = murci.x - mago.getX();
                double dy = murci.y - mago.getY();
                if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                    mago.reducirVida(); // Reduce vida del mago
                    murcielagos.remove(i); // Elimina el murciélago
                    i--;
                }
            }
        }
    }

    // Punto de entrada del programa
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego(); // Crea e inicia el juego
    }
}