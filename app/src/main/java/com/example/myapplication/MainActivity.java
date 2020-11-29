package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.myapplication.services.ApiService;
import com.example.myapplication.services.RecorderService;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecorderService recorderService = null;

    private RecordButton recordButton = null;
    Context context;

    EditText editText;

    Button createBtn;

    public class RecordButton extends androidx.appcompat.widget.AppCompatButton {
        boolean mStartRecording = true;
        View button;

        OnClickListener clicker = v -> {
            recorderService.onRecord(mStartRecording);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();

            if (!mStartRecording) {
                sendTask();
                v.setBackgroundResource(R.drawable.elipse_2);
                layoutParams.width = 234;
                layoutParams.height = 234;
            } else {
                v.setBackgroundResource(R.drawable.rectangle_6);
                layoutParams.width = 600;
                layoutParams.height = 300;
                createBtn.setVisibility(View.INVISIBLE);
            }
            v.setLayoutParams(layoutParams);
            mStartRecording = !mStartRecording;
        };

        public RecordButton(View button, Context context) {
            super(context);
            this.button = button;
            button.setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        context = this;

        this.recorderService = new RecorderService(this);

        View recordBtn = findViewById(R.id.record_button);
        recordButton = new RecordButton(recordBtn, this);

        createBtn = findViewById(R.id.listen_button);
        createBtn.setOnClickListener(view -> {
            createBtn.setEnabled(false);
            ApiService.createTask().subscribe(result -> {
                if (result) {
                    setRecognizeResult("");
                    createBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "Задача успешно создана", Toast.LENGTH_LONG).show();
                }
                createBtn.setEnabled(true);
            });
        });

        editText = findViewById(R.id.recognize_result);
    }

    @Override
    public void onStop() {
        super.onStop();
        recorderService.terminate();
    }

    private void sendTask() {
        createBtn.setVisibility(View.INVISIBLE);

        Observable.fromCallable(() -> {
            File file = new File(recorderService.generateFileName());
            Log.e(LOG_TAG, file.getName());

            return file;
        })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(file -> {
                Toast.makeText(this, "Идет распознавание, подождите", Toast.LENGTH_LONG).show();
                ApiService
                    .recognizeText(file, RecorderService.RECORD_NAME)
                    .subscribe(text -> {
                        setRecognizeResult(text);
                        Toast.makeText(this, "Нажмите кнопку для создания задачи", Toast.LENGTH_LONG).show();
                        createBtn.setVisibility(View.VISIBLE);
                    });
            });
    }

    private void setRecognizeResult(String result) {
        editText.setText(result);
    }
}