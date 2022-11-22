package com.example.test_ui_to_do_list;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Activity_in_List extends AppCompatActivity {
    private RecyclerView tasksRecyclerView;
    private TodoVue taskview;
    private FloatingActionButton fab;

    private List<TodoModel> taskList;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_list_TEMPLATE);

        db = new Database(this);
        db.openDatabase();
        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.itemRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskview = new TodoVue(db,this);
        tasksRecyclerView.setAdapter(taskview);

        fab = findViewById(R.id.item_add);
        taskList =db.getAllTasks();
        Collections.reverse(taskList);
        taskview.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTask.newInstance().show(getSupportFragmentManager(),AddTask.TAG);
            }
        });

    }

    @Override
    public void handleDialogClose(DialogInterface dialogInterface){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskview.setTasks(taskList);
        taskview.notifyDataSetChanged();
    }
}
