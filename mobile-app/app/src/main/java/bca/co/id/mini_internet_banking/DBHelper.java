package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//NOT USED
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context mContext){
        super(mContext, "nasabah_ibank", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE nasabah(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_nasabah INTEGER," +
                        "name TEXT, " +
                        "username TEXT, " +
                        "password TEXT, " +
                        "rekeningNum TEXT, " +
                        "saldo DOUBLE, " +
                        "code TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
