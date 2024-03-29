package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class List_Activity extends AppCompatActivity {

    private TextView btn_AddNewList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        //btn_AddNewList = findViewById(R.id.list_tv_add);
        btn_AddNewList.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListCreationActivity.class);
            startActivity(intent);
        });

        addListUI(new TDA_Liste("nomTEST"));
        addListUI(new TDA_Liste("nomTEST2"));
    }

    private void addListUI(TDA_Liste tda_liste){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_list, null);
        TextView txt = view.findViewById(R.id.myView_element_1_name);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 20, 30, 0);
        txt.setText(tda_liste.getLi_Name());
        //ViewGroup main = findViewById(R.id.list_constLayout_insertPoint);
       // main.addView(view, layoutParams);
    }
}
