package com.traductor.traductorlsc;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.traductor.traductorlsc.BaseDeDatos.BDManager;
import com.traductor.traductorlsc.BaseDeDatos.Utilidades;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    EditText editText, editText2;
    ImageButton imageButton;
    Button button;
    BDManager conn;
    int resID;
    String resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.visualizador);
        editText = (EditText) findViewById(R.id.etPalabra);
        editText2 = (EditText) findViewById(R.id.etRuta);
        imageButton = (ImageButton) findViewById(R.id.boton);
        button = (Button) findViewById(R.id.btnEscuchar);

        //Para poner los videos en el reproductor desde la carpeta RAW
        resID = getResId("a", R.raw.class);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
        videoView.start();

        registrarDatos();
    }

    private void registrarDatos() {
        conn = new BDManager(getApplicationContext(), Utilidades.NOMBRE_BASEDEDATOS, null, Utilidades.VERSION_BASEDEDATOS);
        SQLiteDatabase db = conn.getWritableDatabase();
        db.execSQL(Utilidades.sqlCreate);
        db.close();
    }

    public void onClick(View view) {
        consultar();
    }

    private void consultar() {
        SQLiteDatabase db = conn.getReadableDatabase();
        String[] parametros = {editText.getText().toString()};
        String[] campos = {Utilidades.CAMPO_PALABRA};
        String palabra = editText.getText().toString();
        String query = "SELECT " + Utilidades.CAMPO_PALABRA + " FROM " + Utilidades.TABLA_VOCABULARIO + " WHERE " + Utilidades.CAMPO_PALABRA + " ='" + palabra + "';";

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            resultado = cursor.getString(0);
            editText2.setText(cursor.getString(0));
            cursor.close();

            resID = getResId(resultado, R.raw.class);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
            videoView.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "La palabra no existe dentro de la aplicaciòn", Toast.LENGTH_LONG).show();
            editText2.setText("");
        }
    }
    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}