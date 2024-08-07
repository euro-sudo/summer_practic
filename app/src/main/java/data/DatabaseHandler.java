package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Utils.Util;
import model.Remind;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMIND_TABLE = "CREATE TABLE " + Util.TABLE_NAME + " ("
                + Util.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.KEY_TEXT + " TEXT, "
                + Util.KEY_BACK_COLOR + " INTEGER NOT NULL, "
                + Util.KEY_TEXT_SIZE + " FLOAT NOT NULL" + " );";

        String CREATE_REMIND_PHOTO_TABLE = "CREATE TABLE " + Util.TABLE_NAME_PHOTO + " ("
                + Util.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.KEY_PHOTO + " TEXT, "
                + Util.KEY_REMIND_ID + " INTEGER, "
                + "FOREIGN KEY (" + Util.KEY_REMIND_ID + ") REFERENCES " + Util.TABLE_NAME + "(" + Util.KEY_ID + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(CREATE_REMIND_TABLE);
        db.execSQL(CREATE_REMIND_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME_PHOTO);
        onCreate(db);
    }

    public void addRemind(Remind remind) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Util.KEY_TEXT, remind.getText());
            contentValues.put(Util.KEY_BACK_COLOR, remind.getBackColor());
            contentValues.put(Util.KEY_TEXT_SIZE, remind.getTextSize());

            long remindId = db.insert(Util.TABLE_NAME, null, contentValues);

            for (String photo : remind.getPhotos()) {
                ContentValues photoValues = new ContentValues();
                photoValues.put(Util.KEY_REMIND_ID, remindId);
                photoValues.put(Util.KEY_PHOTO, photo);

                db.insert(Util.TABLE_NAME_PHOTO, null, photoValues);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Remind getRemind(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME, new String[] {Util.KEY_ID, Util.KEY_TEXT, Util.KEY_TEXT_SIZE, Util.KEY_BACK_COLOR},
                Util.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                Remind remind = new Remind(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Util.KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Util.KEY_TEXT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Util.KEY_BACK_COLOR)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(Util.KEY_TEXT_SIZE)),
                        new ArrayList<>()
                );

                Cursor photoCursor = db.query(Util.TABLE_NAME_PHOTO, new String[]{Util.KEY_PHOTO},
                        Util.KEY_REMIND_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

                if (photoCursor != null) {
                    while (photoCursor.moveToNext()) {
                        remind.addPhoto(photoCursor.getString(photoCursor.getColumnIndexOrThrow(Util.KEY_PHOTO)));
                    }
                    photoCursor.close();
                }
                return remind;
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public List<Remind> getAllReminds() {
        List<Remind> remindList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + Util.TABLE_NAME;
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Util.KEY_ID));
                    Remind remind = getRemind(id);
                    if (remind != null) {
                        remindList.add(remind);
                    }
                } while (cursor.moveToNext());
            }
            return remindList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void deleteAllReminds() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_NAME, null, null);
        db.delete(Util.TABLE_NAME_PHOTO, null, null);
        db.close();
    }

    public void deleteRemind(int id) {
        Log.d("DatabaseHandler", "Deleting remind with ID: " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(Util.TABLE_NAME, Util.KEY_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHandler", "Rows deleted from remind table: " + rowsDeleted);
        db.close();
    }




}