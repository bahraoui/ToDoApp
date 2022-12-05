package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ListCreationActivity extends AppCompatActivity {

    private String name_NewList;
    private Button btn_CreateNewList;
    private FirebaseAuth mAuth;
    private DBHandlerList dbList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_creation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        */

        dbList = new DBHandlerList(this);
        EditText et = findViewById(R.id.listcreation_et_name);
        name_NewList = et.getText().toString();
        btn_CreateNewList = findViewById(R.id.signup_btn_create);

        btn_CreateNewList.setOnClickListener(v -> {
            EditText tmp = findViewById(R.id.listcreation_et_name);
            name_NewList = tmp.getText().toString();
            if (name_NewList.isEmpty()) {
                Toast.makeText(this, "Nom de liste vide", Toast.LENGTH_SHORT).show();
                return;
            }
            dbList.addNewList(name_NewList, false, "");
            Toast.makeText(this, "liste "+name_NewList+" ajoutee", Toast.LENGTH_SHORT).show();
            dbList.close();
            finish();
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbList.close();
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
    }

}
