package com.example.test_ui_to_do_list;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class TDA_Liste implements Serializable {
    private static int counter = 0;
    private ArrayList<TDA_Item> li_List;
    private String li_desc;
    private boolean li_isFinish;
    private String li_Name;
    private Drawable li_drawable;
    private String id;
    private String ownerId;


    public void addItem(TDA_Item item){
        li_List.add(item);
    }

    // constructors :
    public TDA_Liste(){
        this.li_List = new ArrayList<TDA_Item>();
        this.li_Name = null;
        //this.id = counter++;
    }

    public TDA_Liste(String listName, String _ownerId){
        this.li_List = new ArrayList<TDA_Item>();
        this.li_Name = listName;
        this.li_List.add(new TDA_Item("item_test"+listName, Calendar.getInstance().getTime()));
        this.li_List.add(new TDA_Item("item_test2"+listName, Calendar.getInstance().getTime()));
        this.ownerId = _ownerId;
        //this.id = counter++;
    }

    // getters and setters :
    public ArrayList<TDA_Item> getLi_List() {
        return li_List;
    }

    public void setLi_List(ArrayList<TDA_Item> li_List) {
        this.li_List = li_List;
    }

    public String getLi_Name() {
        return li_Name;
    }

    public void setLi_Name(String li_Name) {
        this.li_Name = li_Name;
    }

    public String getLi_desc() {
        return li_desc;
    }

    public void setLi_desc(String li_desc) {
        this.li_desc = li_desc;
    }

    public boolean isLi_isFinish() {
        return li_isFinish;
    }

    public void setLi_isFinish(boolean li_isFinish) {
        this.li_isFinish = li_isFinish;
    }

    public String getId() {
        return id;
    }

    public void setId(String _id){
        this.id = _id;
    }

    public Drawable getLi_drawable() {
        return li_drawable;
    }

    public void setLi_drawable(Drawable li_drawable) {
        this.li_drawable = li_drawable;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
