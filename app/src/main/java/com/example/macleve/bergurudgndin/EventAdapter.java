package com.example.macleve.bergurudgndin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mCtx;
    private List<Event> eventList;
    //private List<EventA> eventAList;
    private StorageReference storageReference;


    public EventAdapter(Context mCtx, List<Event> eventList) {
        this.mCtx = mCtx;
        this.eventList = eventList;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_events_cardview, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {
        final Event event = eventList.get(position);
        //holder.nameView.setText(event.getEventID());
        holder.eventName.setText(event.getPostTitle());
        holder.eventVenue.setText(event.getEventVenue());
        holder.eventDate.setText(event.getPostDate());
        String partNo = String.valueOf(event.getEventParNum());
        holder.eparnum.setText(partNo);
        //holder.addressView.setText(event.getEventDescription());eventIMG

        Picasso.with(mCtx).load(event.getPostFileUrl()).fit()
                .centerCrop().into(holder.eventIMG);//using picasso to load image

        holder.eventLayout.setOnClickListener(new View.OnClickListener() {
            //Fragment frag = null;
           // MainMenu test = new MainMenu();


            @Override
            public void onClick(View v) {

                //INTENT TO MAIN MENU AND THEN FROM THERE LOAD THE EVENT DETAIL FRAGMENT

                Intent intent = new Intent(mCtx, MainMenu.class);
                intent.putExtra("vieweventid",event.getPostId());
                intent.putExtra("imgurl",event.getPostFileUrl());
                mCtx.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //initialization
    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventVenue, eventDate,eventDesc,eparnum ;
        CardView eventLayout;
        ImageView eventIMG;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.Ename);
            eventVenue = itemView.findViewById(R.id.Evenue);
            eventDate = itemView.findViewById(R.id.Edate);
            eventIMG= itemView.findViewById(R.id.eventIMG);
            eventLayout = itemView.findViewById(R.id.cvEvent);
            eparnum= itemView.findViewById(R.id.Eparnum);

        }
    }

}
