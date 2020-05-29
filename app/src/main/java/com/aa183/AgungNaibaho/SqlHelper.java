package com.aa183.AgungNaibaho;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music.db";
    private static final int DATABASE_VERSION = 1;

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crtMusik = "CREATE TABLE IF NOT EXISTS tbMusik " +
                "(ID_MSK INTEGER PRIMARY KEY AUTOINCREMENT, JNS_MSK VARCHAR(20) NOT NULL, " +
                "GENRE_MSK VARCHAR(20) NOT NULL, JDL_MSK VARCHAR(500) NOT NULL, ALBUM VARCHAR(500) NOT NULL, "+
                "PENYANYI VARCHAR(500) NOT NULL, THN_MSK VARCHAR(4) NOT NULL, UPLDIMG BYTE NOT NULL);";
        db.execSQL(crtMusik);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public String cekEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor xEmpty = db.rawQuery("SELECT ID_MSK FROM tbMusik ", null);
        StringBuffer buffer = new StringBuffer();
        while (xEmpty.moveToNext()) {
            String xEmp = xEmpty.getString(xEmpty.getColumnIndex("ID_MSK"));
            buffer.append(xEmp + "#");
        }
        db.close();
        return buffer.toString();
    }

    public String cekData(String xJdl, String xPe) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor xData = db.rawQuery("SELECT ID_MSK FROM tbMusik " +
                "WHERE JDL_MSK = '" + xJdl + "' AND PENYANYI = '" + xPe + "' ", null);
        StringBuffer buffer = new StringBuffer();
        while (xData.moveToNext()) {
            String xVAR = xData.getString(xData.getColumnIndex("ID_MSK"));
            buffer.append(xVAR + "#");
        }
        db.close();
        return buffer.toString();
    }

    public String UpdateData(String iuQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(iuQuery);
            VariableGlobal.varSqlHelper = "YA";
        } catch (Exception e) {
            VariableGlobal.varSqlHelper = "NO";
        }
        db.close();
        return VariableGlobal.varSqlHelper;
    }

    public String DeleteData(String dQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(dQuery);
            VariableGlobal.varSqlHelper = "YA";
        } catch (Exception e) {
            VariableGlobal.varSqlHelper = "NO";
        }
        db.close();
        return VariableGlobal.varSqlHelper;
    }

    public long InsertImg(String xJns, String xGenre, String xJdl, String xAlbm, String xPe, String xThn, byte[] xUpldImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("JNS_MSK", xJns);
        contentValues.put("GENRE_MSK", xGenre);
        contentValues.put("JDL_MSK", xJdl);
        contentValues.put("ALBUM", xAlbm);
        contentValues.put("PENYANYI", xPe);
        contentValues.put("THN_MSK", xThn);
        contentValues.put("UPLDIMG", xUpldImg);
        long id = db.insert("tbMusik", null, contentValues);
        if (id >= 1)
            VariableGlobal.varSqlHelper = "YA";
        else
            VariableGlobal.varSqlHelper = "NO";
        return id;
    }

}
