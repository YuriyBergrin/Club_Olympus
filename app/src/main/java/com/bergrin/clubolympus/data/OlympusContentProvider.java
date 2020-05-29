package com.bergrin.clubolympus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import static com.bergrin.clubolympus.data.ClubOlympusContract.AUTHORITY;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_FIRST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_GENDER;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_LAST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_SPORT;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.CONTENT_MULTIPLE_ITEMS;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.CONTENT_SINGLE_ITEM;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_FEMALE;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_MALE;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_UNKNOWN;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.TABLE_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember._ID;
import static com.bergrin.clubolympus.data.ClubOlympusContract.PATH_MEMBERS;

public class OlympusContentProvider extends ContentProvider {
    OlympusDBHelper dbHelper;
    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 222;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(AUTHORITY, PATH_MEMBERS + "/#", MEMBER_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new OlympusDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can`t query incorrect uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String firstName = values.getAsString(COLUMN_FIRST_NAME);
        String lastName = values.getAsString(COLUMN_LAST_NAME);
        if (firstName == null) {
            throw new IllegalArgumentException("You have to input first name");
        }
        if (lastName == null) {
            throw new IllegalArgumentException("You have to input last name");
        }
        Integer gender = values.getAsInteger(COLUMN_GENDER);
        if (gender == null || !(gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN)) {
            throw new IllegalArgumentException("You have to input correct gender");
        }

        String sport = values.getAsString(COLUMN_SPORT);
        if (sport == null) {
            throw new IllegalArgumentException("You have to input sport");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                long id = db.insert(TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e("INSERT_METHOD", "Insertion data in the table failed for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion data in the table failed for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted;

        switch (match) {
            case MEMBERS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can`t delete this uri " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(COLUMN_FIRST_NAME)) {
            String firstName = values.getAsString(COLUMN_FIRST_NAME);
            if (firstName == null) {
                throw new IllegalArgumentException("You have to input first name");
            }
        }

        if (values.containsKey(COLUMN_LAST_NAME)) {
            String lastName = values.getAsString(COLUMN_LAST_NAME);
            if (lastName == null) {
                throw new IllegalArgumentException("You have to input last name");
            }
        }

        if (values.containsKey(COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(COLUMN_GENDER);
            if (gender == null || !(gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN)) {
                throw new IllegalArgumentException("You have to input correct gender");
            }
        }

        if (values.containsKey(COLUMN_SPORT)) {
            String sport = values.getAsString(COLUMN_SPORT);
            if (sport == null) {
                throw new IllegalArgumentException("You have to input sport");
            }
        }


        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsUpdated;

        switch (match) {
            case MEMBERS:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Can`t update this uri " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return CONTENT_MULTIPLE_ITEMS;
            case MEMBER_ID:
                return CONTENT_SINGLE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }
}
