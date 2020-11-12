package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class BlankContract
{

     public static final class petContract implements BaseColumns
     {
         //Constants for gender
         public static final int GENDER_MALE=1;
         public static final int GENDER_FEMALE=2;
         public static final int GENDER_unknown=0;

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
