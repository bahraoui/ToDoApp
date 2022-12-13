package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_sign_up extends AppCompatActivity {
    public FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");
    EditText nom, prenom, email, password;
    Button inscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        nom = findViewById(R.id.signup_et_last_name);
        prenom = findViewById(R.id.signup_et_first_name);
        inscription = findViewById(R.id.signup_btn_signup);
        email = findViewById(R.id.signup_et_mail);
        password = findViewById(R.id.signup_et_password);

        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = findViewById(R.id.signup_et_mail);
                password = findViewById(R.id.signup_et_password);
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(activity_sign_up.this, "Email ou mot de passe vide !", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(activity_sign_up.this, "Mot de passe trop court!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txt_email, txt_password);
                }
            }
        });
    }

    private void registerUser(String _email, String password) {
        mAuth.createUserWithEmailAndPassword(_email, password).addOnCompleteListener(activity_sign_up.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //error message
                    DocumentReference refAdded = usersRef.document();
                    HashMap<String,String> data = new HashMap<>();
                    data.put("id",refAdded.getId());
                    data.put("nom",nom.getText().toString());
                    data.put("prenom",prenom.getText().toString());
                    data.put("email",_email);
                    refAdded.set(data);
                    ArrayList<String> idListes = new ArrayList<>();
                    idListes.add("id1");
                    idListes.add("id1");
                    usersRef.document(refAdded.getId()).update("testnew",idListes);
                    Toast.makeText(activity_sign_up.this, "Enregistrement Reussi!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(activity_sign_up.this, MainActivity.class));
                    finish();
                } else {
                    //success
                    Toast.makeText(activity_sign_up.this, "Echec d'Authentification!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
