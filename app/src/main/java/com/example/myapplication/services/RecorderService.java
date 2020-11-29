package com.example.myapplication.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class RecorderService {
    private static final String LOG_TAG = RecorderService.class.getSimpleName();

    public static final String RECORD_NAME = "audiorecordtest.3gp";
    public static String fileName = null;

    private Context context;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    public RecorderService(Context context) {
        this.context = context;
        fileName = generateFileName();

        Log.e(LOG_TAG, "CREATED");
    }

    public String generateFileName() {
        return context.getExternalCacheDir().getAbsolutePath() + RECORD_NAME;
    }

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording() {
        if (recorder == null) {
            return;
        }
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        player.release();
        player = null;
    }

    public void terminate() {
        Log.e(LOG_TAG, "TERMINATE");
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void onRecord(boolean start) {
        if (start) {
            this.startRecording();
        } else {
            this.stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            this.startPlaying();
        } else {
            this.stopPlaying();
        }
    }

}
