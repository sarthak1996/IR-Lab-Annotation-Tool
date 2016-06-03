package com.example.sarthak.ir_annotation_tool.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.sarthak.ir_annotation_tool.Config;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.Relation;
import com.example.sarthak.ir_annotation_tool.R;

import java.util.ArrayList;

/**
 * Created by sarthak on 4/6/16.
 */

public class RelationRecyclerAdapter extends RecyclerView.Adapter<RelationRecyclerAdapter.CustomRelationViewHolder> {
    private ArrayList<Relation> relationObjects;
    private Context context;
    private LayoutInflater layoutInflater;
    private int lastPositionClicked = -1;
    private RadioButton lastClickedRadioButton = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MenuItem deleteMenuItem;
    public RelationRecyclerAdapter(ArrayList<Relation> relationObjects, Context context) {
        this.relationObjects = relationObjects;
        this.context = context;
        sharedPreferences=context.getSharedPreferences(Config.relationFolder,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void setDeleteMenuItem(MenuItem deleteMenuItem){
        this.deleteMenuItem=deleteMenuItem;
    }

    @Override
    public CustomRelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.adapter_list_of_documents, parent, false);
        return new CustomRelationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomRelationViewHolder holder, final int position) {
        holder.setName(relationObjects.get(position).getRelationSense());
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPositionClicked != -1) {
                    lastClickedRadioButton.setChecked(false);
                }
                lastPositionClicked = position;
                lastClickedRadioButton = holder.radioButton;
                holder.radioButton.setChecked(true);
                editor.putInt(Config.checkedRelation,lastPositionClicked);
                editor.commit();
                deleteMenuItem.setVisible(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return relationObjects.size();
    }

    class CustomRelationViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CardView cardView;
        public RadioButton radioButton;

        public CustomRelationViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewDocuments);
            cardView = (CardView) itemView.findViewById(R.id.card_view_listDocs);
            radioButton = (RadioButton) itemView.findViewById(R.id.radioButtonListDocs);
        }

        public String getName() {
            return name.getText().toString();
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
