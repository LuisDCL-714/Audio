package com.example.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnRecord, btnPlay;
    private static final int REQUEST_RECORD_AUDIO = 1;
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private String fileName = null;
    private boolean recording = false, playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName = fileName + "/audiorecorder.3gp";
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setEnabled(false);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||  ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ){
                    checkPermissionRecord();
                }else{
                    if(!recording){
                        startRecording();
                        btnRecord.setText("Detener");
                        btnPlay.setEnabled(false);
                    }else{
                        stopRecording();
                        btnRecord.setText("Grabar");
                        btnPlay.setEnabled(true);
                    }
                    recording = !recording;
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing){
                    startPlaying();
                    btnPlay.setText("Detener");
                    btnRecord.setEnabled(false);
                }else{
                    stopPlaying();
                }
                playing = !playing;
            }
        });
    }

    private void stopPlaying() {
        btnPlay.setText("Play");
        btnRecord.setEnabled(true);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                playing = !playing;
            }
        });

        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("Error Audio Play", "Causa: "+e.getCause()+", Mensaje: "+e.getMessage()+", Localización del mensaje"+e.getLocalizedMessage()+", Error total: "+e);
        }
    }

    private void startRecording() {
        //Generear instancia
        mediaRecorder = new MediaRecorder();
        //Indicar que medio se va a grabar, en este caso, el microfono
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //Indicar el formato de la salida
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //Indicar el archivo donde se grabara el audio
        mediaRecorder.setOutputFile(fileName);
        //Encodear(?)/Comprimir audio
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e("Error Audio Record", "Causa: "+e.getCause()+", Mensaje: "+e.getMessage()+", Localización del mensaje"+e.getLocalizedMessage()+", Error total: "+e);
        }
    }

    private void checkPermissionRecord() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_RECORD_AUDIO){
            if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                startRecording();
            }else{
                checkPermissionRecord();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
