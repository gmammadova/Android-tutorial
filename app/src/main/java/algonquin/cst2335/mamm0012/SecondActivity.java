package algonquin.cst2335.mamm0012;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {
    private static final int CAMERA_ACTIVITY_REQUEST_CODE = 3456;
    private static final int CALL_ACTIVITY_REQUEST_CODE = 3457;
    private static final String PICTURE_FILENAME = "Picture.png";

    SharedPreferences prefs;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ImageView pictureImageView = findViewById(R.id.pictureImageView);

                Bitmap thumbnail = data.getParcelableExtra("data");
                pictureImageView.setImageBitmap(thumbnail);

                FileOutputStream fOut = null;
                try {
                    fOut = openFileOutput( PICTURE_FILENAME, Context.MODE_PRIVATE);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        Button callButton = findViewById(R.id.callButton);
        ImageView pictureImageView = findViewById(R.id.pictureImageView);
        Button pictureButton = findViewById(R.id.pictureButton);

        Intent fromPrevious = getIntent();

        String emailAddress = fromPrevious.getStringExtra("EmailAddress");
        welcomeTextView.setText("Welcome back " + emailAddress);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phoneEditText.setText(prefs.getString("PhoneNumber", ""));

        callButton.setOnClickListener(clk -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneEditText.getText()));
            startActivityForResult(callIntent, CALL_ACTIVITY_REQUEST_CODE);
        });

        pictureButton.setOnClickListener(clk -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult( cameraIntent, CAMERA_ACTIVITY_REQUEST_CODE);
        });

        File pictureFile = new File(getFilesDir(), PICTURE_FILENAME);
        if (pictureFile.exists()) {
            Log.i("SecondActivity", "Found Picture file at: " + pictureFile.getAbsolutePath());
            Bitmap thumbnail = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            pictureImageView.setImageBitmap(thumbnail);
        } else {
            Log.i("SecondActivity", "Picture file does not exist");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        EditText phoneEditText = findViewById(R.id.phoneEditText);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PhoneNumber", phoneEditText.getText().toString());
        editor.apply();
    }
}