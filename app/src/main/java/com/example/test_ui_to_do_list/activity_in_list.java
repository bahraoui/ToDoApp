package com.example.test_ui_to_do_list;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class activity_in_list extends AppCompatActivity {
    private TextView tv_TitleList;
    private TextView tv_AddButton;
    private ImageView tv_ReturnButton;
    private TDA_Liste tda_liste;
    private final AtomicBoolean isFirstLaunch = new AtomicBoolean(true);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");

    // dialog
    Dialog popup;
    private TDA_Item itemSelectModif;


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
        tda_liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        tv_TitleList.setText(tda_liste.getLi_Name());

        tv_AddButton.setOnClickListener(view -> {
            addItemActivityLaunch();
        });

        tv_ReturnButton.setOnClickListener(view -> {
            finish();
        });
        majUI();
    }

    private void addItemActivityLaunch(){
        Intent i = new Intent(this, activity_element_creation.class);
        TDA_Liste liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        i.putExtra("tda_liste_ajout_item",liste);
        startActivity(i);
    }



    @Override
    protected void onStart() {
        super.onStart();
        EventListener<QuerySnapshot> eventListenerUpdateItems = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value.getMetadata().isFromCache()){
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    DocumentSnapshot dcs = dc.getDocument();

                    switch (dc.getType()){
                        case ADDED:
                            //Toast.makeText(activity_in_list.this, "added", Toast.LENGTH_SHORT).show();
                            if (isFirstLaunch.get()){
                                majUI();
                            }
                            break;
                        case REMOVED:
                            Toast.makeText(activity_in_list.this, "removed", Toast.LENGTH_SHORT).show();
                            majUI();
                            break;
                        case MODIFIED:
                            Toast.makeText(activity_in_list.this, "modified", Toast.LENGTH_SHORT).show();
                            majUI();
                            break;
                    }
                }
            }
        };
        isFirstLaunch.set(false);
        listesRef.addSnapshotListener(eventListenerUpdateItems);
    }

    synchronized private void addItemUI(TDA_Item tda_item){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_element, null);

        EditText txt = view.findViewById(R.id.item_et_title);
        txt.setEnabled(false);
        txt.setText(tda_item.getIt_Name());

        TextView item_date = view.findViewById(R.id.item_date);
        item_date.setText("");
        SimpleDateFormat geek = new SimpleDateFormat("dd / MM");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dateFormatter
                    = DateTimeFormatter.ofPattern("d MMM u", Locale.ENGLISH);
        }
        item_date.setText(geek.format(tda_item.getIt_ObjectifDate()));

        CheckBox it_check = view.findViewById(R.id.item_checkBox);
        it_check.setChecked(tda_item.isFinished());
        it_check.setOnClickListener(view1 -> {
            boolean checked = ((CheckBox) view1).isChecked();
            /// Check which checkbox was clicked
            for (TDA_Item it :
                    tda_liste.getLi_List()) {
                if(it.getId() == tda_item.getId()){
                    it.setFinished(checked);
                    break;
                }
            }
            Toast.makeText(this, "test check :"+checked, Toast.LENGTH_SHORT).show();
            listesRef.document(tda_liste.getId()).update("li_List",tda_liste.getLi_List());
        });



        ImageView crayon = view.findViewById(R.id.item_img_modify);
        crayon.setOnClickListener(view3 -> {
            popup = new Dialog(this);
            for (TDA_Item it :
                    tda_liste.getLi_List()) {
                if(it.getId() == tda_item.getId()){
                    itemSelectModif = it;
                    break;
                }
            }
            showPopUp();
        });
/*
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 50, 0);

 */

        ViewGroup main = findViewById(R.id.items_linearlayout_insertPoint);
        //main.addView(view, layoutParams);
        main.addView(view);
    }

    synchronized private void majUI(){
        //ArrayList<TDA_Liste> toutes_listes = dbList.readLists();
        ViewGroup main = findViewById(R.id.items_linearlayout_insertPoint);
        main.removeAllViewsInLayout();

        listesRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        tda_liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
                        for (QueryDocumentSnapshot dcs : queryDocumentSnapshots){
                            TDA_Liste liste_tmp = dcs.toObject(TDA_Liste.class);
                            String debug = "li nom : "+liste_tmp.getLi_Name()+
                                    "\n"+"li  vrai nom "+tda_liste.getLi_Name()+
                                    "\n"+"li  id "+liste_tmp.getId()+
                                    "\n"+"li vrai       id "+tda_liste.getId();
                            //Toast.makeText(InListActivity.this, debug, Toast.LENGTH_SHORT).show();
                            /*
                            for (String x :
                                    listeIdentifiantsUser_hashmap.keySet()) {
                                Toast.makeText(List_Activity.this, "cle : "+x+" - valeur : "+listeIdentifiantsUser_hashmap.get(x), Toast.LENGTH_SHORT).show();

                            }*/
                            if (liste_tmp != null && liste_tmp.getId()!=null && liste_tmp.getId().equals(tda_liste.getId())
                                /* &&
                                    listeIdentifiantsUser_hashmap.get(liste_tmp.getId()) == mAuth.getCurrentUser().getUid()
                                 */
                            ){
                                //Toast.makeText(InListActivity.this, "OUIIIIIIIIIIIIIII", Toast.LENGTH_SHORT).show();

                                for (TDA_Item item : liste_tmp.getLi_List()) {
                                    addItemUI(item);
                                }
                            }
                        }
                    }
                });
    }

    private void modify_item_name(String new_item_name, int id){
        tda_liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        for (TDA_Item it :
                tda_liste.getLi_List()) {
            if(it.getId() == id){
                it.setIt_Name(new_item_name);
                break;
            }
        }
        listesRef.document(tda_liste.getId()).update("li_List",tda_liste.getLi_List());
        //majUI();
    }

    private void setTitle(TDA_Liste liste){
        tv_TitleList.setText(liste.getLi_Name());
    }

    public void showPopUp() {
        TextView close;
        EditText nameItem;
        CalendarView calendarView;
        Button validationModifItem;
        popup.setContentView(R.layout.popup_item);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        close = (TextView) popup.findViewById(R.id.popup_item_closeWindows);
        nameItem = (EditText) popup.findViewById(R.id.popup_item_modification_et_name);
        calendarView = (CalendarView) popup.findViewById(R.id.popup_item_simpleCalendarView);
        validationModifItem = (Button) findViewById(R.id.pop_item_modification_btn_create);

        nameItem.setText(itemSelectModif.getIt_Name());
        calendarView.setDate(itemSelectModif.getIt_ObjectifDate().getTime());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
        validationModifItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameItem;
                CalendarView calendarView;
                nameItem = (EditText) popup.findViewById(R.id.popup_item_modification_et_name);
                calendarView = (CalendarView) popup.findViewById(R.id.popup_item_simpleCalendarView);
                if(!nameItem.equals(itemSelectModif.getIt_Name())
                        || calendarView.getDate() != itemSelectModif.getIt_ObjectifDate().getTime()){
                    itemSelectModif.setIt_Name(nameItem.getText().toString());
                    itemSelectModif.setIt_ObjectifDate(new Date(calendarView.getDate()));
                    listesRef.document(tda_liste.getId()).update("li_List",tda_liste.getLi_List());
                }
            }
        });
        popup.show();
    }
}
