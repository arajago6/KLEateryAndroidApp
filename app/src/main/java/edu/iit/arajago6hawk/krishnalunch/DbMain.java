package edu.iit.arajago6hawk.krishnalunch;

/**
 * Created by rasuishere on 2/27/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.Spanned;

public class DbMain extends SQLiteOpenHelper {

    public static final String DB_NAME = "OrdHist.db";
    public static final String HISTORY_TABLE_NAME = "ar_history";
    public static final String HISTORY_COLUMN_ID = "id";
    public static final String HISTORY_COLUMN_TS = "tstamp";
    public static final String HISTORY_COLUMN_ITEMS = "items";
    public static final String HISTORY_COLUMN_COST = "cost";

    public DbMain(Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table ar_history " +
                        "(id integer primary key autoincrement, tstamp text, items text, cost float)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS ar_history");
        onCreate(db);
    }

    public boolean insertOrder (String tstamp, String items, double cost)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tstamp", tstamp);
        contentValues.put("items", items);
        contentValues.put("cost", cost);
        db.insert("ar_history", null, contentValues);
        db.close();
        return true;
    }

    public int numberOfOrders(){
        SQLiteDatabase db = this.getReadableDatabase();
        int ordCount = (int) DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME);
        return ordCount;
    }

    public Integer deleteAllOrders ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("ar_history",null,null);
    }

    public ArrayList<Spanned> getAllOrders()
    {
        ArrayList<Spanned> array_list = new ArrayList<Spanned>();
        String retString = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ar_history", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){

            String id  = res.getString(res.getColumnIndex(HISTORY_COLUMN_ID));
            String time = res.getString(res.getColumnIndex(HISTORY_COLUMN_TS));
            String items = res.getString(res.getColumnIndex(HISTORY_COLUMN_ITEMS));
            String cost = res.getString(res.getColumnIndex(HISTORY_COLUMN_COST));

            String htmlString = "<br/><font color=\"#4153B6\"><b><i>Transaction Id: </font></i></b>" + id
                                + "<br/><font color=\"#4153B6\"><b><i>Dish(es): </font></i></b>" + items
                                + "<br/><font color=\"#4153B6\"><b><i>Total: </font></i></b>" + "$" + cost
                                + "<br/><font color=\"#4153B6\"><b><i>Time Log: </font></i></b>" + time + "<br/>";


            //retString = res.getString(res.getColumnIndex(HISTORY_COLUMN_ID)) + "\t" + res.getString(res.getColumnIndex(HISTORY_COLUMN_TS))
            //                + "\t" + res.getString(res.getColumnIndex(HISTORY_COLUMN_ITEMS)) + "\t" + res.getString(res.getColumnIndex(HISTORY_COLUMN_COST));

            array_list.add(Html.fromHtml(htmlString));
            res.moveToNext();
        }
        return array_list;
    }
}