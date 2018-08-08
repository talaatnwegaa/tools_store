package com.talaat_nwegaa.toolsstore;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.talaat_nwegaa.toolsstore.data.ToolContract.ToolEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_TOOL_LOADER = 0;

    private Uri mCurrentToolUri;

    private EditText mToolNameEditText;

    private EditText mUseInEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierContactEditText;

    Button order;
    String phone;

    private boolean mToolHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mToolHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentToolUri = intent.getData();

        if (mCurrentToolUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_tool));
            invalidateOptionsMenu();
        }
        else {
            setTitle(getString(R.string.editor_activity_title_edit_tool));
            getLoaderManager().initLoader(EXISTING_TOOL_LOADER, null, this);
        }
        mToolNameEditText = (EditText) findViewById(R.id.edit_tool_name);
        mUseInEditText = (EditText) findViewById(R.id.edit_use_in);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierContactEditText = (EditText) findViewById(R.id.edit_supplier_contact);

        mToolNameEditText.setOnTouchListener(mTouchListener);
        mUseInEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierContactEditText.setOnTouchListener(mTouchListener);

        order = (Button) findViewById(R.id.order_button);
        mQuantityEditText.setText(String.valueOf(quantity));

    }

    private void saveTool() {
        String toolNameString = mToolNameEditText.getText().toString().trim();
        String useInString = mUseInEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierContactString = mSupplierContactEditText.getText().toString().trim();

        if (mCurrentToolUri == null &&
                TextUtils.isEmpty(toolNameString) && TextUtils.isEmpty(useInString) &&
                TextUtils.isEmpty(priceString) &&  TextUtils.isEmpty(quantityString)&&
                TextUtils.isEmpty(supplierNameString)&&
                TextUtils.isEmpty(supplierContactString)){

            return;
        }

        boolean hasEmptyFields = TextUtils.isEmpty(toolNameString) ||
                TextUtils.isEmpty(useInString) ||
                TextUtils.isEmpty(priceString)||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(supplierContactString) ;


        if (hasEmptyFields){
            Toast.makeText(this, R.string.editor_activity_missing_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ToolEntry.COLUMN_TOOL_NAME, toolNameString);
        values.put(ToolEntry.COLUMN_TOOL_USES, useInString);
        values.put(ToolEntry.COLUMN_TOOL_PRICE, priceString);
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY, quantityString);
        values.put(ToolEntry.COLUMN_TOOL_SUPPLIER_NAME, supplierNameString);
        values.put(ToolEntry.COLUMN_TOOL_SUPPLIER_contact, supplierContactString);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY, quantity);

        if (mCurrentToolUri == null) {
            Uri newUri = getContentResolver().insert(ToolEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_tool_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_tool_successful),
                        Toast.LENGTH_SHORT).show();

            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentToolUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_tool_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_tool_successful),
                        Toast.LENGTH_SHORT).show();

            }
        }
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentToolUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTool();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mToolHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
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

    @Override
    public void onBackPressed() {
        if (!mToolHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_USES,
                ToolEntry.COLUMN_TOOL_PRICE,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_TOOL_SUPPLIER_NAME,
                ToolEntry.COLUMN_TOOL_SUPPLIER_contact,};

        return new CursorLoader(this, mCurrentToolUri, projection,
                null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int toolNameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_NAME);
            int useInColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_USES);
            int priceColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_SUPPLIER_contact);

            String toolName = cursor.getString(toolNameColumnIndex);
            String useIn = cursor.getString(useInColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            phone = cursor.getString(supplierContactColumnIndex);

            mToolNameEditText.setText(toolName);
            mUseInEditText.setText(useIn);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierContactEditText.setText(phone);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mToolNameEditText.setText("");
        mUseInEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierContactEditText.setText("");


    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTool();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteTool() {
        if (mCurrentToolUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentToolUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_tool_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_tool_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void orderProductFromSupplier(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    int quantity=0;

    public void decrement(View view) {
        quantity = Integer.valueOf(mQuantityEditText.getText().toString());
        if (quantity == 0) {
            Toast.makeText(this,getString(R.string.negative_values_are_not_accepted), Toast.LENGTH_SHORT).show();
            return;
        }
           mQuantityEditText.setText(String.valueOf(quantity));
           quantity = quantity - 1;
           mQuantityEditText.setText(String.valueOf(quantity));

    }

    public void increment(View view) {
          quantity = Integer.valueOf(mQuantityEditText.getText().toString());
          quantity = quantity + 1;
          mQuantityEditText.setText(String.valueOf(quantity));
        }
    }

