package com.example.test_ui_to_do_list;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
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
        //Toast.makeText(MainActivity.this, "etape 1", Toast.LENGTH_SHORT).show();

        if(currentUser != null) {
//            currentUser.reauthenticate(currentUser.get);
            startActivity(new Intent(this, List_Activity.class));
            //finish();
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
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                loginUser(txt_email, txt_password);
            }
        });

        // inscription
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        if(!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MainActivity.this, "Connexion Reussi!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, List_Activity.class);
                    startActivity(intent);
                }
            });
            Toast.makeText(MainActivity.this, "Probleme de connexion", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Email ou MDP vide", Toast.LENGTH_SHORT).show();
        }
    }
}