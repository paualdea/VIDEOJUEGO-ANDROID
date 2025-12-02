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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;

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
    private Bitmap imagenFondo;
    private Paint fondoOscuro;

    // Creamos el mapache
    private Mapache mapache;

    // Variable para parar el juego cuando un objeto colisione con el mapache
    private boolean alive = true;

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
                mapache = new Mapache(getContext(), anchoPantalla, altoPantalla);
            }
            mapache.draw(canvas);

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
