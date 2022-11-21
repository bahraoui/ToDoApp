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

    private EditText name_NewList;
    private Button btn_CreateNewList;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_creation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        name_NewList = findViewById(R.id.listcreation_et_name);
        btn_CreateNewList = findViewById(R.id.signup_btn_create);
    }
}
