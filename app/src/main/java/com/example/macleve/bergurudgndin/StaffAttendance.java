package com.example.macleve.bergurudgndin;

//Association class;
public class StaffAttendance{
    String staffId;
    String postId;
    String confirmAttendance;
    //Below is the proper way to use association class
/*
    Post postID;//Post postId[0...*] so if there another list of Post it will be like below
                //Post postName[1...*]
    Staff staffID;
    boolean confirmAttendance = false;//default
    public StaffAttendance(Post postid, Staff staffid, boolean confirmAttendance){//using post and staff obj index, we store new addtional data into both specific post and staff id
        postID = postid;
        staffID = staffid;
        this.confirmAttendance = confirmAttendance;
    }*/

//Below is manual creating the association class
    public StaffAttendance(){
        //empty constructor needed
    }
    public StaffAttendance(String staffId, String postId, String confirmAttendance){
        this.staffId = staffId;
        this.postId = postId;
        this.confirmAttendance = confirmAttendance;
    }
    //getter
    public String getPostID(){return postId ;}
    public String getStaffID(){return  staffId;}
    public String getConfirmAttendance(){return confirmAttendance;}
    //setter
    public void setConfirmAttendance(String confirmAttendance){
        this.confirmAttendance=confirmAttendance;
    }
}
