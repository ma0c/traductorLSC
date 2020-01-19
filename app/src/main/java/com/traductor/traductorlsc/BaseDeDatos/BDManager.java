package com.traductor.traductorlsc.BaseDeDatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDManager extends SQLiteOpenHelper {

    public BDManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Utilidades.NOMBRE_BASEDEDATOS, null, Utilidades.VERSION_BASEDEDATOS);
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.sqlCreate);
        db.execSQL(Utilidades.sqlInsert);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Utilidades.sqlDrop);
        onCreate(db);
    }
}
