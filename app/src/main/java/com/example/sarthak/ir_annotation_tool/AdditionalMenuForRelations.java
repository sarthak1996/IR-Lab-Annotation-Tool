package com.example.sarthak.ir_annotation_tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private int allowed = 1;
    private String typeClicked;

    public AdditionalMenuForRelations() {
    }

    public AdditionalMenuForRelations(Context context, String docId, TextView textView, FloatingActionButton floatingActionButton, String typeClicked) {
        this.context = context;
        this.docId = docId;
        this.textView = textView;
        this.allowed = 1;
        this.typeClicked = typeClicked;
        this.floatingActionButton = floatingActionButton;
    }

    public AdditionalMenuForRelations(Context context, String docId, TextView textView, FloatingActionButton floatingActionButton, int allowed, String typeClicked) {
        this.context = context;
        this.docId = docId;
        this.textView = textView;
        this.typeClicked = typeClicked;
        this.floatingActionButton = floatingActionButton;
        this.allowed = allowed;
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
        start = textView.getSelectionStart();
        end = textView.getSelectionEnd();
        if (start == -1 || end == -1) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.addAttrsToRelations:
                String choices[] = {"Connective", "Arg1", "Arg2"};
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
                                        Gson gson = new Gson();
                                        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.relationFolder, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        String temp = sharedPreferences.getString(Config.savedRelation, "");
                                        if (temp != null && !temp.isEmpty() && !temp.trim().equals("")) {
                                            relation = gson.fromJson(temp, Relation.class);
                                            if (relation.isFilled() || allowed == 0) {
                                                floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_done_white_24dp));
                                            } else {
                                                floatingActionButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add_white_24dp));
                                            }
                                        } else {
                                            relation = new Relation();
                                        }
                                        switch (which) {
                                            case 0:
                                                relation.getConnective().add(new Sentence(start, end, docId));
                                                Toast.makeText(context, ""+typeClicked, Toast.LENGTH_SHORT).show();
                                                if (typeClicked.equals("connective")) {
                                                    String textContent = textView.getText().toString();
                                                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(textContent);
                                                    for (int i = 0; i < relation.getConnective().size(); i++) {
                                                        spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), relation.getConnective().get(i).getStart(), relation.getConnective().get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    }
                                                    textView.setText(spannable);
                                                }
                                                break;
                                            case 1:
                                                relation.getArg1().add(new Sentence(start, end, docId));
                                                if (typeClicked.equals("arg1")) {
                                                    String textContent = textView.getText().toString();
                                                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(textContent);
                                                    for (int i = 0; i < relation.getArg1().size(); i++) {
                                                        spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), relation.getArg1().get(i).getStart(), relation.getArg1().get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    }
                                                    textView.setText(spannable);
                                                }
                                                break;
                                            case 2:
                                                relation.getArg2().add(new Sentence(start, end, docId));
                                                if (typeClicked.equals("arg2")) {
                                                    String textContent = textView.getText().toString();
                                                    Spannable spannable = Spannable.Factory.getInstance().newSpannable(textContent);
                                                    for (int i = 0; i < relation.getArg2().size(); i++) {
                                                        spannable.setSpan(new BackgroundColorSpan(0x80FFFF00), relation.getArg2().get(i).getStart(), relation.getArg2().get(i).getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    }
                                                    textView.setText(spannable);
                                                }
                                                break;
                                        }
                                        editor.putString(Config.savedRelation, gson.toJson(relation));
                                        editor.commit();
                                        return true;
                                    }
                                }

                        )
                        .

                                positiveText("Choose")

                        .

                                negativeText("Cancel")

                        .

                                show();


        }
        return false;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
    }


}