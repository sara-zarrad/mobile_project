package com.example.myapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.database.Task;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface OnTaskDeleteListener {
        void onTaskDelete(Task task);
    }

    private OnTaskDeleteListener onTaskDeleteListener;

    public void setOnTaskDeleteListener(OnTaskDeleteListener listener) {
        this.onTaskDeleteListener = listener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.getTime().equals(newItem.getTime());
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.textViewTaskName.setText(currentTask.getName());
        holder.textViewTaskDateTime.setText(currentTask.getDate() + " " + currentTask.getTime());
        holder.imageViewDeleteTask.setOnClickListener(v -> {
            if (onTaskDeleteListener != null) {
                onTaskDeleteListener.onTaskDelete(currentTask);
            }
        });
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskName;
        private final TextView textViewTaskDateTime;
        private final ImageView imageViewDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.textViewTaskName);
            textViewTaskDateTime = itemView.findViewById(R.id.textViewTaskdateTime);
            imageViewDeleteTask = itemView.findViewById(R.id.imageViewDeleteTask);
        }
    }
}
