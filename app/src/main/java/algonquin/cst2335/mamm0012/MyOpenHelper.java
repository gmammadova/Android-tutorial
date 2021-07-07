package algonquin.cst2335.mamm0012;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class MyOpenHelper extends SQLiteOpenHelper {
    public static final String name = "TheDatabase";
    public static final int version = 1;
    public static final String TABLE_NAME = "Messages";
    public static final String col_message = "Message";
    public static final String col_send_reveive = "SendOrReceive";
    public static final String col_time_sent = "TimeSent";

    public MyOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
         + col_message + " TEXT,"
         + col_send_reveive + " INTEGER,"
         + col_time_sent + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
