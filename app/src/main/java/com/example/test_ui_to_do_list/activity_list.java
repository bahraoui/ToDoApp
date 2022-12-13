package com.example.test_ui_to_do_list;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class activity_list extends AppCompatActivity {

    private TextView btn_AddNewList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DBHandlerList dbList;
    private int nbViews;
    private String textPopUp;

    ImageButton fragmentListBtn, fragmentAccountBtn, fragmentSettingsBtn;
    private Dialog popup;

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

        if(isFirstLaunch.get()){
            showPopUp();
        }

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

        nbViews = 0;
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
        //final AtomicBoolean isFirstListener = new AtomicBoolean(true);
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
                        case REMOVED:
                        case MODIFIED:
                            majListeUser();
                            if (isFirstLaunch.get()){
                                majUI();
                            }
                            break;
                    }
                }
            }
        };
        isFirstLaunch.set(false);
        userListes.addSnapshotListener(activity_list.this, eventListenerUpdateListe);
        listesRef.addSnapshotListener(activity_list.this, eventListenerUpdateListe);
            /*
        } else {
            Toast.makeText(this, "not first launch", Toast.LENGTH_SHORT).show();
            //majUI();
        }
*/
    }

    private void addListUI(TDA_Liste tda_liste, ViewGroup main){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_list, null);

        // titre texte
        TextView txt = view.findViewById(R.id.myView_element_1_name);
        txt.setText(tda_liste.getLi_Name());

        // icone
        ImageView iv_icon = view.findViewById(R.id.myImage_element_1);
        iv_icon.setImageDrawable(getResources().getDrawable(tda_liste.strToIdDrawable()));

        ConstraintLayout cl = view.findViewById(R.id.myView_list_constraintLayout);
        cl.setOnClickListener(v -> {
            Intent intent = new Intent(this, activity_in_list.class);
            intent.putExtra("tda_liste",tda_liste);
            startActivity(intent);
        });

        TextView progress = view.findViewById(R.id.myView_element_1_progress);
        float progress_pourcentage = tda_liste.progressFinish();
        if (progress_pourcentage > 80f){
            progress.setTextColor(Color.rgb(0,255,0)); // vert
        } else if (progress_pourcentage > 30f){
            progress.setTextColor(Color.rgb(255,215,0)); // orange
        } else {
            progress.setTextColor(Color.rgb(255,51,51)); // rouge
        }
        progress.setText(Integer.toString(Math.round(progress_pourcentage))+"%");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 50, 0);

        main.addView(view, layoutParams);
        nbViews++;
    }

    private void majUI(){
        //ArrayList<TDA_Liste> toutes_listes = dbList.readLists();
        ViewGroup main = this.findViewById(R.id.list_constLayout_insertPoint);
        main.removeAllViews();
        listesRef.whereEqualTo("ownerId",mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot dcs : queryDocumentSnapshots){
                            TDA_Liste liste_tmp = dcs.toObject(TDA_Liste.class);
                            /* for (String x :
                                    listeIdentifiantsUser_hashmap.keySet()) {
                                Toast.makeText(List_Activity.this, "cle : "+x+" - valeur : "+listeIdentifiantsUser_hashmap.get(x), Toast.LENGTH_SHORT).show();

                            }*/
                            if (liste_tmp != null
                                // && listeIdentifiantsUser_hashmap.get(liste_tmp.getId()) == mAuth.getCurrentUser().getUid()
                            ){
/*                                    if(liste_tmp.getOwnerId() != mAuth.getCurrentUser().getUid()){
                                        // la liste en question est une liste partagee
                                        // mettre un icone de partage ici pour l'indiquer
                                    }*/
                                    addListUI(liste_tmp, main);
                                }
                            }
                        }
                });
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
                    }
                }
            }
        }).continueWith(new Continuation<QuerySnapshot, Object>() {
            @Override
            public Object then(@NonNull Task<QuerySnapshot> task) throws Exception {
                //majUI();
                return null;
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
    }

    public void showPopUp() {

        TextView close;
        popup.setContentView(R.layout.popup);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        close = (TextView) popup.findViewById(R.id.closeWindows);
        close.setOnClickListener(view -> popup.dismiss());

        HashMap<String,ArrayList<String>> toDoToday = new HashMap<>();
        textPopUp = "";
        listesRef.whereEqualTo("ownerId",mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
              for (QueryDocumentSnapshot dcs : queryDocumentSnapshots) {
                  TDA_Liste liste_tmp = dcs.toObject(TDA_Liste.class);
                  if (liste_tmp != null) {
                      ArrayList<TDA_Item>  liList= liste_tmp.getLi_List();
                      for (TDA_Item item : liList) {
                            Date itemDate = item.getIt_ObjectifDate();
                            if(itemDate.compareTo(Calendar.getInstance().getTime()) <= 0) {
                                if(item.isFinished() == false) {
                                    ArrayList<String> list;
                                    if(toDoToday.containsKey(liste_tmp.getId())) {
                                        list = toDoToday.get(liste_tmp.getId());
                                    }
                                    else{
                                        list = new ArrayList<String>();
                                        list.add(liste_tmp.getLi_Name());
                                    }
                                    list.add(item.getIt_Name());
                                    toDoToday.put(liste_tmp.getId(),list);
                                }
                            }
                      }
                  }
              }
        }).continueWith(task -> {

            //textPopUp += toDoToday.toString();
            for (Map.Entry<String, ArrayList<String>> set : toDoToday.entrySet()) {

                ArrayList<String> respondList = set.getValue();

                textPopUp += respondList.get(0) + " : - " + respondList.get(1) + "\n";
                for (int counter = 2; counter < respondList.size(); counter++) {
                    int size = respondList.get(0).length();
                    for(int i = 0; i < size; i++) {
                        textPopUp += "  ";
                    }
                    textPopUp += "   - " + respondList.get(counter) +"\n";
                }
                textPopUp+= "\n";
            }

            TextView popupText =  popup.findViewById(R.id.popup_text);
            popupText.setText(textPopUp);
            if(!textPopUp.isEmpty())
                popup.show();
            return null;
        });
    }
}
