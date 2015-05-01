package com.example.alias.androidmedia;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MovieModelContainer {
    ArrayList<Map<String,String>> arrayList;
    int id = 0;
    public MovieModelContainer() {
        arrayList = new ArrayList<>();
    }


    public void addItem(String name, String url){
        Map<String,String> map = new HashMap();
        map.put("id", ""+ (arrayList.size()+1));
        map.put("name", name);
        map.put("url", url);

        arrayList.add(map);
    }

    public ArrayList<Map<String, String>> getArrayList() {
        return arrayList;
    }

    private Map<String,String> find(String name){
        for (Map<String,String> map : arrayList){
            if(name.equals(map.get("name"))) return map;
        }
        return null;
    }

    private Map<String,String> getById(int id){
        String value = id + "";
        for (Map<String,String> map : arrayList){
            if(value.equals(map.get("id"))) return map;
        }
        return null;
    }

    @Deprecated
    public int getId(String name){
        Map<String,String> m = find(name);
        if(m!=null) return Integer.parseInt(m.get("id"));
        else return -1;
    }

    @Deprecated
    public String getUrl(String name){
        Map<String,String> m = find(name);
        if(m!=null) return m.get("url");
        else return null;
    }

    public String getUrl(int id){
        Map<String,String> m = getById(id);
        return m.get("url");
    }

    public String getUrl(){
       return getUrl(id);
    }

    @Deprecated
    public boolean isFirst(String name){
        if(getId(name)== 1) return true;
        else return false;
    }

    public boolean isFirst(){
        if(id == 1) return true;
        else return false;
    }

    @Deprecated
    public boolean isLast(String name){
        if(getId(name)== arrayList.size()) return true;
        else return false;
    }

    public boolean isLast(){
        if(id == arrayList.size()) return true;
        else return false;
    }

    private String getName(int id){
        Map<String,String> m = getById(id);
        return m.get("name");
    }

    public String getName(){
        return getName(id);
    }

    @Deprecated
    public String getPrevious(String name){
        int curId = getId(name);
        if(isFirst(name)) return name;
        else return getName(curId - 1);

    };

    public String getPrevious(){
        if(isFirst()) return getName();
        else return getName(id--);
    }
    @Deprecated
    public String getNext(String name){
        int curId = getId(name);
        if(isLast(name))
            return name;
        else return getName(curId + 1);

    };

    public String getNext(){
        if(isLast()) return getName();
        id++;
        return getName();
    }

    public void setCurrent(int currentId){
        id = currentId;
    }


}
