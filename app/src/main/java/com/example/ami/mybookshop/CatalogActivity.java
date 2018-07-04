package com.example.ami.mybookshop;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ami.mybookshop.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find ListView to populate
        final ListView bookListView = findViewById(R.id.book_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup cursor adapter using cursor from last step
        mBooksAdapter = new BookCursorAdapter(this, null);

        // Attach cursor adapter to the ListView
        bookListView.setAdapter(mBooksAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Respond to clicks on item
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewIntent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                viewIntent.setData(currentBookUri);
                viewIntent.putExtra(EditorActivity.INTENT_VIEW_ITEM, EditorActivity.MODE_VIEW);
                startActivity(viewIntent);
            }
        });
    }

    /**
     * Setup menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // "Insert dummy data" button
            case R.id.action_insert_dummy_data:
                insertDummyBook();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            // About button
            case R.id.action_about:
                Toast.makeText(this, getResources().getString(R.string.credit_icons), Toast.LENGTH_LONG).show();
                return true;
            // "Delete all books" button
            case R.id.action_delete_all:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllBooks() {
        int mRowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        if (mRowsDeleted > 0) {
            Toast.makeText(this, R.string.editor_delete_book_successful_all, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.editor_delete_book_failed_all, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Insert dummy data to test the app
     */
    private void insertDummyBook() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "Three Men in a Boat");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Jerome K. Jerome");
        values.put(BookEntry.COLUMN_BOOK_YEAR, 1889);
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, 1);
        values.put(BookEntry.COLUMN_BOOK_PRICE, 17.99);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 44);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "BooksRUs");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "555-666-777");

        // Insert the new row, returning the primary key value of the new row
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        String message = "";
        if (newUri != null) {
            message = "Book saved";
        } else {
            message = "Error saving book";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY
        };
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, BookEntry.CONTENT_URI,
                projection, null, null, null);
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBooksAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBooksAdapter.swapCursor(null);
    }
}
