package com.ponnex.restosearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.ponnex.restosearch.ui.activity.LoginThroughFBActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivityManually extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manually);

        final AutoCompleteTextView email = (AutoCompleteTextView)findViewById(R.id.email);
        final EditText password = (EditText)findViewById(R.id.password);
        Button signIn = (Button)findViewById(R.id.email_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ParseUser.getCurrentUser() != null) {
                    ParseUser.logOut();
                }

                String setEmail = email.getText().toString();
                String setPassword = password.getText().toString();

                ParseUser.logInInBackground(setEmail,setPassword, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.

                            ParseUser.getCurrentUser();

                            Intent intent = new Intent(LoginActivityManually.this, LoginThroughFBActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Welcome back " + email.getText().toString(), Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}

