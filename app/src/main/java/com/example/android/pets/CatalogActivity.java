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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.BlankContract;
import com.example.android.pets.data.database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.pets.data.BlankContract.petContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    database mDbHelper;
    static SQLiteDatabase db;
    TextView displayView;
    Toast t1;
    int testInt=0;

    String name,breed;
    int gender,weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","called");
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        displayView=(TextView)findViewById(R.id.text_view_pet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivityForResult(intent,1);
            }
        });
        testInt=1;
        // To access our database, we instantiate our subclass of SQLiteOpenHelper i.e. database.java
        // and pass the context, which is the current activity.
        mDbHelper = new database(this);
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
        row_id=db.insert("pets",null,insertVal);
        t1=Toast.makeText(this,String.valueOf(row_id),Toast.LENGTH_SHORT);
        t1.show();
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
       // displayView = (TextView) findViewById(R.id.text_view_pet);
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

        Log.d("debug 1"," displayDataInfo() called");

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + BlankContract.petContract.Table_Name, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
        displayView.setText("Number of rows in pets database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
