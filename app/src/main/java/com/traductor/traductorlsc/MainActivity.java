package com.traductor.traductorlsc;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.traductor.traductorlsc.BaseDeDatos.BDManager;
import com.traductor.traductorlsc.BaseDeDatos.Utilidades;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, edu.cmu.pocketsphinx.RecognitionListener {

    VideoView videoView;
    EditText editText;
    TextView textView;
    Button button;
    BDManager conn;
    int resID;
    String resultado;

    TextToSpeech tts;
    private SpeechRecognizer recognizer;
    String text;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        videoView = findViewById(R.id.visualizador);
        editText = findViewById(R.id.etPalabra);
        textView = findViewById(R.id.tvRuta);
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

        String palabra;
        String previoPalabra = editText.getText().toString().toLowerCase();
        if(previoPalabra.equals("papá")) palabra = "papaa";
        else if (previoPalabra.contains("ñ")) palabra = previoPalabra.replace("ñ", "nn");
        else if (previoPalabra.contains("á")) palabra = previoPalabra.replace("á", "a");
        else if (previoPalabra.contains("é")) palabra = previoPalabra.replace("é", "e");
        else if (previoPalabra.contains("í")) palabra = previoPalabra.replace("í", "i");
        else if (previoPalabra.contains("ó")) palabra = previoPalabra.replace("ó", "o");
        else if (previoPalabra.contains("ú")) palabra = previoPalabra.replace("ú", "u");
        else palabra = editText.getText().toString().toLowerCase();

        String query = "SELECT " + Utilidades.CAMPO_PALABRA + " FROM " + Utilidades.TABLA_VOCABULARIO + " WHERE " + Utilidades.CAMPO_PALABRA + " ='" + palabra + "';";

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            resultado = cursor.getString(0);
            textView.setText(cursor.getString(0));
            cursor.close();

            resID = getResId(resultado, R.raw.class);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
            videoView.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "La palabra no existe dentro de la aplicaciòn", Toast.LENGTH_LONG).show();
            textView.setText("");
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

        //Reiniciamos el reconocedor, de esta forma reconoce voz de forma continua y limpia el buffer
        resetRecognizer();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        Log.e("Tecto", "Aux " + text);
        text = "";
        /*if(text != null){
            for(int i=0;i<listado.size();i++){
                if(palabra.getNombre().contains(listado.get(i).split(";")[1])) {
                    if (text.contains(listado.get(i).split(";")[0]))
                        acierto = true;
                }
            }
        }else {
            acierto = false;
        }

        TextView textView = (TextView) findViewById(R.id.textView_escuchaN1);
        if(acierto){
            textView.setText("¡¡Dijiste la palabra!!");
            palabraReconocida();
        }else{
            textView.setText("¡Vuelve a intentarlo!");
        }
        text=null;
        acierto=false;*/
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
}