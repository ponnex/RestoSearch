package com.ponnex.restosearch.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ponnex.restosearch.LoginActivityManually;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginThroughFBActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private Button loginButton;
    private Button logoutButton;
    private TextView mUsername, mEmailID;
    private Profile mFbProfile;
    private ParseUser parseUser;
    private TextView signUp;
    private String name = null, email = null;
    private CallbackManager callbackManager;
    private TextView signInManual;

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_facebook);

        loginButton = (Button) findViewById(R.id.login_button);
        logoutButton = (Button) findViewById(R.id.logout_button);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        signInManual = (TextView) findViewById(R.id.log_in);
        signUp = (TextView) findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginThroughFBActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView logIn = (TextView) findViewById(R.id.log_in);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginThroughFBActivity.this, LoginActivityManually.class);
                startActivity(intent);
                finish();
            }
        });

        mUsername = (TextView) findViewById(R.id.txt_name);
        mEmailID = (TextView) findViewById(R.id.txt_email);

        mFbProfile = Profile.getCurrentProfile();
        callbackManager = CallbackManager.Factory.create();

        parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            Log.d("MyApp", "parseUser is NOT null initially");
            getInitialDetailsFromParse();
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.GONE);
            signInManual.setVisibility(View.GONE);
        } else {
            Log.d("MyApp", "parseUser is NULL initially");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
            signInManual.setVisibility(View.VISIBLE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginThroughFBActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailsFromFB();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailsFromParse();
                        }
                    }
                });
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser();
            }
        });
    }

    private void logOutUser() {
        mUsername.setText("Name");
        mEmailID.setText("Email");
        loginButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
        mProfileImage.setImageResource(R.color.colorAccent);
        signUp.setVisibility(View.VISIBLE);
        signInManual.setVisibility(View.VISIBLE);
        ParseUser.logOut();
        parseUser = ParseUser.getCurrentUser();
    }


    private void getInitialDetailsFromParse() {
        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEmailID.setText(parseUser.getEmail());
        mUsername.setText(parseUser.getUsername());
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();
        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEmailID.setText(parseUser.getEmail());
        mUsername.setText(parseUser.getUsername());
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);

        Toast.makeText(LoginThroughFBActivity.this, "Welcome back " + mUsername.getText().toString(), Toast.LENGTH_SHORT).show();
        signUp.setVisibility(View.GONE);
        signInManual.setVisibility(View.GONE);
    }

    private void getUserDetailsFromFB() {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
         /* handle the result */
                        try {
                            email = response.getJSONObject().getString("email");
                            mEmailID.setText(email);
                            name = response.getJSONObject().getString("name");
                            mUsername.setText(name);
                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");
                            new ProfilePhotoAsync(pictureUrl).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);


//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] data = stream.toByteArray();
            String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    parseUser.put("profileThumb", parseFile);

                    //Finally save all the user details
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(LoginThroughFBActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                            signUp.setVisibility(View.GONE);
                            signInManual.setVisibility(View.GONE);
                        }
                    });

                }
            });
        }

    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url;

        public ProfilePhotoAsync(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProfileImage.setImageBitmap(bitmap);

            saveNewUser();
        }
    }

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }
}
