package com.example.sarthak.ir_annotation_tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarthak.ir_annotation_tool.Adapters.DocumentRecyclerAdapter;
import com.example.sarthak.ir_annotation_tool.NetworkClasses.VolleyAppController;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.DocumentObject;

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
public class ListOfDocuments extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private RadioButton radioButton;
    private ArrayList<DocumentObject> documentObjects;
    private ArrayList<DocumentObject> allDocs;
    //    private RadioGroup radioGroup;
    private SweetAlertDialog pDialog;
//    private FloatingActionButton floatingActionButton;
    private SharedPreferences sharedPreferences;
    private SearchView searchView;
    private ArrayList<DocumentObject> searchedDocuments;
    private DocumentRecyclerAdapter documentRecyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_documents);

        initialiseElements();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Choose a Document");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_Feedback:
                        CustomWebView customWebView = new CustomWebView(ListOfDocuments.this, "https://goo.gl/g4XcgZ");
                        customWebView.shouldOverrideUrlLoading(new WebView(ListOfDocuments.this), "https://goo.gl/g4XcgZ");
                        return true;
                    case R.id.navigation_item_SignOut:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Config.isLoggedIn, false);
                        editor.commit();
                        Intent intent = new Intent(ListOfDocuments.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                }
                return true;
            }
        });

        populateDocumentNames();

        recyclerView.setLayoutManager(linearLayoutManager);
        documentRecyclerAdapter = new DocumentRecyclerAdapter(documentObjects, ListOfDocuments.this);
        recyclerView.setAdapter(documentRecyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                documentObjects.clear();
                for (int i = 0; i < allDocs.size(); i++) {
                    if (allDocs.get(i).getName().contains(query)) {
                        documentObjects.add(allDocs.get(i));
                    }
                }
                documentRecyclerAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                documentObjects.clear();
                for (int i = 0; i < allDocs.size(); i++) {
                    if (allDocs.get(i).getName().toLowerCase().contains(newText.toLowerCase())) {
                       documentObjects.add(allDocs.get(i));
                    }
                }
                documentRecyclerAdapter.notifyDataSetChanged();
                return true;
            }
        });


//        radioGroup.setOrientation(RadioGroup.VERTICAL);


//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int checked=radioGroup.getCheckedRadioButtonId();
//                if(checked!=-1){
//                    /*Navigate to the content of the document*/
//                    Intent intent=new Intent(ListOfDocuments.this,ViewContentOfDocument.class);
//                    intent.putExtra(Config.docId,documentObjects.get(checked).getDocId());
//                    intent.putExtra(Config.docName,documentObjects.get(checked).getName());
//                    startActivity(intent);
//                }
//            }
//        });

    }

    private void initialiseElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_list_of_Documents);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        linearLayoutManager = new LinearLayoutManager(ListOfDocuments.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        documentObjects = new ArrayList<>();
        allDocs = new ArrayList<>();
        sharedPreferences = getSharedPreferences(Config.loginPrefs, MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Docs);
//        radioGroup=(RadioGroup)findViewById(R.id.radioGroup_ListOfDocuments);
        searchedDocuments = new ArrayList<>();
        searchView = (SearchView) findViewById(R.id.searchView);
//        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_documentSelected);
    }

    private void populateDocumentNames() {
        pDialog = new SweetAlertDialog(ListOfDocuments.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Fetching documents");
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

        pDialog.show();
        String tag_json_req = "json_obj_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", sharedPreferences.getString(Config.userName, ""));
        params.put("password", sharedPreferences.getString(Config.password, ""));

        String url = Config.baseIp + "/gujarati_connective/Android/getAllDocs.php";
        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        JSONObject jsonArray = response.getJSONObject("message");
                        Iterator<?> keys = jsonArray.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            allDocs.add(new DocumentObject(key, jsonArray.getString(key)));
                            documentObjects.add(new DocumentObject(key, jsonArray.getString(key)));
                        }
//                        for(int i=0;i<documentObjects.size();i++){
//                            radioButton=new RadioButton(ListOfDocuments.this);
//                            radioButton.setText(documentObjects.get(i).getName());
//                            radioButton.setTextSize(20);
//                            radioButton.setId(i);
//                            radioButton.setPaddingRelative(40,20,20,40);
//                            radioGroup.addView(radioButton);
//                        }
                        documentRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ListOfDocuments.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    pDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListOfDocuments.this, "Could not connect to server\nTry again Later", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });
        VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
        pDialog = null;
    }
}
