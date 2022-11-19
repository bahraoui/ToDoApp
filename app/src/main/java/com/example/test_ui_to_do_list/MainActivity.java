package com.example.test_ui_to_do_list;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;


import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private ImageView fbuser;
    private ImageView guser;
    private Button login;
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
        //Initialisation du SDK Facebook
        //callbackManager = CallbackManager.Factory.create();


        // Callback registration
        guser = findViewById(R.id.imageViewLogoGoogle);
        fbuser = findViewById(R.id.imageViewLogoFacebook);
        email = findViewById(R.id.log_editTextEmail);
        password = findViewById(R.id.log_editTextPassword);
        login = findViewById(R.id.buttonSignIn);

        fbuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FacebookAuth.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        guser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GoogleAuth.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                loginUser(txt_email, txt_password);
            }
        });
    }

    private void loginUser(String email, String password) {
        if(!email.isEmpty() || !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MainActivity.this, "Connexion Reussi!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, List_Activity.class));
                    finish();
                }
            });
        }
    }
}