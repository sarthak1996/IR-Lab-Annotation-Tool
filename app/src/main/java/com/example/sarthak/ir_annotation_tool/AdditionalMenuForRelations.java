package com.example.sarthak.ir_annotation_tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Relation;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Sentence;
import com.google.gson.Gson;

/**
 * Created by sarthak on 22/5/16.
 */

public class AdditionalMenuForRelations implements android.view.ActionMode.Callback {

    private Context context;
    private Relation relation;
    private int start;
    private int end;
    private FloatingActionButton floatingActionButton;
    private TextView textView;
    private String docId;
    private int allowed=1;
    public AdditionalMenuForRelations() {
    }

    public AdditionalMenuForRelations(Context context,String docId,TextView textView,FloatingActionButton floatingActionButton) {
        this.context = context;
        this.docId=docId;
        this.textView=textView;
        this.allowed=1;
        this.floatingActionButton=floatingActionButton;
    }

    public AdditionalMenuForRelations(Context context,String docId,TextView textView,FloatingActionButton floatingActionButton,int allowed) {
        this.context = context;
        this.docId=docId;
        this.textView=textView;
        this.floatingActionButton=floatingActionButton;
        this.allowed=allowed;
    }



    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_add_attributes_of_relations, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
//        SharedPreferences prefs=context.getSharedPreferences(Config.sentenceFolder,Context.MODE_PRIVATE);
//        start=prefs.getInt(Config.savedSentenceStart,-1);
//        end=prefs.getInt(Config.savedSentenceEnd,-1);
        start=textView.getSelectionStart();
        end=textView.getSelectionEnd();
        if(start==-1 || end==-1){
            return false;
        }
        switch(item.getItemId()) {
            case R.id.addAttrsToRelations:
                String choices[]={"Connective","Arg1","Arg2"};
                new MaterialDialog.Builder(context)
                        .title("Choose an attribute")
                        .items(choices)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                Gson gson=new Gson();
                                SharedPreferences sharedPreferences=context.getSharedPreferences(Config.relationFolder,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                String temp=sharedPreferences.getString(Config.savedRelation,"");
                                if(temp!=null && !temp.isEmpty() && !temp.trim().equals("")) {
                                    relation = gson.fromJson(temp, Relation.class);
                                    if (relation.isFilled() || allowed==0) {
                                        floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_done_white_24dp));
                                    } else {
                                        floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add_white_24dp));
                                    }
                                }else {
                                    relation=new Relation();
                                }
                                switch (which){
                                    case 0:
                                        relation.getConnective().add(new Sentence(start,end,docId));
                                        break;
                                    case 1:relation.getArg1().add(new Sentence(start,end,docId));
                                        break;
                                    case 2: relation.getArg2().add(new Sentence(start,end,docId));
                                        break;
                                }
                                editor.putString(Config.savedRelation,gson.toJson(relation));
                                editor.commit();
                                return true;
                            }
                        })
                        .positiveText("Choose")
                        .negativeText("Cancel")
                        .show();


        }
        return false;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
    }


}