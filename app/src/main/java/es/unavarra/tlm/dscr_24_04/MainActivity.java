package es.unavarra.tlm.dscr_24_04;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridTableroJugador1;
    private GridLayout gridTableroJugador2;
    private EditText inputFila;
    private EditText inputColumna;
    private Button buttonDisparar;
    private Button buttonColocarBarco;
    private OkHttpClient client = new OkHttpClient(); // Asegúrate de usar OkHttpClient aquí
    private String gameId = "tu_game_id"; // Cambiar a un ID de juego real

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar los GridLayouts de los tableros
        gridTableroJugador1 = findViewById(R.id.gridTableroJugador1);
        gridTableroJugador2 = findViewById(R.id.gridTableroJugador2);
        inputFila = findViewById(R.id.inputFila);
        inputColumna = findViewById(R.id.inputColumna);
        buttonDisparar = findViewById(R.id.buttonDisparar);
        buttonColocarBarco = findViewById(R.id.buttonColocarBarco);

        // Crear el tablero visualmente
        generarTableroVisual(gridTableroJugador1);
        generarTableroVisual(gridTableroJugador2);

        // Configurar el evento del botón de disparar
        buttonDisparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fila = Integer.parseInt(inputFila.getText().toString());
                int columna = Integer.parseInt(inputColumna.getText().toString());
                realizarDisparoAPI(fila, columna);
            }
        });

        // Configurar el evento del botón de colocar barco
        buttonColocarBarco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementar la lógica para colocar un barco
            }
        });
    }

    // Método para generar el tablero visualmente
    private void generarTableroVisual(GridLayout gridLayout) {
        gridLayout.removeAllViews(); // Limpiar el tablero

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                TextView textView = new TextView(this);
                textView.setText("~"); // Representación de agua inicialmente
                textView.setPadding(8, 8, 8, 8);
                gridLayout.addView(textView);
            }
        }
    }

    // Método para realizar un disparo a través de la API
    private void realizarDisparoAPI(int fila, int columna) {
        String url = "https://api.battleship.tatai.es/v2/game/" + gameId + "/shoot";

        try {
            JSONObject json = new JSONObject();
            JSONObject position = new JSONObject();
            position.put("row", (char) ('A' + fila)); // Convertir fila a letra
            position.put("column", columna + 1); // Ajustar columna para que empiece desde 1

            json.put("position", position);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al realizar el disparo", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Disparo realizado", Toast.LENGTH_SHORT).show();
                            // Aquí puedes manejar la respuesta y actualizar el tablero
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
