package com.example.macleve.bergurudgndin;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Announcement extends Post{

    String infoType;

    public Announcement() {
        //empty constructor needed
    }

    public Announcement(String postId, String postTitle, String postDesc, String postDate, String postType, String postFileUrl, String infoType ) {
        super(postId, postTitle, postDesc, postDate, postType, postFileUrl);

        this.infoType = infoType;
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
    public String getInfoType() {
        return infoType;
    }

   /* @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("postTitle", postTitle);
        result.put("postDesc", postDesc);
        result.put("postDate", postDate);
        result.put("postType", postType);
        result.put("postImgUrl", postFileUrl);
        result.put("infoType", infoType);

        return result;
    }*/
}