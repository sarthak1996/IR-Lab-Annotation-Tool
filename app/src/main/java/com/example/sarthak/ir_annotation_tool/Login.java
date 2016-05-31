package com.example.sarthak.ir_annotation_tool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sarthak.ir_annotation_tool.NetworkClasses.VolleyAppController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sarthak on 20/5/16.
 */
public class Login extends AppCompatActivity {
    private ActionProcessButton btnSignIn;
    private EditText userName;
    private EditText password;
    private boolean successLogin = false;
    private SharedPreferences sharedPreferences;
    private String enteredUserName;
    private String enteredPassword;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialiseElements();

        /*To check button sign in loading progress bar appearance*/
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setProgress(0);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAuthenticUser=authenticateUser();
                Log.d("here","here"+isAuthenticUser);
            }
        });
    }

    private void initialiseElements() {
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        userName = (EditText) findViewById(R.id.editText_UserName);
        password = (EditText) findViewById(R.id.editText_Password);
        sharedPreferences=getSharedPreferences(Config.loginPrefs,MODE_PRIVATE);
    }

    private boolean authenticateUser() {
        enteredUserName= userName.getText().toString();
        enteredPassword = password.getText().toString();
        String tag_json_req = "json_obj_req";

        final String url = Config.baseIp + "/gujarati_connective/Android/login.php";

        final String TAG = "Volley Request";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username",enteredUserName);
        params.put("password",enteredPassword);

        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if(response.getInt("success")==1){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean(Config.isLoggedIn,true);
                        editor.putString(Config.userName,enteredUserName);
                        editor.putString(Config.password,enteredPassword);
                        editor.commit();
                        Intent intent = new Intent(Login.this, ListOfDocuments.class);
                        startActivity(intent);
                        finish();
                        Log.d("Login1",response.getString("message"));
                        successLogin=true;
                    }else{
                        //Log.d("Login0",response.getString("message"));
                        Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        successLogin=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                successLogin=false;
                Log.d("VolleyError",error.getMessage());
            }
        });

        // Adding request to request queue
        VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);
        return successLogin;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
