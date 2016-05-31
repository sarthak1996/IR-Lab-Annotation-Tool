package com.example.sarthak.ir_annotation_tool.ObjectClasses;

/**
 * Created by sarthak on 20/5/16.
 */
public class DocumentObject {
    private String name;
    private String content;
    private boolean checked;
    private String docPath;

    public DocumentObject(String name){
        this.name=name;
        checked=false;
        content="";
    }
    public DocumentObject(String name,String docPath){
        this.name=name;
        this.docPath=docPath;
        checked=false;
        content="";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDocId() {
        return docPath;
    }

    public void setDocId(String docId) {
        this.docPath = docId;
    }
}
