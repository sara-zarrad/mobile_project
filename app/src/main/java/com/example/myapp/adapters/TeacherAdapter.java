package com.example.myapp.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.database.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teachers = new ArrayList<>();
    private OnEmailClickListener emailClickListener;

    // Interface for email click events
    public interface OnEmailClickListener {
        void onEmailClick(Teacher teacher);
    }

    // Method to set the email click listener
    public void setOnEmailClickListener(OnEmailClickListener listener) {
        this.emailClickListener = listener;
    }

    public void submitList(List<Teacher> newTeachers) {
        teachers = newTeachers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.textViewName.setText(teacher.getName());
        holder.textViewEmail.setText(teacher.getEmail());

        // First letter of the name for profile icon
        String firstLetter = teacher.getName().isEmpty() ? "?" :
                String.valueOf(teacher.getName().charAt(0)).toUpperCase();
        holder.textViewIcon.setText(firstLetter);

        // Set click listener for more options to trigger email
        holder.moreOptionsIcon.setOnClickListener(v -> {
            if (emailClickListener != null) {
                emailClickListener.onEmailClick(teacher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewEmail;
        TextView textViewIcon;
        ImageView moreOptionsIcon;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewTeacherName);
            textViewEmail = itemView.findViewById(R.id.textViewTeacherEmail);
            textViewIcon = itemView.findViewById(R.id.textViewTeacherIcon);
            moreOptionsIcon = itemView.findViewById(R.id.imageViewMoreOptions);
        }
    }
}