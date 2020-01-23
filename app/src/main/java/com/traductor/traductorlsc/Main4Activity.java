package com.traductor.traductorlsc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class Main4Activity extends AppCompatActivity {

    VideoView videoView;
    Button btnRepetir;
    TextView tvPalabra;
    int resID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);


        Intent as = getIntent();
        Bundle bb = as.getExtras();
        String palabraVideo;
        if (savedInstanceState != null){
            palabraVideo = savedInstanceState.getString("palabra");
        }else{
            palabraVideo = bb != null ? bb.getString("palabra"):"";
        }

        videoView = findViewById(R.id.visualizador);
        btnRepetir = findViewById(R.id.btnRepetir);
        tvPalabra = findViewById(R.id.tvPalabra);

        resID = MainActivity.getResId(""+palabraVideo, R.raw.class);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));
        videoView.start();

        tvPalabra.setText(""+palabraVideo.toUpperCase());
    }

    public void repetirVideo(View view) {
        videoView.start();
    }
}
