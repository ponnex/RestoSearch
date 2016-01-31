package com.ponnex.restosearch;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.ponnex.restosearch.ui.activity.LoginThroughFBActivity;


/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_manually);

        final EditText username = (EditText)findViewById(R.id.username);
        final AutoCompleteTextView email = (AutoCompleteTextView)findViewById(R.id.email);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText confirm_password = (EditText)findViewById(R.id.confirm_password);
        Button signUp = (Button)findViewById(R.id.email_sign_up_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equals(confirm_password.getText().toString())){
                    Snackbar.make(findViewById(android.R.id.content), "Password did not match", Snackbar.LENGTH_SHORT).show();
                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(username.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setEmail(email.getText().toString());

                    if (ParseUser.getCurrentUser() != null) {
                        ParseUser.logOut();
                    }

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                ParseUser.getCurrentUser();

                                Intent intent = new Intent(SignUpActivity.this, LoginThroughFBActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                Toast.makeText(getApplicationContext(), "New user:" + username.getText().toString() + " Signed up", Toast.LENGTH_SHORT).show();

                                finish();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }

}

