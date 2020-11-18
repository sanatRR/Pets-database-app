package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BlankContract
{
    public static final String CONTENT_AUTHORITY="com.example.android.pets";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PETS="pets";    //This is the Table Name.

    public static final class petContract implements BaseColumns
     {
        //Formed the complete uri, named it as CONTENT_URI
         public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);
         //CURSOR_DIR_BASE_TYPE=vnd.android.cursor.dir
         public static final String CONTENT_LIST_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PETS;
         //CURSOR_ITEM_BASE_TYPE=vnd.android.cursor.item
         public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PETS;

         //Constants for gender
         public static final int GENDER_MALE=1;
         public static final int GENDER_FEMALE=2;
         public static final int GENDER_unknown=0;
         public static final String _COUNT=BaseColumns._COUNT;

         //Name of the table
         public static final String Table_Name="pets";

         //Constants for representing columns
         public static final String _ID=BaseColumns._ID;
         public static final String COLUMN_NAME="name";
         public static final String COLUMN_BREED="breed";
         public static final String COLUMN_GENDER="gender";
         public static final String COLUMN_WEIGHT="weight";
     }

}
