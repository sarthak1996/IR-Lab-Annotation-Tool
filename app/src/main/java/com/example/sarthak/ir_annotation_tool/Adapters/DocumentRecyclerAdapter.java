package com.example.sarthak.ir_annotation_tool.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarthak.ir_annotation_tool.Config;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.DocumentObject;
import com.example.sarthak.ir_annotation_tool.R;
import com.example.sarthak.ir_annotation_tool.ViewContentOfDocument;

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
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.adapter_list_of_documents, parent, false);
        return new CustomDocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomDocumentViewHolder holder, final int position) {
        holder.setName(documentObjects.get(position).getName());
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewContentOfDocument.class);
                intent.putExtra(Config.docId, documentObjects.get(position).getDocId());
                intent.putExtra(Config.docName, documentObjects.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentObjects.size();
    }

    class CustomDocumentViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CardView cardView;

        public CustomDocumentViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewDocuments);
            cardView = (CardView) itemView.findViewById(R.id.card_view_listDocs);
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
