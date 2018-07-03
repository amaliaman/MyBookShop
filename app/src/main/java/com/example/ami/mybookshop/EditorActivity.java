package com.example.ami.mybookshop;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ami.mybookshop.data.BookContract.BookEntry;

import java.util.Currency;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mYearEditText;
    private Spinner mCategorySpinner;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;

    private int quantity;
    private int mCategory = 0;
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private boolean mBookHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Setup Save button
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri != null) {
            // update existing book
            this.setTitle(getString(R.string.edit_book));
            // Prepare the loader. Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        } else {
            // insert new book
            this.setTitle(getString(R.string.add_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        }

        // Show currency symbol in Price field
        TextView priceLabel = findViewById(R.id.price_label);
        priceLabel.append(" (" + Currency.getInstance(Locale.getDefault()).getSymbol() + ")");

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.name);
        mAuthorEditText = findViewById(R.id.author);
        mYearEditText = findViewById(R.id.year);
        mCategorySpinner = findViewById(R.id.category);
        mPriceEditText = findViewById(R.id.price);
        mQuantityEditText = findViewById(R.id.quantity);

        setupSpinner();

        // Setup quantity increase/decrease buttons
        // todo: show 0 if in insert mode
        Button mMinusButton = findViewById(R.id.decrease);
        Button mPlusButton = findViewById(R.id.increase);
        mMinusButton.setOnClickListener(quantityButtonListener);
        mPlusButton.setOnClickListener(quantityButtonListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                this,
                mCurrentBookUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            mNameEditText.setText(data.getString(data.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            mAuthorEditText.setText(data.getString(data.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR)));
            mYearEditText.setText(String.valueOf(data.getInt(data.getColumnIndex(BookEntry.COLUMN_BOOK_YEAR))));
            mCategorySpinner.setSelection(data.getInt(data.getColumnIndex(BookEntry.COLUMN_BOOK_CATEGORY)));
            mPriceEditText.setText(String.valueOf(data.getDouble(data.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE))));
            mQuantityEditText.setText(String.valueOf(data.getInt(data.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mYearEditText.setText("");
        mCategorySpinner.setSelection(0);
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mCategory = position;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = 0; // Unknown
            }
        });
    }

    /**
     * {link @View.OnClickListener} to monitor button clicks
     */
    private View.OnClickListener quantityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // todo: disable below zero
            if (TextUtils.isEmpty(mQuantityEditText.getText())) {
                quantity = 0;
            } else {
                quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            }
            switch (v.getId()) {
                case R.id.decrease:
                    quantity--;
                    break;
                case R.id.increase:
                    quantity++;
                    break;
            }
            mQuantityEditText.setText(String.valueOf(quantity));
        }
    };
}
