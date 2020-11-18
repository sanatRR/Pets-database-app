package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.CatalogActivity;
import com.example.android.pets.data.BlankContract.petContract;

import java.util.IllformedLocaleException;

/**
 * {@link ContentProvider} for Pets app.
 * The query method of the petprovider  translates the passed parameters into SQL and then the database is acted on
 */

public class PetProvider extends ContentProvider {

    private databaseHelper db;
    private Context mContext;
    private static final UriMatcher sUriMatcher;
    //set codes for identifying both the cases
    private static final int PETS=100,PET_ID=101;

    static
    {
        //Create a UriMatcher object with No Matches
        sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        //Add both the cases, i.e. The entire table and a specific id
        sUriMatcher.addURI(BlankContract.CONTENT_AUTHORITY,BlankContract.PATH_PETS,PETS);
        sUriMatcher.addURI(BlankContract.CONTENT_AUTHORITY,BlankContract.PATH_PETS+"/#",PET_ID);
    }

    public PetProvider(Context context)
    {
        super();
        //Context is required in databaseHelper
        mContext=context;
    }

    public PetProvider()
    {
        Log.d("Debug -1","No arg constructor");
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        //getContext() : returns the Context which is linked to the Activity from which is called.
        db=new databaseHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = db.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor=database.query(petContract.Table_Name,null,null,null,null,null,null);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = petContract._ID+ "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(petContract.Table_Name, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        int match=sUriMatcher.match(uri);
        switch (match)
        {
            //Only PETS case is supported for insertion, we cannot insert a row for a particular id.
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported");

        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) throws IllegalArgumentException
    {

        if(!isvalidGender(contentValues.getAsInteger(petContract.COLUMN_GENDER)))
            throw new IllegalArgumentException("Invalid Gender");

        if(contentValues.getAsString(petContract.COLUMN_NAME).length()==0)
        {
            Toast nullName=Toast.makeText(getContext(),"Name is necessary",Toast.LENGTH_SHORT);
            nullName.show();
            throw new IllegalArgumentException("Name null");
        }

        if(contentValues.getAsInteger(petContract.COLUMN_WEIGHT)<0)
        {
            Toast negativeWeight=Toast.makeText(getContext(),"Weight must be positive",Toast.LENGTH_SHORT);
            negativeWeight.show();
            throw new IllegalArgumentException("Weight Negative");

        }


        SQLiteDatabase WriteDatabase=db.getWritableDatabase();
        //insert will return id to the row at which info is inserted
        long id=WriteDatabase.insert(petContract.Table_Name,null,contentValues);
        //return Uri with id appended
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    private boolean isvalidGender(int gender)
    {
        if(gender==petContract.GENDER_unknown || gender==petContract.GENDER_FEMALE || gender==petContract.GENDER_MALE)
            return true;
        else
            return false;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if(contentValues.size()==0)
            return 0;
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                return updatePet(uri,contentValues,selection,selectionArgs);
            case PET_ID:
                selection=petContract._ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("update is not supported for "+uri);
        }
    }

    private int updatePet(Uri uri,ContentValues values,String selection,String[] selectionArgs)
    {
        if(values.containsKey(petContract.COLUMN_GENDER) && !isvalidGender(values.getAsInteger(petContract.COLUMN_GENDER)))
            throw new IllegalArgumentException("Invalid Gender");

        if(values.containsKey(petContract.COLUMN_NAME) && values.getAsString(petContract.COLUMN_NAME).length()==0)
        {
            Toast nullName=Toast.makeText(getContext(),"Name is necessary",Toast.LENGTH_SHORT);
            nullName.show();
            throw new IllegalArgumentException("Name null");
        }

        if(values.containsKey(petContract.COLUMN_WEIGHT) && values.getAsInteger(petContract.COLUMN_WEIGHT)<0)
        {
            Toast negativeWeight=Toast.makeText(getContext(),"Weight must be positive",Toast.LENGTH_SHORT);
            negativeWeight.show();
            throw new IllegalArgumentException("Weight Negative");

        }

        SQLiteDatabase updateDb=db.getWritableDatabase();
        int update=updateDb.update(petContract.Table_Name,values,selection,selectionArgs);
        if(update!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return update;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase delDbase=db.getWritableDatabase();
        int match=sUriMatcher.match(uri),rowsDeleted;
        getContext().getContentResolver().notifyChange(uri,null);
        switch (match)
        {
            case PETS:
                rowsDeleted=delDbase.delete(petContract.Table_Name,selection,selectionArgs);
                break;
            case PET_ID:
                selection=petContract._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=delDbase.delete(petContract.Table_Name,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if(rowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     * i.e. return a string that describes type of data store in Uri
     */
    @Override
    public String getType(Uri uri)
    {
        int matchType=sUriMatcher.match(uri);
        switch(matchType)
        {
            case PETS:
                return petContract.CONTENT_LIST_TYPE;
            case PET_ID:
                return petContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri+" with match "+matchType);
        }
    }
}