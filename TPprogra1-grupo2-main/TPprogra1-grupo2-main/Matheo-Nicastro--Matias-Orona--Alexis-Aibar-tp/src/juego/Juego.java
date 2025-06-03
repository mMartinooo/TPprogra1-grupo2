package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
    // Constantes que definen parámetros del juego
    // MARGEN: Define un margen de 50 píxeles para limitar la generación de enemigos en los bordes del área de juego
    private static final int MARGEN = 50;
    // MURCIELAGOS_POR_SPAWN: Cantidad de murciélagos generados por spawn en cada oleada (índices 0-4 representan oleadas 1-5)
    private static final int[] MURCIELAGOS_POR_SPAWN = {3, 3, 3, 3, 4};
    // INTERVALOS_SPAWN: Intervalos de tiempo (en frames) entre spawns de murciélagos para cada oleada
    private static final int[] INTERVALOS_SPAWN = {200, 180, 160, 140, 135};
    // ENEMIGOS_POR_OLEADA: Cantidad total de enemigos a eliminar en cada oleada para avanzar
    private static final int[] ENEMIGOS_POR_OLEADA = {15, 25, 35, 45, 50};

    // Variables de estado del juego
    // juegoTerminado: Indica si el jugador ha perdido (vidas <= 0)
    private boolean juegoTerminado = false;
    // juegoGanado: Indica si el jugador ha ganado (completó la oleada 5)
    private boolean juegoGanado = false;
    // reset: Bandera para reiniciar el juego cuando se presiona la tecla 'r'
    private boolean reset = false;
    // numeroOleada: Índice de la oleada actual (0 a 4, corresponde a oleadas 1 a 5)
    private int numeroOleada = 0;
    // murcielagosEliminadosOleada5: Contador de murciélagos eliminados en la oleada 5
    private int murcielagosEliminadosOleada5 = 0;
    // tiempoTranscurrido: Contador de frames para medir el tiempo de juego
    private int tiempoTranscurrido = 0;
    // enemigosEliminados: Contador de enemigos eliminados en la oleada actual
    private int enemigosEliminados = 0;
    // contadorSpawn: Contador de frames para controlar el intervalo de generación de murciélagos
    private int contadorSpawn = 0;
    // mensajeOleada: Texto del mensaje que muestra la oleada actual
    private String mensajeOleada = null;
    // framesMensajeOleada: Duración en frames del mensaje de oleada en pantalla
    private int framesMensajeOleada = 0;

    // Objetos principales del juego
    // entorno: Objeto que gestiona la ventana gráfica y la entrada del usuario
    private Entorno entorno;
    // menuPrincipal: Objeto que maneja el menú principal y las interfaces de victoria/derrota
    private MenuPrincipal menuPrincipal;
    // mago: Objeto que representa al jugador (el mago)
    private Mago mago;
    // murcielagos: Lista de enemigos (murciélagos) presentes en el juego
    private ArrayList<Murcielago> murcielagos;
    // rocas: Arreglo de obstáculos estáticos en el mapa
    private Roca[] rocas;
    // objetos: Instancia que maneja la creación de pociones y potenciadores
    private Objetos objetos;
    // pociones: Lista de pociones que el mago puede recolectar
    private ArrayList<Objetos.Pocion> pociones;
    // potenciadores: Lista de potenciadores que el mago puede recolectar
    private ArrayList<Objetos.Potenciador> potenciadores;
    // seleccionPotenciador: Bandera que indica si el mago está seleccionando un potenciador
    private boolean seleccionPotenciador = false;
    // rayo: Hechizo de tipo "RAYO" con daño y energía específica
    private Hechizo rayo;
    // centella: Hechizo de tipo "CENTELLA" con daño y energía específica
    private Hechizo centella;
    // hechizoLanzado: Hechizo actualmente activo en el juego
    private Hechizo hechizoLanzado = null;

    // Recursos gráficos
    // fondo: Imagen de fondo del juego, escalada a 1050x805 píxeles
    private Image fondo;
    // fin: Imagen mostrada al perder el juego
    private Image fin;
    // ganaste: Imagen mostrada al ganar el juego
    private Image ganaste;
    // imgRayoLanzado: Imagen del hechizo "rayo" cuando es lanzado
    private Image imgRayoLanzado;
    // imgCentellaLanzado: Imagen del hechizo "centella" cuando es lanzado
    private Image imgCentellaLanzado;

    // Constructor: Inicializa el juego y sus componentes
    public Juego() {
        // Crea la ventana del juego con dimensiones 800x600
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo XX - v1", 800, 600);
        // Inicializa el menú principal
        this.menuPrincipal = new MenuPrincipal(entorno);
        // Carga y escala la imagen de fondo
        this.fondo = Herramientas.cargarImagen("img/fondo10.png").getScaledInstance(1050, 805, Image.SCALE_SMOOTH);
        // Carga la imagen de derrota
        this.fin = Herramientas.cargarImagen("img/seTermino.png");
        // Carga la imagen de victoria
        this.ganaste = Herramientas.cargarImagen("img/ganaste.png");
        // Carga y escala la imagen del hechizo rayo
        this.imgRayoLanzado = Herramientas.cargarImagen("img/rayo.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        // Carga y escala la imagen del hechizo centella
        this.imgCentellaLanzado = Herramientas.cargarImagen("img/hechizoArea.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        // Inicializa los hechizos con sus propiedades
        this.rayo = new Hechizo("RAYO", 0, 150, imgRayoLanzado, entorno);
        this.centella = new Hechizo("CENTELLA", 50, 80, imgCentellaLanzado, entorno);
        // Inicializa el mago en la posición central (400, 300)
        this.mago = new Mago(400, 300, entorno);
        // Inicializa la lista de murciélagos
        this.murcielagos = new ArrayList<>();
        // Inicializa el gestor de objetos
        this.objetos = new Objetos(entorno);
        // Inicializa las listas de pociones y potenciadores
        this.pociones = new ArrayList<>();
        this.potenciadores = new ArrayList<>();
        // Inicializa las rocas en posiciones fijas
        this.rocas = new Roca[] {
                new Roca(200, 150), new Roca(550, 200), new Roca(250, 450),
                new Roca(450, 150), new Roca(150, 350)
        };
        // Inicia el entorno gráfico
        this.entorno.iniciar();
    }

    // Genera una cantidad específica de murciélagos para la oleada actual
    private void generarMurcielagosOleada(int cant) {
        // Contador de murciélagos generados
        int murcielagosGenerados = 0;
        // Genera murciélagos hasta alcanzar la cantidad deseada
        while (murcielagosGenerados < cant) {
            Murcielago nuevoMurcielago;
            boolean superpuesto;
            boolean demasiadoCerca;
            // Limita los intentos para evitar bucles infinitos
            int intentos = 0;
            do {
                // Asigna vida inicial según la oleada (20 por defecto, 35 en oleada 4, 140 en oleada 5)
                int vidaInicial = (numeroOleada == 3) ? 35 : (numeroOleada == 4) ? 140 : 20;
                // Crea un nuevo murciélago
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                // Selecciona un borde aleatorio para la aparición (0: arriba, 1: derecha, 2: abajo, 3: izquierda)
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0: // Borde superior
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1: // Borde derecho
                        nuevoMurcielago.x = 600 - MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                    case 2: // Borde inferior
                        nuevoMurcielago.x = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = 600 - MARGEN;
                        break;
                    case 3: // Borde izquierdo
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (600 - MARGEN - MARGEN)) + MARGEN;
                        break;
                }
                // Verifica si el murciélago está superpuesto con otro
                superpuesto = estaMurcielagoSuperpuesto(nuevoMurcielago);
                // Verifica si el murciélago está demasiado cerca del mago
                demasiadoCerca = estaMurcielagoCercaDeMago(nuevoMurcielago);
                intentos++;
            } while ((superpuesto || demasiadoCerca) && intentos < 50);
            // Si no está superpuesto ni cerca del mago, lo agrega a la lista
            if (!superpuesto && !demasiadoCerca) {
                murcielagos.add(nuevoMurcielago);
                murcielagosGenerados++;
            }
        }
    }

    // Controla la generación de murciélagos según la oleada y el límite de enemigos en pantalla
    private void generarMurcielagos() {
        // Limita la cantidad de murciélagos en pantalla a 10
        if (murcielagos.size() >= 10) {
            return;
        }
        // Calcula cuántos murciélagos generar, respetando el límite de 10
        int murcielagosAGenerar = Math.min(MURCIELAGOS_POR_SPAWN[numeroOleada], 10 - murcielagos.size());
        if (murcielagosAGenerar > 0) {
            generarMurcielagosOleada(murcielagosAGenerar);
        }
    }

    // Verifica si un murciélago está superpuesto con otro
    private boolean estaMurcielagoSuperpuesto(Murcielago nuevo) {
        // Recorre la lista de murciélagos existentes
        for (Murcielago murci : murcielagos) {
            // Compara la distancia entre el nuevo murciélago y los existentes
            if (Math.abs(nuevo.x - murci.x) < 50 && Math.abs(nuevo.y - murci.y) < 50) {
                return true; // Retorna true si están demasiado cerca
            }
        }
        return false;
    }

    // Verifica si un murciélago está demasiado cerca del mago
    private boolean estaMurcielagoCercaDeMago(Murcielago nuevo) {
        // Calcula la distancia euclidiana entre el murciélago y el mago
        double distanciaAlMago = Math.sqrt(
                Math.pow(nuevo.x - mago.getX(), 2) + Math.pow(nuevo.y - mago.getY(), 2)
        );
        return distanciaAlMago < 100; // Retorna true si la distancia es menor a 100
    }

    // Mantiene una distancia mínima entre murciélagos para evitar superposición
    private void mantenerDistanciaMurcielagos() {
        // Recorre todos los murciélagos
        for (Murcielago murci : murcielagos) {
            for (Murcielago otroMurci : murcielagos) {
                // Calcula la distancia entre dos murciélagos
                double dx = murci.x - otroMurci.x;
                double dy = murci.y - otroMurci.y;
                double distancia = Math.sqrt(dx * dx + dy * dy);
                // Si están demasiado cerca, los separa ligeramente
                if (distancia < 50 && distancia > 0) {
                    murci.x += dx / distancia * 1.5;
                    murci.y += dy / distancia * 1.5;
                }
            }
        }
    }

    // Gestiona la caída de objetos (poción o potenciador) al morir un murciélago
    private void soltarObjetosAlMorir(Murcielago m) {
        // Si el mago tiene poca vida (≤80), hay un 5% de probabilidad de soltar una poción
        if (mago.vidas <= 80 && Math.random() < 0.05) {
            // Crea una nueva poción en la posición del murciélago
            Objetos.Pocion nuevaPocion = objetos.new Pocion((int)m.x, (int)m.y, objetos);
            // Ajusta la posición si está fuera del límite derecho
            if (nuevaPocion.x >= 600) {
                nuevaPocion.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            pociones.add(nuevaPocion);
        // Si no se genera poción, hay un 2% de probabilidad de soltar un potenciador
        } else if (Math.random() < 0.02) {
            // Crea un nuevo potenciador en la posición del murciélago
            Objetos.Potenciador nuevoPotenciador = objetos.new Potenciador((int)m.x, (int)m.y, objetos);
            // Ajusta la posición si está fuera del límite derecho
            if (nuevoPotenciador.x >= 600) {
                nuevoPotenciador.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            potenciadores.add(nuevoPotenciador);
        }
        // Incrementa el contador de enemigos eliminados
        enemigosEliminados++;
        // Incrementa el contador específico para la oleada 5
        if (numeroOleada == 4) {
            murcielagosEliminadosOleada5++;
        }
    }

    // Reinicia el estado del juego a los valores iniciales
    private void reiniciarJuego() {
        // Restablece las banderas de estado
        juegoTerminado = false;
        juegoGanado = false;
        // Crea un nuevo mago en la posición inicial
        mago = new Mago(400, 300, entorno);
        // Limpia la lista de murciélagos
        murcielagos.clear();
        // Reinicia los contadores
        tiempoTranscurrido = 0;
        enemigosEliminados = 0;
        murcielagosEliminadosOleada5 = 0;
        // Desactiva el hechizo activo
        hechizoLanzado = null;
        // Deselecciona los hechizos
        rayo.deseleccionar();
        centella.deseleccionar();
        // Limpia las listas de objetos
        pociones.clear();
        potenciadores.clear();
        // Desactiva la selección de potenciadores
        seleccionPotenciador = false;
        // Reinicia la oleada
        numeroOleada = 0;
        // Limpia el mensaje de oleada
        mensajeOleada = null;
        framesMensajeOleada = 0;
        contadorSpawn = 0;
        // Vuelve al menú principal
        menuPrincipal.setMenu(true);
        menuPrincipal.setJuego(false);
    }

    // Dibuja el mensaje de la oleada actual en pantalla
    private void dibujarMensajeOleada() {
        // Si hay un mensaje activo y su duración no ha expirado
        if (mensajeOleada != null && framesMensajeOleada > 0) {
            // Configura la fuente y color del texto
            entorno.cambiarFont("Arial", 40, Color.YELLOW);
            // Dibuja el mensaje en el centro de la pantalla
            entorno.escribirTexto(mensajeOleada, 200, 300);
            // Reduce el contador de frames del mensaje
            framesMensajeOleada--;
            // Si el mensaje ha expirado, lo elimina
            if (framesMensajeOleada == 0) {
                mensajeOleada = null;
            }
        }
    }

    // Método principal del bucle de juego, ejecutado en cada frame
    @Override
    public void tick() {
        // Si está en el menú principal
        if (menuPrincipal.isMenu()) {
            // Maneja la lógica del menú y, si se inicia el juego, configura la primera oleada
            if (menuPrincipal.manejarMenuPrincipal()) {
                menuPrincipal.setMenu(false);
                menuPrincipal.setJuego(true);
                mensajeOleada = "Oleada 1";
                framesMensajeOleada = 100;
                generarMurcielagos();
                contadorSpawn = 0;
            }
        // Si está en modo juego
        } else if (menuPrincipal.isJuego()) {
            // Muestra la pantalla de victoria si el jugador ganó
            if (juegoGanado) {
                manejarPantallaVictoria();
            // Muestra la pantalla de derrota si el jugador perdió
            } else if (juegoTerminado) {
                manejarPantallaDerrota();
            // Ejecuta la lógica del juego activo
            } else {
                manejarJuego();
            }
        }

        // Ldeclara la lógica para generar murciélagos y avanzar oleadas
        if (menuPrincipal.isJuego() && !juegoTerminado && !juegoGanado) {
            // Incrementa el contador de spawn
            contadorSpawn++;
            // Genera murciélagos según el intervalo de la oleada actual
            if (contadorSpawn >= INTERVALOS_SPAWN[numeroOleada]) {
                if (numeroOleada < 4 && enemigosEliminados < ENEMIGOS_POR_OLEADA[numeroOleada] ||
                    numeroOleada == 4 && murcielagosEliminadosOleada5 < ENEMIGOS_POR_OLEADA[4]) {
                    generarMurcielagos();
                }
                contadorSpawn = 0;
            }

            // Avanza a la siguiente oleada si se cumple el objetivo de enemigos eliminados
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
                juegoGanado = true; // Marca la victoria al completar la oleada 5
            }

            // Termina el juego si el mago se queda sin vidas
            if (mago.vidas <= 0) {
                juegoTerminado = true;
            }
        }

        // Reinicia el juego si se presiona 'r' en las pantallas de victoria o derrota
        if ((juegoTerminado || juegoGanado) && entorno.sePresiono('r')) {
            reset = true;
        }
        if (reset) {
            reiniciarJuego();
            reset = false;
        }
    }

    // Muestra la pantalla de victoria
    private void manejarPantallaVictoria() {
        // Dibuja el menú de victoria y la imagen correspondiente
        menuPrincipal.dibujarMenuPrincipal(400, 300);
        entorno.dibujarImagen(ganaste, 400, 300, 0.0);
    }

    // Muestra la pantalla de derrota
    private void manejarPantallaDerrota() {
        // Dibuja el menú de derrota y la imagen correspondiente
        menuPrincipal.dibujarMenuDerrota(400, 300);
        entorno.dibujarImagen(fin, 400, 300, 0.0);
    }

    // Maneja la lógica principal del juego durante una partida activa
    private void manejarJuego() {
        // Incrementa el contador de tiempo
        tiempoTranscurrido++;
        // Dibuja el fondo del juego
        entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0.0, 0.75);
        // Dibuja la interfaz de usuario (vidas, energía, oleada, etc.)
        menuPrincipal.dibujarFondoUI(mago, rayo, centella, tiempoTranscurrido, enemigosEliminados, numeroOleada);
        
        // Maneja la entrada del usuario para lanzar hechizos
        Hechizo hechizoNuevo = menuPrincipal.manejarEntradaHechizos(seleccionPotenciador, rayo, centella, mago);
        if (hechizoNuevo != null) {
            hechizoLanzado = hechizoNuevo; // Actualiza el hechizo activo
        }

        // Actualiza y dibuja el hechizo activo, eliminando murciélagos afectados
        if (hechizoLanzado != null) {
            ArrayList<Murcielago> eliminados = new ArrayList<>();
            hechizoLanzado.actualizar(murcielagos, eliminados);
            for (Murcielago m : eliminados) {
                soltarObjetosAlMorir(m); // Suelta objetos al eliminar un murciélago
            }
            murcielagos.removeAll(eliminados);
            enemigosEliminados += eliminados.size();
            if (hechizoLanzado.estaActivo()) {
                hechizoLanzado.dibujar(); // Dibuja el hechizo si está activo
            } else {
                hechizoLanzado = null; // Desactiva el hechizo si ha terminado
            }
        }

        // Mueve al mago, respetando colisiones con rocas y el estado del juego
        mago.mover(rocas, seleccionPotenciador, hechizoLanzado != null && hechizoLanzado.estaActivo());
        // Gestiona la interacción del mago con pociones y potenciadores
        mago.interactuarConObjetos(pociones, potenciadores, seleccionPotenciador);
        // Actualiza el estado de selección de potenciadores
        seleccionPotenciador = mago.manejarSeleccionPotenciador(seleccionPotenciador);
        // Elimina pociones y potenciadores inactivos
        pociones.removeIf(pocion -> !pocion.activa);
        potenciadores.removeIf(potenciador -> !potenciador.activa);
        // Actualiza y dibuja los enemigos
        actualizarYDibujarEnemigos();
        // Gestiona la invulnerabilidad del mago
        mago.invulnerabilidad();
        // Regenera la energía del mago
        mago.regenerarEnergia(seleccionPotenciador);
        // Dibuja los objetos del juego
        dibujarObjetosJuego();
        // Dibuja el mensaje de la oleada
        dibujarMensajeOleada();
    }

    // Dibuja todos los objetos del juego (rocas, pociones, potenciadores, mago)
    private void dibujarObjetosJuego() {
        for (Roca roca : rocas) {
            roca.dibujar(entorno); // Dibuja cada roca
        }
        for (Objetos.Pocion pocion : pociones) {
            pocion.dibujar(); // Dibuja cada poción
        }
        for (Objetos.Potenciador potenciador : potenciadores) {
            potenciador.dibujar(); // Dibuja cada potenciador
        }
        mago.dibujar(); // Dibuja al mago
    }

    // Actualiza y dibuja los enemigos, manejando colisiones con el mago
    private void actualizarYDibujarEnemigos() {
        // Si se está seleccionando un potenciador, no actualiza los enemigos
        if (seleccionPotenciador) {
            return;
        }
        // Mantiene la distancia entre murciélagos
        mantenerDistanciaMurcielagos();
        // Recorre la lista de murciélagos
        for (int i = 0; i < murcielagos.size(); i++) {
            Murcielago murci = murcielagos.get(i);
            // Dibuja el murciélago si está dentro del área de juego
            if (murci.x < 600) {
                murci.dibujar();
            }
            // Hace que el murciélago persiga al mago
            murci.seguirMago(mago);
            // Limita el movimiento del murciélago dentro del área de juego
            murci.x = Math.max(MARGEN, Math.min(murci.x, 600 - MARGEN));
            murci.y = Math.max(MARGEN, Math.min(murci.y, 600 - MARGEN));
            // Verifica colisión con el mago si no es invulnerable
            if (!mago.invulnerable) {
                double dx = murci.x - mago.getX();
                double dy = murci.y - mago.getY();
                if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                    mago.reducirVida(); // Reduce la vida del mago
                    murcielagos.remove(i); // Elimina el murciélago
                    i--; // Ajusta el índice tras la eliminación
                }
            }
        }
    }

    // Punto de entrada del programa
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        // Crea una instancia del juego e inicia el entorno
        Juego juego = new Juego();
    }
}