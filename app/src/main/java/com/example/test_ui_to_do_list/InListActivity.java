package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class InListActivity extends AppCompatActivity {
    private TextView tv_TitleList;
    private TextView tv_AddButton;
    private ImageView tv_ReturnButton;
    private TDA_Liste tda_liste;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference listesRef = db.collection("Listes");
    private CollectionReference userListes = db.collection("ListesID");

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
        Intent i = new Intent(this, ItemCreationActivity.class);
        TDA_Liste liste = (TDA_Liste) getIntent().getSerializableExtra("tda_liste");
        i.putExtra("tda_liste_ajout_item",liste);
        startActivity(i);
    }

    synchronized private void majUI(){
        //ArrayList<TDA_Liste> toutes_listes = dbList.readLists();
        ViewGroup main = findViewById(R.id.items_linearlayout_insertPoint);
        main.removeAllViews();

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
                                Toast.makeText(InListActivity.this, "OUIIIIIIIIIIIIIII", Toast.LENGTH_SHORT).show();

                                for (TDA_Item item : liste_tmp.getLi_List()) {
                                    addItemUI(item);
                                }
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

    synchronized private void addItemUI(TDA_Item tda_item){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_view_element, null);

        EditText txt = view.findViewById(R.id.item_et_title);
        txt.setEnabled(false);
        txt.setText(tda_item.getIt_Name());

        CheckBox it_check = view.findViewById(R.id.item_checkBox);
        it_check.setChecked(tda_item.isFinished());
        it_check.setOnClickListener(view1 -> {
            boolean checked = ((CheckBox) view1).isChecked();
            // Check which checkbox was clicked
            tda_item.setFinished(checked);
        });

        ImageView crayon = view.findViewById(R.id.item_img_modify);
        crayon.setOnClickListener(view3 -> {
            if(txt.isEnabled()){
                crayon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            } else {
                crayon.setImageDrawable(getResources().getDrawable(R.drawable.ic_crayon_modify));
            }
            txt.setEnabled(!txt.isEnabled());
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

    private void setTitle(TDA_Liste liste){
        tv_TitleList.setText(liste.getLi_Name());
    }
}
