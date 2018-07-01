package com.example.ami.mybookshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ami.mybookshop.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookShop.db";
    private static final int DATABASE_VERSION = 1;

    BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BookEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
