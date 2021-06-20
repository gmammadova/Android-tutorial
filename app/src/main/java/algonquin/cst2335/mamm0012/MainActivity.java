package algonquin.cst2335.mamm0012;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Gulchaman Mammadova
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /** This holds the text at the centre of the screen */
    private TextView tv = null;

    /** This holds password */
    private EditText et = null;

    /** This holds Login button on the screen */
    private Button btn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        et = findViewById(R.id.editText);
        btn = findViewById(R.id.button);

        btn.setOnClickListener( clk -> {
            String password = et.getText().toString();

            if(checkPasswordComplexity(password)){
                tv.setText("Your password meets the requirements");
            } else{
                tv.setText("You shall not pass!");
            }
        });

    }

    /** This function checks if the given character is one of #$%^&*!@?
     *
     * @param c Character to check
     * @return Returns true if the character is one of #$%^&*!@?
     */
    boolean isSpecialCharacter(char c) {
        switch (c) {
            case '#':
            case '$':
            case '%':
            case '^':
            case '&':
            case '!':
            case '?':
            case '*':
            case '@':
                return true;
            default:
                return false;
        }
    }

    /** This function checks password's complexity
     *
     * @param pw The String object that we are checking
     * @return Returns true if the password is complex enough
     */
    boolean checkPasswordComplexity(String pw) {
        boolean foundUpperCase = false;
        boolean foundLowerCase = false;
        boolean foundNumber = false;
        boolean foundSpecial = false;

        for (int i = 0; i < pw.length(); i++){
            char c = pw.charAt(i);
            if (Character.isDigit(c)){
                foundNumber = true;
            } else if (Character.isUpperCase(c)){
                foundUpperCase = true;
            } else if (Character.isLowerCase(c)){
                foundLowerCase = true;
            } else if (isSpecialCharacter(c)) {
                foundSpecial = true;
            }
        }

        if (!foundUpperCase){
            Toast.makeText(getApplicationContext(), "The password should contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if( ! foundLowerCase)
        {
            Toast.makeText( getApplicationContext(), "The password should contain at least one lowercase letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (! foundNumber) {
            Toast.makeText(getApplicationContext(), "The password should contain at least one digit", Toast.LENGTH_SHORT).show();
            return false;
        } else if (! foundSpecial) {
            Toast.makeText(getApplicationContext(), "The password should contain at least one special character", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }


}