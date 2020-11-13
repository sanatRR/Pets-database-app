package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.android.pets.data.BlankContract.petContract;

public class database extends SQLiteOpenHelper {

    private static String name="pet";
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
        String CREATE_PET_TABLE="CREATE TABLE "+petContract.Table_Name+" ("+petContract._ID +" INTEGER PRIMARY KEY AUTOINCREMENT"+COMMA_SEP+petContract.COLUMN_NAME+" TEXT "+COMMA_SEP+petContract.COLUMN_BREED+" TEXT "+COMMA_SEP+petContract.COLUMN_GENDER+" INTEGER DEFAULT 0"+COMMA_SEP+petContract.COLUMN_WEIGHT+" INTEGER"+");";
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
