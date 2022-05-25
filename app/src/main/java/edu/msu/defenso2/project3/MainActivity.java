package edu.msu.defenso2.project3;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import edu.msu.defenso2.project3.Cloud.Cloud;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        // CHECKS USER FOR PERMISSION TO ACCESS LOCATION
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Also, dont forget to add overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //   int[] grantResults)
                // to handle the case where the user grants the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        // Gets preferences
        SharedPreferences settings = getSharedPreferences("LOGINDETAILS", MODE_PRIVATE);

        // If the user wanted the login details saved on the device
        if (settings.getBoolean("REMEMBER", false))
        {
            // Gets the username and password from preferences
            String user = settings.getString("USERNAME", "");
            String pw = settings.getString("PASSWORD","");

            // Gets the username and password edit text boxes
            EditText username = (EditText)findViewById(R.id.userName);
            EditText password = (EditText)findViewById(R.id.password);

            // Writes the login details to the text boxes
            username.setText(user);
            password.setText(pw);

            // Checks the box
            CheckBox cb = (CheckBox)findViewById(R.id.Remember);
            cb.setChecked(true);
        }

        super.onCreate(savedInstanceState);
    }


    /**
     * Saves the username and password in prefernces
     * @param view The view currently being worked with
     */
    public void OnRememberMe(View view) {
        // Gets the editor
        SharedPreferences settings = getSharedPreferences("LOGINDETAILS", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        CheckBox cb = (CheckBox)findViewById(R.id.Remember);
        if (cb.isChecked()) // User wants remembered username and password
        {
            // Gets the username and password
            EditText username = (EditText)findViewById(R.id.userName);
            EditText password = (EditText)findViewById(R.id.password);

            // Adds the username and password
            editor.putString("USERNAME", username.getText().toString());
            editor.putString("PASSWORD", password.getText().toString());
            editor.putBoolean("REMEMBER", true);
        }
        else // Users wants username and password to be removed
        {
            editor.remove("USERNAME");
            editor.remove("PASSWORD");
            editor.putBoolean("REMEMBER", false);
        }

        editor.apply(); // Adds changes
    }


    /**
     * Handles logging into the server
     * @param view The view currently being worked with
     */
    public void onLogin(View view) {
        final View login = findViewById(R.id.loginView);
        final EditText editName = login.findViewById(R.id.userName);
        final EditText editPassword = login.findViewById(R.id.password);

        // Create a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.loginFromCloud(editName.getText().toString(), editPassword.getText().toString());
                if(!ok) { // failed to log in
                    login.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(login.getContext(), "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else { // This will take you to a new game
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("USER", editName.getText().toString());
                    startActivity(intent);
                }
            }
        }).start();
    }


    /**
     * Takes the user to the create account page where they can make an account
     * @param view The view we currently dealing with
     */
    public void onCreateAccount(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }
}

