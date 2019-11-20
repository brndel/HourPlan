package com.brndl.hourplan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String AGE = "age";
    private static final String CREATE_TABLE_STUDENT = " create table STUDENTS ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL , age TEXT );";
    private static final String DB_NAME = "STUDENTS.DB";
    private static final int DB_VERSION = 1;
    public static final String NAME = "name";
    public static final String TABLE_NAME_STUDENT = "STUDENTS";
    public static final String _ID = "_id";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS STUDENTS");
        onCreate(db);
    }
}
