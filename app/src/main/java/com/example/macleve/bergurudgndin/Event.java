package com.example.macleve.bergurudgndin;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Event extends Post{

    //String eventDate;
    //String eventDescription;
    //String eventName;
    //String eventID;
    private String eventVenue;
    private int eventParNum;
   // public Map<String, Integer> eventParNo = new HashMap<>();
    //eventParNo.put("eventParNum", );


    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String postId, String postTitle, String postDesc, String postDate, String postType,String postFileUrl, String eventVenue,int eventParNum ) {
        super(postId,postTitle, postDesc, postDate, postType, postFileUrl);
        //this.eventID = eventID;
        this.eventVenue = eventVenue;
        this.eventParNum = eventParNum;

    }


    public String getPostId(){return postId;}
    public String getPostTitle() { return postTitle; }
    public String getPostDesc() {
        return postDesc;
    }
    public String getPostDate() {
        return postDate;
    }
    public String getPostType() {
        return postType;
    }
    public String getPostFileUrl() {
        return postFileUrl;
    }//imgurl
    public String getEventVenue() {
        return eventVenue;
    }
    public int getEventParNum() { return eventParNum; }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("postTitle", postTitle);
        result.put("postDesc", postDesc);
        result.put("postDate", postDate);
        result.put("postType", postType);
        result.put("postFileUrl", postFileUrl);
        result.put("eventVenue", eventVenue);
        result.put("eventParNum", eventParNum);

        return result;
    }

}