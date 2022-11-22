package com.example.test_ui_to_do_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoVue extends RecyclerView.Adapter<TodoVue.ViewHolder> {
    private List<TodoModel> todolist;
    private Activity_in_List activity_in_list;
    private Database db;

    public TodoVue(Database db,Activity_in_List activity_in_list){
        this.db = db;
        this.activity_in_list = activity_in_list;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list, parent, false);
        return new ViewHolder(itemView);
    }
    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        TodoModel item = todolist.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.updateStatus(item.getId(), 1);
                }
                else{
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    public int getItemCount() {
        return todolist.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }
    public void setTasks(List<TodoModel> todolist){
        this.todolist = todolist;
        notifyDataSetChanged();
    }
    public Context getContext(){
        return activity_in_list;
    }

    public void deleteItem(int position){
        TodoModel item = todolist.get(position);
        db.deleteTask(item.getId());
        todolist.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        TodoModel item = todolist.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        AddTask fragment = new AddTask();
        fragment.setArguments(bundle);
        fragment.show(activity_in_list.getSupportFragmentManager(), AddTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todo_checkbox);
        }
    }

}
