package com.talaat_nwegaa.toolsstore.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;


public final class ToolContract {

    private ToolContract() {}

    public static final String CONTENT_AUTHORITY = "com.talaat_nwegaa.toolsstore";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TOOLS = "tools";


    public static final class ToolEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TOOLS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;

        public final static String TABLE_NAME = "tools";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TOOL_NAME ="name";
        public final static String COLUMN_TOOL_USES = "uses";
        public final static String COLUMN_TOOL_PRICE = "price";
        public final static String COLUMN_TOOL_QUANTITY = "quantity";
        public final static String COLUMN_TOOL_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_TOOL_SUPPLIER_contact = "supplier_contact";




    }

}

