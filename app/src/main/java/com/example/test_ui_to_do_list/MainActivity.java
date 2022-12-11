package com.example.test_ui_to_do_list;

import static android.content.ContentValues.TAG;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private ImageView fbuser;
    private ImageView guser;
    private Button login;
    private TextView tvSignUp;
    // Choose an arbitrary request code value
    //private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    //Facebook
    //CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
//            currentUser.reauthenticate(currentUser.get);
            startActivity(new Intent(this, activity_list.class));
            finish();
        }

        //Initialisation du SDK Facebook
        //callbackManager = CallbackManager.Factory.create();


        // Connexion
        guser = findViewById(R.id.main_img_logoGoogle);
        fbuser = findViewById(R.id.main_img_logoFacebook);
        email = findViewById(R.id.main_et_email);
        password = findViewById(R.id.main_et_password);
        login = findViewById(R.id.main_btn_SignIn);

        // Inscription
        tvSignUp = findViewById(R.id.main_tv_SignUp);


        // Voir classe "FacebookAuth" pour la connexion a facebook
        fbuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FacebookAuth.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // Voir classe "GoogleAuth" pour la connexion a google
        guser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GoogleAuth.class);
                startActivity(intent);
            }
        });

        // connexion avec l'email et le passsword
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = findViewById(R.id.main_et_email);
                password = findViewById(R.id.main_et_password);
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                loginUser(txt_email, txt_password);
            }
        });

        // inscription
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, activity_sign_up.class);
                startActivity(intent);
            }
        });
    }

    private void forcageConnexion(){
        // forcage de connexion :
        Toast.makeText(MainActivity.this, "FORCAGE CO", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, activity_list.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String email, String password) {
        if(!email.isEmpty() && !password.isEmpty()) {
            //Cette fonction ne sert qu'a debug
            //forcageConnexion();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(MainActivity.this, activity_list.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            /*
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MainActivity.this, "Connexion Reussi!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, List_Activity.class);
                    startActivity(intent);
                }
            });*/
            //Toast.makeText(MainActivity.this, "DEBUG LOG", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Email ou MDP vide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            startActivity(new Intent(this, activity_list.class));
            finish();
        }
    }
}