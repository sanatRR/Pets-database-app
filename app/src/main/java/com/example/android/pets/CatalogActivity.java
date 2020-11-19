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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.pets.data.PetCursorAdapter;
import com.example.android.pets.data.databaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.pets.data.BlankContract.petContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    databaseHelper mDbHelper;
    static SQLiteDatabase db;
    Toast t1;
    int testInt=0;

    String name,breed;
    int gender,weight;
    ListView l1;
    RelativeLayout r1;
    PetCursorAdapter p1;
    Intent edit;
    private Cursor mCursor;
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","called");
        setContentView(R.layout.list);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        l1=(ListView)findViewById(R.id.list);
        r1=findViewById(R.id.empty_view);
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
        p1=new PetCursorAdapter(this,null);
        l1.setAdapter(p1);
        getSupportLoaderManager().initLoader(1,null,this);
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                mCursor.moveToFirst();
                Uri uri=Uri.withAppendedPath(petContract.CONTENT_URI, String.valueOf(id));
                Log.d("Uri is", String.valueOf(uri));
                mCursor.move(position);
                Log.d("mCursor", String.valueOf(mCursor.getPosition()));
                edit=new Intent(CatalogActivity.this, EditorActivity.class);
                edit.setData(uri); mUri=uri;
                edit.putExtra("eName",mCursor.getString(mCursor.getColumnIndex(petContract.COLUMN_NAME)));
                edit.putExtra("eBreed",mCursor.getString(mCursor.getColumnIndex(petContract.COLUMN_BREED)));
                edit.putExtra("eGender",mCursor.getInt(mCursor.getColumnIndex(petContract.COLUMN_GENDER)));
                edit.putExtra("eWeight",mCursor.getInt(mCursor.getColumnIndex(petContract.COLUMN_WEIGHT)));
                startActivityForResult(edit,2);
            }
        });
    }

    public void savePet(String name, String breed, int gender, int weight)
    {
        ContentValues insertVal=new ContentValues();
        insertVal.put(petContract.COLUMN_NAME,name);
        insertVal.put(petContract.COLUMN_BREED,breed);
        insertVal.put(petContract.COLUMN_GENDER,gender);
        insertVal.put(petContract.COLUMN_WEIGHT,weight);
        Log.d("debug 0", String.valueOf(mUri));
        if(mUri==null)
        {
           Log.d("debug 1", String.valueOf(mUri));
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
       else
       {
          try {
                  Log.d("uriupdate","insert");
                  getContentResolver().update(mUri,insertVal,null,null);
                  mUri=null;
                  t1=Toast.makeText(this,"pet updated",Toast.LENGTH_SHORT);
                  t1.show();
              }
              catch(IllegalArgumentException e)
              {
                  e.printStackTrace();
                  t1=Toast.makeText(this,"changes not saved",Toast.LENGTH_SHORT);
                  t1.show();
              }
          }
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
        // Respond to a click on the "Delete all entries" menu option
        if (item.getItemId() == R.id.action_delete_all_entries) {
            getContentResolver().delete(petContract.CONTENT_URI, null, null);
            t1 = Toast.makeText(this, "All pets deleted", Toast.LENGTH_LONG);
            t1.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1||requestCode==2)
        {
            if(resultCode==RESULT_OK)
            {
                name=data.getStringExtra("iname");
                breed=data.getStringExtra("ibreed");
                gender=data.getIntExtra("igender",0);
                weight=data.getIntExtra("iweight",0);
                mUri=data.getData();
                Log.d("debug -1", String.valueOf(mUri));
                savePet(name,breed,gender,weight);
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,petContract.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //Update p1 PetCursorApadter with updated pet data
        Log.d("cursor data", String.valueOf(data.getPosition()));
        mCursor=data;
        p1.swapCursor(data);
        if(data.getCount()==0)
            r1.setVisibility(View.VISIBLE);
        else
            r1.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //Callback called when data needs to be deleted
        p1.swapCursor(null);
    }
}
