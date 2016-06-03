package com.example.sarthak.ir_annotation_tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarthak.ir_annotation_tool.Adapters.RelationRecyclerAdapter;
import com.example.sarthak.ir_annotation_tool.NetworkClasses.VolleyAppController;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Relation;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Sentence;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sarthak on 20/5/16.
 */
public class ListOfRelations extends AppCompatActivity {
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private SweetAlertDialog progressDialog;
    private String docName = "";
    private ArrayList<Relation> relationObjects;
    private String docId;
    private FloatingActionButton floatingActionButton;
    private int selectedRelation;
    private MenuItem deleteMenu;
    private RecyclerView recyclerView;
    private RelationRecyclerAdapter adapter;
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


        populateRelationNames();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = getSharedPreferences(Config.relationFolder,MODE_PRIVATE).getInt(Config.checkedRelation,-1);
                if (checked != -1) {
                    /*Navigate to the content of the Relation*/
                    Intent intent = new Intent(ListOfRelations.this, EditRelation.class);
                    Gson gson = new Gson();
                    intent.putExtra(Config.relation, gson.toJson(relationObjects.get(checked)));
                    intent.putExtra(Config.docId, docId);
                    intent.putExtra(Config.docName, docName);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RelationRecyclerAdapter(relationObjects,ListOfRelations.this);
        recyclerView.setAdapter(adapter);

    }

    private void initialiseElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_list_of_Relations);
        linearLayoutManager = new LinearLayoutManager(ListOfRelations.this);
        sharedPreferences = getSharedPreferences(Config.loginPrefs, MODE_PRIVATE);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        relationObjects = new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_Relations);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_RelationSelected);
        progressDialog = new SweetAlertDialog(ListOfRelations.this, SweetAlertDialog.PROGRESS_TYPE);
    }

    class AsyncPopulateRelation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            progressDialog.hide();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitleText("Getting Relations");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {

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
            return null;
        }
    }

    private void populateRelationNames() {
        new AsyncPopulateRelation().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_list_of_relations, menu);
        deleteMenu = menu.findItem(R.id.deleteRelation);
        Log.d("DELMENU",""+deleteMenu);
        adapter.setDeleteMenuItem(deleteMenu);
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
        final int relation_id = getSharedPreferences(Config.relationFolder,MODE_PRIVATE).getInt(Config.checkedRelation,-1);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", sharedPreferences.getString(Config.userName, ""));
        params.put("password", sharedPreferences.getString(Config.password, ""));
        params.put("user_name", sharedPreferences.getString(Config.password, ""));
        params.put("doc_name", docName);
        params.put("relation_id", relationObjects.get(relation_id).getRelation_id() + "");
        progressDialog.setTitleText("Deleting the relation");
        progressDialog.show();

        String url = Config.baseIp + "/gujarati_connective/Android/deleteRelation.php";
        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        Toast.makeText(ListOfRelations.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        relationObjects.remove(relation_id);
                        SharedPreferences.Editor editor=getSharedPreferences(Config.relationFolder,MODE_PRIVATE).edit();
                        editor.putInt(Config.checkedRelation,-1);
                        editor.commit();
                        adapter.notifyDataSetChanged();
                        deleteMenu.setVisible(false);
                    } else {
                        Toast.makeText(ListOfRelations.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.d("Relation", error.getMessage());
                Toast.makeText(ListOfRelations.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
            }
        });
        VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
        progressDialog = null;
    }
}
