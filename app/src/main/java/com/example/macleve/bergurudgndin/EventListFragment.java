package com.example.macleve.bergurudgndin;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment implements View.OnClickListener{

    private FloatingActionButton mAddEventBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Event> mEventList;
    private Context context;
    private static String type;//use static so any manipulation will be stored incrementally


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    //create instances //initialize
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.activity_event_list, container,false);
        Intent intent = getActivity().getIntent();
        type = intent.getStringExtra("usertype");
      // type = this.getArguments().getString("usertype");
        Log.d("WHAHA", "onCreateView: "+type);
        mAddEventBtn = v.findViewById(R.id.AddBtn);

             //if intent value is not received
            if(type==null){
                Toast.makeText(getActivity().getApplicationContext(), "Slow internet connection, please wait" , Toast.LENGTH_SHORT).show();
                //Reload data from db
                //get usertype
                mDatabase.child("Staff").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            Staff staff = dataSnapshot.getValue(Staff.class);
                            type = staff.getUsertype();//assign the usertype after reload db
                            mAddEventBtn.setVisibility(View.VISIBLE);
                        }else{
                            type = "parents";
                            mAddEventBtn.setVisibility(View.INVISIBLE);
                        }
                        //if usertype = staff, do some action
                        if(type.equalsIgnoreCase("staff")){
                            //when scrolling button is set to hide
                            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    if (dy > 0 || dy < 0 && mAddEventBtn.isShown()) {
                                        mAddEventBtn.hide();
                                    }
                                }

                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        mAddEventBtn.show();
                                    }
                                    super.onScrollStateChanged(recyclerView, newState);
                                }
                            });
                        }//end of if
                        Log.d("reload","Database has been refreshed.");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else if(type.equalsIgnoreCase("staff")){
                mAddEventBtn.setVisibility(View.VISIBLE);

            }else{
                mAddEventBtn.setVisibility(View.INVISIBLE);
            }

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();

        //create instances //initialize
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase.keepSynced(true);

        //link with xml
        mAddEventBtn = view.findViewById(R.id.AddBtn);
        mRecyclerView = view.findViewById(R.id.rvEvent);

        if(type!=null){
            if(type.equalsIgnoreCase("staff")){
                //when scrolling button is set to hide
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0 || dy < 0 && mAddEventBtn.isShown()) {
                            mAddEventBtn.hide();
                        }
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            mAddEventBtn.show();
                        }
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            }
        }//end of if

        //attach listener
        mAddEventBtn.setOnClickListener(this);

        mDatabase.child("Event").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //iterating through all the values in database
                mEventList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    mEventList.add(event);
                }
                //creating adapter
                mAdapter = new EventAdapter(context, mEventList);

                //adding adapter to recyclerview
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //other method
        mRecyclerView.setHasFixedSize(true); //set fixed size for element in recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
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
        Fragment fragment = null;
        if( v == mAddEventBtn){
            fragment = new AddEventFragment();
            replaceFragment(fragment);
            //getActivity().finish();
            //startActivity(new Intent(context, CreateEventActivity.class));//must convert create event to fragment
        }
    }

}
