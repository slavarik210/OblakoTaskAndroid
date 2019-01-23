package com.example.myoblako;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class Todo {

    String text;
    Boolean isCompleted;
//    Integer position;
    Integer id;
    Integer projectId;

    Todo(int id, String text, boolean isCompleted, int projectId){
        this.id = id;
        this.text = text;
        this.isCompleted = isCompleted;
        this.projectId = projectId;
    }

//    public Todo(String text) {
//        super();
//        this.text = text;
//    }
//
//    public Todo() {
//        super();
//        this.text = "Unknown todo name";
//    }
}
