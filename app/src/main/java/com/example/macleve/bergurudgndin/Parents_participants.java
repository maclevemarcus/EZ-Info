package com.example.macleve.bergurudgndin;


public class Parents_participants {


    private String postId;
    private String parentsId;
    private String attendance;
    //String eventName;




    public Parents_participants() {
        //empty constructor needed
    }

    public Parents_participants(String parentsId, String postId, String attendance ) {

        this.parentsId = parentsId;
        this.postId = postId;
        this.attendance = attendance;

    }

    public String getPostId() {
        return postId;
    }
    public String getParentsId() {
        return parentsId;
    }
    public String getAttendance(){return attendance;}


}