package com.talaat_nwegaa.toolsstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.talaat_nwegaa.toolsstore.data.ToolContract.ToolEntry;


public class ToolDbHelper extends SQLiteOpenHelper{



        public static final String LOG_TAG = ToolDbHelper.class.getSimpleName();

        private static final String DATABASE_NAME = "store.db";


        private static final int DATABASE_VERSION = 1;


        public ToolDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TOOLS_TABLE =  "CREATE TABLE " + ToolEntry.TABLE_NAME + " ("
                    + ToolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ToolEntry.COLUMN_TOOL_NAME + " TEXT NOT NULL, "
                    + ToolEntry.COLUMN_TOOL_USES + " TEXT, "
                    + ToolEntry.COLUMN_TOOL_PRICE + " REAL, "
                    + ToolEntry.COLUMN_TOOL_QUANTITY + " INTEGER , "
                    + ToolEntry.COLUMN_TOOL_SUPPLIER_NAME + " TEXT , "
                    + ToolContract.ToolEntry.COLUMN_TOOL_SUPPLIER_contact + " TEXT );";
            db.execSQL(SQL_CREATE_TOOLS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
