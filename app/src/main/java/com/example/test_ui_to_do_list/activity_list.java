package com.example.test_ui_to_do_list;

import android.app.Dialog;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class activity_list extends AppCompatActivity {

    private TextView btn_AddNewList;
    private FirebaseAuth mAuth;
    private DBHandlerList dbList;
    private int nbViews;

    ImageButton fragmentListBtn, fragmentAccountBtn, fragmentSettingsBtn;
    Dialog popup;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");
    private HashMap<String, String> listeIdentifiantsUser_hashmap = new HashMap<String,String>();
    private ArrayList<String> listeIdentifiantsUser = new ArrayList<>();
    private ArrayList<String> listeIdentifiantsUser_uid = new ArrayList<>();
    private final AtomicBoolean isFirstLaunch = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);

        fragmentListBtn = findViewById(R.id.fragmentListBtn);
        fragmentAccountBtn = findViewById(R.id.fragmentAccountBtn);
        fragmentSettingsBtn = findViewById(R.id.fragmentSettingsBtn);
        popup = new Dialog(this);

        showPopUp();

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
        mAuth.getCurrentUser().getUid();
        /*

        if(currentUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        */

        dbList = new DBHandlerList(this);
        btn_AddNewList = findViewById(R.id.list_tv_add);
        btn_AddNewList.setOnClickListener(v -> {
            Intent intent = new Intent(this, activity_list_creation.class);
            startActivity(intent);
        });


        //majUI();
        majListeUser();
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

    @Override
    protected void onStart() {
        super.onStart();
        //majUI();
        final AtomicBoolean isFirstListener = new AtomicBoolean(true);
        //if(isFirstLaunch.get()){
        EventListener<QuerySnapshot> eventListenerUpdateListe = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value.getMetadata().isFromCache()){
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    DocumentSnapshot dcs = dc.getDocument();

                    switch (dc.getType()){
                        case ADDED:
                            Toast.makeText(activity_list.this, "added", Toast.LENGTH_SHORT).show();
                            if (isFirstLaunch.get()){
                                majUI();
                            }
                            break;
                        case REMOVED:
                            Toast.makeText(activity_list.this, "removed", Toast.LENGTH_SHORT).show();
                            majListeUser();
                            majUI();
                            break;
                        case MODIFIED:
                            Toast.makeText(activity_list.this, "modified", Toast.LENGTH_SHORT).show();
                            majListeUser();
                            majUI();
                            break;
                    }
                }
            }
        };
        isFirstLaunch.set(false);
        listesRef.addSnapshotListener(activity_list.this, eventListenerUpdateListe);
        userListes.addSnapshotListener(activity_list.this, eventListenerUpdateListe);
            /*
        } else {
            Toast.makeText(this, "not first launch", Toast.LENGTH_SHORT).show();
            //majUI();
        }
*/
    }

    synchronized private void addListUI(TDA_Liste tda_liste){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_list, null);

        TextView txt = view.findViewById(R.id.myView_element_1_name);
        txt.setText(tda_liste.getLi_Name());
        txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_autres,0,0,0);

        txt.setOnLongClickListener(v -> {
            Toast.makeText(this, "SUCCES", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, activity_in_list.class);
            intent.putExtra("tda_liste",tda_liste);
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

    synchronized private void majUI(){
        //ArrayList<TDA_Liste> toutes_listes = dbList.readLists();
        ViewGroup main = findViewById(R.id.list_constLayout_insertPoint);
        main.removeAllViews();

        listesRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot dcs : queryDocumentSnapshots){
                            TDA_Liste liste_tmp = dcs.toObject(TDA_Liste.class);
                            /*
                            for (String x :
                                    listeIdentifiantsUser_hashmap.keySet()) {
                                Toast.makeText(List_Activity.this, "cle : "+x+" - valeur : "+listeIdentifiantsUser_hashmap.get(x), Toast.LENGTH_SHORT).show();

                            }*/
                            if (liste_tmp != null
                                /* &&
                                    listeIdentifiantsUser_hashmap.get(liste_tmp.getId()) == mAuth.getCurrentUser().getUid()
                                 */
                            ){
/*
                                    if(liste_tmp.getOwnerId() != mAuth.getCurrentUser().getUid()){
                                        // la liste en question est une liste partagee
                                        // mettre un icone de partage ici pour l'indiquer
                                    }

 */
                                    addListUI(liste_tmp);
                                }
                            }
                        }
                });

/*
        for (int i = nbViews; i < toutes_listes.size(); i++){
            addListUI(toutes_listes.get(i));
        }
*/
    }

    synchronized private void majListeUser(){
        userListes.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot dcs : queryDocumentSnapshots){
                    Map<String, Object> identifiantsListe = dcs.getData();
                    if (identifiantsListe != null){
                        String cle = (String) identifiantsListe.get("id_liste");
                        String valeur = (String) identifiantsListe.get("ownerId");
                        listeIdentifiantsUser_hashmap.put(cle,valeur);
                        //Toast.makeText(List_Activity.this, "ajouter id liste "+identifiantsListe.get("id_liste"), Toast.LENGTH_SHORT).show();

                        //Toast.makeText(List_Activity.this, "hashmap 1 : "+listeIdentifiantsUser_hashmap.containsKey((String) identifiantsListe.get("id_liste") + "---" + (String) identifiantsListe.get("id_liste")), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(List_Activity.this, "hashmap 2 : "+listeIdentifiantsUser_hashmap.get((String) identifiantsListe.get("id_liste")), Toast.LENGTH_SHORT).show();
                    }
                }
            }
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
        /*
        if(isFirstLaunch.get()){
            Toast.makeText(this, "first launch", Toast.LENGTH_SHORT).show();
            isFirstLaunch.set(false);
        } else {
            Toast.makeText(this, "not first launch", Toast.LENGTH_SHORT).show();
            majUI();
        }*/
        majUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause list activity", Toast.LENGTH_SHORT).show();
    }

    public void showPopUp() {
        TextView close;
        popup.setContentView(R.layout.popup);
        close = (TextView) popup.findViewById(R.id.closeWindows);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
        popup.show();
    }
}
