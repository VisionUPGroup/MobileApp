package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.eyeCheck.Exam;

import java.util.List;

public class ExamGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<Exam> exams;

    public ExamGridAdapter(Context context, List<Exam> exams) {
        this.context = context;
        this.exams = exams;
    }

    @Override
    public int getCount() {
        return exams.size();
    }

    @Override
    public Object getItem(int position) {
        return exams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return exams.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_exam, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageViewExam);
        TextView textView = convertView.findViewById(R.id.textViewExamName);

        Exam exam = exams.get(position);
        imageView.setImageResource(exam.getImageResource());
        textView.setText(exam.getType());

        return convertView;
    }
}

