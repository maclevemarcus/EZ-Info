package com.example.macleve.bergurudgndin;


public class Staff_participants {


    private String postId;
    private String staffId;
    private String attendance;
    //String eventName;




    public Staff_participants() {
        //empty constructor needed
    }

    public Staff_participants(String staffId, String postId, String attendance ) {

        this.staffId = staffId;
        this.postId = postId;
        this.attendance = attendance;


    }

    public String getPostId() {
        return postId;
    }
    public String getStaffId() {
        return staffId;
    }
    public String getAttendance(){return attendance;}


}