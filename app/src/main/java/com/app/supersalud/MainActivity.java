package com.app.supersalud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int GOOGLE_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;

    private TextView tx;

    private SignInButton googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Guardado de datos
        //SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        googleButton = (SignInButton) findViewById(R.id.sign_in_button);

        googleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                tx.setText("Boton pulsado inicio");
                ///signIn();
                // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.

                // Build a GoogleSignInClient with the options specified by gso.

                mGoogleSignInClient.signOut(); /////////////////AAAAAAAAAAAAAAAAAAAAAAAAAAH

                myActivityResultLauncher.launch(mGoogleSignInClient.getSignInIntent());

                tx.setText("Boton pulsado terminado");

            }
        });


        myActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // The Task returned from this call is always completed, no need to attach
                            // a listener.
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account != null) {
                                    firebaseAuthWithGoogle(account.getIdToken());
                                }

                                /////updateUI(account);
                            } catch (ApiException e) {
                                // Google Sign In failed, update UI appropriately
                                Log.w(TAG, "Google sign in failed.", e);
                                //updateUI(null);
                            }

                        }
                    }
                });

        tx = (TextView) findViewById(R.id.textView3);
        tx.setText("Hola");


    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        tx.setText("Patata");

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            goHome(user);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });

    }
    // [END auth_with_google]

    private void updateUI(FirebaseUser user) {

    }

    private void goHome(FirebaseUser user) {
        Bundle b = new Bundle();
        b.putString("email", user.getEmail());
        Intent intent = new Intent(this, Home.class);
        intent.putExtras(b);
        startActivity(intent);
    }


}