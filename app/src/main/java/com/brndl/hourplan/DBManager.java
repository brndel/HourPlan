package com.brndl.hourplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private Context context;
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DBManager(Context c) {
        this.context = c;
    }

    public DBManager open() throws SQLException {
        this.dbHelper = new SQLiteHelper(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public void insert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLiteHelper.NAME, name);
        contentValue.put(SQLiteHelper.AGE, desc);
        this.database.insert(SQLiteHelper.TABLE_NAME_STUDENT, null, contentValue);
    }


    public Cursor fetch() {
        Cursor cursor = this.database.query(SQLiteHelper.TABLE_NAME_STUDENT, new String[]{SQLiteHelper._ID, SQLiteHelper.NAME, SQLiteHelper.AGE}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.NAME, name);
        contentValues.put(SQLiteHelper.AGE, desc);
        return this.database.update(SQLiteHelper.TABLE_NAME_STUDENT, contentValues, "_id = " + _id, null);
    }

    public void delete(long _id) {
        this.database.delete(SQLiteHelper.TABLE_NAME_STUDENT, "_id=" + _id, null);
    }
}
