package com.example.test_ui_to_do_list;

import android.widget.TextView;

import java.util.ArrayList;

public class TDA_Theme {
    private static int counter = 0;
    private ArrayList<TDA_Liste> th_List;
    private TextView th_Name;
    private int id;


    // constructors :
    public TDA_Theme(){
        this.th_List = new ArrayList<TDA_Liste>();
        this.th_Name = null;
        this.id = counter++;
    }

    public TDA_Theme(TextView themeName){
        this.th_List = new ArrayList<TDA_Liste>();
        this.th_Name = themeName;
        this.id = counter++;
    }


    // getters and setters :
    public ArrayList<TDA_Liste> getLi_List() {
        return th_List;
    }

    public void setLi_List(ArrayList<TDA_Liste> th_List) {
        this.th_List = th_List;
    }

    public TextView getTh_Name() {
        return th_Name;
    }

    public void setLi_Name(TextView th_Name) {
        this.th_Name = th_Name;
    }

    public int getId() {
        return id;
    }

}
