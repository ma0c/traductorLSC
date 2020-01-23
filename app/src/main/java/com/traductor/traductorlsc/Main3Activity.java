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

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent as = getIntent();
        Bundle bb = as.getExtras();
        String categoriaBusqueda;
        if (savedInstanceState != null){
            categoriaBusqueda = savedInstanceState.getString("categoria");
        }else{
            categoriaBusqueda = bb != null ? bb.getString("categoria"):"";
        }

        final ArrayList<String> palabras = new ArrayList<>();
        BDManager conn = new BDManager(getApplicationContext(), Utilidades.NOMBRE_BASEDEDATOS, null, Utilidades.VERSION_BASEDEDATOS);
        SQLiteDatabase db = conn.getReadableDatabase();
        String query = "SELECT " + Utilidades.CAMPO_PALABRA + " FROM " + Utilidades.TABLA_VOCABULARIO + " WHERE " + Utilidades.CAMPO_CATEGORIA + " ='" + categoriaBusqueda + "';";

        if (db != null) {
            db.beginTransaction();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    palabras.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        conn.close();

        LinearLayout layout = findViewById(R.id.layoutPalabras);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < palabras.size(); i++) {
            Button palabra = new Button(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                palabra.setBackground(this.getDrawable(R.drawable.btn_default));
            }

            palabra.setTextSize(24);//pequeño 16, mediano 20, grande 24
            palabra.setLayoutParams(lp);
            palabra.setText(palabras.get(i));
            palabra.setId(i);
            final int finalI = i;
            palabra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buscarVideo(palabras.get(finalI));
                }
            });
            layout.addView(palabra);
        }
    }

    private void buscarVideo(String palabra) {
        Intent intent = new Intent(this, Main4Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("palabra", palabra);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
