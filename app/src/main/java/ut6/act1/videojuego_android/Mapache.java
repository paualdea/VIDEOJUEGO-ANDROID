package ut6.act1.videojuego_android;

// IMPORTS
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Esta es la clase que controla el movimiento, animaciones y lógica del personaje (mapache).
 */
public class Mapache {
    // Configuracion variables spreadsheet Mapache
    private int COLUMNAS = 8;
    private int FILAS = 4;
    private Bitmap sprite;
    /*
        La variable recorteSprite recorta uno de los frames dentro del spreadsheet
        Por otro lado, la variable destinoSprite muestra ese recorte (escalado) en el juego
     */
    private Rect recorteSpite, destinoSprite;

    // Variables mapache
    private int mapacheAncho, mapacheAlto, pantallaAncho, escala = 9;
    private int x, y, velocidad, velocidadMaxima = 25;
    // Esta variable sirve para detectar si el mapache está mirando a la derecha o izquierda y asi invertir el spreadsheet para la correcta animación
    private boolean mirandoDerecha = true, movimiento = false;

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
    public Mapache(Context context, int pantallaAncho, int pantallaAlto) {
        this.pantallaAncho = pantallaAncho;

        // Cargamos el spreadsheet usando BitmapFactory para evitar suavizado borroso de los pixeles
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Con esta opcion evitamos que al escalar los pixeles se vean borrosos
        options.inScaled = false;
        sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapache, options);

        // Calculamos el tamaño de cada mapache diviendo entre columnas y filas
        mapacheAncho = sprite.getWidth() / COLUMNAS;
        mapacheAlto = sprite.getHeight() / FILAS;

        // Posicion inicial mapache
        x = pantallaAncho / 3;
        y = pantallaAlto - (mapacheAlto * escala) - 115;

        recorteSpite = new Rect();
        destinoSprite = new Rect();
        lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        // Controlamos el movimiento sumando la velocidad a la posicion X
        x += velocidad;

        // Evitar que se salga de la pantalla por la izquierda
        if (x < 0) {
            x = 0;
            // Ponemos la velocidad a 0 para que no parezca que corre contra la pared
            velocidad = 0;
        }

        // Evitar que se salga de la pantalla por la derecha (calculamos ancho pantalla menos el ancho del mapache escalado)
        if (x > pantallaAncho - (mapacheAncho * escala)) {
            x = pantallaAncho - (mapacheAncho * escala);
            // Ponemos la velocidad a 0 para que no parezca que corre contra la pared
            velocidad = 0;
        }

        // Si el mapache se mueve, actualizar la animación de movimiento
        if (velocidad != 0) {
            movimiento = true;
        }
        // Sino, actualizar la animación en parado
        else {
            movimiento = false;
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

    public void draw(Canvas canvas) {
        // Calculamos donde recortar
        int srcX = currentFrame * mapacheAncho;
        int srcY;

        // Animación en parado si no hay movimiento (fila 0)
        if (!movimiento) {
            srcY = 0 * mapacheAlto;
        }
        // Si hay movimiento animación segunda fila (fila 1)
        else {
            srcY = mapacheAlto;
        }

        recorteSpite.left = srcX;
        recorteSpite.top = srcY;
        recorteSpite.right = srcX + mapacheAncho;
        recorteSpite.bottom = srcY + mapacheAlto;

        // Calcular el dibujo del mapache en pantalla
        destinoSprite.left = x;
        destinoSprite.top = y;
        destinoSprite.right = x + (mapacheAncho * escala);
        destinoSprite.bottom = y + (mapacheAlto * escala);

        // Efecto espejo si vamos para la izquierda
        if (!mirandoDerecha) {
            canvas.save();
            // Espejo horizontal usando el centro del mapache
            float centroX = destinoSprite.centerX();
            float centroY = destinoSprite.centerY();
            canvas.scale(-1, 1, centroX, centroY);
            canvas.drawBitmap(sprite, recorteSpite, destinoSprite, null);
            // Devolvemos el estado original del sprite
            canvas.restore();
        }
        // Si mira para la derecha dibujar normalmente el sprite
        else {
            canvas.drawBitmap(sprite, recorteSpite, destinoSprite, null);
        }
    }

    // CONTROLES
    public void setMoviendoDerecha(boolean moviendo) {
        if (moviendo) {
            velocidad = velocidadMaxima;
            mirandoDerecha = true;
        } else {
            velocidad = 0;
        }
    }
    public void setMoviendoIzquierda(boolean moviendo) {
        if (moviendo) {
            velocidad = -velocidadMaxima;
            mirandoDerecha = false;
        } else {
            velocidad = 0;
        }
    }
}
