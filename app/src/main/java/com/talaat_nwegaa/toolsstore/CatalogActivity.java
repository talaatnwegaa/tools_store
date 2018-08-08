package com.talaat_nwegaa.toolsstore;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.talaat_nwegaa.toolsstore.data.ToolContract.ToolEntry;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TOOL_LOADER = 0;

    ToolCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ImageView adding = (ImageView) findViewById(R.id.adding);
        adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView toolListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        toolListView.setEmptyView(emptyView);

        mCursorAdapter = new ToolCursorAdapter(this, null);
        toolListView.setAdapter(mCursorAdapter);

        toolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentToolUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI, id);
                intent.setData(currentToolUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(TOOL_LOADER, null, this);
    }

    private void insertTool() {

        ContentValues values = new ContentValues();
        values.put(ToolEntry.COLUMN_TOOL_NAME, "name");
        values.put(ToolEntry.COLUMN_TOOL_USES, "uses");
        values.put(ToolEntry.COLUMN_TOOL_PRICE, 1);
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY, 1);
        values.put(ToolEntry.COLUMN_TOOL_SUPPLIER_NAME, "supplier");
        values.put(ToolEntry.COLUMN_TOOL_SUPPLIER_contact, "phone");

        Uri newUri = getContentResolver().insert(ToolEntry.CONTENT_URI, values);
    }


    private void deleteAllTools() {
        int rowsDeleted = getContentResolver().delete(ToolEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from tool database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertTool();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllTools();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_TOOL_PRICE };

        return new CursorLoader(this, ToolEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}

