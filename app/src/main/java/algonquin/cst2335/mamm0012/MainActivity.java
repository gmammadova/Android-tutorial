package algonquin.cst2335.mamm0012;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String stringURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button forecastBtn = findViewById(R.id.forecastButton);
        EditText cityText = findViewById(R.id.cityTextField);

        forecastBtn.setOnClickListener((click) -> {
            Executor newThread = Executors.newSingleThreadExecutor();

            newThread.execute( () -> { /* This runs in a separate thread */
                try {
                    String cityName = cityText.getText().toString();

                    stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                            + URLEncoder.encode(cityName, "UTF-8")
                            + "&appid=89dea8c99d670c628daf134b22e6bd9e&Units=Metric";

                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                }
                catch (IOException ioe) {
                    Log.e("Connection error:", ioe.getMessage());
                }
            } );
        });

    }
}