package com.example.test_ui_to_do_list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DBHandlerItem extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "tododb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "todoitems";

    // below variable is for our id column.
    private static final String ID_COL = "idItem";

    // below variable is for our course name column
    private static final String NAME_COL = "nameItem";

    // below variable id for our course duration column.
    private static final String IS_FINISH = "isFinishItem";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "descriptionItem";

    // below variable for our course description column.
    private static final String DATE_COL = "dateItem";

    // creating a constructor for our database handler.
    public DBHandlerItem(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + IS_FINISH + " BOOLEAN,"
                + DATE_COL + " INTEGER,"
                + DESCRIPTION_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewItem(String itemName, boolean isFinishItem, String descItem, int dateItem) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, itemName);
        values.put(IS_FINISH, isFinishItem);
        values.put(DESCRIPTION_COL, descItem);
        values.put(DATE_COL, dateItem);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<TDA_Item> readItems() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorItems = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<TDA_Item> items_ArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorItems.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                items_ArrayList.add(new TDA_Item(cursorItems.getString(1), Calendar.getInstance().getTime()));
            } while (cursorItems.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorItems.close();
        return items_ArrayList;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

