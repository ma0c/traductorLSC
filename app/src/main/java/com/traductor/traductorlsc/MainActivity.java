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

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    EditText editText, editText2;
    ImageButton imageButton;
    Button button;
    Object rutaObject;
    BDManager conn;

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
        rutaObject = R.raw.agosto;
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + rutaObject));
        videoView.start();

        //registrarDatos();
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
        String[] campos = {Utilidades.CAMPO_RUTA};

        try {
            Cursor cursor = db.query(Utilidades.TABLA_VOCABULARIO, campos, Utilidades.CAMPO_ID + "=?", parametros, null, null, null);
            cursor.moveToFirst();
            editText2.setText(cursor.getString(0));
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "El documento no existe", Toast.LENGTH_LONG).show();
            editText2.setText("");
        }
    }
}