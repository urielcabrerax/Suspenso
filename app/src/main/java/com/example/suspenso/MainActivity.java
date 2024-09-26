package com.example.suspenso;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView countriesRecyclerView;
    private CountryAdapter countryAdapter;
    private List<Country> countryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countriesRecyclerView = findViewById(R.id.recyclerViewCountries);
        countriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener la lista de países
        new Thread(() -> fetchCountries()).start();
    }

    private void fetchCountries() {
        try {
            URL url = new URL("http://www.geognos.com/api/en/countries/info/all.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            parseCountries(response.toString());
        } catch (Exception e) {
            Log.e("MainActivity", "Error fetching countries", e);
        }
    }

    private void parseCountries(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response).getJSONObject("Results");
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject countryData = jsonObject.getJSONObject(key);
                String name = countryData.getString("Name");
                countryList.add(new Country(name, key));
            }

            runOnUiThread(() -> {
                countryAdapter = new CountryAdapter(countryList, MainActivity.this);
                countriesRecyclerView.setAdapter(countryAdapter);
            });

        } catch (JSONException e) {
            Log.e("MainActivity", "Error parsing JSON", e);
        }
    }
}
