package com.example.android.pets.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.pets.EditorActivity;
import com.example.android.pets.data.BlankContract.petContract;

import com.example.android.pets.R;

public class PetCursorAdapter extends CursorAdapter
{
   private  Context mContext;
   private Cursor mCursor;
    LinearLayout l1;

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c);
      mContext=context;
      mCursor=c;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //root view??
        return  LayoutInflater.from(context).inflate(R.layout.list_item,null);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor)
    {
        TextView Name=view.findViewById(R.id.tv_Name),Breed=view.findViewById(R.id.tv_breed);
        Name.setText(cursor.getString(cursor.getColumnIndex(petContract.COLUMN_NAME)));
        Breed.setText(cursor.getString(cursor.getColumnIndex(petContract.COLUMN_BREED)));
    }
}
