package com.example.test_ui_to_do_list;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TDA_Item {
    private static int counter = 0;
    private TextView it_Name;
    private Date it_ObjectifDate;
    private boolean isFinished;
    private int id;


    public boolean checkTime(){
        if (it_ObjectifDate.compareTo(Calendar.getInstance().getTime()) < 0){
            return (isFinished = true);
        }
        return isFinished;
    }

    // constructors :
    public TDA_Item(){
        this.it_Name = null;
        this.isFinished = false;
        this.it_ObjectifDate = Calendar.getInstance().getTime();
        this.id = counter++;
    }

    public TDA_Item(TextView themeName, Date newDate){
        this.it_Name = themeName;
        this.isFinished = false;
        this.it_ObjectifDate = newDate;
        this.id = counter++;
    }


    // getters and setters :
    public TextView getIt_Name() {
        return it_Name;
    }

    public void setIt_Name(TextView it_Name) {
        this.it_Name = it_Name;
    }

    public int getId() {
        return id;
    }
}
