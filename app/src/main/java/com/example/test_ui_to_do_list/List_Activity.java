package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class List_Activity extends AppCompatActivity {

    private TextView btn_AddNewList;
    private FirebaseAuth mAuth;
    private DBHandlerList dbList;
    private int nbViews;

    ImageButton fragmentListBtn, fragmentAccountBtn, fragmentSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);

        fragmentListBtn = findViewById(R.id.fragmentListBtn);
        fragmentAccountBtn = findViewById(R.id.fragmentAccountBtn);
        fragmentSettingsBtn = findViewById(R.id.fragmentSettingsBtn);

        fragmentListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentList());
                fragmentListBtn.setImageResource(R.drawable.ic_list_blue);
                fragmentAccountBtn.setImageResource(R.drawable.ic_user);
                fragmentSettingsBtn.setImageResource(R.drawable.ic_settings);
            }
        });

        fragmentAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentAccount());
                fragmentListBtn.setImageResource(R.drawable.ic_list);
                fragmentAccountBtn.setImageResource(R.drawable.ic_user_blue);
                fragmentSettingsBtn.setImageResource(R.drawable.ic_settings);
            }
        });

        fragmentSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentSettings());
                fragmentListBtn.setImageResource(R.drawable.ic_list);
                fragmentAccountBtn.setImageResource(R.drawable.ic_user);
                fragmentSettingsBtn.setImageResource(R.drawable.ic_settings_blue);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        nbViews = 0;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*

        if(currentUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        */

        dbList = new DBHandlerList(this);
        btn_AddNewList = findViewById(R.id.list_tv_add);
        btn_AddNewList.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListCreationActivity.class);
            startActivity(intent);
        });


        majUI();
        /*
        addListUI(new TDA_Liste("nomTEST"));
        addListUI(new TDA_Liste("nomTEST2"));
        */
    }

    private void replaceFragment(Fragment parFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FrameLayout,parFragment);
        fragmentTransaction.commit();

    }

    private void addListUI(TDA_Liste tda_liste){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_list, null);

        TextView txt = view.findViewById(R.id.myView_element_1_name);
        txt.setText(tda_liste.getLi_Name());
        txt.setOnLongClickListener(v -> {
            Toast.makeText(this, "SUCCES", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, InListActivity.class);
            intent.putExtra("titreListe",tda_liste.getLi_Name());
            startActivity(intent);
            return true;
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 50, 0);

        ViewGroup main = findViewById(R.id.list_constLayout_insertPoint);
        main.addView(view, layoutParams);
        nbViews++;
    }

    private void majUI(){
        ArrayList<TDA_Liste> toutes_listes = dbList.readLists();

        for (int i = nbViews; i < toutes_listes.size(); i++){
            addListUI(toutes_listes.get(i));
        }
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
        majUI();
    }
}
