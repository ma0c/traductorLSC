package com.traductor.traductorlsc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.traductor.traductorlsc.BaseDeDatos.BDManager;
import com.traductor.traductorlsc.BaseDeDatos.Utilidades;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final ArrayList<String> categorias = new ArrayList<>();
        BDManager conn = new BDManager(getApplicationContext(), Utilidades.NOMBRE_BASEDEDATOS, null, Utilidades.VERSION_BASEDEDATOS);
        SQLiteDatabase db = conn.getReadableDatabase();
        String query = "SELECT DISTINCT " + Utilidades.CAMPO_CATEGORIA + " FROM " + Utilidades.TABLA_VOCABULARIO + ";";

        if (db != null) {
            db.beginTransaction();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    categorias.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        conn.close();

        LinearLayout layout = findViewById(R.id.layoutCategorias);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < categorias.size(); i++) {
            Button categoria = new Button(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                categoria.setBackground(this.getDrawable(R.drawable.btn_default));
            }

            categoria.setTextSize(16);//pequeño 16, mediano 20, grande 24
            categoria.setLayoutParams(lp);
            categoria.setText(categorias.get(i));
            categoria.setId(i);
            final int finalI = i;
            categoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buscarPalabras(categorias.get(finalI));
                }
            });
            layout.addView(categoria);
        }
    }

    public void buscarPalabras(String categoria) {
        Intent intent = new Intent(this, Main3Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("categoria", categoria);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
