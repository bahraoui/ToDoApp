package com.example.test_ui_to_do_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.constraintlayout.widget.ConstraintLayout;

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
    private ConstraintLayout button_share;
    private TDA_Liste tda_liste;
    private final AtomicBoolean isFirstLaunch = new AtomicBoolean(true);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");

    // dialog
    private Dialog popup_item;
    private TDA_Item itemSelectModif;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_list);
        tv_TitleList = findViewById(R.id.inlist_tv_title);
        tv_AddButton = findViewById(R.id.list_tv_add);
        tv_ReturnButton = findViewById(R.id.imageView_return);
        button_share = findViewById(R.id.constraintLayout_inlist_share);
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

        button_share.setOnClickListener(view -> {
            deleteList();
        });
        popup_item = new Dialog(this);
        majUI();
    }

    private void deleteList() {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_in_list.this);

        // Set the message show for the Alert time
        builder.setMessage("Voulez-vous vraiment supprimer la liste "+tda_liste.getLi_Name()+" ?");

        // Set Alert Title
        builder.setTitle("Suppression liste "+tda_liste.getLi_Name());

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // When the user click yes button then app will close
            // SUPPRESSION LISTE ICI
            listesRef.document(tda_liste.getId()).delete();
            finish();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();

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
                            if (isFirstLaunch.get()){
                                majUI();
                            }
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
            popup_item = new Dialog(this);
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
                                    "\nli vrai nom "+tda_liste.getLi_Name()+
                                    "\nli id "+liste_tmp.getId()+
                                    "\nli vrai id "+tda_liste.getId();
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

    private void setTitle(TDA_Liste liste){
        tv_TitleList.setText(liste.getLi_Name());
    }

    public void showPopUp() {
        TextView close;
        final EditText[] nameItem = new EditText[1];
        final CalendarView[] calendarView = new CalendarView[1];
        Button validationModifItem;
        popup_item.setContentView(R.layout.popup_item);
        popup_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        close = (TextView) popup_item.findViewById(R.id.popup_item_closeWindows);
        nameItem[0] = (EditText) popup_item.findViewById(R.id.popup_item_modification_et_name);
        calendarView[0] = (CalendarView) popup_item.findViewById(R.id.popup_item_simpleCalendarView);
        validationModifItem = (Button) popup_item.findViewById(R.id.pop_item_modification_btn_create);

        nameItem[0].setText(itemSelectModif.getIt_Name());
        calendarView[0].setDate(itemSelectModif.getIt_ObjectifDate().getTime());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_item.dismiss();
            }
        });
        validationModifItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameItem[0] = (EditText) popup_item.findViewById(R.id.popup_item_modification_et_name);
                calendarView[0] = (CalendarView) popup_item.findViewById(R.id.popup_item_simpleCalendarView);
                if(!nameItem[0].getText().toString().equals(itemSelectModif.getIt_Name())
                        || itemSelectModif.getIt_ObjectifDate().compareTo(new Date(calendarView[0].getDate())) != 0){
                    itemSelectModif.setIt_Name(nameItem[0].getText().toString());
                    itemSelectModif.setIt_ObjectifDate(new Date(calendarView[0].getDate()));
                    listesRef.document(tda_liste.getId()).update("li_List",tda_liste.getLi_List());
                    popup_item.dismiss();
                } else {
                    String afficher = "";
                    Toast.makeText(activity_in_list.this,afficher,Toast.LENGTH_SHORT).show();
                }
            }
        });
        popup_item.show();
    }
}
