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


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.databaseHelper;


import com.example.android.pets.data.BlankContract.petContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    String Name,Breed,WeightTemp;
    int Weight,gender;
    Intent i;

    /** EditText field to enter the pet's name */
    public  EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    public  EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    public  EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    public  Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    public static int mGender = 0;
    boolean mEditMode,mPetHasChanged=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mNameEditText.setOnTouchListener(mTOuchListener);
        mBreedEditText.setOnTouchListener(mTOuchListener);
        mWeightEditText.setOnTouchListener(mTOuchListener);
        mGenderSpinner.setOnTouchListener(mTOuchListener);
        setupSpinner();
        //Return the intent that started this activity
        i=getIntent();
        Name=i.getStringExtra("iname");
        Breed=i.getStringExtra("ibreed");
        Weight=i.getIntExtra("iweight",0);
        gender=i.getIntExtra("igender",0);
        mEditMode=i.getData()!=null;
        if(mEditMode)
        {
            this.setTitle("Edit pet");
            mNameEditText.setText(i.getStringExtra("eName"));
            mBreedEditText.setText(i.getStringExtra("eBreed"));
            mWeightEditText.setText(String.valueOf(i.getIntExtra("eWeight",0)));
            mGenderSpinner.setSelection(i.getIntExtra("eGender",0));
            Log.d("Editor Activity", String.valueOf(ContentUris.parseId(i.getData())));
        }
    }

    private View.OnTouchListener mTOuchListener=new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged=true;
            //return false??
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(!mEditMode)
        {
            MenuItem menuitem=menu.findItem(R.id.action_delete);
            menuitem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        //Create a display dialog asking for choice
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        //positive button=yes, Negative button=no
        builder.setPositiveButton("DISCARD",discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null)
                    dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        if(!mPetHasChanged)
        {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonListener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonListener);
    }

    private void showDeleteConfirmationDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete this pet?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePet();
                Toast t1=Toast.makeText(getApplicationContext(),"pet deleted",Toast.LENGTH_SHORT);
                t1.show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null)
                {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void deletePet()
    {
        databaseHelper delHelper=new databaseHelper(this);
        SQLiteDatabase delBase=delHelper.getWritableDatabase();
        getContentResolver().delete(i.getData(),null,null);
    }



    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             /*   String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = petContract.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = petContract.GENDER_FEMALE; // Female
                    } else {
                        mGender = petContract.GENDER_unknown; // Unknown
                    }
                } */
                switch (position)
                {
                    case 0:
                        mGender=petContract.GENDER_unknown;
                        break;
                    case 1:
                        mGender=petContract.GENDER_MALE;
                        break;
                    case 2:
                        mGender=petContract.GENDER_FEMALE;
                        break;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                Name=mNameEditText.getText().toString().trim();
                Breed=mBreedEditText.getText().toString().trim();
                WeightTemp=mWeightEditText.getText().toString().trim();
                if(WeightTemp.length()==0)
                    Weight=0;
                else
                   Weight=Integer.parseInt(WeightTemp);
                if(Breed.length()==0)
                    Breed="Unknown breed";
                Intent result=new Intent();
                result.putExtra("iname",Name);
                result.putExtra("ibreed",Breed);
                result.putExtra("iweight",Weight);
                result.putExtra("igender",mGender);
                result.setData(i.getData());
                Log.d("debug -2", String.valueOf(i.getData()));
                setResult(RESULT_OK,result);
                //finish() to close this activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return  true;
                }
                DialogInterface.OnClickListener discardButtonClickListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}