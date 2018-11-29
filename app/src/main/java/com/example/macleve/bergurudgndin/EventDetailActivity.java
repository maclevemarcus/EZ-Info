package com.example.macleve.bergurudgndin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import java.util.Optional;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Context context;
    private ImageView eventIMG;
    private TextView eventName, eventVenue, eventDate, eventDesc, partNo ;
    private Button attenConfirmBtn;
    private Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        mDialog = new Dialog(this);
        //create instances to the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        context = getApplicationContext();

        eventName = findViewById(R.id.Ename);
        eventVenue = findViewById(R.id.Evenue);
        eventDate = findViewById(R.id.Edate);
        eventIMG = findViewById(R.id.eventIMG);
        eventDesc = findViewById(R.id.Edesc);
        partNo = findViewById(R.id.Eparnum);
        attenConfirmBtn = findViewById(R.id.AttenConfirmBtn);



        Intent intent = getIntent();
        final String eventid = intent.getStringExtra("eventid");

        //*********FOR EVENT************
        // Read from the database
        mDatabase.child("Event").child(eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Event event = dataSnapshot.getValue(Event.class);


                if( event!=null){

                Picasso.with(context).load(event.getPostFileUrl()).resize(750,750)
                        .centerInside().into(eventIMG);

                String eventname = event.getPostTitle();
                String eventvenue = event.getEventVenue();
                String eventdate = event.getPostDate();
                String eventdesc = event.getPostDesc();
                //String par = String.valueOf(et.getPartNo());

                Log.d("123","hoho yg bru ni atas");
                eventName.setText(eventname);
                eventVenue.setText(eventvenue);
                eventDate.setText(eventdate);
                eventDesc.setText(eventdesc);
                //partNo.setText(par);
                }//end of if
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });

        ////*********FOR EVENTATTENDANCE************
        // Read from the database
        mDatabase.child("EventAttendance").child(eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                final EventAttendance et = dataSnapshot.getValue(EventAttendance.class);
                if( et!=null) {
                    final String eventname = eventName.getText().toString();
                    partNo.setText(String.valueOf(et.getPartNo()));

                    if (et.getPartNo() != 0) {
                        final int partNom = et.getPartNo();

                        attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                confirm(eventid, eventname, partNom);
                            }
                        });
                    } else if (et.getPartNo() == 0) {
                        final int partNom = 0;

                        attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                confirm(eventid, eventname, partNom);
                            }
                        });
                    }
                }//end of if
            }//enf of onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirm( String eventid, String eventname, int partNom){

        partNom = partNom +1;
                final EventAttendance et = new EventAttendance(eventid, eventname, partNom);
                mDatabase.child("EventAttendance").child(eventid).setValue(et).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            String partNum = String.valueOf(et.getPartNo());
                            partNo.setText(partNum);
                            attenConfirmBtn.setEnabled(false);
                            Log.d("123", "huhu");

                        } else {
                            Toast.makeText(EventDetailActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
       /* if( v == attenConfirmBtn){
            ConfirmAttendance();
        }*/
        /*else if(v == txtclose){
            mDialog.dismiss();
        }else if (v == editPostBtn){
            mDialog.setContentView(R.layout.editpostpopup);
            mDialog.show();
        }*/
    }
   public void ShowPopup(View v){
        TextView txtclose;
        Button editPostBtn, deleteBtn, updateBtn;

       mDialog.setContentView(R.layout.editpostpopup);
       txtclose = mDialog.findViewById(R.id.txtclose);
       editPostBtn = mDialog.findViewById(R.id.EditPostBtn);
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

               Intent intent1 = getIntent();
               String eventid = intent1.getStringExtra("eventid");
               String imgurl = intent1.getStringExtra("imgurl");

               Intent intent = new Intent(context, MainMenu.class);
               //send id to be delete
               intent.putExtra("deleteeventid",eventid);
               intent.putExtra("deleteimgurl",imgurl);
               startActivity(intent);
               finish();

           }//end of onclick
       });
       updateBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mDialog.dismiss();

               Intent intent1 = getIntent();
               String eventid = intent1.getStringExtra("eventid");
               String imgurl = intent1.getStringExtra("imgurl");

               Intent intent = new Intent(context, MainMenu.class);
               //send id to be updated
               intent.putExtra("updateeventid",eventid);
               intent.putExtra("currentimage",imgurl);

               startActivity(intent);
               //dont destroy so that from update fragment can go to event detail
               //finish();

           }//end of onclick
       });
       mDialog.show();

    }
    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
