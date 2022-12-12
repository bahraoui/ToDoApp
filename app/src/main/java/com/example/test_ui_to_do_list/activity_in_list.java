package com.example.test_ui_to_do_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class activity_in_list extends AppCompatActivity {
    private TextView tv_TitleList;
    private TextView tv_AddButton;
    private ImageView tv_ReturnButton;
    private ConstraintLayout button_share;
    private ImageView modifyList;
    private TDA_Liste tda_liste;
    private final AtomicBoolean isFirstLaunch = new AtomicBoolean(true);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");

    // dialog
    private Dialog popup_item;
    private TDA_Item itemSelectModif;
    private Dialog popup_liste;
    private ImageView imgSelectionne;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_list);
        tv_TitleList = findViewById(R.id.inlist_tv_title);
        tv_AddButton = findViewById(R.id.list_tv_add);
        tv_ReturnButton = findViewById(R.id.imageView_return);
        button_share = findViewById(R.id.constraintLayout_inlist_share);
        modifyList = findViewById(R.id.modify_liste_inlist);
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
            // donner adresse mail pour
            // partager
        });

        modifyList.setOnClickListener(v -> {
            showPopup_liste();
        });
        popup_item = new Dialog(this);
        popup_liste = new Dialog(this);
        majUI();
    }

    private void deleteList(Dialog _dialog) {
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
            _dialog.dismiss();
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
                            if (isFirstLaunch.get()){
                                majUI();
                            }
                            break;
                        case REMOVED:
                            if (isFirstLaunch.get()){
                                majUI();
                            }
                            break;
                        case MODIFIED:
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
            showPopUp_item();
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

        // pourcentage liste
        TextView bottomPourcentage = findViewById(R.id.inlist_tv_progress);
        int nbItemsDone = tda_liste.nbItemsDone();
        int nbItems = tda_liste.getLi_List().size();
        int pourcentageDone = Math.round(tda_liste.progressFinish());
        if (pourcentageDone > 80f){
            bottomPourcentage.setTextColor(Color.rgb(46,184,42)); // vert
        } else if (pourcentageDone > 30f){
            bottomPourcentage.setTextColor(Color.rgb(251,190,80)); // orange
        } else {
            bottomPourcentage.setTextColor(Color.rgb(244,76,51)); // rouge
        }
        String displayBottomText = nbItemsDone+"/"+nbItems+" - "+pourcentageDone+"%";
        bottomPourcentage.setText(displayBottomText);

        // icone liste
        ImageView iv_icon = findViewById(R.id.inlist_iv_icon);
        iv_icon.setImageDrawable(new BitmapDrawable(getResources(), tda_liste.getLi_drawable()));
        // titre liste
        tv_TitleList.setText(tda_liste.getLi_Name());

        // ajout item ui
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
                            /*
                            for (String x :
                                    listeIdentifiantsUser_hashmap.keySet()) {
                                Toast.makeText(List_Activity.this, "cle : "+x+" - valeur : "
                                +listeIdentifiantsUser_hashmap.get(x), Toast.LENGTH_SHORT).show();

                            }*/
                            if (liste_tmp != null && liste_tmp.getId()!=null && liste_tmp.getId().equals(tda_liste.getId())
                                /* &&
                                    listeIdentifiantsUser_hashmap.get(liste_tmp.getId()) == mAuth.getCurrentUser().getUid()
                                 */
                            ){
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

    public void showPopUp_item() {
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
                    String afficher = "aucun changement";
                    Toast.makeText(activity_in_list.this,afficher,Toast.LENGTH_SHORT).show();
                }
            }
        });
        popup_item.show();
    }

    public void showPopup_liste(){
        ArrayList<ImageView> logos;
        logos = new ArrayList<>();
        popup_liste.setContentView(R.layout.popup_liste);
        popup_liste.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imgSelectionne = popup_liste.findViewById(R.id.logo5);
        imgSelectionne.setTag(R.drawable.ic_car);
        logos.add(popup_liste.findViewById(R.id.logo1));
        logos.add(popup_liste.findViewById(R.id.logo2));
        logos.add(popup_liste.findViewById(R.id.logo3));
        logos.add(popup_liste.findViewById(R.id.logo4));
        logos.add(popup_liste.findViewById(R.id.logo5));
        logos.add(popup_liste.findViewById(R.id.logo6));
        logos.add(popup_liste.findViewById(R.id.logo7));
        logos.add(popup_liste.findViewById(R.id.logo8));
        logos.add(popup_liste.findViewById(R.id.logo9));
        logos.add(popup_liste.findViewById(R.id.logo10));
        selectIconDrawable(popup_liste.findViewById(R.id.logo1));

        // default value
        EditText et2 = (EditText) popup_liste.findViewById(R.id.popup_liste_modification_et_name);
        et2.setText(tda_liste.getLi_Name());
        Drawable drawableListIcon = new BitmapDrawable(getResources(), tda_liste.getLi_drawable());
        for(ImageView iv : logos){
            if(iv.getDrawable()==drawableListIcon){
                selectIconDrawable(iv);
            }
        }

        // bouton valider
        Button validateModification = popup_liste.findViewById(R.id.pop_liste_modification_btn_create);
        validateModification.setOnClickListener(v -> {
            EditText et = (EditText) popup_liste.findViewById(R.id.popup_liste_modification_et_name);
            String newNameList = et.getText().toString();
            if(newNameList.isEmpty()) return;
            Drawable drawableListIcon2 = new BitmapDrawable(getResources(), tda_liste.getLi_drawable());
            if (tda_liste.getLi_Name().equals(newNameList)
                    && drawableListIcon2  == imgSelectionne.getDrawable()){
                Toast.makeText(this,"aucun changement",Toast.LENGTH_SHORT).show();
                // afficher qu'il n'ya eu aucun changement
            } else {
                tda_liste.setLi_Name(newNameList);
                tda_liste.setLi_drawable(TDA_Liste.drawableToBitmap(imgSelectionne.getDrawable()));
                listesRef.document(tda_liste.getId()).update("li_Name",tda_liste.getLi_Name(),
                        "li_drawable",tda_liste.getLi_drawable());
                popup_liste.dismiss();
                //majUI();
            }
        });

        // bouton supprimer
        Button btn_delete = popup_liste.findViewById(R.id.pop_liste_modification_btn_delete);
        btn_delete.setOnClickListener(v -> {
            deleteList(popup_liste);
        });

        // bouton fermer
        Button closeWindow = popup_liste.findViewById(R.id.popup_liste_closeWindows);
        closeWindow.setOnClickListener(v -> {
            popup_liste.dismiss();
        });

        // afficher popup modification liste
        popup_liste.show();
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
}
