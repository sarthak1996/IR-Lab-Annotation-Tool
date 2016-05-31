package com.example.sarthak.ir_annotation_tool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarthak.ir_annotation_tool.Login;
import com.example.sarthak.ir_annotation_tool.ObjectClasses.TasksObject;
import com.example.sarthak.ir_annotation_tool.R;

import java.util.ArrayList;

/**
 * Created by sarthak on 19/5/16.
 */
public class TasksAdapterMain extends RecyclerView.Adapter<TasksAdapterMain.CustomTaskViewHolder> {

    private ArrayList<TasksObject> tasksObjects;
    private Context context;
    private LayoutInflater layoutInflater;
    public TasksAdapterMain(ArrayList<TasksObject> tasksObjects, Context context) {
        this.tasksObjects = tasksObjects;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public CustomTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.adapter_main,parent,false);
        return new CustomTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomTaskViewHolder holder, int position) {
        TasksObject tasksObject=tasksObjects.get(position);
        holder.setTitle(tasksObject.getTitle());
        holder.setDescription(tasksObject.getDescription());
        holder.setDescriptionShown(tasksObject.isDescriptionShown());
    }

    @Override
    public int getItemCount() {
        return tasksObjects.size();
    }

    public class CustomTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView description;
        private TextView learn_more;
        private TextView login;
        private boolean descriptionShown;
        public CustomTaskViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.textView_title_main);
            description=(TextView)itemView.findViewById(R.id.textView_description_main);
            learn_more=(TextView)itemView.findViewById(R.id.textView_LearnMore);
            login=(TextView)itemView.findViewById(R.id.textView_Login);
            setUpOnClickListeners();
        }

        public String getTitle() {
            return title.getText().toString();
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public String getDescription() {
            return description.getText().toString();
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
        private void setUpOnClickListeners(){
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, Login.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
            learn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(descriptionShown){
                        description.setVisibility(View.GONE);
                        learn_more.setVisibility(View.VISIBLE);
                        learn_more.setText("LEARN MORE");
                        descriptionShown=false;
                    }else{
                        description.setVisibility(View.VISIBLE);
                        learn_more.setText("HIDE");
                        descriptionShown=true;
                    }
                }
            });

        }

        public boolean isDescriptionShown() {
            return descriptionShown;
        }

        public void setDescriptionShown(boolean descriptionShown) {
            this.descriptionShown = descriptionShown;
        }
    }
}
