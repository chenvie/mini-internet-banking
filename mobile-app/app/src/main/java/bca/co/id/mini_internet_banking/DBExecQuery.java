package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DBExecQuery {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBExecQuery(Context context){
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void saveDataToLocal(){
        if (Nasabah.id != null && Nasabah.name != null && Nasabah.username != null && Nasabah.password != null && Nasabah.rekeningNum != null && Nasabah.saldo != 0 && Nasabah.code != null) {
            SQLiteStatement stmt = db.compileStatement(
                    "INSERT INTO nasabah(id_nasabah, name, username, password, rekeningNum, saldo, code) VALUES(?, ?, ?, ?, ?, ?, ?)");
            stmt.bindString(1, Nasabah.id);
            stmt.bindString(2, Nasabah.name);
            stmt.bindString(3, Nasabah.username);
            stmt.bindString(4, Nasabah.password);
            stmt.bindString(5, Nasabah.rekeningNum);
            stmt.bindDouble(6, Nasabah.saldo);
            stmt.bindString(7, Nasabah.code);
            stmt.execute();
            /*db.execSQL(
                    "INSERT INTO nasabah(id, name, username, password, rekeningNum, saldo, code) VALUES ('" +
                            Nasabah.id + "', '" +
                            Nasabah.name + "', '" +
                            Nasabah.username + "', '" +
                            Nasabah.password + "', '" +
                            Nasabah.rekeningNum + "', '" +
                            Nasabah.saldo + "', '" +
                            Nasabah.code + "')"
            );*/
        }
    }

    public void readLocalData(){
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, rekeningNum, saldo, code FROM nasabah WHERE id = '" + Nasabah.id + "'", null);
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Nasabah.id = cursor.getString(0);
            Nasabah.name = cursor.getString(1);
            Nasabah.username = cursor.getString(2);
            Nasabah.password = cursor.getString(3);
            Nasabah.rekeningNum = cursor.getString(4);
            Nasabah.saldo = Double.parseDouble(cursor.getString(5));
            Nasabah.code = cursor.getString(6);
        }
    }

    public void updatePassword(){
        SQLiteStatement stmt = db.compileStatement("UPDATE nasabah SET password=? where id=?");
        stmt.bindString(1, Nasabah.password);
        stmt.bindString(2, Nasabah.id);
        stmt.execute();
    }
}
