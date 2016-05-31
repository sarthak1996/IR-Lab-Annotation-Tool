package com.example.sarthak.ir_annotation_tool.ObjectClasses;

/**
 * Created by sarthak on 20/5/16.
 */
public class Sentence {
    private int start;
    private int end;
    private String docId;

    public Sentence(int start, int end, String docId) {
        this.start = start;
        this.end = end;
        this.docId = docId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String  docId) {
        this.docId = docId;
    }
}
