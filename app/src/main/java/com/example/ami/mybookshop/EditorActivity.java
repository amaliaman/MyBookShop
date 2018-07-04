package com.example.ami.mybookshop;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ami.mybookshop.data.BookContract.BookEntry;
import com.example.ami.mybookshop.data.Constants;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Editor constants
     */
    // Available EditorActivity modes
    public static final int MODE_INSERT = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_VIEW = 2;
    // Default editor mode
    public static final int DEFAULT_EDITOR_MODE = MODE_INSERT;
    // Unique name for edit item intent
    public static final String INTENT_VIEW_ITEM = "com.example.ami.mybookshop.INTENT_VIEW_ITEM";
    public static final String INTENT_EDIT_ITEM = "com.example.ami.mybookshop.INTENT_EDIT_ITEM";
    public static final String INTENT_ADD_ITEM = "com.example.ami.mybookshop.INTENT_ADD_ITEM";
    /**
     * User input controls in layout
     */
    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mYearEditText;
    private Spinner mCategorySpinner;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    /**
     * Buttons in layout
     */
    private ImageButton mMinusButton;
    private ImageButton mPlusButton;
    /**
     * Default editor mode
     */
    private int mEditorMode = DEFAULT_EDITOR_MODE;
    /**
     * Cursor loader ID
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    /**
     * URI of current book when in edit/view mode
     */
    private Uri mCurrentBookUri;
    /**
     * Track changes to controls
     */
    private boolean mBookHasChanged = false;

    private int mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Determine the current mode - inset, view or edit
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (intent.hasExtra(INTENT_VIEW_ITEM)) {
            mEditorMode = intent.getIntExtra(INTENT_VIEW_ITEM, DEFAULT_EDITOR_MODE);
        } else if (intent.hasExtra(INTENT_EDIT_ITEM)) {
            mEditorMode = intent.getIntExtra(INTENT_EDIT_ITEM, DEFAULT_EDITOR_MODE);
        } else if (intent.hasExtra(INTENT_ADD_ITEM)) {
            mEditorMode = intent.getIntExtra(INTENT_EDIT_ITEM, DEFAULT_EDITOR_MODE);
        }

        // Hide menu items according to the editor's mode
        invalidateOptionsMenu();

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

        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mYearEditText.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);

        ImageButton orderButton = findViewById(R.id.order_button);

        String title = "";
        switch (mEditorMode) {
            // view existing book
            case MODE_VIEW:
                title = getString(R.string.view_book);
                disableInput();
                // Prepare the loader. Either re-connect with an existing one,
                // or start a new one.
                getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
                orderButton.setVisibility(View.VISIBLE);
                break;

            // update existing book
            case MODE_EDIT:
                title = getString(R.string.edit_book);
                getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
                if (mQuantity == 0) {
                    mMinusButton.setEnabled(false);
                    mMinusButton.setAlpha(Constants.DISABLED_ALPHA);
                }
                break;

            // insert new book
            case MODE_INSERT:
                title = getString(R.string.add_book);
                mQuantityEditText.setText("0");
                mMinusButton.setEnabled(false);
                mMinusButton.setAlpha(Constants.DISABLED_ALPHA);
                break;
        }
        this.setTitle(title);

        // Show currency symbol in Price field
        TextView priceLabel = findViewById(R.id.price_label);
        priceLabel.append(" (" + Currency.getInstance(Locale.getDefault()).getSymbol() + ")");

        setupSpinner();

        // Setup quantity increase/decrease buttons
        ImageButton mMinusButton = findViewById(R.id.decrease);
        ImageButton mPlusButton = findViewById(R.id.increase);
        mMinusButton.setOnClickListener(quantityButtonListener);
        mPlusButton.setOnClickListener(quantityButtonListener);

        // Setup order button

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +
                        mSupplierPhoneEditText.getText().toString()));
                startActivity(orderIntent);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hide menu items according to the editor's mode
        switch (mEditorMode) {
            case MODE_VIEW:
                MenuItem saveItem = menu.findItem(R.id.action_save);
                MenuItem cancelItem = menu.findItem(R.id.action_cancel);
                saveItem.setVisible(false);
                cancelItem.setVisible(false);
                break;

            case MODE_EDIT:
                // same as next case
            case MODE_INSERT:
                MenuItem editItem = menu.findItem(R.id.action_edit);
                MenuItem deleteItem = menu.findItem(R.id.action_delete);
                editItem.setVisible(false);
                deleteItem.setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Disable all input controls in activity when in view mode
     */
    private void disableInput() {
        ArrayList<EditText> editTexts = new ArrayList<>();
        editTexts.add(mNameEditText);
        editTexts.add(mAuthorEditText);
        editTexts.add(mYearEditText);
        editTexts.add(mPriceEditText);
        editTexts.add(mQuantityEditText);
        editTexts.add(mSupplierNameEditText);
        editTexts.add(mSupplierPhoneEditText);

        for (EditText et : editTexts) {
            et.setEnabled(false);
            et.setTextColor(getResources().getColor(R.color.secondaryTextColor));
            et.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        mCategorySpinner.setEnabled(false);
        mCategorySpinner.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mMinusButton.setVisibility(View.GONE);
        mPlusButton.setVisibility(View.GONE);
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

            mQuantity = data.getInt(data.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));
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

        mQuantity = 0;
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // Handling unsaved changes
        if (!mBookHasChanged) {
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

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(categorySpinnerAdapter);
    }

    /**
     * {link @View.OnClickListener} to monitor quantity increment/decrement button clicks
     */
    private View.OnClickListener quantityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantity = 0;
            if (!TextUtils.isEmpty(mQuantityEditText.getText())) {
                quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            }
            switch (v.getId()) {
                case R.id.decrease:
                    if (quantity > 0) {
                        quantity--;
                        if (quantity == 0) {
                            mMinusButton.setEnabled(false);
                            mMinusButton.setAlpha(Constants.DISABLED_ALPHA);
                        }
                    } else {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.quantity_zero),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.increase:
                    quantity++;
                    if (quantity > 0) {
                        mMinusButton.setEnabled(true);
                        mMinusButton.setAlpha(1F);
                    }
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
                editIntent.putExtra(INTENT_EDIT_ITEM, MODE_EDIT);
                startActivity(editIntent);
                return true;
            case R.id.action_save:
                if (saveBook()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_cancel:
                // Handle unsaved changes
                if (!mBookHasChanged) {
                    finish();
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case android.R.id.home:
                // Handle unsaved changes
                if (!mBookHasChanged) {
                    super.onBackPressed();
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListenerHome =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditorActivity.super.onBackPressed();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListenerHome);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        if (mEditorMode == MODE_VIEW) {
            // delete existing book
            int mRowsDeleted = getContentResolver().delete(
                    mCurrentBookUri,   // the user dictionary content URI
                    null,                    // the column to select on
                    null                      // the value to compare to
            );
            if (mRowsDeleted > 0) {
                Toast.makeText(this, R.string.editor_delete_book_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_book_failed, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private boolean saveBook() {
        // Retrieve values from controls
        String nameValue = mNameEditText.getText().toString().trim();
        String authorValue = mAuthorEditText.getText().toString().trim();

        int yearValue = 0;
        String yearRaw = mYearEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(yearRaw)) {
            yearValue = Integer.parseInt(yearRaw);
        }

        int categoryValue = mCategorySpinner.getSelectedItemPosition();

        double priceValue = 0;
        String priceRaw = mPriceEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(priceRaw)) {
            priceValue = Double.parseDouble(priceRaw);
        }

        int quantityValue = 0;
        String quantityRaw = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityRaw)) {
            quantityValue = Integer.parseInt(quantityRaw);
        }

        String supplierNameValue = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneValue = mSupplierPhoneEditText.getText().toString().trim();

        // Check that all fields are filled
        boolean isBookValid = true;
        if (TextUtils.isEmpty(nameValue) && TextUtils.isEmpty(authorValue) &&
                yearValue == 0 && categoryValue == BookEntry.CATEGORY_GENERAL &&
                priceValue == 0 && quantityValue == 0 &&
                TextUtils.isEmpty(supplierNameValue) && TextUtils.isEmpty(supplierPhoneValue)) {
            isBookValid = false;
        }

        if (isBookValid) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, nameValue);
            values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorValue);
            values.put(BookEntry.COLUMN_BOOK_YEAR, yearValue);
            values.put(BookEntry.COLUMN_BOOK_CATEGORY, categoryValue);
            values.put(BookEntry.COLUMN_BOOK_PRICE, priceValue);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityValue);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameValue);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneValue);

            int messageId = 0;
            switch (mEditorMode) {
                case MODE_INSERT:
                    // Insert new book
                    Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                    if (newUri != null) {
                        messageId = R.string.success_add;
                    } else {
                        messageId = R.string.error_add;
                    }
                    break;
                case MODE_EDIT:
                    // Update existing book
                    int mRowsUpdated = getContentResolver().update(mCurrentBookUri, values, null, null);
                    if (mRowsUpdated > 0) {
                        messageId = R.string.success_edit;
                    } else {
                        messageId = R.string.error_edit;
                    }
                    break;
            }
            Toast.makeText(this, getResources().getString(messageId), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
