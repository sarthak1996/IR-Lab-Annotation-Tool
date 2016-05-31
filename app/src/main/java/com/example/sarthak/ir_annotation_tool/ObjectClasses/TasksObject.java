package com.example.sarthak.ir_annotation_tool.ObjectClasses;

/**
 * Created by sarthak on 19/5/16.
 */
public class TasksObject {
    private String title;
    private String description;
    private boolean descriptionShown;

    public TasksObject() {
        title="";
        description="";
        descriptionShown=false;
    }

    public TasksObject(String title, String description) {
        this.title = title;
        this.description = description;
        descriptionShown=false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDescriptionShown() {
        return descriptionShown;
    }

    public void setDescriptionShown(boolean descriptionShown) {
        this.descriptionShown = descriptionShown;
    }
}
