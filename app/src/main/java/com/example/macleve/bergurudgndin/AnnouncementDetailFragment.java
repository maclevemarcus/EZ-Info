package com.example.macleve.bergurudgndin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AnnouncementDetailFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Context context;
    private TextView annName, annDate, annDesc ;
    private Dialog mDialog;
    private static String usertype;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v= inflater.inflate(R.layout.fragment_announcement_detail, container,false);

        Intent intent = getActivity().getIntent();
        usertype = intent.getStringExtra("usertype");
        FloatingActionButton editPostbtn = v.findViewById(R.id.EditPostBtn);
        if(usertype.equalsIgnoreCase("staff")) {

            editPostbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//if want to open other class will use intent thats when we need to call getactivity()
                    mDialog = new Dialog(getActivity());
                    ShowPopUp();
                }
            });
        }else{
            editPostbtn.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //create instances to the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        context = getActivity();
        //mDialog = new Dialog(context);

        annName = view.findViewById(R.id.Aname);
        annDate = view.findViewById(R.id.Adate);
        annDesc = view.findViewById(R.id.Adesc);

        Intent intent = getActivity().getIntent();
        final String announID = intent.getStringExtra("viewAnnId");
        // Read from the database
        mDatabase.child("Announcement").child(announID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Announcement ann = dataSnapshot.getValue(Announcement.class);
                if( ann!=null) {

                    String aname = ann.getPostTitle();
                    String adate = ann.getPostDate();
                    String adesc = ann.getPostDesc();

                    annName.setText(aname);
                    annDate.setText(adate);
                    annDesc.setText(adesc);
                }//end of if
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void ShowPopUp(){
        TextView txtclose;
        Button  deleteBtn, updateBtn;

        mDialog.setContentView(R.layout.editpostpopup);
        txtclose = mDialog.findViewById(R.id.txtclose);
        //editPostBtn = mDialog.findViewById(R.id.EditPostBtn);
        updateBtn = mDialog.findViewById(R.id.updateBtn);
        deleteBtn = mDialog.findViewById(R.id.deleteBtn);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                Intent intent1 = getActivity().getIntent();
                String deleteAnnID = intent1.getStringExtra("viewAnnId");

                Intent intent = new Intent(context, MainMenu.class);
                //send id to be delete
                intent.putExtra("deleteAnnId",deleteAnnID);
                startActivity(intent);
                //finish();

            }//end of onclick
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
/*
                Intent intent1 = getActivity().getIntent();
                String eventid = intent1.getStringExtra("vieweventid");
                String imgurl = intent1.getStringExtra("imgurl");


                intent1.putExtra("updateeventid",eventid);
                UpdateEventFragment fragment = new UpdateEventFragment();
                replaceFragment(fragment);
*/
            }//end of onclick
        });
        mDialog.show();

    }

    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {

    }
}

