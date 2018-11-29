package com.example.macleve.bergurudgndin;

import java.util.Set;

public class Staff {
    //Set<StaffAttendance> staffAttendanceSet; > For proper implementation of association class
    String uid;
    String name;
    String email;
    String address;
    String contact;
    String usertype;


    public Staff(){
        //empty constructor needed
    }

    public Staff(String uid, String name, String email, String address, String contact,String usertype){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.contact = contact;
        this.usertype = usertype;

    }
    public String getUid(){return uid;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getAddress(){return address;}
    public String getContact(){return contact;}
    public String getUsertype(){return usertype;}
}