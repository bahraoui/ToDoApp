package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    // Create a storage reference from our app
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


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

        // set icone
        new_liste.setLi_drawable((String) imgSelectionne.getTag());
        // set liste id;
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
        // listesRef.add(new TDA_Liste(name_NewList));
        dbList.close();
        finish();
                        /*
                        return null;
                    });
                    return null;
                });
*/
    }

    public void selectIconDrawable(View v){
        // ancien selectionnee:
        imgSelectionne.setBackground(null);
        // nouveau selectionnee:
        imgSelectionne = (ImageView) v;
        switch (imgSelectionne.getId()){
            case R.id.logo1:
                //imgSelectionne.setTag(R.drawable.ic_autres);
                imgSelectionne.setTag("logo1");
                break;
            case R.id.logo2:
                //imgSelectionne.setTag(R.drawable.ic_flatware);
                imgSelectionne.setTag("logo2");
                break;
            case R.id.logo3:
                //imgSelectionne.setTag(R.drawable.ic_baseline_fastfood_24);
                imgSelectionne.setTag("logo3");
                break;
            case R.id.logo4:
                //imgSelectionne.setTag(R.drawable.ic_baseline_shopping_cart);
                imgSelectionne.setTag("logo4");
                break;
            case R.id.logo5:
                //imgSelectionne.setTag(R.drawable.ic_car);
                imgSelectionne.setTag("logo5");
                break;
            case R.id.logo6:
                //imgSelectionne.setTag(R.drawable.ic_flight);
                imgSelectionne.setTag("logo6");
                break;
            case R.id.logo7:
                //imgSelectionne.setTag(R.drawable.ic_baseline_account_balance_24);
                imgSelectionne.setTag("logo7");
                break;
            case R.id.logo8:
                //imgSelectionne.setTag(R.drawable.ic_home);
                imgSelectionne.setTag("logo8");
                break;
            case R.id.logo9:
                //imgSelectionne.setTag(R.drawable.ic_baseline_local_phone_24);
                imgSelectionne.setTag("logo9");
                break;
            case R.id.logo10:
                //imgSelectionne.setTag(R.drawable.ic_baseline_medication_24);
                imgSelectionne.setTag("logo10");
                break;
        }
        imgSelectionne.setBackground(getResources().getDrawable(R.drawable.background_selection_icon));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
