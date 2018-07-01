package com.example.ami.mybookshop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    public static final String CONTENT_AUTHORITY = "com.example.ami.mybookshop";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static abstract class BookEntry implements BaseColumns {
        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Table name
        public static final String TABLE_NAME = "books";

        // Column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "name";
        public static final String COLUMN_BOOK_AUTHOR = "author";
        public static final String COLUMN_BOOK_YEAR = "year";
        public static final String COLUMN_BOOK_CATEGORY = "category";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";


        // SQL statements
        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String REAL_TYPE = " REAL";
        private static final String NOT_NULL = " NOT NULL";
        private static final String COMMA_SEP = ", ";
        // Create table
        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        COLUMN_BOOK_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_AUTHOR + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_YEAR + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_CATEGORY + INTEGER_TYPE + NOT_NULL + " DEFAULT 0" + COMMA_SEP +
                        COLUMN_BOOK_PRICE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_QUANTITY + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_SUPPLIER_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_SUPPLIER_PHONE + TEXT_TYPE +
                        ")";
    }
}