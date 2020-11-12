package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class database extends SQLiteOpenHelper {

    private static String name="datatest";
    private static int databaseVersion=1;

    /**
     * Called when object of database is made
     * @param context Context of activity from which the method was called
     */
    public database(Context context)
    {
        /**
         * @params name database name
         * @params factory cursor factory, currently set to null
         * @params databaseVersion Version of database to be made.
          * */
        super(context,name,null,databaseVersion);
        //third argument is cursor factory
    }

    /**Called for first time when App is run
     * Run when the database file did not exist and was just made.
     * @param sqLiteDatabase object of SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String COMMA_SEP=",";
        //Command to create table
        String CREATE_PET_TABLE="CREATE TABLE "+BlankContract.petContract.Table_Name+" ("+BlankContract.petContract._ID +" INTEGER PRIMARY KEY AUTOINCREMENT"+COMMA_SEP+BlankContract.petContract.COLUMN_NAME+" TEXT NOT NULL"+COMMA_SEP+BlankContract.petContract.COLUMN_BREED+" TEXT NOT NULL"+COMMA_SEP+BlankContract.petContract.COLUMN_GENDER+" INTEGER DEFAULT 0"+COMMA_SEP+BlankContract.petContract.COLUMN_WEIGHT+" INTEGER"+");";
        Log.i("debug 2: ",CREATE_PET_TABLE);
        //Execute the sql query, execSQL cannot be used for select statements
        sqLiteDatabase.execSQL(CREATE_PET_TABLE);
    }

    /**
     *Called when database file exists, but stored version number is lower than the requested one.
     * @param sqLiteDatabase  object of SQLiteDatabase
     * @param i old version
     * @param i1 new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        //Execute the DROP sql query
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+BlankContract.petContract.Table_Name);
        onCreate(sqLiteDatabase);
    }
}
