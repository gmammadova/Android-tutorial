package algonquin.cst2335.mamm0012;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private String stringURL;

    private TextView tempTextView;
    private TextView minTempTextView;
    private TextView maxTempTextView;
    private TextView humidityTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private EditText cityNameEditText;
    float oldSize = 14;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hide_views:
                tempTextView.setVisibility(View.INVISIBLE);
                maxTempTextView.setVisibility(View.INVISIBLE);
                minTempTextView.setVisibility(View.INVISIBLE);
                humidityTextView.setVisibility(View.INVISIBLE);
                descriptionTextView.setVisibility(View.INVISIBLE);
                iconImageView.setVisibility(View.INVISIBLE);
                cityNameEditText.setText(""); //clear the city name
                break;

            case R.id.id_increase:
                oldSize++;
                tempTextView.setTextSize(oldSize);
                minTempTextView.setTextSize(oldSize);
                maxTempTextView.setTextSize(oldSize);
                humidityTextView.setTextSize(oldSize);
                descriptionTextView.setTextSize(oldSize);
                cityNameEditText.setTextSize(oldSize);
                break;

            case R.id.id_decrease:
                oldSize = Float.max(oldSize-1, 5);
                tempTextView.setTextSize(oldSize);
                minTempTextView.setTextSize(oldSize);
                maxTempTextView.setTextSize(oldSize);
                humidityTextView.setTextSize(oldSize);
                descriptionTextView.setTextSize(oldSize);
                cityNameEditText.setTextSize(oldSize);
                break;
            case 5:
                runForecast(item.getTitle().toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
               onOptionsItemSelected(item);
               drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        Button forecastBtn = findViewById(R.id.forecastButton);
        cityNameEditText = findViewById(R.id.cityTextField);

        tempTextView= findViewById(R.id.temp);

        minTempTextView = findViewById(R.id.minTemp);

        maxTempTextView = findViewById(R.id.maxTemp);

        humidityTextView = findViewById(R.id.humidity);

        descriptionTextView = findViewById(R.id.description);

        iconImageView = findViewById(R.id.icon);


        forecastBtn.setOnClickListener((click) -> {
            String cityName = cityNameEditText.getText().toString();

            myToolbar.getMenu().add(1, 5, 10, cityName)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            runForecast(cityName);
        });

    }

    private void runForecast(String cityName) {
        Executor newThread = Executors.newSingleThreadExecutor();

        newThread.execute( () -> { /* This runs in a separate thread */
            try {
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(cityName, "UTF-8")
                        + "&appid=89dea8c99d670c628daf134b22e6bd9e&units=metric";

                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String text = (new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject theDocument = new JSONObject( text );

                JSONObject coord = theDocument.getJSONObject( "coord" );
                JSONArray weatherArray = theDocument.getJSONArray ( "weather" );
                JSONObject position0 = weatherArray.getJSONObject(0);

                String description = position0.getString("description");
                String iconName = position0.getString("icon");

                JSONObject mainObject = theDocument.getJSONObject( "main" );
                double current = mainObject.getDouble("temp");
                double min = mainObject.getDouble("temp_min");
                double max = mainObject.getDouble("temp_max");
                int humidity = mainObject.getInt("humidity");
                int vis = theDocument.getInt("visibility");
                String name = theDocument.getString( "name" );

                Bitmap image = null;
                File file = new File(getFilesDir(), iconName + ".png");
                if (file.exists()) {
                    image = BitmapFactory.decodeFile(getFilesDir() + "/" + iconName + ".png");
                } else {
                    URL imgUrl = new URL( "https://openweathermap.org/img/w/" + iconName + ".png" );
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        image = BitmapFactory.decodeStream(inputStream);
                        FileOutputStream fOut = null;
                        try {
                            fOut = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }


                Bitmap finalImage = image;
                runOnUiThread( (  )  -> {
                    TextView tv = findViewById(R.id.temp);
                    tv.setText("The current temperature is " + current);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.minTemp);
                    tv.setText("The min temperature is " + min);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.maxTemp);
                    tv.setText("The min temperature is " + max);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.humidity);
                    tv.setText("The min temperature is " + humidity + "%");
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.description);
                    tv.setText(description);
                    tv.setVisibility(View.VISIBLE);

                    ImageView iv = findViewById(R.id.icon);
                    iv.setImageBitmap(finalImage);
                    iv.setVisibility(View.VISIBLE);
                });

            }
            catch (IOException | JSONException ioe) {
                Log.e("Connection error:", ioe.getMessage());
            }
        } );
    }
}