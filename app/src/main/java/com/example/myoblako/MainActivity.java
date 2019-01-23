package com.example.myoblako;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.scalified.fab.ActionButton;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    ActionButton TodoBtn;
    ListView lv;
    ArrayList<Project> projectList;
    ArrayList<Todo> todoList;
    public static Context context;
    JsonArray jsonArray = new JsonArray();
    JsonObject jsonString = new JsonObject();
    private CustomAdapter mAdapter;
    int Count = 0;
    ProgressBar dataLoadingProgressBar;
    LinearLayout errorLayout;
    Button reloadButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.oblako);
//        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
//            }
//        });

        // список через customadapter

//        lv = (ListView) findViewById(R.id.list);
//        displayProjectList();
//        mAdapter = new CustomAdapter(this, projects);
//        for (int i = 0; i < projects.size(); i++) {
//            Project project = projects.get(i);
//            mAdapter.addSectionHeaderItem(project.title);
//            for (int j = 0; j < project.todos.size(); j++) {
//                mAdapter.addItem(project.todos.get(j));
//            }
//        }
//        lv.setAdapter(mAdapter);

        lv = (ListView)findViewById(R.id.list);
        dataLoadingProgressBar = (ProgressBar) findViewById(R.id.dataLoadingProgressBar);

        errorLayout = (LinearLayout)findViewById(R.id.errorLayout);
        reloadButton = (Button)findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loading();
            }
        });
        // Проблема тут
        TodoBtn = (ActionButton) findViewById(R.id.fab);
        TodoBtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();

                ArrayList<String> projectTitles = new ArrayList<String>();
                ArrayList<Integer> projectIds = new ArrayList<Integer>();
                ArrayList<String> todoText = new ArrayList<String>();
                ArrayList<Integer> todoProj = new ArrayList<Integer>();

                for(Project p: projectList){
                    projectTitles.add(p.title);
                    projectIds.add(p.id);
                }
                for(Todo t: todoList){
                    todoText.add(t.text);
                    todoProj.add(t.projectId);
                }

                b.putStringArrayList("projectTitles", projectTitles);
                b.putIntegerArrayList("projectIds", projectIds);
                b.putStringArrayList("todoText", todoText);
                b.putIntegerArrayList("todoProj", todoProj);

                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
                intent.putExtras(b);
                startActivityForResult(intent, 0);
            }
        });

        Loading();
    }

    private void fillData(){
        ArrayList<Todo> todos_test = new ArrayList<Todo>();
        if(Count < 2) return;

        dataLoadingProgressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        TodoBtn.show();
        lv.setVisibility(View.VISIBLE);

        mAdapter = new CustomAdapter(this);
        for(Project p: projectList){
            mAdapter.addSectionHeaderItem(p.title);
            for(Todo t: todoList){
                if(t.projectId == p.id){
                    mAdapter.addItem(t);
                }
            }
        }
        lv.setAdapter(mAdapter);
    }

    private void Loading(){
        TodoBtn.hide();
        lv.setVisibility(View.GONE);

        Ion.with(this).load(this.getResources().getString(R.string.server) + "projects/all.json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(e != null) {
                            showError();
                        }
                        if (result != null) {
                            projectList = new ArrayList<Project>();
                            for (final JsonElement projectJsonElement : result) {
                                projectList.add(new Gson().fromJson(projectJsonElement, Project.class));
                            }

                            Count++;
                            fillData();
                        }
                    }
                });

        Ion.with(this).load(this.getResources().getString(R.string.server) + "todos/all.json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(e != null) {
                            showError();
                        }
                        if (result != null) {
                            todoList = new ArrayList<Todo>();
                            for (final JsonElement projectJsonElement : result) {
                                todoList.add(new Gson().fromJson(projectJsonElement, Todo.class));
                            }

                            Count++;
                            fillData();
                        }
                    }
                });
    }
    private void showError(){
        dataLoadingProgressBar.setVisibility(View.GONE);
        TodoBtn.hide();
        lv.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == 1){
                Loading();
            }
        }
    }


//    private void displayProjectList(){
//        Project proj1 = new Project("First");
//        Project proj2 = new Project("Second");
//        proj1.todos.add(new Todo("1", true));
//        proj2.todos.add(new Todo("2", true));
//        proj2.todos.add(new Todo("3"));
//        proj2.todos.add(new Todo("4", true));
//        projects.add(proj1);
//        projects.add(proj2);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.fab:
//                Intent intent = new Intent(this, AddTodoActivity.class);
//                startActivity(intent); // TODO call second activity
//                break;
//            default:
//                break;
//        }
//    }
//


}

