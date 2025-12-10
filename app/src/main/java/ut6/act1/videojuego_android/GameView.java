package ut6.act1.videojuego_android;

// IMPORTS
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;

/**
 * Esta es la clase que funciona como motor del juego.
 *
 * En este caso heredamos de SurfaceView que dibuja los elementos del juego de forma asíncrona e independiente,
 * lo que permite que el juego pueda dibujar todos los elementos que tiene sin ir uno a uno saturando el hilo principal.
 *
 * Implementamos juntamente Runnable que permite ejecutar el proceso del juego en segundo plano sin interferir en el hilo principal.
 */
public class GameView extends SurfaceView implements Runnable {
    // Creamos el thread que ejecutara los 60 FPS
    private Thread hiloJuego;

    // Elementos para dibujar nuestro juego
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    // Esto es el mapa de bits de la imagen de fondo
    private Bitmap imagenFondo, imagenObstaculos, imagenMapache;
    private Paint fondoOscuro;

    // Creamos el mapache y el array de objetos (y sus hitboxs)
    private Mapache mapache;
    ArrayList<Objeto> objetos = new ArrayList<>();

    // Variable para parar el juego cuando un objeto colisione con el mapache
    private boolean alive = true;

    // Variables tiempo
    private long lastSpawn = System.currentTimeMillis(), lastNivel = System.currentTimeMillis();
    private int spawnTime = 3000, nivelTime = 8000;

    // Variable para contabilizar la puntuación y paint para dibujarla
    private int puntuacion = 0;
    private Paint paintPuntuacion = new Paint();
    private Typeface fuente;

    // Constructor de la clase
    public GameView(Context context) {
        super(context);

        // Inicializamos los objetos de dibujo
        surfaceHolder = getHolder();
        paint = new Paint();

        // Cargamos la imagen de fondo
        imagenFondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.fondo);

        // Aplicamos un filtro oscurecedor
        fondoOscuro = new Paint();
        fondoOscuro.setColorFilter(new PorterDuffColorFilter(0x80000000, PorterDuff.Mode.SRC_ATOP));

        // Cargamos el spreadsheet de los objetos
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        imagenObstaculos = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstaculos, options);
        imagenMapache = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapache, options);

        // Importamos la fuente del juego
        fuente = ResourcesCompat.getFont(context, R.font.pixellari);

        // Creamos el estilo del marcador de puntuación
        paintPuntuacion.setColor(android.graphics.Color.WHITE);
        paintPuntuacion.setTextSize(90);
        paintPuntuacion.setTextAlign(Paint.Align.CENTER);
        paintPuntuacion.setTypeface(fuente);

        // Añadimos sombra sobre este texto (hecho con IA)
        paintPuntuacion.setShadowLayer(10f, 5f, 5f, android.graphics.Color.BLACK);
    }

    /**
     * Funcion que escucha la pantalla y detecta si debemos ir a la izquierda o derecha.
     *
     * @param event Recibe el evento que se ha realizado en la pantalla
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Calculamos la mitad de la pantalla
        int mitad = getWidth() / 2;

        // Comprobamos que sea un toque o slide
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // Si el toque ha sido de la mitad a la izquierda, mover a la izq.
            if (event.getX() < mitad) {
                mapache.setMoviendoIzquierda(true);
            }
            // Sino mover a la der.
            else {
                mapache.setMoviendoDerecha(true);
            }
        }
        // Si no hay toque ni slide parar el movimiento
        else {
            mapache.setMoviendoIzquierda(false);
            mapache.setMoviendoDerecha(false);
        }

        return true;
    }

    /**
     * Esta es la función que ejecuta el bucle del juego (actualizar, dibujar, etc.)
     */
    @Override
    public void run() {
        // Mientras el mapache siga con vida, seguir ejecutando
        while (alive) {
            // Actualizamos las posiciones
            update();
            // Dibujamos los elementos
            draw();
            // Controlamos los FPS
            control();
        }
    }

    private void update() {
        // Si el mapache existe, actualizar su estado
        if (mapache != null) {
            mapache.update();
        }

        /*
            Actualizamos todos los objetos del array
            (Recorremos el array al reves para evitar un error de indice al borrar un objeto mientras se ejecuta el array)
         */
        for (int i = objetos.size() - 1; i >= 0; i--) {
            Objeto objeto = objetos.get(i);

            // Obtenemos la velocidad del objeto para comprobar si esta fuera de la pantalla
            int velocidad = objeto.getVelocidad();

            // Si esta fuera de la pantalla, lo borramos
            if (velocidad == 0) {
                objetos.remove(objeto);

                // Sumamos puntos por haber evitado el objeto
                puntuacion += 10;
            }

            // Actualizamos los objetos en movimiento
            objeto.update();
        }

        // Comprobamos si hay choque entre objeto y mapache
        for (Objeto objeto : objetos) {
            if (Rect.intersects(mapache.getHitbox(), objeto.getHitbox())) {
                // TODO mandar a pantalla GAMEOVER
            }
        }
    }

    private void draw() {
        // Comprobamos si el canvas esta en null
        if (surfaceHolder.getSurface().isValid()) {
            // Bloqueamos el canvas para pintar
            canvas = surfaceHolder.lockCanvas();

            // Variable zoom imagen fondo original
            float ZOOM = 1.3f;

            // Dimensiones
            int anchoPantalla = canvas.getWidth();
            int altoPantalla = canvas.getHeight();
            int anchoImagen = imagenFondo.getWidth();
            int altoImagen = imagenFondo.getHeight();

            // Escalas y finales
            float escalaX = (float) anchoPantalla / anchoImagen;
            float escalaY = (float) altoPantalla / altoImagen;
            float escala = Math.max(escalaX, escalaY);
            escala *= ZOOM;
            int anchoFinal = (int) (anchoImagen * escala);
            int altoFinal = (int) (altoImagen * escala);

            // Coordenadas (para ajustar abajo a la izquierda)
            int x = 0;
            int y = altoPantalla - altoFinal;


            // Creamos un rectangulo usando el ancho calculado y la altura de la pantalla
            Rect pantalla = new Rect(x,y,x+anchoFinal,y+altoFinal);

            // Dibujamos el fondo con el filtro oscurecedor y el rectangulo calculado
            canvas.drawBitmap(imagenFondo, null, pantalla, fondoOscuro);

            // Creamos y dibujamos el mapache
            if (mapache == null) {
                mapache = new Mapache(getContext(), anchoPantalla, altoPantalla, imagenMapache);
            }
            mapache.draw(canvas);

            /*
            Creamos los objetos cada x tiempo (empieza en 3s)
            Guardamos el tiempo de ultima creación de objeto y la comparamos con el tiempo actual usando el span que ponemos
            */
            if (System.currentTimeMillis() - lastSpawn > spawnTime) {
                // Añadimos un nuevo objeto al array 'objetos'
                objetos.add(new Objeto(getContext(), anchoPantalla, altoPantalla, imagenObstaculos));

                // Actualizamos la variable para que espere otros 5 segundos
                lastSpawn = System.currentTimeMillis();
            }

            // Aumentamos la dificultad cada 15 segundos
            if (System.currentTimeMillis() - lastNivel > nivelTime) {
                // Ponemos un maximo de dificultad
                if (spawnTime <= 250) {
                    spawnTime = 250;
                } else {
                    // Quitamos 250ms cada vez
                    spawnTime -= 250;
                }

                // Actualizamos la variable para que espere otros 15 segundos
                lastNivel = System.currentTimeMillis();
            }

            // Dibujamos todos los objetos
            for (Objeto objeto : objetos) {
                objeto.draw(canvas);
            }

            // Dibujamos el marcador de puntuación
            canvas.drawText("--- " + puntuacion + " ---", getWidth() / 2 , 150, paintPuntuacion);

            // --- TODO DEBUG PARA VER HITBOXES (hecho con IA) ---
//            Paint paintDebug = new Paint();
//            paintDebug.setColor(android.graphics.Color.RED);
//            paintDebug.setStyle(Paint.Style.STROKE); // Solo bordes
//            paintDebug.setStrokeWidth(5); // Borde grueso
//
//            // Dibujar hitbox del mapache
//            if (mapache != null) {
//                canvas.drawRect(mapache.getHitbox(), paintDebug);
//            }
//
//            // Dibujar hitboxes de los objetos
//            for (Objeto objeto : objetos) {
//                canvas.drawRect(objeto.getHitbox(), paintDebug);
//            }
            // --- TODO FIN DEBUG ---

            // Desbloqueamos y actualizamos el canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // Función que estabiliza el juego a 60 FPS
    private void control() {
        // Implementamos una estructura de control por obligación
        try {
            // Paramos el hilo 17ms para conseguir los 60 FPS
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // FUNCIONES al iniciar / parar el juego
    public void resume() {
        alive = true;

        // Creamos e iniciamos el hilo del juego
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }
    public void pause() {
        alive = false;
        try {
            // Detenemos el hilo del juego antes de volver a la actividad principal
            hiloJuego.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
