package juego;

// Importamos las clases necesarias para manejar gráficos, colores y listas
import java.awt.Image;
import java.awt.Color;
import java.util.ArrayList;

// Importamos las herramientas del framework "entorno" para gráficos y lógica del juego
import entorno.Herramientas;
import entorno.Entorno;
import entorno.InterfaceJuego;

// Clase principal del juego, hereda de InterfaceJuego para integrarse con el framework
public class Juego extends InterfaceJuego {
    // **Constantes del juego**: valores fijos que definen reglas y dimensiones
    // Ancho del menú lateral derecho (donde van los botones y estadísticas)
    private static final int ANCHO_MENU = 200;
    // Margen para limitar el movimiento en los bordes de la pantalla
    private static final int MARGEN = 50;
    // Límite derecho del área jugable (pantalla de 600 menos el margen)
    private static final int LIMITE_DERECHO = 600 - MARGEN;
    // Límite inferior del área jugable (alto de 600 menos el margen)
    private static final int LIMITE_INFERIOR = 600 - MARGEN;
    // Cuántos frames dura un hechizo activo en pantalla
    private static final int DURACION_HECHIZO = 20;
    // Cada cuántos frames se regenera un punto de energía mágica
    private static final int FRAMES_REGEN_ENERGIA = 15;
    // Cuántos frames se muestra el mensaje de oleada (ej: "Oleada 1")
    private static final int DURACION_MENSAJE_OLEADA = 120;
    // Máximo de murciélagos que pueden estar en pantalla a la vez
    private static final int MAX_MURCIELAGOS_EN_PANTALLA = 10;

    // **Configuración de oleadas**: arrays que controlan la dificultad por oleada
    // Cuántos murciélagos aparecen por spawn en cada oleada (índice 0 = oleada 1, etc.)
    private static final int[] MURCIELAGOS_POR_SPAWN = {3, 3, 3, 3, 4};
    // Intervalo en frames entre cada spawn de murciélagos
    private static final int[] INTERVALOS_SPAWN = {200, 180, 160, 140, 135};
    // Total de enemigos que hay que eliminar para completar cada oleada
    private static final int[] ENEMIGOS_POR_OLEADA = {6, 10, 14, 18, 25};

    // **Variables de estado del juego**: controlan en qué pantalla estamos y cómo avanza
    // ¿Estamos en el menú principal?
    private boolean menu = true;
    // ¿El juego está activo (jugando)?
    private boolean juego = false;
    // ¿Perdimos (mago sin vidas)?
    private boolean juegoTerminado = false;
    // ¿Ganamos (completamos todas las oleadas)?
    private boolean juegoganado = false;
    // ¿Se pidió reiniciar el juego?
    private boolean reset = false;
    // Qué oleada estamos jugando (0 = oleada 1, 1 = oleada 2, etc.)
    private int numeroOleada = 0;
    // Contador de murciélagos eliminados en la oleada 5
    private int murcielagosEliminadosOleada5 = 0;
    // Tiempo total en frames (para mostrar segundos en pantalla)
    private int tiempoTranscurrido = 0;
    // Total de enemigos eliminados en la oleada actual
    private int enemigosEliminados = 0;
    // Contador para regenerar energía mágica cada ciertos frames
    private int contadorRecuperacion = 0;
    // Contador para controlar el spawn de murciélagos
    private int contadorSpawn = 0;
    // Mensaje de la oleada actual (ej: "Oleada 1")
    private String mensajeOleada = null;
    // Cuántos frames queda el mensaje de oleada en pantalla
    private int framesMensajeOleada = 0;

    // **Objetos del juego**: instancias de las clases que componen el juego
    // Entorno gráfico donde se dibuja todo
    private Entorno entorno;
    // Menú principal que se muestra al inicio
    private MenuPrincipal menuPrincipalF;
    // El mago, nuestro personaje jugable
    private Mago mago;
    // Lista de murciélagos enemigos en pantalla
    private ArrayList<Murcielago> murcielagos;
    // Array de rocas que actúan como obstáculos
    private Roca[] rocas;
    // Clase que maneja pociones y potenciadores
    private Objetos objetos;
    // Lista de pociones en el suelo
    private ArrayList<Objetos.Pocion> pociones;
    // Lista de potenciadores en el suelo
    private ArrayList<Objetos.Potenciador> potenciadores;
    // ¿El mago está eligiendo un potenciador?
    private boolean seleccionPotenciador = false;
    // Mapa del juego, que cambia según la oleada
    private Mapa mapeado;

    // **Variables de hechizos**: controlan los hechizos del mago
    // Hechizo Rayo (daño directo)
    private Hechizo hechizo1;
    // Hechizo Centella (daño en área)
    private Hechizo hechizo2;
    // Hechizo actualmente activo (si hay uno)
    private Hechizo hechizoLanzado = null;
    // Coordenada X del hechizo activo
    private double hechizoX = -1;
    // Coordenada Y del hechizo activo
    private double hechizoY = -1;
    // Cuántos frames lleva el hechizo activo
    private int hechizoFrames = 0;
    // Velocidad en X del hechizo Rayo
    private double rayoVelX;
    // Velocidad en Y del hechizo Rayo
    private double rayoVelY;
    // Ángulo de movimiento del hechizo Rayo
    private double rayoAngle;

    // **Imágenes**: recursos gráficos del juego
    // Fondo del área de juego
    private Image fondo;
    // Imagen del menú lateral derecho
    private Image menuImage;
    // Pantalla de "Game Over"
    private Image fin;
    // Pantalla de victoria
    private Image ganaste;
    // Título del juego en el menú principal
    private Image titulo;
    // Botón "Iniciar" (estado normal)
    private Image imagenBotonMenu;
    // Botón "Iniciar" (estado activo, con mouse encima)
    private Image imagenBotonMenu2;
    // Botón para seleccionar el hechizo Rayo
    private Image imgBotonRayo;
    // Botón para seleccionar el hechizo Centella
    private Image imgBotonCentella;
    // Botón Rayo cuando está seleccionado
    private Image rayomenuseleccionado;
    // Botón Centella cuando está seleccionado
    private Image centellamenuseleccionado;
    // Imagen del hechizo Rayo cuando se lanza
    private Image imgRayoLanzado;
    // Imagen del hechizo Centella cuando se lanza
    private Image imgCentellaLanzado;

    // **Botones**: elementos interactivos del juego
    // Botón del menú principal para iniciar el juego
    private Botonazo botonMenu;

    // **Constructor**: inicializa todo lo necesario para arrancar el juego
    public Juego() {
        // Creamos el entorno gráfico con título y resolución 800x600
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo XX - v1", 800, 600);
        // Inicializamos el menú principal
        this.menuPrincipalF = new MenuPrincipal(entorno);
        // Creamos el botón "Iniciar" en el menú (posición 600,200, tamaño 150x29)
        this.botonMenu = new Botonazo(600, 200, 150, 29, entorno);
        // Creamos el mapa con dimensiones 800x600 y oleada inicial (0)
        this.mapeado = new Mapa(800, 600, 0, entorno);
        // Cargamos la imagen de la pantalla de derrota
        this.fin = Herramientas.cargarImagen("img/seTermino.png");
        // Cargamos la imagen de la pantalla de victoria
        this.ganaste = Herramientas.cargarImagen("img/ganaste.png");
        // Cargamos y escalamos la imagen del botón "Iniciar" (estado normal)
        this.imagenBotonMenu = Herramientas.cargarImagen("img/iniciar.png")
                .getScaledInstance(botonMenu.ancho, botonMenu.alto, Image.SCALE_SMOOTH);
        // Cargamos y escalamos la imagen del botón "Iniciar" (estado activo)
        this.imagenBotonMenu2 = Herramientas.cargarImagen("img/iniciarArriba.png")
                .getScaledInstance(botonMenu.ancho, botonMenu.alto, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el título del juego
        this.titulo = Herramientas.cargarImagen("img/title.png")
                .getScaledInstance(600, 200, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el fondo del juego
        this.fondo = Herramientas.cargarImagen("img/fondo10.png")
                .getScaledInstance(1050, 805, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el menú lateral
        this.menuImage = Herramientas.cargarImagen("img/menu.jpg")
                .getScaledInstance(550, 805, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el botón del hechizo Rayo
        this.imgBotonRayo = Herramientas.cargarImagen("img/rayomenu.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el botón del hechizo Centella
        this.imgBotonCentella = Herramientas.cargarImagen("img/centellamenu.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el botón seleccionado del hechizo Rayo
        this.rayomenuseleccionado = Herramientas.cargarImagen("img/rayomenuseleccionado.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        // Cargamos y escalamos el botón seleccionado del hechizo Centella
        this.centellamenuseleccionado = Herramientas.cargarImagen("img/centellamenuseleccionado.png")
                .getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        // Creamos botones para seleccionar hechizos (pero no los guardamos, solo se dibujan)
        new Boton(700, 50, 120, 40, imgBotonRayo, entorno);
        new Boton(700, 100, 120, 40, imgBotonCentella, entorno);
        // Creamos el hechizo Rayo: sin costo de magia, 150 de daño
        this.hechizo1 = new Hechizo("RAYO", 0, 150);
        // Creamos el hechizo Centella: 50 de costo, 80 de daño
        this.hechizo2 = new Hechizo("CENTELLA", 50, 80);
        // Cargamos y escalamos la imagen del hechizo Rayo lanzado
        this.imgRayoLanzado = Herramientas.cargarImagen("img/rayo.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        // Cargamos y escalamos la imagen del hechizo Centella lanzado
        this.imgCentellaLanzado = Herramientas.cargarImagen("img/hechizoArea.png")
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        // Creamos al mago en el centro de la pantalla (x=400, y=300)
        this.mago = new Mago(400, 300, entorno);
        // Establecemos 100 como vida máxima
        this.mago.vidasMaximas = 100;
        // Establecemos 100 como energía mágica máxima
        this.mago.energiaMagicaMaxima = 100;
        // Aseguramos que las vidas no superen el máximo
        this.mago.vidas = Math.min(mago.vidas, mago.vidasMaximas);
        // Aseguramos que la energía no supere el máximo
        this.mago.energiaMagica = Math.min(mago.energiaMagica, mago.energiaMagicaMaxima);
        // Inicializamos la lista de murciélagos
        this.murcielagos = new ArrayList<>();
        // Creamos el objeto que maneja pociones y potenciadores
        this.objetos = new Objetos(entorno);
        // Inicializamos la lista de pociones
        this.pociones = new ArrayList<>();
        // Inicializamos la lista de potenciadores
        this.potenciadores = new ArrayList<>();
        // Creamos las rocas en posiciones fijas como obstáculos
        this.rocas = new Roca[] {
                new Roca(200, 150),
                new Roca(550, 200),
                new Roca(250, 450),
                new Roca(450, 150),
                new Roca(150, 350)
        };
        // Iniciamos el entorno gráfico para que el juego comience
        this.entorno.iniciar();
    }

    // **Método generarMurcielagosOleada**: crea una cantidad específica de murciélagos
    private void generarMurcielagosOleada(int cant) {
        // Contador de murciélagos generados
        int murcielagosGenerados = 0;
        // Seguimos hasta generar la cantidad deseada
        while (murcielagosGenerados < cant) {
            // Variable para el nuevo murciélago
            Murcielago nuevoMurcielago;
            // ¿Se superpone con otro murciélago?
            boolean superpuesto;
            // ¿Está muy cerca del mago?
            boolean demasiadoCerca;
            // Contador de intentos para evitar bucles infinitos
            int intentos = 0;
            do {
                // Asignamos vida según la oleada: 35 en oleada 3, 140 en oleada 4, 20 en las demás
                int vidaInicial = (numeroOleada == 3) ? 35 : (numeroOleada == 4) ? 140 : 20;
                // Creamos el murciélago con la vida inicial
                nuevoMurcielago = new Murcielago(entorno, vidaInicial);
                // Elegimos un borde aleatorio para que aparezca (0: arriba, 1: derecha, 2: abajo, 3: izquierda)
                int borde = (int)(Math.random() * 4);
                switch (borde) {
                    case 0: // Borde superior
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = MARGEN;
                        break;
                    case 1: // Borde derecho
                        nuevoMurcielago.x = LIMITE_DERECHO;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
                        break;
                    case 2: // Borde inferior
                        nuevoMurcielago.x = (int)(Math.random() * (LIMITE_DERECHO - MARGEN)) + MARGEN;
                        nuevoMurcielago.y = LIMITE_INFERIOR;
                        break;
                    case 3: // Borde izquierdo
                        nuevoMurcielago.x = MARGEN;
                        nuevoMurcielago.y = (int)(Math.random() * (LIMITE_INFERIOR - MARGEN)) + MARGEN;
                        break;
                }
                // Verificamos si se superpone con otros murciélagos
                superpuesto = estaMurcielagoSuperpuesto(nuevoMurcielago);
                // Verificamos si está muy cerca del mago
                demasiadoCerca = estaMurcielagoCercaDeMago(nuevoMurcielago);
                // Incrementamos los intentos
                intentos++;
            // Seguimos intentando hasta que no haya superposición ni cercanía, o hasta 50 intentos
            } while ((superpuesto || demasiadoCerca) && intentos < 50);
            // Si la posición es válida, agregamos el murciélago a la lista
            if (!superpuesto && !demasiadoCerca) {
                murcielagos.add(nuevoMurcielago);
                murcielagosGenerados++;
            }
        }
    }

    // **Método generarMurcielagos**: genera murciélagos respetando el límite en pantalla
    private void generarMurcielagos() {
        // Si ya hay demasiados murciélagos, no generamos más
        if (murcielagos.size() >= MAX_MURCIELAGOS_EN_PANTALLA) {
            return;
        }
        // Calculamos cuántos murciélagos podemos generar según la oleada y el límite
        int murcielagosAGenerar = Math.min(MURCIELAGOS_POR_SPAWN[numeroOleada], 
                MAX_MURCIELAGOS_EN_PANTALLA - murcielagos.size());
        // Si hay murciélagos por generar, llamamos al método
        if (murcielagosAGenerar > 0) {
            generarMurcielagosOleada(murcielagosAGenerar);
        }
    }

    // **Método estaMurcielagoSuperpuesto**: verifica si un murciélago se superpone con otros
    private boolean estaMurcielagoSuperpuesto(Murcielago nuevo) {
        // Recorremos todos los murciélagos existentes
        for (Murcielago murci : murcielagos) {
            // Si la distancia entre ellos es menor a 50 píxeles, están superpuestos
            if (Math.abs(nuevo.x - murci.x) < 50 && Math.abs(nuevo.y - murci.y) < 50) {
                return true;
            }
        }
        return false;
    }

    // **Método estaMurcielagoCercaDeMago**: verifica si un murciélago está muy cerca del mago
    private boolean estaMurcielagoCercaDeMago(Murcielago nuevo) {
        // Calculamos la distancia euclidiana al mago
        double distanciaAlMago = Math.sqrt(
                Math.pow(nuevo.x - mago.x, 2) + Math.pow(nuevo.y - mago.y, 2)
        );
        // Si está a menos de 100 píxeles, está demasiado cerca
        return distanciaAlMago < 100;
    }

    // **Método mantenerDistanciaMurcielagos**: evita que los murciélagos se amontonen
    private void mantenerDistanciaMurcielagos() {
        // Recorremos todos los murciélagos
        for (Murcielago murci : murcielagos) {
            // Comparamos con los demás murciélagos
            for (Murcielago otroMurci : murcielagos) {
                // Calculamos la distancia entre ellos
                double dx = murci.x - otroMurci.x;
                double dy = murci.y - otroMurci.y;
                double distancia = Math.sqrt(dx * dx + dy * dy);
                // Si están a menos de 50 píxeles y no son el mismo murciélago
                if (distancia < 50 && distancia > 0) {
                    // Los separamos un poco para evitar superposición
                    murci.x += dx / distancia * 1.5;
                    murci.y += dy / distancia * 1.5;
                }
            }
        }
    }

    // **Método lanzarHechizo**: lanza un hechizo hacia un punto en la pantalla
    private void lanzarHechizo(Hechizo hechizo, double targetX, double targetY) {
        // Si no hay suficiente energía mágica, no lanzamos
        if (mago.energiaMagica < hechizo.costoMagia) {
            return;
        }
        // Descontamos la energía mágica necesaria
        mago.energiaMagica -= hechizo.costoMagia;
        // Activamos el hechizo por una duración fija
        hechizoFrames = DURACION_HECHIZO;
        // Guardamos el hechizo activo
        hechizoLanzado = hechizo;

        // Configuramos según el tipo de hechizo
        if (hechizo.nombre.equals("RAYO")) {
            configurarHechizoRayo(targetX, targetY);
        } else if (hechizo.nombre.equals("CENTELLA")) {
            configurarHechizoCentella();
        }
    }

    // **Método configurarHechizoRayo**: calcula la trayectoria del hechizo Rayo
    private void configurarHechizoRayo(double targetX, double targetY) {
        // El hechizo sale desde la posición del mago
        hechizoX = mago.x;
        hechizoY = mago.y;
        // Calculamos la diferencia en X e Y hasta el objetivo
        double dx = targetX - mago.x;
        double dy = targetY - mago.y;
        // Calculamos el ángulo del movimiento
        rayoAngle = Math.atan2(dy, dx);
        // Velocidad fija del hechizo (10 píxeles por frame)
        double velocidad = 10.0;
        // Calculamos la distancia al objetivo
        double distancia = Math.sqrt(dx * dx + dy * dy);
        // Si hay distancia, calculamos las velocidades en X e Y
        if (distancia > 0) {
            rayoVelX = (dx / distancia) * velocidad;
            rayoVelY = (dy / distancia) * velocidad;
        } else {
            // Si no hay distancia (clic en el mago), no se mueve
            rayoVelX = 0;
            rayoVelY = 0;
        }
    }

    // **Método configurarHechizoCentella**: aplica daño en área para el hechizo Centella
    private void configurarHechizoCentella() {
        // El hechizo se centra en la posición del mago
        hechizoX = mago.x;
        hechizoY = mago.y;
        // Lista para los murciélagos que serán eliminados
        ArrayList<Murcielago> eliminados = new ArrayList<>();
        // Radio del área de efecto (60 píxeles)
        int radio = 60;
        // Recorremos todos los murciélagos
        for (Murcielago m : murcielagos) {
            // Calculamos la distancia al mago
            double distancia = Math.sqrt(
                    Math.pow(m.x - mago.x, 2) + Math.pow(m.y - mago.y, 2)
            );
            // Si están dentro del radio, reciben daño
            if (distancia <= radio) {
                m.vida -= hechizo2.area;
                // Si se quedan sin vida, los marcamos para eliminar
                if (m.vida <= 0) {
                    eliminados.add(m);
                    soltarObjetosAlMorir(m);
                }
            }
        }
        // Sumamos los eliminados al contador
        enemigosEliminados += eliminados.size();
        // Quitamos los murciélagos eliminados de la lista
        murcielagos.removeAll(eliminados);
    }

    // **Método soltarObjetosAlMorir**: suelta pociones o potenciadores al morir un murciélago
    private void soltarObjetosAlMorir(Murcielago m) {
        // Si el mago tiene poca vida (80 o menos), 5% de chance de soltar una poción
        if (mago.vidas <= 80 && Math.random() < 0.05) {
            // Creamos una nueva poción en la posición del murciélago
            Objetos.Pocion nuevaPocion = objetos.new Pocion((int)m.x, (int)m.y, objetos);
            // Si está más allá del límite derecho, la reposicionamos
            if (nuevaPocion.x >= 600) {
                nuevaPocion.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            // Agregamos la poción a la lista
            pociones.add(nuevaPocion);
        // Si no, 2% de chance de soltar un potenciador
        } else if (Math.random() < 0.02) {
            // Creamos un nuevo potenciador en la posición del murciélago
            Objetos.Potenciador nuevoPotenciador = objetos.new Potenciador((int)m.x, (int)m.y, objetos);
            // Si está más allá del límite derecho, lo reposicionamos
            if (nuevoPotenciador.x >= 600) {
                nuevoPotenciador.x = (int)(Math.random() * (600 - 50)) + 50;
            }
            // Agregamos el potenciador a la lista
            potenciadores.add(nuevoPotenciador);
        }
        // Incrementamos el contador de enemigos eliminados
        enemigosEliminados++;
        // Si es la oleada 5, contamos los murciélagos eliminados
        if (numeroOleada == 4) {
            murcielagosEliminadosOleada5++;
        }
    }

    // **Método reiniciarJuego**: resetea el juego a su estado inicial
    private void reiniciarJuego() {
        // Desactivamos las pantallas de victoria y derrota
        juegoTerminado = false;
        juegoganado = false;
        // Creamos un mago nuevo en el centro
        mago = new Mago(400, 300, entorno);
        // Configuramos sus stats máximos
        mago.vidasMaximas = 100;
        mago.energiaMagicaMaxima = 100;
        // Aseguramos que no exceda los máximos
        mago.vidas = Math.min(mago.vidas, mago.vidasMaximas);
        mago.energiaMagica = Math.min(mago.energiaMagica, mago.energiaMagicaMaxima);
        // Vaciamos la lista de murciélagos
        murcielagos.clear();
        // Reseteamos contadores
        tiempoTranscurrido = 0;
        enemigosEliminados = 0;
        murcielagosEliminadosOleada5 = 0;
        // Desactivamos cualquier hechizo activo
        hechizoLanzado = null;
        hechizoFrames = 0;
        // Deseleccionamos los hechizos
        hechizo1.deseleccionar();
        hechizo2.deseleccionar();
        // Vaciamos las listas de pociones y potenciadores
        pociones.clear();
        potenciadores.clear();
        // Desactivamos la selección de potenciador
        seleccionPotenciador = false;
        // Volvemos al mapa inicial (oleada 1)
        mapeado.setNumeroMapa(0);
        // Volvemos a la primera oleada
        numeroOleada = 0;
        // Quitamos el mensaje de oleada
        mensajeOleada = null;
        framesMensajeOleada = 0;
        contadorSpawn = 0;
        // Volvemos al menú principal
        menu = true;
        juego = false;
    }

    // **Método dibujarMensajeOleada**: muestra el mensaje de la oleada en pantalla
    private void dibujarMensajeOleada() {
        // Si hay un mensaje y no se acabó el tiempo
        if (mensajeOleada != null && framesMensajeOleada > 0) {
            // Usamos fuente Arial, tamaño 40, color amarillo
            entorno.cambiarFont("Arial", 40, Color.YELLOW);
            // Mostramos el mensaje centrado en la pantalla
            entorno.escribirTexto(mensajeOleada, 200, 300);
            // Reducimos el contador de frames
            framesMensajeOleada--;
            // Si se acaba el tiempo, borramos el mensaje
            if (framesMensajeOleada <= 0) {
                mensajeOleada = null;
            }
        }
    }

    // **Método tick**: bucle principal del juego, se ejecuta cada frame
    @Override
    public void tick() {
        // Según el estado, manejamos el menú, el juego, victoria o derrota
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

        // **Control de oleadas**: si estamos jugando y no hemos ganado ni perdido
        if (juego && !juegoTerminado && !juegoganado) {
            // Incrementamos el contador de spawn
            contadorSpawn++;
            // Si alcanzamos el intervalo de spawn de la oleada
            if (contadorSpawn >= INTERVALOS_SPAWN[numeroOleada]) {
                // Generamos murciélagos si no hemos completado la oleada
                if (numeroOleada < 4 && enemigosEliminados < ENEMIGOS_POR_OLEADA[numeroOleada] ||
                    numeroOleada == 4 && murcielagosEliminadosOleada5 < ENEMIGOS_POR_OLEADA[4]) {
                    generarMurcielagos();
                }
                // Reseteamos el contador de spawn
                contadorSpawn = 0;
            }

            // **Transiciones entre oleadas**
            // De oleada 1 a 2
            if (numeroOleada == 0 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[0] && murcielagos.size() == 0) {
                numeroOleada = 1;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 2";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
                enemigosEliminados = 0;
            // De oleada 2 a 3
            } else if (numeroOleada == 1 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[1] && murcielagos.size() == 0) {
                numeroOleada = 2;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 3";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
                enemigosEliminados = 0;
            // De oleada 3 a 4
            } else if (numeroOleada == 2 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[2] && murcielagos.size() == 0) {
                numeroOleada = 3;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 4";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
                enemigosEliminados = 0;
            // De oleada 4 a 5
            } else if (numeroOleada == 3 && enemigosEliminados >= ENEMIGOS_POR_OLEADA[3] && murcielagos.size() == 0) {
                numeroOleada = 4;
                mapeado.setNumeroMapa(numeroOleada + 1);
                mensajeOleada = "Oleada 5";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                contadorSpawn = 0;
                mago.x = 400;
                mago.y = 300;
                enemigosEliminados = 0;
            // Victoria al completar la oleada 5
            } else if (numeroOleada == 4 && murcielagosEliminadosOleada5 >= ENEMIGOS_POR_OLEADA[4] && murcielagos.size() == 0) {
                juegoganado = true;
            }
            // Si el mago se queda sin vidas, perdemos
            if (mago.vidas <= 0) {
                juegoTerminado = true;
            }
        }

        // **Reinicio del juego**: si se presiona 'r' al ganar o perder
        if ((juegoTerminado || juegoganado) && entorno.sePresiono('r')) {
            reset = true;
        }
        if (reset) {
            reiniciarJuego();
            reset = false;
        }
    }

    // **Método manejarMenuPrincipal**: maneja la lógica y dibujo del menú principal
    private void manejarMenuPrincipal() {
        // Dibujamos el fondo del menú principal
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        // Mostramos el título del juego en la parte superior
        entorno.dibujarImagen(titulo, 400, 90, 0.0);
        // Por defecto, usamos la imagen normal del botón "Iniciar"
        Image botonImagen = imagenBotonMenu;
        // Si el mouse está sobre el botón
        if (botonMenu.contienePunto(entorno.mouseX(), entorno.mouseY())) {
            // Cambiamos a la imagen activa (hover)
            botonImagen = imagenBotonMenu2;
            // Si se hace clic izquierdo
            if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
                // Salimos del menú y empezamos el juego
                menu = false;
                juego = true;
                // Mostramos el mensaje de la primera oleada
                mensajeOleada = "Oleada 1";
                framesMensajeOleada = DURACION_MENSAJE_OLEADA;
                // Generamos los primeros murciélagos
                generarMurcielagos();
                // Reseteamos el contador de spawn
                contadorSpawn = 0;
            }
        }
        // Dibujamos el botón con la imagen correspondiente
        botonMenu.dibujarBoton(botonImagen);
    }

    // **Método manejarPantallaVictoria**: muestra la pantalla de victoria
    private void manejarPantallaVictoria() {
        // Dibujamos el fondo del menú
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        // Mostramos la imagen de victoria centrada
        entorno.dibujarImagen(ganaste, 400, 300, 0.0);
    }

    // **Método manejarPantallaDerrota**: muestra la pantalla de derrota
    private void manejarPantallaDerrota() {
        // Dibujamos el fondo del menú
        menuPrincipalF.dibujarMenuPrincipal(400, 300);
        // Mostramos la imagen de derrota centrada
        entorno.dibujarImagen(fin, 400, 300, 0.0);
    }

    // **Método manejarJuego**: maneja la lógica principal del juego
    private void manejarJuego() {
        // Incrementamos el tiempo transcurrido (en frames)
        tiempoTranscurrido++;
        // Dibujamos el fondo y la interfaz
        dibujarFondoYUI();
        // Manejamos la selección y lanzamiento de hechizos
        manejarEntradaHechizos();
        // Actualizamos y dibujamos el hechizo activo
        actualizarYDibujarHechizo();
        // Dibujamos los objetos del juego (rocas, pociones, etc.)
        dibujarObjetosJuego();
        // Manejamos el movimiento del mago
        manejarMovimientoMago();
        // Manejamos las interacciones con pociones y potenciadores
        manejarInteraccionesObjetos();
        // Actualizamos y dibujamos los murciélagos
        actualizarYDibujarEnemigos();
        // Actualizamos el estado del mago (invulnerabilidad, energía)
        actualizarEstadoMago();
        // Mostramos el mensaje de la oleada
        dibujarMensajeOleada();
    }

    // **Método dibujarFondoYUI**: dibuja el fondo y la interfaz de usuario
    private void dibujarFondoYUI() {
        // Dibujamos el fondo del juego, escalado al 75%
        entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0.0, 0.75);
        // Dibujamos el menú lateral derecho, también al 75%
        entorno.dibujarImagen(menuImage, entorno.ancho(), entorno.alto() / 2, 0.0, 0.75);
        // Mostramos los botones de los hechizos Rayo y Centella
        entorno.dibujarImagen(imgBotonRayo, 700, 50, 0);
        entorno.dibujarImagen(imgBotonCentella, 700, 100, 0);
        // Si el hechizo Rayo está seleccionado, mostramos su imagen activa
        if (hechizo1.seleccionado) {
            entorno.dibujarImagen(rayomenuseleccionado, 700, 50, 0.0);
        // Si el hechizo Centella está seleccionado, mostramos su imagen activa
        } else if (hechizo2.seleccionado) {
            entorno.dibujarImagen(centellamenuseleccionado, 700, 100, 0.0);
        }
        // Dibujamos las barras de vida y energía
        dibujarBarrasEstado();
        // Mostramos las estadísticas (tiempo, enemigos, oleada)
        dibujarEstadisticasJuego();
    }

    // **Método dibujarBarrasEstado**: dibuja las barras de vida y energía del mago
    private void dibujarBarrasEstado() {
        // **Barra de energía**
        // Definimos posición y tamaño de la barra
        int barraX = 620, barraY = 400, anchoBarra = 150, altoBarra = 20;
        // Dibujamos el fondo de la barra (gris oscuro)
        entorno.dibujarRectangulo(barraX + anchoBarra / 2, barraY + altoBarra / 2,
                anchoBarra, altoBarra, 0, Color.DARK_GRAY);
        // Calculamos la proporción de energía actual
        double proporcionEnergia = mago.energiaMagica / (double) mago.energiaMagicaMaxima;
        // Calculamos el ancho del relleno según la proporción
        int anchoRelleno = (int)(anchoBarra * proporcionEnergia);
        // Si hay energía, dibujamos el relleno (azul)
        if (anchoRelleno > 0) {
            entorno.dibujarRectangulo(barraX + anchoRelleno / 2, barraY + altoBarra / 2,
                    anchoRelleno, altoBarra, 0, Color.BLUE);
        }
        // Mostramos el texto con la energía actual y máxima
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(mago.energiaMagica + "/" + mago.energiaMagicaMaxima,
                barraX + 55, barraY + 15);
        // Mostramos la etiqueta "ENERGIA"
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENERGIA", barraX + 35, barraY - 5);

        // **Barra de vida**
        // Definimos posición y tamaño de la barra
        int vidaX = barraX, vidaY = 460, anchoVida = 150, altoVida = 20;
        // Dibujamos el fondo de la barra (gris oscuro)
        entorno.dibujarRectangulo(vidaX + anchoVida / 2, vidaY + altoVida / 2,
                anchoVida, altoVida, 0, Color.DARK_GRAY);
        // Calculamos la proporción de vida actual
        double proporcionVida = mago.vidas / (double)mago.vidasMaximas;
        // Calculamos el ancho del relleno según la proporción
        int anchoRellenoVida = (int)(anchoVida * proporcionVida);
        // Si hay vida, dibujamos el relleno (rojo)
        if (anchoRellenoVida > 0) {
            entorno.dibujarRectangulo(vidaX + anchoRellenoVida / 2, vidaY + altoVida / 2,
                    anchoRellenoVida, altoVida, 0, Color.RED);
        }
        // Mostramos el texto con la vida actual y máxima
        entorno.cambiarFont("Arial", 14, Color.WHITE);
        entorno.escribirTexto(mago.vidas + "/" + mago.vidasMaximas, vidaX + 55, vidaY + 15);
        // Mostramos la etiqueta "VIDA"
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("VIDA", vidaX + 45, vidaY - 5);
    }

    // **Método dibujarEstadisticasJuego**: muestra las estadísticas del juego
    private void dibujarEstadisticasJuego() {
        // Mostramos el tiempo transcurrido en segundos (frames / 60)
        entorno.cambiarFont("MV Boli", 30, Color.WHITE);
        entorno.escribirTexto(String.format("%d s", tiempoTranscurrido / 60),
                entorno.ancho() - 140, 550);
        // Mostramos las estadísticas de enemigos eliminados y oleada
        entorno.cambiarFont("MV Boli", 20, Color.WHITE);
        entorno.escribirTexto("ENEMIGOS", 650, 240);
        entorno.escribirTexto("ELIMINADOS", 645, 270);
        entorno.escribirTexto("" + enemigosEliminados, 700, 300);
        entorno.escribirTexto("OLEADA", 650, 150);
        entorno.escribirTexto("" + (numeroOleada + 1), 700, 180);
    }

    // **Método manejarEntradaHechizos**: maneja la selección y lanzamiento de hechizos
    private void manejarEntradaHechizos() {
        // Solo actuamos si no estamos eligiendo un potenciador y se hace clic izquierdo
        if (!seleccionPotenciador && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            // Obtenemos la posición del mouse
            int mouseX = entorno.mouseX();
            int mouseY = entorno.mouseY();
            // Si se hace clic en el botón del hechizo Rayo (posición 700,50, tamaño 120x40)
            if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 50 - 20 && mouseY <= 50 + 20) {
                hechizo1.seleccionar();
                hechizo2.deseleccionar();
            // Si se hace clic en el botón del hechizo Centella (posición 700,100)
            } else if (mouseX >= 700 - 60 && mouseX <= 700 + 60 && mouseY >= 100 - 20 && mouseY <= 100 + 20) {
                hechizo2.seleccionar();
                hechizo1.deseleccionar();
            // Si se hace clic en el área jugable (izquierda del menú)
            } else if (mouseX < entorno.ancho() - ANCHO_MENU) {
                // Elegimos el hechizo seleccionado es Rayo o Centella
                Hechizo hechizo = hechizo1.seleccionado ? hechizo1 : (hechizo2.seleccionado ? hechizo2 : null);
                // Si hay un hechizo seleccionado, lo lanzamos
                if (hechizo != null) {
                    lanzarHechizo(hechizo, mouseX, mouseY);
                    hechizo.deseleccionar();
                }
            }
        }
    }

    // **Método actualizarYDibujarHechizo**: actualiza y dibuja el hechizo activo
    private void actualizarYDibujarHechizo() {
        // Si no hay hechizo activo o se acabó el tiempo, no hacemos nada
        if (hechizoFrames <= 0 || hechizoLanzado == null) {
            return;
        }
        // Según el hechizo, actualizamos y dibujamos
        if (hechizoLanzado.nombre.equals("RAYO")) {
            actualizarYDibujarRayo();
        } else if (hechizoLanzado.nombre.equals("CENTELLA")) {
            // Para Centella, solo dibujamos su imagen en la posición del mago
            entorno.dibujarImagen(imgCentellaLanzado, hechizoX, hechizoY, 0.0);
        }
        // Reducimos el contador de frames
        hechizoFrames--;
        // Si se acaba el tiempo, desactivamos el hechizo
        if (hechizoFrames == 0) {
            hechizoLanzado = null;
        }
    }

    // **Método actualizarYDibujarRayo**: actualiza y dibuja el hechizo Rayo
    private void actualizarYDibujarRayo() {
        // Actualizamos la posición del hechizo según su velocidad
        hechizoX += rayoVelX;
        hechizoY += rayoVelY;
        // Lista para los murciélagos que serán eliminados
        ArrayList<Murcielago> eliminados = new ArrayList<>();
        // Dimensiones del área de daño del Rayo
        double rayoAncho = 60, rayoAlto = 30;
        // Chequeamos colisiones con murciélagos
        for (Murcielago m : murcielagos) {
            // Si el hechizo golpea al murciélago
            if (Math.abs(m.x - hechizoX) <= rayoAncho / 2 && Math.abs(m.y - hechizoY) <= rayoAlto / 2) {
                // Le quitamos vida según el daño del hechizo
                m.vida -= hechizo1.area;
                // Si se queda sin vida, lo marcamos para eliminar
                if (m.vida <= 0) {
                    eliminados.add(m);
                    soltarObjetosAlMorir(m);
                }
            }
        }
        // Quitamos los murciélagos eliminados
        murcielagos.removeAll(eliminados);
        // Si el hechizo sale del área jugable, lo desactivamos
        if (hechizoX < 0 || hechizoX > entorno.ancho() - ANCHO_MENU ||
            hechizoY < 0 || hechizoY > entorno.alto()) {
            hechizoFrames = 0;
            hechizoLanzado = null;
        } else {
            // Dibujamos el hechizo con su ángulo de movimiento
            entorno.dibujarImagen(imgRayoLanzado, hechizoX, hechizoY, rayoAngle);
        }
    }

    // **Método dibujarObjetosJuego**: dibuja todos los objetos del juego
    private void dibujarObjetosJuego() {
        // Dibujamos cada roca (obstáculos)
        for (Roca roca : rocas) {
            roca.dibujar(entorno);
        }
        // Dibujamos cada poción en el suelo
        for (Objetos.Pocion pocion : pociones) {
            pocion.dibujar();
        }
        // Dibujamos cada potenciador en el suelo
        for (Objetos.Potenciador potenciador : potenciadores) {
            potenciador.dibujar();
        }
        // Dibujamos al mago
        mago.dibujar();
    }

    // **Método manejarMovimientoMago**: maneja el movimiento del mago con teclas
    private void manejarMovimientoMago() {
        // Si hay un hechizo activo o estamos eligiendo un potenciador, no nos movemos
        if (hechizoFrames > 0 || seleccionPotenciador) {
            return;
        }
        // Guardamos la posición actual del mago
        int nuevaX = mago.x;
        int nuevaY = mago.y;
        // Movemos según las teclas presionadas (velocidad: 3 píxeles por frame)
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

        // Chequeamos colisiones con las rocas
        boolean hayColision = false;
        for (Roca roca : rocas) {
            if (roca.colisionaCon(nuevaX, nuevaY)) {
                hayColision = true;
                break;
            }
        }

        // Definimos márgenes para mantener al mago dentro del área jugable
        int margenX = 30, margenY = 40;
        // Si no hay colisión y estamos dentro de los límites, actualizamos la posición
        if (!hayColision && nuevaX >= margenX && nuevaX <= LIMITE_DERECHO &&
                nuevaY >= margenY && nuevaY <= entorno.alto() - margenY) {
            mago.x = nuevaX;
            mago.y = nuevaY;
        }
    }

    // **Método manejarInteraccionesObjetos**: maneja colisiones con pociones y potenciadores
    private void manejarInteraccionesObjetos() {
        // Chequeamos colisiones con pociones
        for (Objetos.Pocion pocion : pociones) {
            // Si el mago toca una poción
            if (pocion.colisionConMago(mago)) {
                // Aumentamos la vida en 30, respetando el máximo
                mago.vidas = Math.min(mago.vidas + 30, mago.vidasMaximas);
                // Desactivamos la poción
                pocion.desactivar();
            }
        }
        // Chequeamos colisiones con potenciadores
        for (Objetos.Potenciador potenciador : potenciadores) {
            // Si el mago toca un potenciador y no está eligiendo uno
            if (potenciador.colisionConMago(mago) && !seleccionPotenciador) {
                // Activamos el modo de selección
                seleccionPotenciador = true;
                // Desactivamos el potenciador
                potenciador.desactivar();
            }
        }
        // Si estamos eligiendo un potenciador
        if (seleccionPotenciador) {
            // Mostramos las opciones de mejora
            entorno.cambiarFont("Arial", 30, Color.WHITE);
            entorno.escribirTexto("Elige mejora:", 200, 250);
            entorno.escribirTexto("1 - Vida +10", 200, 280);
            entorno.escribirTexto("2 - Energía +10", 200, 310);
            // Si se presiona '1', mejoramos la vida
            if (entorno.sePresiono('1')) {
                mago.vidasMaximas += 10;
                mago.vidas = Math.min(mago.vidas + 10, mago.vidasMaximas);
                seleccionPotenciador = false;
            // Si se presiona '2', mejoramos la energía
            } else if (entorno.sePresiono('2')) {
                mago.energiaMagicaMaxima += 10;
                mago.energiaMagica = Math.min(mago.energiaMagica + 10, mago.energiaMagicaMaxima);
                seleccionPotenciador = false;
            }
        }
        // Quitamos las pociones y potenciadores desactivados
        pociones.removeIf(pocion -> !pocion.activa);
        potenciadores.removeIf(potenciador -> !potenciador.activa);
    }

    // **Método actualizarYDibujarEnemigos**: actualiza y dibuja los murciélagos
    private void actualizarYDibujarEnemigos() {
        // Si estamos eligiendo un potenciador, no actualizamos enemigos
        if (seleccionPotenciador) {
            return;
        }
        // Evitamos que los murciélagos se amontonen
        mantenerDistanciaMurcielagos();
        // Recorremos todos los murciélagos
        for (int i = 0; i < murcielagos.size(); i++) {
            Murcielago murci = murcielagos.get(i);
            // Solo dibujamos si está dentro del área jugable (x < 600)
            if (murci.x < 600) {
                murci.dibujar();
            }
            // Hacemos que el murciélago persiga al mago
            murci.seguirMago(mago);
            // Limitamos su movimiento dentro del área jugable
            murci.x = Math.max(MARGEN, Math.min(murci.x, LIMITE_DERECHO));
            murci.y = Math.max(MARGEN, Math.min(murci.y, LIMITE_INFERIOR));
            // Si el mago no es invulnerable, chequeamos colisiones
            if (!mago.invulnerable) {
                // Calculamos la distancia entre el murciélago y el mago
                double dx = murci.x - mago.x;
                double dy = murci.y - mago.y;
                // Si están muy cerca (menos de 20 píxeles), el mago recibe daño
                if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                    mago.reducirVida();
                    // Eliminamos el murciélago
                    murcielagos.remove(i);
                    i--;
                }
            }
        }
    }

    // **Método actualizarEstadoMago**: actualiza el estado del mago
    private void actualizarEstadoMago() {
        // Actualizamos el estado de invulnerabilidad (si aplica)
        mago.invulnerabilidad();
        // Si no estamos eligiendo un potenciador y falta energía
        if (!seleccionPotenciador && mago.energiaMagica < mago.energiaMagicaMaxima) {
            // Incrementamos el contador de recuperación
            contadorRecuperacion++;
            // Si alcanzamos el intervalo de regeneración
            if (contadorRecuperacion >= FRAMES_REGEN_ENERGIA) {
                // Regeneramos un punto de energía
                mago.energiaMagica++;
                // Reseteamos el contador
                contadorRecuperacion = 0;
            }
        }
    }

    // **Método main**: punto de entrada del programa
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        // Creamos una instancia del juego para iniciarlo
        Juego juego = new Juego();
    }
}