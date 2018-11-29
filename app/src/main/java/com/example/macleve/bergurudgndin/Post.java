package com.example.macleve.bergurudgndin;

import android.app.Activity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Post{
    //Set<StaffAttendance> staffAttendanceSet; > For proper implementation of association class
    public String postId;
    public String postTitle;
    public String postDesc;
    public String postDate;
    public String postType;
    public String postFileUrl;


    public Post() {
        //empty constructor needed
    }

    public Post(String postId, String postTitle, String postDesc, String postDate, String postType,String postFileUrl ) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postDesc = postDesc;
        this.postDate = postDate;
        this.postType = postType;
        this.postFileUrl = postFileUrl;

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
    }

   /* @Exclude

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("postTitle", postTitle);
        result.put("postDesc", postDesc);
        result.put("postDate", postDate);
        result.put("postType", postType);
        result.put("postFileUrl", postFileUrl);

        return result;
    }*/

}






