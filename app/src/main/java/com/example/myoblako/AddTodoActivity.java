package com.example.myoblako;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddTodoActivity extends AppCompatActivity {

    ListView projectList;
    Button cancelBtn, createBtn;

    EditText editText;

    Spinner projectSpinner;

    Context context;

    ArrayList<ThisProject> base = new ArrayList<ThisProject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        context = this;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.addtodoblank);
        getDataFromParent();
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        projectList = findViewById(R.id.projectList);

        //projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
        setSpinnerData();
        projectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        editText = (EditText) findViewById(R.id.editText);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if(editText.getText() == null || editText.getText().toString().equals("")){
            return false;
        }
        LinearLayout header = (LinearLayout)findViewById(R.id.TodoHeader);
        header.setVisibility(View.GONE);
        LinearLayout bottom = (LinearLayout)findViewById(R.id.TodoBottom);
        bottom.setVisibility(View.GONE);
        JsonObject params = new JsonObject();
        params.addProperty("text", editText.getText().toString());
        params.addProperty("project_id", projectList.getCheckedItemPosition() + 1);
        Ion.with(MainActivity.context).load("POST",MainActivity.context.getResources().getString(R.string.server) + "todos/")
                .setJsonObjectBody(params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        setResult(1);
                        finish();
                    }
                });
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void getDataFromParent(){
        Bundle b = this.getIntent().getExtras();
        int x = 0;
        if(b != null){
            ArrayList<String> projectTitles = b.getStringArrayList("projectTitles");
            ArrayList<Integer> projectIds = b.getIntegerArrayList("projectIds");
            ArrayList<String> todoText = b.getStringArrayList("todoText");
            ArrayList<Integer> todoProj = b.getIntegerArrayList("todoProj");

            for(int i = 0; i < projectTitles.size(); i++){
                ArrayList<String> todos_new = new ArrayList<String>();
                ArrayList<Boolean> _todoStates = new ArrayList<Boolean>();
                for(int j = 0; j < todoText.size(); j++){
                    if(todoProj.get(j) == projectIds.get(i)){
                        todos_new.add(todoText.get(j));
                    }
                }
                ThisProject p = new ThisProject(projectTitles.get(i), projectIds.get(i), todos_new);
                base.add(p);
            }
        }
        else {
            Log.d("getDataFromParent", "empty!");
        }
    }

    private void setSpinnerData(){
        ArrayList<String> _projects = new ArrayList<String>();
        for(ThisProject p: base){
            _projects.add(p.title);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, _projects);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

        projectList.setAdapter(adapter);
    }

//    private void setListViewData(int index){
//        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.projects_cell, base.get(index).todos);
//        projectList.setAdapter(listAdapter);
//    }


    private class ThisProject {
        String title;
        int id;
        ArrayList<String> todos;

        ThisProject(String title, int id, ArrayList<String> todos){
            this.title = title;
            this.id = id;
            this.todos = todos;
        }
    }
}
