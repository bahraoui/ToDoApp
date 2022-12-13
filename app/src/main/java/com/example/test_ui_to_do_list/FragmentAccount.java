package com.example.test_ui_to_do_list;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String prenomSTR;
    private String nomSTR;
    private String emailSTR;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");

    public FragmentAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Account.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAccount newInstance(String param1, String param2) {
        FragmentAccount fragment = new FragmentAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersRef.get().addOnSuccessListener(getActivity(),queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot dcs : queryDocumentSnapshots){
                if(dcs.get("idUsersAuth",String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    prenomSTR = dcs.get("prenom", String.class);
                    nomSTR = dcs.get("nom", String.class);
                    emailSTR = dcs.get("email",String.class);
                }
            }
        });
        if (getArguments() != null) {
            prenomSTR = getArguments().getString(ARG_PARAM1);
            nomSTR = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btn_deco = getView().findViewById(R.id.account_pass_change2);
        TextView prenomTv = getView().findViewById(R.id.fragmentAccount_prenom);
        TextView nomTv = getView().findViewById(R.id.fragmentAccount_nom);
        EditText emailEt = getView().findViewById(R.id.fragmentaccount_mail);
        prenomTv.setText(prenomSTR);
        nomTv.setText(nomSTR);
        emailEt.setText(emailSTR);
        btn_deco.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        });
        super.onViewCreated(view, savedInstanceState);
    }
}