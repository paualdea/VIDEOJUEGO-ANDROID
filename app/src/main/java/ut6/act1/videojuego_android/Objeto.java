package ut6.act1.videojuego_android;

// IMPORTS
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.Random;

public class Objeto {
    // Variable para randomizar las posiciones de los objetos
    Random random = new Random();

    // Configuracion variables spreadsheet Mapache
    private int COLUMNAS = 5;
    private int FILAS = 2;
    private Bitmap sprite;
    /*
        La variable recorteSprite recorta uno de los frames dentro del spreadsheet
        Por otro lado, la variable destinoSprite muestra ese recorte (escalado) en el juego
     */
    private Rect recorteSpite, destinoSprite;

    // Variables objeto
    private int objetoAncho, objetoAlto, pantallaAncho, pantallaAlto, escala = 3;
    private int x, y;
    int velocidad = 25;
    boolean objetoGrande = false;

    // Animación
    private int currentFrame = 0, frameDuration = 80;
    private long lastFrameTime;

    /**
     * Constructor de la clase.
     * Recibe como parametro el contexto, el ancho de la pantalla y el alto de la pantalla.
     *
     * @param context
     * @param pantallaAncho
     */
    public Objeto(Context context, int pantallaAncho, int pantallaAlto) {
        this.pantallaAncho = pantallaAncho;
        this.pantallaAlto = pantallaAlto;

        // Cargamos el spreadsheet usando BitmapFactory para evitar suavizado borroso de los pixeles
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Con esta opcion evitamos que al escalar los pixeles se vean borrosos
        options.inScaled = false;
        sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstaculos, options);

        // Calculamos el tamaño de cada objeto diviendo entre columnas y filas
        objetoAncho = sprite.getWidth() / COLUMNAS;
        objetoAlto = sprite.getHeight() / FILAS;

        // Posicion inicial objeto (randomizada)
        x = random.nextInt(pantallaAncho - (objetoAncho / escala));
        y = 0;

        recorteSpite = new Rect();
        destinoSprite = new Rect();
        lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        // Controlamos el movimiento sumando la velocidad a la posicion y (vertical)
        y += velocidad;

        // Controlar cuando salga de la pantalla
        if (y > pantallaAlto) {
            velocidad = 0;
        }

        // Actualizamos la animación
        if (System.currentTimeMillis() - lastFrameTime > frameDuration) {
            currentFrame++;
            if (currentFrame >= COLUMNAS) {
                currentFrame = 0;
            }
            lastFrameTime = System.currentTimeMillis();
        }
    }

    public void draw (Canvas canvas) {
        // Calculamos donde recortar
        int srcX = currentFrame * objetoAncho;
        int srcY;

        // Selección animación piedra (fila 0)
        if (!objetoGrande) {
            srcY = 0 * objetoAlto;
        }
        // Selección animación bolsa (fila 1)
        else {
            srcY = objetoAlto;
        }

        recorteSpite.left = srcX;
        recorteSpite.top = srcY;
        recorteSpite.right = srcX + objetoAncho;
        recorteSpite.bottom = srcY + objetoAlto;

        // Calcular el dibujo del objeto en pantalla
        destinoSprite.left = x;
        destinoSprite.top = y;
        destinoSprite.right = x + (objetoAncho / escala);
        destinoSprite.bottom = y + (objetoAlto / escala);

        // Dibujamos el objeto
        canvas.drawBitmap(sprite, recorteSpite, destinoSprite, null);
    }

    // GETTER velocidad objeto
    public int getVelocidad() {
        return velocidad;
    }
}
