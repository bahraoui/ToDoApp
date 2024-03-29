package com.example.test_ui_to_do_list;

import java.util.Calendar;
import java.util.Date;

public class TDA_Item {
    private static int counter = 0;
    private String it_Name;
    private Date it_ObjectifDate;
    private boolean isFinished;
    private boolean isOutdated;
    private int id;


    public boolean checkTime(){
        if (it_ObjectifDate.compareTo(Calendar.getInstance().getTime()) < 0){
            return (isOutdated = true);
        }
        return isOutdated;
    }

    // constructors :
    public TDA_Item(){
        this.it_Name = null;
        this.isFinished = false;
        this.isOutdated = false;
        this.it_ObjectifDate = Calendar.getInstance().getTime();
        this.id = counter++;
    }

    public TDA_Item(String themeName, Date newDate){
        this.it_Name = themeName;
        this.isFinished = false;
        this.isOutdated = false;
        this.it_ObjectifDate = newDate;
        this.id = counter++;
    }


    // getters and setters :
    public String getIt_Name() {
        return it_Name;
    }

    public void setIt_Name(String it_Name) {
        this.it_Name = it_Name;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getId() {
        return id;
    }
}
