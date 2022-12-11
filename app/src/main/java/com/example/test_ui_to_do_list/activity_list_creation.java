package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_list_creation extends AppCompatActivity {

    private String name_NewList;
    private Button btn_CreateNewList;
    private FirebaseAuth mAuth;
    private DBHandlerList dbList;

    // example
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");
    private ArrayList<ImageView> logos;
    private ImageView imgSelectionne;

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
        EditText et = findViewById(R.id.itemcreation_et_name);
        name_NewList = et.getText().toString();
        btn_CreateNewList = findViewById(R.id.itemadd_btn_create);

        btn_CreateNewList.setOnClickListener(v -> {
            createNewList();
        });

        // icones / logos :
        logos = new ArrayList<>();
        imgSelectionne = findViewById(R.id.logo5);
        imgSelectionne.setTag(R.drawable.ic_car);
        logos.add(findViewById(R.id.logo1));
        logos.add(findViewById(R.id.logo2));
        logos.add(findViewById(R.id.logo3));
        logos.add(findViewById(R.id.logo4));
        logos.add(findViewById(R.id.logo5));
        logos.add(findViewById(R.id.logo6));
        logos.add(findViewById(R.id.logo7));
        logos.add(findViewById(R.id.logo8));
        logos.add(findViewById(R.id.logo9));
        logos.add(findViewById(R.id.logo10));
        selectIconDrawable(findViewById(R.id.logo1));
    }

    public void finishActivity(View v) {
        finish();
    }

    private void createNewList(){
        EditText tmp = findViewById(R.id.itemcreation_et_name);
        name_NewList = tmp.getText().toString();
        if (name_NewList.isEmpty()) {
            Toast.makeText(this, "Nom de liste vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // ajout sql
        dbList.addNewList(name_NewList, false, "");

        // ajout liste firebase
        DocumentReference refAdded;
        String id_liste;
        refAdded = listesRef.document();
        id_liste = refAdded.getId();
        // creation de l'objet liste
        TDA_Liste new_liste = new TDA_Liste(name_NewList, mAuth.getCurrentUser().getUid());
        new_liste.setLi_drawable((Integer) imgSelectionne.getTag());
        new_liste.setId(id_liste);
        // ajout a la base de donnees
        refAdded.set(new_liste);
        // ajout id liste a la base de donnes
        refAdded = userListes.document();
        HashMap<String, String> identifiantsListe = new HashMap<>();
        identifiantsListe.put("nom_liste",name_NewList);
        identifiantsListe.put("id_liste",id_liste);
        identifiantsListe.put("ownerId",mAuth.getCurrentUser().getUid());
        refAdded.set(identifiantsListe);


        //listesRef.add(new TDA_Liste(name_NewList));

        Toast.makeText(this, "liste "+name_NewList+" ajoutee", Toast.LENGTH_SHORT).show();
        dbList.close();
        finish();
    }

    public void selectIconDrawable(View v){
        // ancien selectionnee:
        imgSelectionne.setBackground(null);
        // nouveau selectionnee:
        imgSelectionne = (ImageView) v;
        switch (imgSelectionne.getId()){
            case R.id.logo1:
                imgSelectionne.setTag(R.drawable.ic_autres);
                break;
            case R.id.logo2:
                imgSelectionne.setTag(R.drawable.ic_flatware);
                break;
            case R.id.logo3:
                imgSelectionne.setTag(R.drawable.ic_baseline_fastfood_24);
                break;
            case R.id.logo4:
                imgSelectionne.setTag(R.drawable.ic_baseline_shopping_cart);
                break;
            case R.id.logo5:
                imgSelectionne.setTag(R.drawable.ic_car);
                break;
            case R.id.logo6:
                imgSelectionne.setTag(R.drawable.ic_flight);
                break;
            case R.id.logo7:
                imgSelectionne.setTag(R.drawable.ic_baseline_account_balance_24);
                break;
            case R.id.logo8:
                imgSelectionne.setTag(R.drawable.ic_home);
                break;
            case R.id.logo9:
                imgSelectionne.setTag(R.drawable.ic_baseline_local_phone_24);
                break;
            case R.id.logo10:
                imgSelectionne.setTag(R.drawable.ic_baseline_medication_24);
                break;
        }
        imgSelectionne.setBackground(getResources().getDrawable(R.drawable.background_selection_icon));
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
