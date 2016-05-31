package com.example.sarthak.ir_annotation_tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarthak.ir_annotation_tool.NetworkClasses.VolleyAppController;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.DocumentObject;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Relation;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Sentence;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sarthak on 20/5/16.
 */
public class ListOfRelations extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private RadioButton radioButton;
    private String docName = "";
    private ArrayList<Relation> relationObjects;
    private RadioGroup radioGroup;
    private String docId;
    private FloatingActionButton floatingActionButton;
    private int selectedRelation;
    private MenuItem deleteMenu;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_relations);
        Intent intent = getIntent();
        if (intent != null) {
            docName = intent.getStringExtra(Config.docName);
            docId = intent.getStringExtra(Config.docId);
        }
        initialiseElements();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Choose a Relation");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Toast.makeText(ListOfRelations.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        populateRelationNames();

        radioGroup.setOrientation(RadioGroup.VERTICAL);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRelation = checkedId;
                deleteMenu.setVisible(true);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = radioGroup.getCheckedRadioButtonId();
                if (checked != -1) {
                    /*Navigate to the content of the Relation*/
                    Intent intent = new Intent(ListOfRelations.this, EditRelation.class);
                    Gson gson=new Gson();
                    intent.putExtra(Config.relation,gson.toJson(relationObjects.get(checked)));
                    intent.putExtra(Config.docId,docId);
                    intent.putExtra(Config.docName,docName);
                    startActivity(intent);
                }
            }
        });

    }

    private void initialiseElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_list_of_Relations);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        linearLayoutManager = new LinearLayoutManager(ListOfRelations.this);
        sharedPreferences = getSharedPreferences(Config.loginPrefs, MODE_PRIVATE);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        relationObjects = new ArrayList<>();
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_ListOfRelations);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_RelationSelected);
    }

    private void populateRelationNames() {
        String tag_json_req = "json_obj_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", sharedPreferences.getString(Config.userName, ""));
        params.put("password", sharedPreferences.getString(Config.password, ""));
        params.put("doc_id", docName);

        String url = Config.baseIp + "/gujarati_connective/Android/getRelation.php";
        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {

                        JSONObject jsonArray = response.getJSONObject("message");
                        Iterator<?> keys = jsonArray.keys();
                        String temp = "";
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject jsonObject = jsonArray.getJSONObject(key);
                            Relation relation = new Relation(jsonObject.getString("relation_name"), jsonObject.getString("sense"));
                            Log.d("Res", jsonObject.toString());
                            relation.setRelation_id(Integer.parseInt(key));
                            String connective = jsonObject.getString("connective_span");
                            Log.d("ConnectiveKey", connective + "@" + key);
                            String splitConnective[] = connective.split(";");
                            Toast.makeText(ListOfRelations.this, "" + splitConnective.length, Toast.LENGTH_SHORT).show();
                            Log.d("length", "" + splitConnective.length);
                            for (int i = 0; i < splitConnective.length; i++) {
                                String boundsConnective[] = splitConnective[i].trim().split(":");
                                if (boundsConnective[0].isEmpty() && boundsConnective[0].trim().equals("")) {
                                    continue;
                                }
                                relation.getConnective().add(new Sentence(Integer.parseInt(boundsConnective[0]), Integer.parseInt(boundsConnective[1]), docId));
                            }

                            String arg1 = jsonObject.getString("arg1_span");
                            String splitArg1[] = arg1.split(";");
                            for (int i = 0; i < splitArg1.length; i++) {
                                String boundsArg1[] = splitArg1[i].split(":");
                                if (boundsArg1[0].isEmpty() && boundsArg1[0].trim().equals("")) {
                                    continue;
                                }
                                relation.getArg1().add(new Sentence(Integer.parseInt(boundsArg1[0]), Integer.parseInt(boundsArg1[1]), docId));
                            }

                            String arg2 = jsonObject.getString("arg2_span");
                            String splitArg2[] = arg2.split(";");
                            for (int i = 0; i < splitArg2.length; i++) {
                                String boundsArg2[] = splitArg2[i].split(":");
                                if (boundsArg2[0].isEmpty() && boundsArg2[0].trim().equals("")) {
                                    continue;
                                }
                                relation.getArg2().add(new Sentence(Integer.parseInt(boundsArg2[0]), Integer.parseInt(boundsArg2[1]), docId));
                            }
                            relationObjects.add(relation);
                        }
                        Log.d("Response", temp);

                        for (int i = 0; i < relationObjects.size(); i++) {
                            radioButton = new RadioButton(ListOfRelations.this);
                            radioButton.setText(relationObjects.get(i).getRelationSense());
                            radioButton.setTextSize(20);
                            radioButton.setId(i);
                            radioButton.setPaddingRelative(40, 20, 20, 40);
                            radioGroup.addView(radioButton);
                        }

                    } else {
                        Toast.makeText(ListOfRelations.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListOfRelations.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Something", error.getMessage());
            }
        });
        VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_list_of_relations, menu);
        deleteMenu = menu.findItem(R.id.deleteRelation);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.deleteRelation:
                deleteRelation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteRelation() {
        String tag_json_req = "json_obj_req";
        int relation_id=radioGroup.getCheckedRadioButtonId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", sharedPreferences.getString(Config.userName, ""));
        params.put("password", sharedPreferences.getString(Config.password, ""));
        params.put("user_name", sharedPreferences.getString(Config.password, ""));
        params.put("doc_name", docName);
        params.put("relation_id",relationObjects.get(relation_id).getRelation_id()+"");

        String url = Config.baseIp + "/gujarati_connective/Android/deleteRelation.php";
        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        Toast.makeText(ListOfRelations.this,response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListOfRelations.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Relation", error.getMessage());
                Toast.makeText(ListOfRelations.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
            }
        });
        VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);
        relationObjects.remove(selectedRelation);
        radioGroup.removeAllViewsInLayout();
        radioGroup.removeAllViews();
        for (int i = 0; i < relationObjects.size(); i++) {
            radioButton = new RadioButton(this);
            radioButton.setText(relationObjects.get(i).getRelationSense());
            radioButton.setTextSize(20);
            radioButton.setId(i);
            radioButton.setPaddingRelative(40, 20, 20, 40);
            radioGroup.addView(radioButton);
        }
        radioGroup.clearCheck();
        deleteMenu.setVisible(false);
    }

    private void editRelation() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
