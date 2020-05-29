package com.bergrin.clubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_FIRST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_GENDER;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_LAST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_SPORT;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.CONTENT_URI;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_FEMALE;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_MALE;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.GENDER_UNKNOWN;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember._ID;

public class AddMemberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int EDIT_MEMBER_LOADER = 111;
    Uri currentMemberUri;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText sportEditText;
    private Spinner genderSpinner;
    private int gender = 0;
    private ArrayAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();
        currentMemberUri = intent.getData();
        if (currentMemberUri == null) {
            setTitle("Add a member");
        } else {
            setTitle("Edit the member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null, this);
        }

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        sportEditText = findViewById(R.id.sportEditText);
        genderSpinner = findViewById(R.id.genderSpinner);

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selectedGender)) {
                    switch (selectedGender) {
                        case "Male":
                            gender = GENDER_MALE;
                            break;
                        case "Female":
                            gender = GENDER_FEMALE;
                            break;
                        default:
                            gender = GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                saveMember();
                return true;
            case R.id.delete_member:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMember() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Input the first name", Toast.LENGTH_LONG).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRST_NAME, firstName);
        contentValues.put(COLUMN_LAST_NAME, lastName);
        contentValues.put(COLUMN_SPORT, sport);
        contentValues.put(COLUMN_GENDER, gender);

        if (currentMemberUri == null) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(CONTENT_URI, contentValues);
            if (uri == null) {
                Toast.makeText(this,
                        "Insertion of data in the table failed",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "Data saved", Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues, null, null);
            if (rowsChanged == 0) {
                Toast.makeText(this,
                        "Saving data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "Member updated", Toast.LENGTH_LONG).show();
            }
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String projection[] = {_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_GENDER, COLUMN_SPORT};
        return new CursorLoader(this, currentMemberUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int firstNameColumnIndex = data.getColumnIndex(COLUMN_FIRST_NAME);
            int lastNameColumnIndex = data.getColumnIndex(COLUMN_LAST_NAME);
            int genderColumnIndex = data.getColumnIndex(COLUMN_GENDER);
            int sportColumnIndex = data.getColumnIndex(COLUMN_SPORT);

            String firstName = data.getString(firstNameColumnIndex);
            String lastName = data.getString(lastNameColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            String sport = data.getString(sportColumnIndex);

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            sportEditText.setText(sport);

            switch (gender) {
                case GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                case GENDER_UNKNOWN:
                    genderSpinner.setSelection(0);
                    break;

            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
