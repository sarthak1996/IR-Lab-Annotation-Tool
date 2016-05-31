package com.example.sarthak.ir_annotation_tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.sarthak.ir_annotation_tool.Adapters.TasksAdapterMain;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.TasksObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<TasksObject> tasksObjects;
    private TasksAdapterMain adapter;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sharedPreferences;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_Main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("IR Annotation Tool");
        intialiseElements();
        populateTasks();
        pDialog.dismiss();
        if(sharedPreferences.getBoolean(Config.isLoggedIn,false)){
            Intent intent=new Intent(MainActivity.this,ListOfDocuments.class);
            startActivity(intent);
            finish();
        }
        adapter=new TasksAdapterMain(tasksObjects,MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void intialiseElements() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Tasks_Main);
        tasksObjects = new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sharedPreferences=getSharedPreferences(Config.loginPrefs,MODE_PRIVATE);
    }

    private void populateTasks() {
        /*To get data from the server*/
        tasksObjects.add(new TasksObject("Gujarati Connectives", MainActivity.this.getString(R.string.gujrati_connective_description)));
    }
}
