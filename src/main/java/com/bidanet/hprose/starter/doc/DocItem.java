package com.bidanet.hprose.starter.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuejike on 2017/6/28.
 */
public class DocItem {
    private String title;
    private String name;
    private List<List<DocItem>> table=new ArrayList<>();
    private List<DocItem> subDoc=new ArrayList<>(1);


    public DocItem(String title) {
        this.title = title;
    }

    public DocItem(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public void addRow(String ... data){
        ArrayList<DocItem> docItems = new ArrayList<>();
        for (String s : data) {
            docItems.add(new DocItem(s));
        }
        table.add(docItems);
    }
    public void setTitle(String... title){
        List<DocItem> docItems;
        if (table.size()>0){
            docItems=table.get(0);
            docItems.clear();
        }else{
            docItems= new ArrayList<>();
            table.add(docItems);
        }

        for (String s : title) {
            docItems.add(new DocItem(s));
        }


    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<List<DocItem>> getTable() {
        return table;
    }

    public void setTable(List<List<DocItem>> table) {
        this.table = table;
    }

    public List<DocItem> getSubDoc() {
        return subDoc;
    }

    public void setSubDoc(List<DocItem> subDoc) {
        this.subDoc = subDoc;
    }

    public void addSubDoc(DocItem docItem){
        this.subDoc.add(docItem);
    }
    @Override
    public String toString() {
        return getTitle();
    }
}
