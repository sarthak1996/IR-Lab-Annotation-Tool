package com.example.sarthak.ir_annotation_tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sarthak.ir_annotation_tool.NetworkClasses.VolleyAppController;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Relation;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Sentence;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sarthak on 20/5/16.
 */
public class ViewContentOfDocument extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private int index;
    private int removeIndex;
    private String content = "";
    private TextView textViewContent;
    private String docId;
    private Relation relation;
    private SharedPreferences relationSharedPreferences;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Sentence> connectives;
    private ArrayList<Sentence> arg1;
    private ArrayList<Sentence> arg2;
    private String relation_name[] = {"Explicit", "Implicit", "AtLex", "EntRel"};
    private MenuItem deleteAttr;
    private SharedPreferences sharedPreferences;
    private String doc_name;
    private String typeClicked = "";
    private String font = "NotoSansGujarati-Regular.ttf";
    private SweetAlertDialog pdialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_content_of_document);
        Intent intent = getIntent();
        if (intent != null) {
            docId = intent.getStringExtra(Config.docId);
            doc_name = intent.getStringExtra(Config.docName);
            initialiseElements();
            SharedPreferences.Editor editor = relationSharedPreferences.edit();
            editor.remove(Config.savedRelation);
            Typeface gujaratiTypeface = Typeface.createFromAsset(getAssets(), font);
            textViewContent.setTypeface(gujaratiTypeface);
            fetchContent();
        } else {
            Toast.makeText(ViewContentOfDocument.this, "Unexpected Error has occured", Toast.LENGTH_SHORT).show();
            finish();
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Content");


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem menuItem) {
                                                                 menuItem.setChecked(true);
                                                                 drawerLayout.closeDrawers();
                                                                 switch (menuItem.getItemId()) {
                                                                     case R.id.navigation_item_connective_relationAttrs:
                                                                         typeClicked = "connective";
                                                                         getConnectives();
                                                                         highlightConnectives();
                                                                         return true;
                                                                     case R.id.navigation_item_arg1_relationAttrs:
                                                                         typeClicked = "arg1";
                                                                         getArg1s();
                                                                         highlightArg1();
                                                                         return true;
                                                                     case R.id.navigation_item_arg2_relationAttrs:
                                                                         typeClicked = "arg2";
                                                                         getArg2s();
                                                                         highlightArg2();
                                                                         return true;
                                                                     case R.id.navigation_item_List_Document:
                                                                         onBackPressed();
                                                                         return true;
                                                                     case R.id.navigation_item_Content_Document:
                                                                         updateRelation();
                                                                         deleteAttr.setVisible(false);
                                                                         fetchContent();
                                                                         textViewContent.setText(content);
                                                                         return true;
                                                                     case R.id.navigation_item_relationName_relationAttrs:
                                                                         updateRelation();
                                                                         MaterialDialog dialog1 = new MaterialDialog.Builder(ViewContentOfDocument.this)
                                                                                 .title("Enter Relation Name")
                                                                                 .customView(R.layout.dialog_add_relation_name, true)
                                                                                 .positiveText("Ok")
                                                                                 .autoDismiss(false)
                                                                                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                     @Override
                                                                                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                         RadioGroup radioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.relation_name_radioGroup);
                                                                                         int checked = radioGroup.getCheckedRadioButtonId();
                                                                                         if (checked != -1) {
                                                                                             switch (checked) {
                                                                                                 case R.id.explicit_relation:
                                                                                                     relation.setRelationName(relation_name[0]);
                                                                                                     break;
                                                                                                 case R.id.implicit_relation:
                                                                                                     relation.setRelationName(relation_name[1]);
                                                                                                     break;
                                                                                                 case R.id.atLex_relation:
                                                                                                     relation.setRelationName(relation_name[2]);
                                                                                                     break;
                                                                                                 case R.id.entRel_relation:
                                                                                                     relation.setRelationName(relation_name[3]);
                                                                                                     break;
                                                                                             }
                                                                                             dialog.dismiss();
                                                                                         } else {
                                                                                             Toast.makeText(ViewContentOfDocument.this, "Enter a relation name", Toast.LENGTH_SHORT).show();
                                                                                         }
                                                                                     }
                                                                                 })
                                                                                 .negativeText("Cancel")
                                                                                 .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                     @Override
                                                                                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                         dialog.dismiss();
                                                                                     }
                                                                                 })
                                                                                 .show();
                                                                         RadioGroup radioGroup = (RadioGroup) dialog1.getCustomView().findViewById(R.id.relation_name_radioGroup);
                                                                         if (relation.getRelationName() != null && !relation.getRelationName().isEmpty() && !relation.getRelationName().trim().equals("")) {
                                                                             if (relation.getRelationName().equals("Explicit")) {
                                                                                 radioGroup.check(R.id.explicit_relation);
                                                                             }
                                                                             if (relation.getRelationName().equals("Implicit")) {
                                                                                 radioGroup.check(R.id.implicit_relation);
                                                                             }
                                                                             if (relation.getRelationName().equals("AtLex")) {
                                                                                 radioGroup.check(R.id.atLex_relation);
                                                                             }
                                                                             if (relation.getRelationName().equals("EntRel")) {
                                                                                 radioGroup.check(R.id.entRel_relation);
                                                                             }
                                                                         }
                                                                         return true;
                                                                     case R.id.navigation_item_relationSense_relationAttrs:
                                                                         updateRelation();
                                                                         MaterialDialog dialog2 = new MaterialDialog.Builder(ViewContentOfDocument.this)
                                                                                 .title("Enter Relation Sense")
                                                                                 .customView(R.layout.dialog_add_relation_sense, true)
                                                                                 .positiveText("Ok")
                                                                                 .autoDismiss(false)
                                                                                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                     @Override
                                                                                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                         EditText editTextRelationSense = (EditText) dialog.getCustomView().findViewById(R.id.editText_RelationSense);
                                                                                         String relationSense = editTextRelationSense.getText().toString();
                                                                                         if (relationSense != null && !relationSense.isEmpty() && !relationSense.equals("")) {
                                                                                             relation.setRelationSense(relationSense);
                                                                                             dialog.dismiss();
                                                                                         } else {
                                                                                             Toast.makeText(ViewContentOfDocument.this, "Enter a relation sense", Toast.LENGTH_SHORT).show();
                                                                                         }
                                                                                     }
                                                                                 })
                                                                                 .negativeText("Cancel")
                                                                                 .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                     @Override
                                                                                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                         dialog.dismiss();
                                                                                     }
                                                                                 })
                                                                                 .show();
                                                                         EditText editTextRelationSense = (EditText) dialog2.getCustomView().findViewById(R.id.editText_RelationSense);
                                                                         if (relation.getRelationSense() != null && !relation.getRelationSense().isEmpty() && !relation.getRelationSense().trim().equals("")) {
                                                                             editTextRelationSense.setText(relation.getRelationSense());
                                                                         }
                                                                         return true;
                                                                     case R.id.navigation_item_SignOut:
                                                                         SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                         editor.putBoolean(Config.isLoggedIn, false);
                                                                         editor.commit();
                                                                         Intent intent = new Intent(ViewContentOfDocument.this, MainActivity.class);
                                                                         startActivity(intent);
                                                                         Toast.makeText(ViewContentOfDocument.this, "Signed Out", Toast.LENGTH_LONG).show();
                                                                         finish();
                                                                 }

                                                                 return true;
                                                             }
                                                         }

        );

        textViewContent.setText(content);
        textViewContent.setCustomSelectionActionModeCallback(new

                AdditionalMenuForRelations(ViewContentOfDocument.this, docId, textViewContent, floatingActionButton)

        );
        textViewContent.setOnTouchListener(new View.OnTouchListener()

                                           {
                                               @Override
                                               public boolean onTouch(View v, MotionEvent event) {
                                                   Layout layout = ((TextView) v).getLayout();
                                                   int x = (int) event.getX();
                                                   int y = (int) event.getY();
                                                   if (layout != null) {
                                                       int line = layout.getLineForVertical(y);
                                                       int offset = layout.getOffsetForHorizontal(line, x);
                                                       Log.v("index", "" + offset);
                                                       int flag = -1;

                                                       index = flag;
                                                       if (typeClicked.equals("connective")) {
                                                           getConnectives();
                                                           for (int i = 0; i < connectives.size(); i++) {
                                                               if (connectives.get(i).getStart() <= offset && offset <= connectives.get(i).getEnd()) {
                                                                   flag = i;
                                                                   break;
                                                               }
                                                           }
                                                           if (flag != -1) {
                                                               removeIndex = flag;
                                                               deleteAttr.setVisible(true);
                                                           } else {
                                                               deleteAttr.setVisible(false);
                                                           }
                                                       } else if (typeClicked.equals("arg1")) {
                                                           getArg1s();
                                                           for (int i = 0; i < arg1.size(); i++) {
                                                               if (arg1.get(i).getStart() <= offset && offset <= arg1.get(i).getEnd()) {
                                                                   flag = i;
                                                                   break;
                                                               }
                                                           }
                                                           if (flag != -1) {
                                                               removeIndex = flag;
                                                               deleteAttr.setVisible(true);
                                                           } else {
                                                               deleteAttr.setVisible(false);
                                                           }
                                                       } else if (typeClicked.equals("arg2")) {
                                                           getArg2s();
                                                           for (int i = 0; i < arg2.size(); i++) {
                                                               if (arg2.get(i).getStart() <= offset && offset <= arg2.get(i).getEnd()) {
                                                                   flag = i;
                                                                   break;
                                                               }
                                                           }
                                                           if (flag != -1) {
                                                               removeIndex = flag;
                                                               deleteAttr.setVisible(true);
                                                           } else {
                                                               deleteAttr.setVisible(false);
                                                           }
                                                       }
                                                   }
                                                   return false;
                                               }
                                           }

        );


        floatingActionButton.setOnClickListener(new View.OnClickListener()

                                                {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (relation.isFilled()) {
                                                            updateRelation();
                                                            saveRelationToDatabase();
                                                        } else {
                                                            boolean wrapInScrollView = true;
                                                            updateRelation();
                                                            final MaterialDialog materialDialog = new MaterialDialog.Builder(ViewContentOfDocument.this)
                                                                    .title("Enter Relation Parameters")
                                                                    .customView(R.layout.dialog_add_relation, wrapInScrollView)
                                                                    .positiveText("Ok")
                                                                    .autoDismiss(false)
                                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            RadioGroup radioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.relation_name_radioGroup);
                                                                            EditText editTextRelationSense = (EditText) dialog.getCustomView().findViewById(R.id.editText_RelationSense);
                                                                            int checked = radioGroup.getCheckedRadioButtonId();
                                                                            String relationSense = editTextRelationSense.getText().toString();
                                                                            if (checked != -1) {
                                                                                if (relationSense != null && !relationSense.isEmpty() && !relationSense.equals("")) {
                                                                                    switch (checked) {
                                                                                        case R.id.explicit_relation:
                                                                                            relation.setRelationName(relation_name[0]);
                                                                                            break;
                                                                                        case R.id.implicit_relation:
                                                                                            relation.setRelationName(relation_name[1]);
                                                                                            break;
                                                                                        case R.id.atLex_relation:
                                                                                            relation.setRelationName(relation_name[2]);
                                                                                            break;
                                                                                        case R.id.entRel_relation:
                                                                                            relation.setRelationName(relation_name[3]);
                                                                                            break;
                                                                                    }
                                                                                    relation.setRelationSense(relationSense);
                                                                                    Gson gson = new Gson();

                                                                                    SharedPreferences.Editor editor = relationSharedPreferences.edit();
                                                                                    String json = gson.toJson(relation);
                                                                                    editor.putString(Config.savedRelation, json);
                                                                                    editor.commit();
                                                                                    dialog.dismiss();
                                                                                    if (relation.isFilled()) {
                                                                                        floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_24dp));
                                                                                    } else {
                                                                                        floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_white_24dp));
                                                                                    }

                                                                                } else {
                                                                                    Toast.makeText(ViewContentOfDocument.this, "Enter Relation Sense", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            } else {
                                                                                Toast.makeText(ViewContentOfDocument.this, "Enter Relation Name", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    })
                                                                    .negativeText("Cancel")
                                                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    }

                                                }

        );

    }

    private int getSelectionEnd() {
        SharedPreferences pref = getSharedPreferences(Config.sentenceFolder, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Config.savedSentenceStart, textViewContent.getSelectionStart());
        editor.commit();
        return textViewContent.getSelectionEnd();
    }

    private int getSelectionStart() {
        SharedPreferences pref = getSharedPreferences(Config.sentenceFolder, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Config.savedSentenceEnd, textViewContent.getSelectionEnd());
        editor.commit();
        return textViewContent.getSelectionStart();
    }

    class AsyncFetchContent extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pdialog.setTitleText("Fetching content");
            pdialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            String url = Config.baseIp + "/gujarati_connective/Android/getDocumentContent.php";
            String tag_json_req = "json_request";
            final Map<String, String> params = new HashMap<String, String>();
            params.put("username", sharedPreferences.getString(Config.userName, ""));
            params.put("password", sharedPreferences.getString(Config.password, ""));
            //params.put("doc_path", "/media/Annotation_Interface/data/raw/a");
            params.put("doc_path", docId);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    content = response;
                    Log.d("Content", "here" + response);
                    Typeface gujaratiTypeface = Typeface.createFromAsset(getAssets(), font);
                    textViewContent.setTypeface(gujaratiTypeface);
                    textViewContent.setText(content);
                    SharedPreferences.Editor editor = relationSharedPreferences.edit();
                    editor.putString(Config.contentOfDocument, content);
                    editor.commit();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ViewContentOfDocument.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            VolleyAppController.getInstance().addToRequestQueue(stringRequest, tag_json_req);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdialog.hide();
            super.onPostExecute(aVoid);

        }
    }

    private void fetchContent() {
        new AsyncFetchContent().execute();

    }

    private void initialiseElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_content_Document);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_saveChanges);
        textViewContent = (TextView) findViewById(R.id.textView_content_Document);
        connectives = new ArrayList<>();
        arg1 = new ArrayList<>();
        arg2 = new ArrayList<>();
        relation = new Relation();
        relationSharedPreferences = getSharedPreferences(Config.relationFolder, MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(Config.loginPrefs, MODE_PRIVATE);
        pdialog = new SweetAlertDialog(ViewContentOfDocument.this, SweetAlertDialog.PROGRESS_TYPE);
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
            case R.id.allRelationsMenu:
                Intent intent = new Intent(ViewContentOfDocument.this, ListOfRelations.class);
                intent.putExtra(Config.docId, docId);
                intent.putExtra(Config.docName, doc_name);
                startActivity(intent);
                //finish();
                return true;
            case R.id.deleteAttrRelation:
                if (typeClicked.equals("connective")) {
                    String text = textViewContent.getText().toString();
                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                    for (int i = 0; i < connectives.size(); i++) {
                        if (i != removeIndex)
                            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), connectives.get(i).getStart(), connectives.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    textViewContent.setText(spannable);
                    Log.d("Size+=", "" + relation.getConnective().size());
                    relation.getConnective().remove(removeIndex);
                    Log.d("Size+=", "" + relation.getConnective().size());
                    SharedPreferences.Editor editor = relationSharedPreferences.edit();
                    Gson gson = new Gson();
                    editor.putString(Config.savedRelation, gson.toJson(relation));
                    editor.commit();
                    getConnectives();
                } else if (typeClicked.equals("arg1")) {

                    String text = textViewContent.getText().toString();
                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                    for (int i = 0; i < arg1.size(); i++) {
                        if (i != removeIndex)
                            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), arg1.get(i).getStart(), arg1.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    relation.getArg1().remove(removeIndex);
                    SharedPreferences.Editor editor = relationSharedPreferences.edit();
                    Gson gson = new Gson();
                    editor.putString(Config.savedRelation, gson.toJson(relation));
                    editor.commit();
                    getArg1s();
                } else if (typeClicked.equals("arg2")) {

                    String text = textViewContent.getText().toString();
                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                    for (int i = 0; i < arg2.size(); i++) {
                        if (i != removeIndex)
                            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), arg2.get(i).getStart(), arg2.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    relation.getArg2().remove(removeIndex);
                    SharedPreferences.Editor editor = relationSharedPreferences.edit();
                    Gson gson = new Gson();
                    editor.putString(Config.savedRelation, gson.toJson(relation));
                    editor.commit();
                    getArg2s();
                }
                deleteAttr.setVisible(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_content_docs, menu);
        deleteAttr = (MenuItem) menu.findItem(R.id.deleteAttrRelation);
        return true;
    }

    private void getConnectives() {
        updateRelation();
        connectives = relation.getConnective();

    }

    private void getArg1s() {
        updateRelation();
        arg1 = relation.getArg1();
    }

    private void getArg2s() {
        updateRelation();
        arg2 = relation.getArg2();
    }

    private void updateRelation() {
        Gson gson = new Gson();
        String temp = relationSharedPreferences.getString(Config.savedRelation, "");
        if (temp != null && !temp.isEmpty() && !temp.trim().equals("")) {
            relation = gson.fromJson(temp, Relation.class);
            Log.d("Connectives", relation.getConnective().toString());
        } else {
            relation = new Relation();
        }

    }

    private void highlightConnectives() {
        String text = textViewContent.getText().toString();
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        for (int i = 0; i < connectives.size(); i++) {
            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), connectives.get(i).getStart(), connectives.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewContent.setText(spannable);
    }

    private void highlightArg1() {
        String text = textViewContent.getText().toString();
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        ArrayList<Sentence> arg1 = relation.getArg1();
        for (int i = 0; i < arg1.size(); i++) {
            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), arg1.get(i).getStart(), arg1.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewContent.setText(spannable);
    }

    private void highlightArg2() {
        String text = textViewContent.getText().toString();
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        ArrayList<Sentence> arg2 = relation.getArg2();
        for (int i = 0; i < arg2.size(); i++) {
            spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), arg2.get(i).getStart(), arg2.get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewContent.setText(spannable);
    }

    class AsyncSave extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            pdialog.hide();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            pdialog.setTitleText("Saving relation");
            pdialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            updateRelation();
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", sharedPreferences.getString(Config.userName, ""));
            params.put("password", sharedPreferences.getString(Config.password, ""));
            params.put("user_name", sharedPreferences.getString(Config.userName, ""));
            params.put("doc_name", doc_name);
            params.put("relation_name", relation.getRelationName());
            params.put("connective", format("connective"));
            params.put("arg1", format("arg1"));
            params.put("arg2", format("arg2"));
            params.put("sense", relation.getRelationSense());

            String tag_json_req = "json_obj_req";
            final String url = Config.baseIp + "/gujarati_connective/Android/addRelation.php";
            CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("success") == 1) {
                            Toast.makeText(ViewContentOfDocument.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = relationSharedPreferences.edit();
                            editor.remove(Config.savedRelation);
                            editor.commit();
                        } else {
                            Toast.makeText(ViewContentOfDocument.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ViewContentOfDocument.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                }
            });
            VolleyAppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_req);

            return null;
        }
    }
    private void saveRelationToDatabase() {
        new AsyncSave().execute();
    }

    private String format(String type) {
        String returnString = "";
        if (type.equals("connective")) {
            connectives.clear();
            connectives = relation.getConnective();
            for (int i = 0; i < connectives.size(); i++) {
                returnString += connectives.get(i).getStart() + ":" + (int) (connectives.get(i).getEnd());
                if (i != connectives.size() - 1) {
                    returnString += ";";
                }
            }
        } else if (type.equals("arg1")) {
            arg1.clear();
            arg1 = relation.getArg1();
            for (int i = 0; i < arg1.size(); i++) {
                returnString += arg1.get(i).getStart() + ":" + (int) (arg1.get(i).getEnd());
                if (i != arg1.size() - 1) {
                    returnString += ";";
                }
            }
        } else if (type.equals("arg2")) {
            arg2.clear();
            arg2 = relation.getArg2();
            for (int i = 0; i < arg2.size(); i++) {
                returnString += arg2.get(i).getStart() + ":" + (int) (arg2.get(i).getEnd());
                if (i != arg2.size() - 1) {
                    returnString += ";";
                }
            }
        }
        return returnString;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = relationSharedPreferences.edit();
        editor.remove(Config.savedRelation);
        editor.commit();
    }
}
