package com.traductor.traductorlsc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.traductor.traductorlsc.BaseDeDatos.BDManager;
import com.traductor.traductorlsc.BaseDeDatos.Utilidades;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, edu.cmu.pocketsphinx.RecognitionListener {

    private VideoView videoView;
    private EditText editText;
    private TextView textView;
    private Button button;
    private BDManager conn;
    private int resID;
    private String resultado;

    private TextToSpeech tts;
    private SpeechRecognizer recognizer;
    private String text;
    private ArrayList<String> palabras;
    private boolean acierto;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        tts = new TextToSpeech(this, this);

        videoView = findViewById(R.id.visualizador);
        editText = findViewById(R.id.etPalabra);
        textView = findViewById(R.id.tvRuta);
        //textView.setVisibility(View.GONE);
        button = findViewById(R.id.btnEscuchar);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        recognizer.stop();
                        textView.setText("");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        try {
                            Assets assets = new Assets(getApplicationContext());
                            File assetDir = assets.syncAssets();
                            setupRecognizer(assetDir);
                            recognizer.startListening("frases");
                            textView.setText("Escuchando...");
                        } catch (IOException e) {
                            Toast.makeText(getBaseContext(), "Failed to init recognizer " + e, Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return false;
            }
        });

        //Para poner los videos en el reproductor desde la carpeta RAW
        resID = getResId("defecto", R.raw.class);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
        videoView.start();

        registrarDatos();
        obtenerPalabras();
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
        String palabra;
        String previoPalabra = editText.getText().toString().toLowerCase();
        if (previoPalabra.equals("papá")) previoPalabra = "papaa";
        if (previoPalabra.contains(" ")) previoPalabra = previoPalabra.replace(" ", "");
        if (previoPalabra.contains("á")) previoPalabra = previoPalabra.replace("á", "a");
        if (previoPalabra.contains("ñ")) previoPalabra = previoPalabra.replace("ñ", "nn");
        if (previoPalabra.contains("é")) previoPalabra = previoPalabra.replace("é", "e");
        if (previoPalabra.contains("í")) previoPalabra = previoPalabra.replace("í", "i");
        if (previoPalabra.contains("ó")) previoPalabra = previoPalabra.replace("ó", "o");
        if (previoPalabra.contains("ú")) previoPalabra = previoPalabra.replace("ú", "u");
        else palabra = editText.getText().toString().toLowerCase();
        palabra = previoPalabra;

        buscarVideo(palabra);
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

    private void setupRecognizer(File assetsDir) throws IOException {
        //Esta es la configuración básica, donde se le indican las bibliotecas con las palabras en español
        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "words1"))
                //.setAcousticModel(new File(assetsDir, "es-ptm"))
                .setDictionary(new File(assetsDir, "words1.dict"))
                //.setDictionary(new File(assetsDir, "es.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        //Aquí indicamos el archivo que contiene las palabras clave que queremos reconocer
        // para realizar diferentes acciones. En este caso yo creo un archivo llamado "keys.gram"
        //File keysGrammar = new File(assetsDir, "nivel1.gram");
        File keysGrammar = new File(assetsDir, "words1.gram");
        recognizer.addKeywordSearch("frases", keysGrammar);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "Este lenguaje no es soportado");
            } else {
                //speakOut();
            }
        } else {
            Log.e("TTS", "Inicialización del lenguaje fallida");
        }
    }

    private void resetRecognizer() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.startListening("frases");
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        //Obtenemos el String de la Hypothesiss

        text = hypothesis.getHypstr();
        Log.e("Texto parcial", "Aux " + text);

        //Reiniciamos el reconocedor, de esta forma reconoce voz de forma continua y limpia el buffer
        //resetRecognizer();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        Log.e("Texto Resultado", "Aux " + text);
        if (text != null) {
            text = text.replace(" ", "");
            for (int i = 0; i < palabras.size(); i++) {
                //System.out.println("Si " + palabras.get(i) + " contiene " + text + " > " + palabras.get(i).contains(text));
                if (palabras.get(i).contains(text)) {
                    acierto = true;
                }
            }
        } else {
            acierto = false;
            editText.setText("");
        }

        if (acierto) {
            editText.setText(text);
            buscarVideo(text);
        } else {
            textView.setText("¡Intentalo de nuevo!");
        }
        text = null;
        acierto = false;
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onTimeout() {

    }

    public void repetirVideo(View view) {
        videoView.start();
    }

    public void traerCategorias(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public void buscarVideo(String palabra) {
        SQLiteDatabase db = conn.getReadableDatabase();
        String query = "SELECT " + Utilidades.CAMPO_PALABRA + " FROM " + Utilidades.TABLA_VOCABULARIO + " WHERE " + Utilidades.CAMPO_PALABRA + " ='" + palabra + "';";

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            resultado = cursor.getString(0);
            //textView.setText(cursor.getString(0));
            cursor.close();

            resID = getResId(resultado, R.raw.class);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
            videoView.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "La palabra no existe dentro de la aplicación", Toast.LENGTH_LONG).show();
            textView.setText("");
        }
    }

    public void obtenerPalabras() {
        palabras = new ArrayList<>();
        BDManager conn = new BDManager(getApplicationContext(), Utilidades.NOMBRE_BASEDEDATOS, null, Utilidades.VERSION_BASEDEDATOS);
        SQLiteDatabase db = conn.getReadableDatabase();
        String query = "SELECT " + Utilidades.CAMPO_PALABRA + " FROM " + Utilidades.TABLA_VOCABULARIO + ";";

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
    }
}