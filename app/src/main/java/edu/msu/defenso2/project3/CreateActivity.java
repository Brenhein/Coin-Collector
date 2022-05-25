package edu.msu.defenso2.project3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.defenso2.project3.Cloud.Cloud;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    /**
     * Handles creating a new account by checking the server to see if possible
     * @param view The current view we dealing with
     */
    public void onCreateAccount(View view) {
        final View create = findViewById(R.id.createView);
        final EditText username = (EditText)create.findViewById(R.id.usernameCreate);
        final EditText password = (EditText)create.findViewById(R.id.passwordCreate);
        final EditText pw2 = (EditText)create.findViewById(R.id.passwordConfirm);
        if (!(password.getText().toString().equals(pw2.getText().toString()))) {
            create.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(create.getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    final boolean ok = cloud.createFromCloud(username.getText().toString(), password.getText().toString());
                    if (!ok) {
                        create.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(create.getContext(), "failed to create account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            }).start();
        }
    }
}
