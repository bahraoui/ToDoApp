package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class InListActivity extends AppCompatActivity {
    private TextView tv_TitleList;
    private TextView tv_AddButton;
    private ImageView tv_ReturnButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_list);
        tv_TitleList = findViewById(R.id.inlist_tv_title);
        tv_AddButton = findViewById(R.id.list_tv_add);
        tv_ReturnButton = findViewById(R.id.imageView_return);
        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String titre = extras.getString("titreListe");
            tv_TitleList.setText(titre);
            //The key argument here must match that used in the other activity
        }

         */
        TDA_Liste liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        tv_TitleList.setText(liste.getLi_Name());

        tv_AddButton.setOnClickListener(view -> {
            addItemActivityLaunch();
        });

        tv_ReturnButton.setOnClickListener(view -> {
            finish();
        });

    }

    private void addItemActivityLaunch(){
        Intent i = new Intent(this, ItemCreationActivity.class);
        TDA_Liste liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        i.putExtra("tda_liste_ajout_item",liste);
        startActivity(i);

    }

    private void setTitle(TDA_Liste liste){
        tv_TitleList.setText(liste.getLi_Name());
    }
}
