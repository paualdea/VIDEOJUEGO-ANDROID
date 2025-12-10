package ut6.act1.videojuego_android;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    // Creamos el objeto que manejara las vistas del videojuego
    private GameView gameView;

    // Creamos la variable para saber si el juego se inicio
    private boolean jugando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Eliminamos la barra superior de la aplicación
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Ponemos la pantalla completa para mejor visualización del juego
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Iniciamos el juego
        iniciarJuego();
    }

    private void iniciarJuego() {
        // Iniciamos el juego
        jugando = true;
        gameView = new GameView(this);

        // Creamos el listener para el juego
        gameView.setGameListener(new GameView.gameListener() {
            @Override
            public void onGameOver(int puntos) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Mostramos el game over
                        iniciarGameOver(puntos);
                    }
                });
            }
        });

        // Mostramos el gameview
        setContentView(gameView);
        gameView.resume();
    }

    private void iniciarGameOver(int puntos) {
        jugando = false;

        // Mostramos el layout de gameover
        setContentView(R.layout.gameover);

        // Mapeamos los dos botones
        Button reintentar = findViewById(R.id.reintentar);
        Button menu = findViewById(R.id.menu);

        // Lanzamos el juego al darle al boton
        reintentar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iniciarJuego();
                    }
                }
        );

        // Volvemos al menu principal si le damos al boton
        menu.setOnClickListener(v -> finish());
    }

    // Si el usuario suspende la aplicación, pausamos el hilo para ahorrar recursos
    @Override
    protected void onPause() {
        super.onPause();
        if (jugando) gameView.pause();
    }
    // Si el usuario vuelve, reanudamos el hilo
    @Override
    protected void onResume() {
        super.onResume();
        if (jugando) gameView.resume();
    }
}
