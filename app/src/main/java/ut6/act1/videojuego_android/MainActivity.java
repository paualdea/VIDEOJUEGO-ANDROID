package ut6.act1.videojuego_android;

// IMPORTS
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Creamos el objeto que manejara las vistas del videojuego
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Eliminamos la barra superior de la aplicación
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Ponemos la pantalla completa para mejor visualización del juego
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Inicializamos la vista del juego con GameView
        gameView = new GameView(this);

        // Ponemos como vista en la pantalla a este GameView
        setContentView(gameView);
    }

    // Si el usuario suspende la aplicación, pausamos el hilo para ahorrar recursos
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    // Si el usuario vuelve, reanudamos el hilo
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}