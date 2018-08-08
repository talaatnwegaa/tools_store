package com.talaat_nwegaa.toolsstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.talaat_nwegaa.toolsstore.data.ToolContract.ToolEntry;


public class ToolProvider extends ContentProvider {


        public static final String LOG_TAG = ToolProvider.class.getSimpleName();

        private static final int TOOLS = 100;

        private static final int TOOL_ID = 101;

        private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        static {

            sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS, TOOLS);

            sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS + "/#", TOOL_ID);
        }
        private ToolDbHelper mDbHelper;

        @Override
        public boolean onCreate() {
            mDbHelper = new ToolDbHelper(getContext());
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            SQLiteDatabase database = mDbHelper.getReadableDatabase();
            Cursor cursor;

            int match = sUriMatcher.match(uri);
            switch (match) {
                case TOOLS:
                    cursor = database.query(ToolEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                case TOOL_ID:
                    selection = ToolEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    cursor = database.query(ToolEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }

        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case TOOLS:
                    return insertTool(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        private Uri insertTool(Uri uri, ContentValues values) {
            String name = values.getAsString(ToolEntry.COLUMN_TOOL_NAME);
            if (name == null) {
                throw new IllegalArgumentException("tool requires a name");
            }
            String toolUses = values.getAsString(ToolEntry.COLUMN_TOOL_USES);
            if (toolUses == null) {
                throw new IllegalArgumentException("what is the uses of this tool");
            }
            Integer price = values.getAsInteger(ToolEntry.COLUMN_TOOL_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("tool requires a valid price");
            }

            Integer quantity = values.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("tool requires a valid quantity");
            }
            String supplierName = values.getAsString(ToolEntry.COLUMN_TOOL_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("tool requires a supplier name");
            }
            String supplierContact = values.getAsString(ToolEntry.COLUMN_TOOL_SUPPLIER_contact);
            if (supplierContact == null) {
                throw new IllegalArgumentException("tool requires a supplier contact");
            }

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            long id = database.insert(ToolEntry.TABLE_NAME, null, values);
            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }

        @Override
        public int update(Uri uri, ContentValues contentValues, String selection,
                          String[] selectionArgs) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case TOOLS:
                    return updateTool(uri, contentValues, selection, selectionArgs);
                case TOOL_ID:
                    selection = ToolEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    return updateTool(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }

        private int updateTool(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            if (values.containsKey(ToolEntry.COLUMN_TOOL_NAME)) {
                String name = values.getAsString(ToolContract.ToolEntry.COLUMN_TOOL_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("tool requires a name");
                }
            }
            if (values.containsKey(ToolEntry.COLUMN_TOOL_USES)) {
                String useIn = values.getAsString(ToolContract.ToolEntry.COLUMN_TOOL_USES);
                if (useIn == null) {
                    throw new IllegalArgumentException("what is the uses of this tool");
                }
            }
            if (values.containsKey(ToolEntry.COLUMN_TOOL_QUANTITY)) {
                Integer quantity = values.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
                if (quantity != null && quantity < 0) {
                    throw new IllegalArgumentException("tool requires valid quantity");
                }
            }

            if (values.containsKey(ToolEntry.COLUMN_TOOL_PRICE)) {
                Integer price = values.getAsInteger(ToolEntry.COLUMN_TOOL_PRICE);
                if (price != null && price < 0) {
                    throw new IllegalArgumentException("tool requires valid price");
                }
            }
            if (values.containsKey(ToolEntry.COLUMN_TOOL_SUPPLIER_NAME)) {
                String supplierName = values.getAsString(ToolContract.ToolEntry.COLUMN_TOOL_SUPPLIER_NAME);
                if (supplierName == null) {
                    throw new IllegalArgumentException("tool requires a supplier name");
                }
            }
            if (values.containsKey(ToolEntry.COLUMN_TOOL_SUPPLIER_contact)) {
                String supplierContact = values.getAsString(ToolContract.ToolEntry.COLUMN_TOOL_SUPPLIER_contact);
                if (supplierContact == null) {
                    throw new IllegalArgumentException("tool requires a supplier contact");
                }
            }

            if (values.size() == 0) {
                return 0;
            }

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            int rowsUpdated = database.update(ToolEntry.TABLE_NAME, values, selection, selectionArgs);
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            int rowsDeleted;
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case TOOLS:
                    rowsDeleted = database.delete(ToolEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case TOOL_ID:
                    selection = ToolContract.ToolEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    rowsDeleted = database.delete(ToolEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }

            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        }

        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case TOOLS:
                    return ToolEntry.CONTENT_LIST_TYPE;
                case TOOL_ID:
                    return ToolEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }
    }

