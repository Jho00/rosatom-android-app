package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.adapters.TasksAdapter;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.services.TasksService;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class TaskActivity extends BaseActivity {
    private CompositeDisposable compositeDisposable =
            new CompositeDisposable();

    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_tasks);

        ((Button) findViewById(R.id.create_task)).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Initialize contacts
        compositeDisposable.add(ApiService.getActualTasks().subscribe(tasks -> {
            TasksService.init(tasks);

            RecyclerView rvContacts = (RecyclerView) findViewById(R.id.myTasks);
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

            TasksAdapter adapter = new TasksAdapter(tasks, this);
            // Attach the adapter to the recyclerview to populate items
            rvContacts.setAdapter(adapter);
            // Set layout manager to position the items
            rvContacts.setLayoutManager(new LinearLayoutManager(this));

            swipeContainer.setOnRefreshListener(() -> {
                ApiService.getActualTasks().subscribe(newTasks -> {
                    TasksService.init(newTasks);
                    synchronized (adapter) {
                        adapter.setTasks(newTasks);
                        adapter.notifyItemInserted(newTasks.size() - 1);
                        adapter.notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                });
            });
        }));
    }
}
