package es.unavarra.tlm.dscr_24_04;

// Para pintar los barcos y generar los botones
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

// Para enviar la información al servidor
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayoutBoard;
    private Ship currentShip; // Barco que el usuario está colocando
    private boolean isPlacingShip = false; // Modo de colocación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayoutBoard = findViewById(R.id.gridLayoutBoard);

        // Inicializar el tablero de 10x10
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                // Crear una vista para cada celda
                ImageView cellView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 90; // Ancho de la celda
                params.height = 90; // Altura de la celda
                cellView.setLayoutParams(params);
                cellView.setBackgroundResource(R.drawable.cell_background); // Fondo de celda

                // Crear un BoardCell asociado a cada celda
                BoardCell boardCell = new BoardCell(row, col);
                cellView.setTag(boardCell);

                // Agregar clic para mostrar las coordenadas
                cellView.setOnClickListener(view -> {
                    BoardCell cell = (BoardCell) view.getTag();
                    handleCellClick(cell);
                });

                // Añadir la vista al GridLayout
                gridLayoutBoard.addView(cellView);
            }
        }

        // Configuración de botones para seleccionar tipos de barcos
        Button buttonCarrier = findViewById(R.id.buttonCarrier);
        buttonCarrier.setOnClickListener(v -> {
            currentShip = new Ship("carrier");
            isPlacingShip = true;
            Toast.makeText(this, "Selecciona 5 celdas para el Carrier", Toast.LENGTH_SHORT).show();
        });

        Button buttonBattleship = findViewById(R.id.buttonBattleship);
        buttonBattleship.setOnClickListener(v -> {
            currentShip = new Ship("battleship");
            isPlacingShip = true;
            Toast.makeText(this, "Selecciona 4 celdas para el Battleship", Toast.LENGTH_SHORT).show();
        });

        Button buttonCruiser = findViewById(R.id.buttonCruiser);
        buttonCruiser.setOnClickListener(v -> {
            currentShip = new Ship("cruiser");
            isPlacingShip = true;
            Toast.makeText(this, "Selecciona 3 celdas para el Cruiser", Toast.LENGTH_SHORT).show();
        });

        Button buttonSubmarine = findViewById(R.id.buttonSubmarine);
        buttonSubmarine.setOnClickListener(v -> {
            currentShip = new Ship("submarine");
            isPlacingShip = true;
            Toast.makeText(this, "Selecciona 3 celdas para el Submarine", Toast.LENGTH_SHORT).show();
        });

        Button buttonDestroyer = findViewById(R.id.buttonDestroyer);
        buttonDestroyer.setOnClickListener(v -> {
            currentShip = new Ship("destroyer");
            isPlacingShip = true;
            Toast.makeText(this, "Selecciona 2 celdas para el Destroyer", Toast.LENGTH_SHORT).show();
        });
    }

    public void sendShipToServer(Ship ship) {
        // Verifica si el barco cumple las condiciones antes de enviarlo
        if (!ship.isValid()) {
            Toast.makeText(this, "La colocación del barco no es válida", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String gameId = "12345"; // Usa el ID real del juego
        String url = "https://api.battleship.tatai.es/v2/game/" + gameId + "/ship";

        // Crear JSON para el cuerpo de la solicitud
        JSONObject json = new JSONObject();
        try {
            json.put("ship_type", ship.getType());

            JSONArray positionsArray = new JSONArray();
            for (Ship.Position pos : ship.getPositions()) {
                JSONObject position = new JSONObject();
                position.put("row", pos.getRow());
                position.put("col", pos.getCol());
                positionsArray.put(position);
            }
            json.put("positions", positionsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "session_key")  // Usa la clave de sesión del usuario
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al colocar el barco", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Barco colocado con éxito", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al colocar el barco", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void handleCellClick(BoardCell cell) {
        if (isPlacingShip && currentShip != null) {
            // Verifica que la celda no esté ya seleccionada
            for (Ship.Position pos : currentShip.getPositions()) {
                if (pos.getRow() == cell.getRow() && pos.getCol() == cell.getCol()) {
                    Toast.makeText(this, "Esta celda ya está ocupada por el barco actual", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Agrega la posición seleccionada al barco
            currentShip.addPosition(cell.getRow(), cell.getCol());

            // Calcula el índice correcto de la celda en el GridLayout y cambia su fondo
            int index = cell.getRow() * 10 + cell.getCol();
            ImageView cellView = (ImageView) gridLayoutBoard.getChildAt(index);
            cellView.setBackgroundResource(R.drawable.ship_background);

            // Verifica si se ha alcanzado el número de celdas requerido para este barco
            if (currentShip.getPositions().size() == getRequiredSizeForShip(currentShip.getType())) {
                isPlacingShip = false; // Desactiva el modo de colocación
                Toast.makeText(this, currentShip.getType() + " colocado", Toast.LENGTH_SHORT).show();

                // Envía el barco al servidor
                sendShipToServer(currentShip);
            }
        }
    }

    // Para determinar el tamaño de cada barco
    private int getRequiredSizeForShip(String type) {
        switch (type) {
            case "carrier": return 5;
            case "battleship": return 4;
            case "cruiser": return 3;
            case "submarine": return 3;
            case "destroyer": return 2;
            default: return 0;
        }
    }
}
