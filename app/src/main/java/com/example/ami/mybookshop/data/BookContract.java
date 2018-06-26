package com.example.ami.mybookshop.data;

import android.provider.BaseColumns;

public final class BookContract {

    public static abstract class BookEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "books";

        // Column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "book_name";
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
                        COLUMN_BOOK_PRICE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_QUANTITY + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_SUPPLIER_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_BOOK_SUPPLIER_PHONE + TEXT_TYPE +
                        ")";
    }
}