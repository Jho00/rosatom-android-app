package com.example.myapplication.services;

import android.util.Log;

import com.example.myapplication.models.Task;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private static final String RECOGNITION_URL = "http://192.168.0.125:54321";
    private static final String MAIN_BACKEND_URL = "http://atom.foolsoft.ru";

    private static final MediaType MEDIA_TYPE_WAV = MediaType.parse("audio/wav");
    private static final String TAG = "ApiService";

    private static OkHttpClient client;
    private ApiService() {}

    static {
        client = new OkHttpClient();
    }

    public static Observable<String> recognizeText(File wav, String wavName) {
        return recognizeText(wav, wavName, true);
    }

    public static Observable<String> recognizeText(File wav, String wavName, boolean toRecognize) {
        return Observable.fromCallable(() -> {
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", wavName, RequestBody.create(MEDIA_TYPE_WAV, wav))
                    .build();

            String uuid = UUID.randomUUID().toString();

            Request request = new Request.Builder()
                    .url((toRecognize ? RECOGNITION_URL : MAIN_BACKEND_URL) + "/uploader?v=" + uuid)
                    .post(formBody)
                    .build();

            String result = "";
            try (Response response = client.newCall(request).execute()) {
                Log.e(TAG, String.valueOf(response.code()));
                result = response.body().string();
//                result = "Создать типовую задачу проверить отчеты срок 1 декабря";
                Log.e(TAG, result);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return "Создать типовую задачу проверить отчеты срок 1 декабря";
            }

            return result;
        })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(Throwable::printStackTrace);
    }

    public static Observable<List<Task>> getActualTasks() {
        return getActualTasks(1234);
    }

    public static Observable<List<Task>> getActualTasks(int userId) {
        return Observable.fromCallable(() -> {
            Log.e(TAG, "getActualTasks");

            Request request = new Request.Builder()
                    .url(MAIN_BACKEND_URL + "/api/tasks/?user_id=" + userId)
                    .build();

            List<Task> tasks;
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try (Response response = client.newCall(request).execute()) {
                String result = response.body().string();
                tasks = objectMapper.readValue(result, objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                return tasks;
            } catch (Throwable e) {
                Log.e(TAG, e.getMessage());
                tasks = Task.createMocks(30);
            }

            return tasks;
        })
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void sendTaskResult(String resultWavPath, int taskId, String resultMessage) {
        Observable.fromCallable(() -> {
//            File file = new File(resultWavPath);
//            recognizeText(file, file.getName(), false);
            RequestBody formBody = new FormBody.Builder()
                    .add("result", resultMessage)
                    .add("task_id", String.valueOf(taskId))
                    .build();

            Request request = new Request.Builder()
                    .url(MAIN_BACKEND_URL + "/result")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                Log.d(TAG, "Result code: " + response.code());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        })
                .onErrorComplete()
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    public static Observable<Boolean> createTask() {
        return Observable.fromCallable(() -> {
            RequestBody formBody = new FormBody.Builder()
                    .add("template", String.valueOf(1))
                    .add("id_template", String.valueOf(7))
                    .add("uid", String.valueOf(1))
                    .add("expire", "2020-12-1 12:00")
                    .build();

            Request request = new Request.Builder()
                    .url(MAIN_BACKEND_URL + "/api/tasks/add/")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String result = response.body().string();
                Log.e(TAG, "Result code: " + response.code());
                Log.e(TAG, "Result: " + result);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return true;
        })
            .onErrorComplete()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
