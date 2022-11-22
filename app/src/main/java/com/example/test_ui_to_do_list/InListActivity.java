package com.example.test_ui_to_do_list;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class InListActivity extends AppCompatActivity {
    private TextView tv_TitleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_list);
        tv_TitleList = findViewById(R.id.inlist_tv_title);

        TDA_Liste listeExemple = new TDA_Liste("nomTest");
        listeExemple.addItem(new TDA_Item("Item1", Calendar.getInstance().getTime()));
        listeExemple.addItem(new TDA_Item("Item2", Calendar.getInstance().getTime()));

    }

    private void setTitle(TDA_Liste liste){
        tv_TitleList.setText(liste.getLi_Name());
    }
}
