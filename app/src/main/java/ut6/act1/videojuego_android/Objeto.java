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

        También creamos un Rect para la hitbox del objeto (que será algo mas pequeña que el Rect del sprite)
     */
    private Rect recorteSpite, destinoSprite, hitbox;
    private int margenX = 20, margenY = 20;

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
    public Objeto(Context context, int pantallaAncho, int pantallaAlto, Bitmap sprite) {
        this.pantallaAncho = pantallaAncho;
        this.pantallaAlto = pantallaAlto;

        // Cargamos el spreadsheet
        this.sprite = sprite;

        // Calculamos el tamaño de cada objeto diviendo entre columnas y filas
        objetoAncho = sprite.getWidth() / COLUMNAS;
        objetoAlto = sprite.getHeight() / FILAS;

        // Posicion inicial objeto (randomizada)
        x = random.nextInt(pantallaAncho - (objetoAncho / escala));
        y = 0;

        recorteSpite = new Rect();
        destinoSprite = new Rect();
        hitbox = new Rect();

        lastFrameTime = System.currentTimeMillis();

        // Elegimos el tipo de objeto con un random
        int tipoObjeto = random.nextInt(101);

        if (tipoObjeto <= 60) {
            objetoGrande = false;
            margenX = 35;
            margenY = 60;
        }
        else {
            objetoGrande = true;
        }
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

        // Establecemos el tamaño de la hitbox en funcion del objeto
        hitbox.set(x + margenX, y + margenY, x + (objetoAncho / escala) - margenX, y + (objetoAlto / escala) - margenY);
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

    // GETTERS
    public int getVelocidad() {
        return velocidad;
    }

    public Rect getHitbox() {
        return hitbox;
    }
}
