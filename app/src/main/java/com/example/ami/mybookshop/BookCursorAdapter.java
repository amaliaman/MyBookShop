package com.example.ami.mybookshop;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ami.mybookshop.data.BookContract.BookEntry;

import java.text.NumberFormat;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = view.findViewById(R.id.name);
        TextView tvAuthor = view.findViewById(R.id.author);
        TextView tvPrice = view.findViewById(R.id.price);
        TextView tvQuantity = view.findViewById(R.id.quantity);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_AUTHOR));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));

        // Populate fields with extracted properties
        tvName.setText(name);
        tvAuthor.setText(author);
        tvPrice.setText(NumberFormat.getCurrencyInstance().format(price));
        tvQuantity.setText(String.valueOf(quantity));
    }
}
