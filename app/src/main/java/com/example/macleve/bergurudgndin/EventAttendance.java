package com.example.macleve.bergurudgndin;


public class EventAttendance {


    String eventID;
    String eventName;
    int partNo;




    public EventAttendance() {
        //empty constructor needed
    }

    public EventAttendance(String eventID, String eventName, int partNo ) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.partNo = partNo;


    }

    public String getEventID() {
        return eventID;
    }
    public String getEventName() {
        return eventName;
    }
    public int getPartNo(){return partNo;}


}