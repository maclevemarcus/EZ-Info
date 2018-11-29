package com.example.macleve.bergurudgndin;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    String uid;
    String name;
    String email;
    String address;
    String contact;


    public User(){
        //empty constructor needed
    }

    public User(String uid, String name, String email, String address, String contact){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.contact = contact;

    }

    public String getUid(){return uid;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getAddress(){return address;}
    public String getContact(){return contact;}

    /*
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        return result;
    }*/
}