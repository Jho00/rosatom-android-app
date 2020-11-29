package com.example.myapplication.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.TaskDetailActivity;
import com.example.myapplication.common.Constants;
import com.example.myapplication.models.Task;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.services.RecorderService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Function;

public class TasksAdapter extends
        RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> tasks;
    private Activity parent;

    private RecorderService recorderService = null;

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public TasksAdapter(List<Task> tasks, Activity parentActivity) {
        this.tasks = tasks;
        parent = parentActivity;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;

        public int id;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(parent, TaskDetailActivity.class);
                intent.putExtra(Constants.TASK_ID, id);
                parent.startActivity(intent);
            });
        }
    }

    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_task, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TasksAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Task task = tasks.get(position);

        holder.id = task.getId();

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(task.getTitle());

        Button button = holder.messageButton;
        button.setEnabled(true);
        button.setOnClickListener(view -> {
            runDoneTaskModal(position);
        });
    }

    private void runDoneTaskModal(int taskPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater inflater = LayoutInflater.from(parent);

        View modal = inflater.inflate(R.layout.done_task_modal, null);
        recorderService = new RecorderService(modal.getContext());

        Button recordBtn = modal.findViewById(R.id.record_button);
        new RecordButton(recordBtn, modal.getContext());

        builder.setView(modal)
                .setPositiveButton(R.string.done, (dialog, id) -> {
                    String comment = ((EditText) modal.findViewById(R.id.task_comment)).getText().toString();

                    recorderService.stopRecording();
                    recorderService.terminate();
                    String filePath = recorderService.generateFileName();

                    recorderService = null;

                    if (taskPosition < tasks.size()) {
                        ApiService.sendTaskResult(filePath, tasks.get(taskPosition).getId(), comment);
                    }

                    removeTask(taskPosition);
                    dialog.cancel();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    recorderService.terminate();
                    recorderService = null;

                    dialog.cancel();
                });

        builder.create().show();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void removeTask(int position) {
        tasks.remove(position);
//        this.notifyItemRemoved(position);
        this.notifyDataSetChanged();
    }

    public class RecordButton extends androidx.appcompat.widget.AppCompatButton {
        boolean mStartRecording = true;
        Button button;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                recorderService.onRecord(mStartRecording);
                if (mStartRecording) {
                    button.setText(R.string.stop_record);
                } else {
                    button.setText(R.string.start_record);
                    recorderService.startPlaying();
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Button button, Context context) {
            super(context);
            this.button = button;
            button.setOnClickListener(clicker);
        }
    }
}