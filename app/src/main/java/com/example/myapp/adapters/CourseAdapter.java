package com.example.myapp.adapters;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.database.Course;
import com.example.myapp.database.DatabaseHelper;

public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseViewHolder> {
    private OnItemClickListener listener;
    private DatabaseHelper dbHelper;
    private static final String TAG = "CourseAdapter";

    public CourseAdapter(DatabaseHelper dbHelper) {
        super(new CourseDiffCallback());
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = getItem(position);
        if (course != null) {
            holder.bind(course);
        } else {
            Log.e(TAG, "Course at position " + position + " is null");
        }
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseNameTextView;
        private final TextView teacherNameTextView;
        private final TextView coefficientTextView;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.tv_course_name);
            teacherNameTextView = itemView.findViewById(R.id.tv_teacher_name);
            coefficientTextView = itemView.findViewById(R.id.tv_coefficient);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(getItem(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }

        public void bind(Course course) {
            if (course == null) {
                Log.e(TAG, "Attempting to bind null course");
                return;
            }

            try {
                courseNameTextView.setText(course.getCourse_name());
                String teacherName = "Unknown";

                if (dbHelper != null) {
                    try (android.database.Cursor cursor = dbHelper.findTeacherById(course.getTeacherId())) {
                        if (cursor != null && cursor.moveToFirst()) {
                            teacherName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting teacher name: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "DatabaseHelper is null");
                }

                teacherNameTextView.setText("Teacher: " + teacherName);
                coefficientTextView.setText("Coefficient: " + course.getCoefficient());
            } catch (Exception e) {
                Log.e(TAG, "Error binding course data: " + e.getMessage());
            }
        }
    }

    // Moved the DiffUtil callback class outside
    public static class CourseDiffCallback extends DiffUtil.ItemCallback<Course> {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourse_name().equals(newItem.getCourse_name()) &&
                    oldItem.getTeacherId() == newItem.getTeacherId() &&
                    oldItem.getCoefficient() == newItem.getCoefficient();
        }
    }

    // Set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEditClick(Course course);
        void onDeleteClick(Course course);
    }
}