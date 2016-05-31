package com.example.sarthak.ir_annotation_tool.ObjectClasses;

import java.util.ArrayList;

/**
 * Created by sarthak on 20/5/16.
 */
public class Relation {
    private String relationName;
    private String relationSense;
    private ArrayList<Sentence> arg1;
    private ArrayList<Sentence> arg2;
    private ArrayList<Sentence> connective;
    private int relation_id;


    public Relation() {
        connective=new ArrayList<>();
        arg1=new ArrayList<>();
        arg2=new ArrayList<>();
    }

    public Relation(String relationName, String relationSense) {
        this.relationName = relationName;
        this.relationSense = relationSense;
        connective=new ArrayList<>();
        arg1=new ArrayList<>();
        arg2=new ArrayList<>();
    }


    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationSense() {
        return relationSense;
    }

    public void setRelationSense(String relationSense) {
        this.relationSense = relationSense;
    }

    public ArrayList<Sentence> getArg1() {
        return arg1;
    }

    public void setArg1(ArrayList<Sentence> arg1) {
        this.arg1 = arg1;
    }

    public ArrayList<Sentence> getArg2() {
        return arg2;
    }

    public void setArg2(ArrayList<Sentence> arg2) {
        this.arg2 = arg2;
    }

    public ArrayList<Sentence> getConnective() {
        return connective;
    }

    public void setConnective(ArrayList<Sentence> connective) {
        this.connective = connective;
    }

    public boolean isFilled() {
        if (relationName != null && !relationName.isEmpty() && !relationName.trim().equals("")) {
            if (relationSense != null && !relationSense.isEmpty() && !relationSense.trim().equals("")) {
                if (arg1.size() > 0 && arg2.size() > 0 && connective.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public int getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(int relation_id) {
        this.relation_id = relation_id;
    }
}
