package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.common.Constants;
import com.example.myapplication.models.Task;
import com.example.myapplication.services.TasksService;

public class TaskDetailActivity  extends AppCompatActivity {
    private Task task;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_task_detail);

        ((Button) findViewById(R.id.create_task)).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra(Constants.TASK_ID, 1);
        task = TasksService.getInstance().getTaskById(id);

        ((TextView)findViewById(R.id.task_tile)).setText(task.getTitle());
        ((TextView)findViewById(R.id.task_text)).setText(task.getText());
        ((TextView)findViewById(R.id.location)).setText("Место: " + task.getLocationTitle());
        ((TextView)findViewById(R.id.priority)).setText("Приоритет: " + task.getPriorityLabel());
        ((TextView)findViewById(R.id.status)).setText("Статус: " + task.getStatusLabel());
        ((TextView)findViewById(R.id.reporter)).setText("Сообщил: " + task.getReporterLogin());
        ((TextView)findViewById(R.id.master)).setText("Мастер: " + task.getMasterLogin());
        ((TextView)findViewById(R.id.assign)).setText("Выполняет: " + task.getAssignLogin());
    }
}
