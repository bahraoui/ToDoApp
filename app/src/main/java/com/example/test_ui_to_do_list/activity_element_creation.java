package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activity_element_creation extends AppCompatActivity {
    private EditText name_NewItem;
    private Button btn_CreateNewItem;
    private CalendarView calendarView;
    private Date selectedDate;

    private boolean isDateValid = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_creation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        selectedDate = Calendar.getInstance().getTime();
        isDateValid = false;
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        name_NewItem = findViewById(R.id.itemcreation_et_name);
        calendarView = findViewById(R.id.simpleCalendarView);
        calendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
            //Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            selectedDate = calendarToDate(c);
        });
        btn_CreateNewItem = findViewById(R.id.itemadd_btn_create);

        btn_CreateNewItem.setOnClickListener(view -> {
            ajouterItem();
        });
    }



    private void ajouterItem(){
        /*
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    isDateValid = true;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    isDateValid = false;
                    break;
            }
        };*/
        if(selectedDate.compareTo((Calendar.getInstance().getTime())) <= 0){
            Toast.makeText(this, "Veuillez sélectionner une date posterieure à celle actuelle", Toast.LENGTH_SHORT).show();
            /*
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setMessage("Vous allez sélectionner une date posterieure")
                    .setPositiveButton("C'est ce que je veux", dialogClickListener)
                    .setNegativeButton("Non je me suis trompé", dialogClickListener).show();
            if (!isDateValid){
                return;
            } else{
                Toast.makeText(this, "dans le esle", Toast.LENGTH_SHORT).show();
                return;
            }*/
            return;
        }

        name_NewItem = findViewById(R.id.itemcreation_et_name);
        String name_item = name_NewItem.getText().toString();

        if (name_item.isEmpty()){
            Toast.makeText(this, "Veuillez donner un nom a votre item", Toast.LENGTH_SHORT).show();
            return;
        }

        TDA_Liste li = (TDA_Liste) getIntent().getSerializableExtra("tda_liste_ajout_item");
        li.addItem(new TDA_Item(name_item,selectedDate));


        // ajout liste firebase
        Map<String, Object> addedMap = new HashMap<>();
        addedMap.put("li_List",li.getLi_List());
        Toast.makeText(getApplicationContext(), "ajout item liste id "+li.getId()+"\nname : "+li.getLi_Name(), Toast.LENGTH_LONG).show();
        listesRef.document(li.getId()).update("li_List",li.getLi_List());
        finish();
    }

    //Convert Date to Calendar
    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    private Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }
}
