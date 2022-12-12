package com.example.test_ui_to_do_list;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
    private String li_drawable;
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
        this.ownerId = _ownerId;
        //this.id = counter++;
    }

    public float progressFinish(){
        int nb_items_finis = 0;
        for (TDA_Item item :
                li_List) {
                if (item.isFinished()) {
                    nb_items_finis++;
                }
            }
        if(li_List.size() == 0)
            return 0;
        else
            return (nb_items_finis*100)/li_List.size();
    }

    public int nbItemsDone(){
        int nb_items_finis = 0;
        for (TDA_Item item :  li_List) {
            /*
            if (item.isFinished()) {
                nb_items_finis++;
            }
            */
            nb_items_finis += item.isFinished() ? 1 : 0;
        }
        return nb_items_finis;
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

    public String getLi_drawable() {
        return li_drawable;
    }

    public void setLi_drawable(String li_drawable) {
        this.li_drawable = li_drawable;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
