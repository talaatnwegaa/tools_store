package com.talaat_nwegaa.toolsstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.talaat_nwegaa.toolsstore.data.ToolContract.ToolEntry;

public class ToolCursorAdapter extends CursorAdapter {

        public ToolCursorAdapter(Context context, Cursor c) {
            super(context, c, 0 /* flags */);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            TextView nameShowTextView = (TextView) view.findViewById(R.id.tool_name_show);
            final TextView quantityShowTextView = (TextView) view.findViewById(R.id.quantity_show);
            TextView priceShowTextView = (TextView) view.findViewById(R.id.price_show);
            LinearLayout parentView = (LinearLayout) view.findViewById(R.id.container_layout);


            int idColumnIndex = cursor.getColumnIndex(ToolEntry._ID);
            int toolNameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_QUANTITY);

            final int rowId = cursor.getInt(idColumnIndex);
            String toolName = cursor.getString(toolNameColumnIndex);
            double toolPrice = cursor.getDouble(priceColumnIndex);
            final int toolQuantity = cursor.getInt(quantityColumnIndex);

            if (toolQuantity <= 1) {
                quantityShowTextView.setText(toolQuantity + " " + context.getResources().getString(R.string.tool));
            } else {
                quantityShowTextView.setText(toolQuantity + " " + context.getResources().getString(R.string.tools));
            }

            nameShowTextView.setText(toolName);
            priceShowTextView.setText(String.valueOf(toolPrice));
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    Uri currentInventoryUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI, rowId);
                    intent.setData(currentInventoryUri);
                    context.startActivity(intent);
                }
            });
            Button saleButton = (Button) view.findViewById(R.id.sale);
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String text = quantityShowTextView.getText().toString();
                    String[] splittedText = text.split(" ");
                    int quantity = Integer.parseInt(splittedText[0]);

                    if (quantity == 0) {
                        Toast.makeText(context, R.string.there_is_no_tool, Toast.LENGTH_SHORT).show();
                    } else if (quantity > 0) {
                        quantity = quantity - 1;

                        String quantityString = Integer.toString(quantity);

                        ContentValues values = new ContentValues();
                        values.put(ToolEntry.COLUMN_TOOL_QUANTITY, quantityString);

                        Uri currentInventoryUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI, rowId);

                        int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);

                        if (rowsAffected != 0) {
                            if (toolQuantity <= 1) {
                                quantityShowTextView.setText(quantity + " " + context.getResources().getString(R.string.tool));
                            } else {
                                quantityShowTextView.setText(quantity + " " + context.getResources().getString(R.string.tools));
                            }
                        } else {
                            Toast.makeText(context, R.string.failed_to_update, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });

        }
}



