package com.example.test_ui_to_do_list;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class listCreationActivity extends AppCompatActivity {

    private EditText name_NewList;
    private Button btn_NewList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_list_creation);

        name_NewList = findViewById(R.id.listcreation_et_name);
        btn_NewList = findViewById(R.id.signup_btn_create);
    }
}
