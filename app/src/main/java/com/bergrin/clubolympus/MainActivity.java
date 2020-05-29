package com.bergrin.clubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_FIRST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_GENDER;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_LAST_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.COLUMN_SPORT;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.CONTENT_URI;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember.TABLE_NAME;
import static com.bergrin.clubolympus.data.ClubOlympusContract.EntryMember._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MEMBER_LOADER = 123;
    MemberCursorAdapter memberCursorAdapter;
    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataListView = findViewById(R.id.dataListView);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });

        memberCursorAdapter = new MemberCursorAdapter(this, null, false);
        dataListView.setAdapter(memberCursorAdapter);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                Uri currentMemberUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentMemberUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(MEMBER_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_SPORT};
        CursorLoader cursorLoader = new CursorLoader(this, CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        memberCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        memberCursorAdapter.swapCursor(null);
    }
}
