package com.example.macleve.bergurudgndin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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

import java.util.HashMap;
import java.util.Map;

public class EventDetailFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Context context;
    private ImageView eventIMG;
    private TextView eventName, eventVenue, eventDate, eventDesc,partNo ;
    private Button attenConfirmBtn;
    private Dialog mDialog;
    private static String usertype;
    private static int participantsNo;
    private static String pEventID,pEventTitle,pEventDesc,pEventDate,pEventType,pEventImgUrl,pEventVenue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v= inflater.inflate(R.layout.event_detail, container,false);

        Intent intent = getActivity().getIntent();
        usertype = intent.getStringExtra("usertype");
        //Button editPostbtn = v.findViewById(R.id.EditPostBtn);
        FloatingActionButton editPostbtn = v.findViewById(R.id.EditPostBtn);
        if(usertype.equalsIgnoreCase("staff")) {

            editPostbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//if want to open other class will use intent thats when we need to call getactivity()
                    //AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    //alert.show();
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

        eventName = view.findViewById(R.id.Ename);
        eventVenue = view.findViewById(R.id.Evenue);
        eventDate = view.findViewById(R.id.Edate);
        eventIMG = view.findViewById(R.id.eventIMG);
        eventDesc = view.findViewById(R.id.Edesc);
        partNo = view.findViewById(R.id.Eparnum);
        attenConfirmBtn = view.findViewById(R.id.AttenConfirmBtn);

        //editPostBtn = view.findViewById(R.id.EditPostBtn);
        //editPostBtn.setOnClickListener(EventDetailFragment.this);


        Intent intent = getActivity().getIntent();
        final String eventid = intent.getStringExtra("vieweventid");
        // Read from the database
        mDatabase.child("Event").child(eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    Picasso.with(context).load(event.getPostFileUrl()).fit()
                            .centerCrop().into(eventIMG);

                    String eventname = event.getPostTitle();
                    String eventvenue = event.getEventVenue();
                    String eventdate = event.getPostDate();
                    String eventdesc = event.getPostDesc();
                    participantsNo = event.getEventParNum();//current event par num

                    eventName.setText(eventname);
                    eventVenue.setText(eventvenue);
                    eventDate.setText(eventdate);
                    eventDesc.setText(eventdesc);
                    partNo.setText(String.valueOf(participantsNo));
                }//end of if
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });

        ////*********CHECK STAFF ATTENDANCE STATUS, IF "NO" THEN DO THIS************
        // Read from the database, to get staff attendance status
        if (usertype.equalsIgnoreCase("staff")) {
            mDatabase.child("Staff_participants").child(mAuth.getUid() + "_" + eventid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        //alertDialog.setTitle("Alert");

                        attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alertDialog.setMessage("Confirm your attendance for this event?");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (usertype.equalsIgnoreCase("staff")) {
                                                    String attend = "yes";
                                                    Staff_participants sp = new Staff_participants(mAuth.getUid(), eventid, attend);
                                                    mDatabase.child("Staff_participants").child(mAuth.getUid() + "_" + eventid).setValue(sp);
                                                }
                                                UpdateEventParNumber();//CALL THIS LATER BUT IN THE METHOD REMOVE ONCLICK BUTTON
                                                //Call AddEventParNumber inside UpdateEventParNumber
                                                Toast.makeText(context, "Attendance confirmed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }

                        });

                    } else if (dataSnapshot.hasChildren()) {
                        attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "You already confirmed your attendance to this event", Toast.LENGTH_SHORT).show();
                            }

                        });

                    }
                }//enf of onDataChange

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
                }
            });
        }//end of usertype staff
        else if (usertype.equalsIgnoreCase("parents")) {
        ////*********CHECK PARENTS ATTENDANCE STATUS, IF "NO" THEN DO THIS************
        // Read from the database, to get parents attendance status

        mDatabase.child("Parents_participants").child(mAuth.getUid() + "_" + eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    //alertDialog.setTitle("Alert");

                    attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.setMessage("Confirm your attendance for this event?");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (usertype.equalsIgnoreCase("parents")) {
                                                String attend = "yes";
                                                Parents_participants pp = new Parents_participants(mAuth.getUid(), eventid, attend);
                                                mDatabase.child("Parents_participants").child(mAuth.getUid() + "_" + eventid).setValue(pp);
                                            }
                                            UpdateEventParNumber();//CALL THIS LATER BUT IN THE METHOD REMOVE ONCLICK BUTTON
                                            //Call AddEventParNumber inside UpdateEventParNumber
                                            Toast.makeText(context, "Attendance confirmed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                        }

                    });

                } else if (dataSnapshot.hasChildren()) {
                    attenConfirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "You already confirmed your attendance to this event", Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            }//enf of onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }//end of if usertype parents
    }
    /*
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
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }*/


    //update only after the user already have association class and if their attendance is "yes"
    public void UpdateEventParNumber(){
        Intent intent = getActivity().getIntent();
        final String eventid = intent.getStringExtra("vieweventid");
        String pID=mAuth.getUid()+"_"+eventid;
        ////*********FOR EVENT ATTENDANCE(STAFF)************
        // Read from the database, to get staff attendance status
        mDatabase.child("Staff_participants").child(pID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final Staff_participants sp = dataSnapshot.getValue(Staff_participants.class);
                    final String attendanceStaff = sp.getAttendance();


                            Log.d("TEST",attendanceStaff);
                            if (attendanceStaff.equalsIgnoreCase("yes")) {
                                AddEventParNumber(eventid,participantsNo);//update participants number of specific event

                                //ADD NUMBER OF PARTICIPANT FOR THAT EVENT by 1 participantsNo
                            }

                }//end of if
            }//enf of onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });

        ////*********FOR EVENT ATTENDANCE(PARENTS)************
        // Read from the database, to get staff attendance status
        mDatabase.child("Parents_participants").child(pID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final Parents_participants pp = dataSnapshot.getValue(Parents_participants.class);
                    final String attendancePar = pp.getAttendance();


                    Log.d("TEST",attendancePar);
                    if (attendancePar.equalsIgnoreCase("yes")) {
                        AddEventParNumber(eventid,participantsNo);//update participants number of specific event

                        //ADD NUMBER OF PARTICIPANT FOR THAT EVENT by 1 participantsNo
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

    //ADDING NUMBER OF EVENT PARTICIPANTS
    //UPDATE USING HASHMAP KEY
    public void AddEventParNumber( String eventid,int partNom){

        partNom = partNom +1;

        HashMap<String, Object> result = new HashMap<>();
        result.put("eventParNum", partNom);
        mDatabase.child("Event").child(eventid).updateChildren(result);
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
                String eventid = intent1.getStringExtra("vieweventid");
                String imgurl = intent1.getStringExtra("imgurl");

                Intent intent = new Intent(context, MainMenu.class);
                //send id to be delete
                intent.putExtra("deleteeventid",eventid);
                intent.putExtra("deleteimgurl",imgurl);
                startActivity(intent);
                //finish();

            }//end of onclick
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                Intent intent1 = getActivity().getIntent();
                String eventid = intent1.getStringExtra("vieweventid");
                String imgurl = intent1.getStringExtra("imgurl");

                intent1.putExtra("updateeventid",eventid);
                intent1.putExtra("currentimage",imgurl);
                UpdateEventFragment fragment = new UpdateEventFragment();
                replaceFragment(fragment);

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
        //if(v==editPostBtn){
            //ShowPopUp
       // }
    }
}
