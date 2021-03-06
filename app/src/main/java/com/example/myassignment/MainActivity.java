package com.example.myassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.hellosharedprefs";


    public static GoogleSignInClient mGoogleSignInClient;

    private static String TAG = "INFO";

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = db.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen*/
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        final VideoView simpleVideoView = (VideoView) findViewById(R.id.videoView2);
        simpleVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turtlesvideo));
        simpleVideoView.start();

        simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                progressBar.setVisibility(View.VISIBLE);
            }
        });


        /**
         * checking network connectivity
         */
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
       // showToast(activeNetworkInfo+"");

        if (!connectivityManager.isDefaultNetworkActive()) {
            showToast("Please enable Network Connection and Restart the App");
            buildAlertMessageNoInternet();
            return;
        }





        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);


        FirebaseUser currentUser = mAuth.getCurrentUser();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String emailString = "";
        emailString = mPreferences.getString("email", null);


        /**
         *if user is already signed in
         * using google account
         * or with email and password
         */
        if (account != null) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            showToast("Already Signed In");

            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);


            finish();

        } else if (currentUser != null && emailString != null && !(TextUtils.isEmpty(emailString))) {

            showToast("Already Signed In");

            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);


            finish();
        } else {

            showToast("Please Login");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(intent);


            finish();
        }


    }


    /**
     * Function to
     * show some message/toast
     * to user
     */
    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5) {
            finish();
        }

    }


    /**
     * Function to show settings alert dialog.
     * On pressing the Yes button it will launch Internet Options and after enabling internet users gets to login screen.
     * else it will logout the user
     */
    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Internet seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS), 5);


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        showToast("Please enable Internet to use the app");
                        finish();
                       // logout();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
