package com.example.suspenso;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CountryDetailActivity extends AppCompatActivity {
    private ImageView flagImageView;
    private TextView countryNameTextView, countryDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);

        flagImageView = findViewById(R.id.flagImageView);
        countryNameTextView = findViewById(R.id.countryNameTextView);
        countryDetailTextView = findViewById(R.id.countryDetailTextView);

        // Obtener el alpha2Code desde el Intent
        String alpha2Code = getIntent().getStringExtra("alpha2Code");

        // Obtener y mostrar la información del país
        new Thread(() -> fetchCountryDetail(alpha2Code)).start();
    }

    private void fetchCountryDetail(String alpha2Code) {
        try {
            // Construir la URL del servicio web con el código del país
            URL url = new URL("http://www.geognos.com/api/en/countries/info/" + alpha2Code + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            parseCountryDetail(response.toString(), alpha2Code);
        } catch (Exception e) {
            Log.e("CountryDetailActivity", "Error fetching country details", e);
        }
    }

    private void parseCountryDetail(String response, String alpha2Code) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject countryData = jsonObject.getJSONObject("Country");

            // Obtener el nombre del país y otros detalles
            String name = countryData.getString("Name");
            JSONObject geoRectangle = countryData.getJSONObject("GeoRectangle");

            final String details = "Nombre: " + name + "\n" +
                    "Latitud: " + geoRectangle.getString("North") + " - " + geoRectangle.getString("South") + "\n" +
                    "Longitud: " + geoRectangle.getString("West") + " - " + geoRectangle.getString("East");

            // Mostrar la información en la interfaz
            runOnUiThread(() -> {
                countryNameTextView.setText(name);
                countryDetailTextView.setText(details);

                // Cargar la bandera del país usando la URL del servicio web
                String flagUrl = "http://www.geognos.com/api/en/countries/flag/" + alpha2Code + ".png";
                Picasso.get().load(flagUrl).into(flagImageView);
            });

        } catch (JSONException e) {
            Log.e("CountryDetailActivity", "Error parsing JSON", e);
        }
    }
}
