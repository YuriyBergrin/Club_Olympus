package com.bergrin.clubolympus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.bergrin.clubolympus.data.ClubOlympusContract.DATABASE_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.DATABASE_VERSION;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_FIRST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_GENDER;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_LAST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_SPORT;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.TABLE_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember._ID;

public class OlympusDBHelper extends SQLiteOpenHelper {

    public OlympusDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEBERS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_FIRST_NAME + " TEXT,"
                + COLUMN_LAST_NAME + " TEXT,"
                + COLUMN_GENDER + " INTEGER NOT NULL,"
                + COLUMN_SPORT + " TEXT" + ")";

        db.execSQL(CREATE_MEBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
