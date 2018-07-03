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
import android.view.Menu;
import android.view.MenuItem;
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
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;


    private Button mMinusButton;
    private Button mPlusButton;
    private int mEditorMode = Constants.DEFAULT_EDITOR_MODE;
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    // todo: private boolean mBookHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Determine the current mode - inset, view or edit
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (intent.hasExtra(Constants.INTENT_VIEW_ITEM)) {
            mEditorMode = intent.getIntExtra(Constants.INTENT_VIEW_ITEM, Constants.DEFAULT_EDITOR_MODE);
        } else if (intent.hasExtra(Constants.INTENT_EDIT_ITEM)) {
            mEditorMode = intent.getIntExtra(Constants.INTENT_EDIT_ITEM, Constants.DEFAULT_EDITOR_MODE);
        } else if (intent.hasExtra(Constants.INTENT_ADD_ITEM)) {
            mEditorMode = intent.getIntExtra(Constants.INTENT_EDIT_ITEM, Constants.DEFAULT_EDITOR_MODE);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.name);
        mAuthorEditText = findViewById(R.id.author);
        mYearEditText = findViewById(R.id.year);
        mCategorySpinner = findViewById(R.id.category);
        mPriceEditText = findViewById(R.id.price);
        mQuantityEditText = findViewById(R.id.quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        mMinusButton = findViewById(R.id.decrease);
        mPlusButton = findViewById(R.id.increase);

        switch (mEditorMode) {
            // view existing book
            case Constants.MODE_VIEW:
                this.setTitle(getString(R.string.view_book));
                // todo: extract to a separate function
                mNameEditText.setEnabled(false);
                mNameEditText.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));

                mAuthorEditText.setEnabled(false);
                mAuthorEditText.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));

                mCategorySpinner.setEnabled(false);

                mMinusButton.setVisibility(View.GONE);
                mPlusButton.setVisibility(View.GONE);

                break;
            // update existing book
            case Constants.MODE_EDIT:
                this.setTitle(getString(R.string.edit_book));
                ///
                break;
            // insert new book
            case Constants.MODE_INSERT:
                this.setTitle(getString(R.string.add_book));
                // Invalidate the options menu, so the "Delete" menu option can be hidden.
                // (It doesn't make sense to delete a book that hasn't been created yet.)
                //todo: invalidateOptionsMenu() for all relevant buttons in all modes;
                break;
            default:
                //
        }

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);


        // Show currency symbol in Price field
        TextView priceLabel = findViewById(R.id.price_label);
        priceLabel.append(" (" + Currency.getInstance(Locale.getDefault()).getSymbol() + ")");


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
            mSupplierNameEditText.setText(data.getString(data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)));
            mSupplierPhoneEditText.setText(data.getString(data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
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
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
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
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
            int quantity = 0;
            if (!TextUtils.isEmpty(mQuantityEditText.getText())) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditorActivity.class);
                editIntent.setData(mCurrentBookUri);
                editIntent.putExtra(Constants.INTENT_EDIT_ITEM, Constants.MODE_EDIT);
                finish();
                startActivity(editIntent);
                return true;
            case R.id.action_cancel:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
