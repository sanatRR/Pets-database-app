/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetCursorAdapter;
import com.example.android.pets.data.databaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.pets.data.BlankContract.petContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    databaseHelper mDbHelper;
    static SQLiteDatabase db;
    Toast t1;
    int testInt=0;

    String name,breed;
    int gender,weight;
    ListView l1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","called");
        setContentView(R.layout.list);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        l1=(ListView)findViewById(R.id.list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                //request code is used to identify the intent while passing data back
                startActivityForResult(intent,1);
            }
        });
        testInt=1;
        // To access our database, we instantiate our subclass of SQLiteOpenHelper i.e. database.java
        // and pass the context, which is the current activity.
        mDbHelper = new databaseHelper(this);
        // Create and/or open a database to read from it
        //.getReadableDatabase() is similar to .open
        //First checks if database already exists, if not then calls the onCreate() of database.java
        // finally returns a SQLiteDatabase object
        db = mDbHelper.getWritableDatabase();

    }


    public void insertPet(String name, String breed, int gender, int weight)
    {
        Long row_id;
        ContentValues insertVal=new ContentValues();
        insertVal.put(petContract.COLUMN_NAME,name);
        insertVal.put(petContract.COLUMN_BREED,breed);
        insertVal.put(petContract.COLUMN_GENDER,gender);
        insertVal.put(petContract.COLUMN_WEIGHT,weight);
        Log.d("debug 1","db is null");
        try {
            Uri uri=getContentResolver().insert(petContract.CONTENT_URI,insertVal);
            Log.d("insertPet", String.valueOf(uri));
            t1=Toast.makeText(this,"Pet Inserted",Toast.LENGTH_SHORT);
            t1.show();
        }catch(IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart","called");
        displayDatabaseInfo();
    }

    @Override
    protected void onStop() {
        Log.d("onStop","called");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("onPause","called");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d("onRestart","called");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy","called");
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
               // insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDatabaseInfo() {

        //cursor object returned by the query method will be used to traverse the table
   /*     String projections[]={petContract._ID,petContract.COLUMN_NAME,petContract.COLUMN_GENDER,petContract.COLUMN_BREED,petContract.COLUMN_WEIGHT};
       Cursor cursor=getContentResolver().query(petContract.CONTENT_URI,projections,null,null,null);

        //Get the column indices since they're needed while getting the data for each row.
        int nameId,breedId,genderId,weightId;
        nameId=cursor.getColumnIndex(petContract.COLUMN_NAME);
        breedId=cursor.getColumnIndex(petContract.COLUMN_BREED);
        genderId=cursor.getColumnIndex(petContract.COLUMN_GENDER);
        weightId=cursor.getColumnIndex(petContract.COLUMN_WEIGHT);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
        displayView.setText("Number of rows in pets database table: " + cursor.getCount());
        displayView.append("\nName\tBreed\tGender\tWeight");
        //moveToNext() method moves cursor to next row, if it is unable to do so then returns false, it starts at row -1.
        while(cursor.moveToNext())
        {
            //Use the column indices obtained earlier to get data for each row
            displayView.append("\n"+cursor.getString(nameId)+"\t"+cursor.getString(breedId)+"\t"+cursor.getInt(genderId)+"\t"+cursor.getInt(weightId));
        }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        } */
        String projections[]={petContract._ID,petContract.COLUMN_NAME,petContract.COLUMN_GENDER,petContract.COLUMN_BREED,petContract.COLUMN_WEIGHT};
        Cursor cursor=getContentResolver().query(petContract.CONTENT_URI,projections,null,null,null);
        PetCursorAdapter p1=new PetCursorAdapter(this,cursor);
        ListView l1=(ListView)findViewById(R.id.list);
        l1.setAdapter(p1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                name=data.getStringExtra("iname");
                breed=data.getStringExtra("ibreed");
                gender=data.getIntExtra("igender",0);
                weight=data.getIntExtra("iweight",0);
                insertPet(name,breed,gender,weight);
            }
        }
    }
}
