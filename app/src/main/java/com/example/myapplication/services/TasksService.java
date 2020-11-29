package com.example.myapplication.services;

import com.example.myapplication.models.Task;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;


public class TasksService {
    private List<Task> tasks;
    private TasksService (List<Task> taskList) {
        this.tasks = taskList;
    };

    private static TasksService instance = null;

    public static TasksService getInstance() {
        return instance;
    }

    public static TasksService init(List<Task> tasks) {
        instance = new TasksService(tasks);

        return instance;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(int id) {
        for (Task task: tasks) {
            if (task.getId() == id) {
                return  task;
            }
        }

        throw new RuntimeException("Have not task");
    }
}
