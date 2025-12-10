package ut6.act1.videojuego_android;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Eliminamos la barra superior de la aplicación
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Ponemos la pantalla completa para mejor visualización del juego
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Mostramos el menu principal
        setContentView(R.layout.activity_main);

        // Mapeamos los dos botones
        Button jugar = findViewById(R.id.jugar);
        Button salir = findViewById(R.id.salir);

        // Lanzamos el juego al darle al boton
        jugar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Creamos el intent para iniciar el juego
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        startActivity(intent);
                    }
                }
        );

        // Cerramos la aplicacion al darle al boton
        salir.setOnClickListener(v -> finish());
    }

    // Si el usuario suspende la aplicación, pausamos el hilo para ahorrar recursos
    @Override
    protected void onPause() {
        super.onPause();
    }
    // Si el usuario vuelve, reanudamos el hilo
    @Override
    protected void onResume() {
        super.onResume();
    }
}