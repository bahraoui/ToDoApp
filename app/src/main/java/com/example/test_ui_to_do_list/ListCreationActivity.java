package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import io.grpc.internal.JsonParser;

public class ListCreationActivity extends AppCompatActivity {

    private String name_NewList;
    private Button btn_CreateNewList;
    private FirebaseAuth mAuth;
    private DBHandlerList dbList;

    // example
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_creation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        dbList = new DBHandlerList(this);
        EditText et = findViewById(R.id.listcreation_et_name);
        name_NewList = et.getText().toString();
        btn_CreateNewList = findViewById(R.id.signup_btn_create);

        btn_CreateNewList.setOnClickListener(v -> {
            createNewList();
        });


    }

    public void finishActivity() {
        finish();
    }

    private void createNewList(){
        EditText tmp = findViewById(R.id.listcreation_et_name);
        name_NewList = tmp.getText().toString();
        if (name_NewList.isEmpty()) {
            Toast.makeText(this, "Nom de liste vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // ajout sql
        dbList.addNewList(name_NewList, false, "");

        // ajout firebase
        DocumentReference refAdded;
        refAdded = listesRef.document();
        TDA_Liste new_liste = new TDA_Liste(name_NewList);
        new_liste.setId(refAdded.getId());
        refAdded.set(new_liste);


        //listesRef.add(new TDA_Liste(name_NewList));

        Toast.makeText(this, "liste "+name_NewList+" ajoutee", Toast.LENGTH_SHORT).show();
        dbList.close();
        finish();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbList.close();
        //Toast.makeText(this, "onDestroy create activity", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbList.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbList = new DBHandlerList(this);
        //Toast.makeText(this, "onResume create activity", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause create activity", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
