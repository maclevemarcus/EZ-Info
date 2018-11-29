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


public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.EventViewHolder> {

    private Context mCtx;
    private List<Announcement> annList;
    private StorageReference storageReference;


    public AnnouncementAdapter(Context mCtx, List<Announcement> annList) {
        this.mCtx = mCtx;
        this.annList = annList;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.announcement_cardview, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {
        final Announcement post = annList.get(position);

        holder.annTitle.setText(post.getPostTitle());
        holder.annDate.setText(post.getPostDate());
        holder.annDesc.setText(post.getPostDesc());
        holder.type.setText(post.getInfoType());
       /* Picasso.with(mCtx).load(post.getPostImgUrl()).fit()
                .centerCrop().into(holder.eventIMG);//using picasso to load image
                */

        holder.annLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INTENT TO MAIN MENU AND THEN FROM THERE LOAD THE EVENT DETAIL FRAGMENT

                Intent intent = new Intent(mCtx, MainMenu.class);
                intent.putExtra("viewAnnId",post.getPostId());
                mCtx.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return annList.size();
    }

    //initialization
    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView annTitle, annDate,annDesc, type ;
        CardView annLayout;
        ImageView eventIMG;

        public EventViewHolder(View itemView) {
            super(itemView);
            annTitle = itemView.findViewById(R.id.AnnouncementTitle);
            annDate = itemView.findViewById(R.id.date);
            annDesc= itemView.findViewById(R.id.descriptionTxt);
            type =itemView.findViewById(R.id.info);
            annLayout = itemView.findViewById(R.id.cvAnnounce);

        }
    }

}
