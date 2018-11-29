package com.example.macleve.bergurudgndin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mCtx;
    private List<User> userList;

    public UserAdapter(Context mCtx, List<User> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_users_cardview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        final User user = userList.get(position);
        holder.nameView.setText(user.getName());
        holder.emailView.setText(user.getEmail());
        holder.addressView.setText(user.getAddress());

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mCtx, UpdateUserActivity.class);
                intent.putExtra("uid",user.getUid());
                intent.putExtra("name",user.getName());
                intent.putExtra("email",user.getEmail());
                intent.putExtra("address",user.getAddress());
                mCtx.startActivity(intent);*/

                Intent intent = new Intent(mCtx, MainMenu.class);
                //intent.putExtra("uid",user.getUid());
                intent.putExtra("uid",user.getUid());
                intent.putExtra("name",user.getName());
                intent.putExtra("email",user.getEmail());
                intent.putExtra("address",user.getAddress());
                intent.putExtra("contact",user.getContact());
                intent.putExtra("usertype","parents");

                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //initialization
    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, emailView, addressView;
        CardView userLayout;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tvName);
            emailView = itemView.findViewById(R.id.tvEmail);
            addressView = itemView.findViewById(R.id.tvAddress);
            userLayout = itemView.findViewById(R.id.cvUser);
        }
    }
}
