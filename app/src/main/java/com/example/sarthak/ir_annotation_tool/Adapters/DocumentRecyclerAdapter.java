package com.example.sarthak.ir_annotation_tool.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.sarthak.ir_annotation_tool.ObjectClasses.DocumentObject;
import com.example.sarthak.ir_annotation_tool.R;

import java.util.ArrayList;

/**
 * Created by sarthak on 2/6/16.
 */
public class DocumentRecyclerAdapter extends RecyclerView.Adapter<DocumentRecyclerAdapter.CustomDocumentViewHolder> {
    private ArrayList<DocumentObject> documentObjects;
    private Context context;
    private LayoutInflater layoutInflater;

    public DocumentRecyclerAdapter(ArrayList<DocumentObject> documentObjects, Context context) {
        this.documentObjects = documentObjects;
        this.context = context;
    }

    @Override
    public CustomDocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.adapter_list_of_documents,parent,false);
        return new CustomDocumentViewHolder(view);
    }
    @Override
    public void onBindViewHolder(CustomDocumentViewHolder holder, int position) {
        holder.setName(documentObjects.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return documentObjects.size();
    }

    class CustomDocumentViewHolder extends RecyclerView.ViewHolder{
        private RadioButton radioButton;
        private TextView name;
        public CustomDocumentViewHolder(View itemView) {
            super(itemView);
            radioButton=(RadioButton)itemView.findViewById(R.id.radioButtonDocumentsList);
            name=(TextView)itemView.findViewById(R.id.textViewDocuments);
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }

        public String getName() {
            return name.getText().toString();
        }

        public void setName(String name) {
            this.name.setText(name);
        }
    }
}
