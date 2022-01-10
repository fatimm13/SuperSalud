package com.app.supersalud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.supersalud.DTO.HistorialSingleton;
import com.app.supersalud.DTO.SingletonMap;
import com.app.supersalud.DTO.UsuarioSingleton;
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

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura las opciones de inicio de sesion de google
        // ACLARACION: aunque default_web_client_id salga como error, en tiempo de ejecucion lo detecta
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configuramos el boton de inicio de sesion con google
        SignInButton googleButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                myActivityResultLauncher.launch(mGoogleSignInClient.getSignInIntent());
            }
        });

        // Configuramos el boton de inicio de sesion anonimo
        Button anonimoButton = findViewById(R.id.bAnonimo);
        anonimoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UsuarioSingleton.getInstance();
                goHome();
            }
        });

        myActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Resultado de lanzar el intent de GoogleSignInClient.getSignInIntent(...);
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                // Inicio de sesion con Google exitoso, autenticacion con Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account != null) {
                                    firebaseAuthWithGoogle(account.getIdToken());
                                }

                            } catch (ApiException e) {
                                // Inicio de sesion con Google fallido
                                Toast.makeText(getApplicationContext(), MainActivity.this.getResources().getString(R.string.fallo_inicio_google), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Limpiamos todas las posibles variables guardadas
        UsuarioSingleton.cerrarSesion();
        HistorialSingleton.cerrarSesion();
        SingletonMap.getInstance().clear();
    }

    /** Autenticacion con google dado el String del id como parametro **/
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesion exitoso
                            Toast.makeText(getApplicationContext(), MainActivity.this.getResources().getString(R.string.inicio_google_exitoso), Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String nombre = user.getDisplayName();
                            UsuarioSingleton.getInstance(email, nombre);
                            goHome();
                        } else {
                            // Inicio de sesion fallido
                            Toast.makeText(getApplicationContext(), MainActivity.this.getResources().getString(R.string.inicio_google_fallido), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** Lleva al Home de la aplicacion **/
    private void goHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}