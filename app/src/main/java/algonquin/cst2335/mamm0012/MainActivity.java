package algonquin.cst2335.mamm0012;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mytext = findViewById(R.id.textview);
        Button btn = findViewById(R.id.mybutton);
        CheckBox checkBox = findViewById(R.id.mycheckbox);
        Switch switchbtn = findViewById(R.id.myswitch);
        RadioButton radioButton = findViewById(R.id.myradiobutton);
        ImageView myimage = findViewById(R.id.logoalgonquin);
        ImageButton imgbtn = findViewById(R.id.myimagebutton);
        EditText myedit = findViewById(R.id.myedittext);

        if (btn !=null) {
            btn.setOnClickListener(v -> {
                String editString = myedit.getText().toString();
                mytext.setText("Your edit text has:" + editString);
            });
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String toastMessage = "You clicked on the Checkbox and it is now: " + isChecked;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, toastMessage, duration);
            toast.show();
        });

        switchbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String toastMessage = "You clicked on the Switch button and it is now: " + isChecked;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastMessage, duration);
            toast.show();
        });

        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String toastMessage = "You clicked on the Radio button and it is now: " + isChecked;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastMessage, duration);
            toast.show();
        });

        imgbtn.setOnClickListener(v -> {
            int width = v.getWidth();
            int height = v.getHeight();
            String toastMessage = "The width = " + width + " and height = " + height;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastMessage, duration);
            toast.show();
        });


    }
}